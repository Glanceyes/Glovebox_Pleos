"""Open a track selected in the Pleos emulator on the same USB-attached, Xcode-paired iPhone."""
import argparse
import fcntl
import json
import os
from pathlib import Path
import plistlib
import re
import select
import socket
import shutil
from iphone_radio import start_radio, close_radio
import subprocess
import tempfile
import time

parser = argparse.ArgumentParser()
parser.add_argument("--adb", required=True)
parser.add_argument("--emulator", required=True)
parser.add_argument("--phone-info", default=shutil.which("pymobiledevice3"))
args = parser.parse_args()
adb = [args.adb, "-s", args.emulator]


def capture(cmd, timeout=15):
    return subprocess.run(cmd, capture_output=True, check=True, timeout=timeout).stdout


def attached_phones():
    roots = plistlib.loads(capture(["/usr/sbin/ioreg", "-p", "IOUSB", "-a", "-l"]))
    work = [roots]
    serials = set()
    while work:
        item = work.pop()
        if isinstance(item, list):
            work.extend(item)
        elif isinstance(item, dict):
            if item.get("idVendor") == 1452 and item.get("USB Product Name") == "iPhone":
                serial = item.get("USB Serial Number", "")
                if serial:
                    serials.add(serial.replace("-", "").lower())
            work.extend(item.get("IORegistryEntryChildren", []))
    return serials


def paired_phone():
    plugged = attached_phones()
    if not plugged:
        return None
    with tempfile.TemporaryDirectory() as directory:
        path = Path(directory) / "devices.json"
        capture(["/usr/bin/xcrun", "devicectl", "list", "devices", "--timeout", "8", "--json-output", str(path)])
        devices = json.loads(path.read_text())["result"]["devices"]
    matches = []
    for device in devices:
        hw = device.get("hardwareProperties", {})
        if hw.get("udid", "").replace("-", "").lower() not in plugged:
            continue
        if device.get("connectionProperties", {}).get("pairingState") == "paired":
            matches.append((device["identifier"], hw["marketingName"], hw["udid"].replace("-", "").lower()))
    return matches[0] if len(matches) == 1 else None


def send(stream, message):
    stream.write((json.dumps(message, ensure_ascii=False) + "\n").encode("utf-8"))


def serve(phone):
    user = capture(adb + ["shell", "am", "get-current-user"]).decode().strip()
    if not user.isdigit():
        return
    port = capture(adb + ["forward", "tcp:0", "localabstract:io.memorydrive.roads.iphone." + user]).decode().strip()
    try:
        with socket.create_connection(("127.0.0.1", int(port)), timeout=5) as channel, channel.makefile("rwb", buffering=0) as stream:
            channel.settimeout(3)
            send(stream, {"device": phone[1]})
            radio = None
            watching = False
            retry_at = 0
            next_usb_check = time.monotonic() + 2
            try:
                while True:
                    now = time.monotonic()
                    if now >= next_usb_check:
                        if phone[2] not in attached_phones():
                            return
                        send(stream, {})
                        next_usb_check = now + 2
                    if watching and radio is None and now >= retry_at:
                        retry_at = now + 20
                        try:
                            radio = start_radio(phone[2], args.phone_info)
                        except (OSError, ValueError, subprocess.SubprocessError):
                            pass
                    readers = [channel] if radio is None else [channel, radio.stdout]
                    ready, _, _ = select.select(readers, [], [], .6)
                    if radio and radio.stdout in ready:
                        line = radio.stdout.readline(8192)
                        if not line:
                            close_radio(radio)
                            radio = None
                            retry_at = time.monotonic() + 5
                            send(stream, {"media": {}})
                        else:
                            report = json.loads(line)
                            if isinstance(report.get("media"), dict):
                                # Objective-C BOOL has signed-char representation on some SDKs.
                                media = report["media"]
                                if "playing" in media:
                                    media["playing"] = bool(media["playing"])
                                send(stream, report)
                    if channel not in ready:
                        continue
                    line = stream.readline(1024)
                    if not line:
                        return
                    event = json.loads(line)
                    if "watch" in event:
                        watching = event["watch"] is True
                        if not watching:
                            close_radio(radio)
                            radio = None
                        continue
                    if phone[2] not in attached_phones():
                        return
                    command = event.get("transport")
                    if command in ("play", "pause"):
                        if radio:
                            radio.stdin.write((command + "\n").encode("ascii"))
                        continue
                    video, seq = event.get("id", ""), event.get("seq")
                    if not isinstance(seq, int) or not re.fullmatch(r"[A-Za-z0-9_-]{11}", video):
                        return
                    try:
                        capture(["/usr/bin/xcrun", "devicectl", "device", "process", "launch",
                                 "--device", phone[0], "--payload-url", "https://music.youtube.com/watch?v=" + video,
                                 "--timeout", "8", "com.google.ios.youtubemusic"], timeout=10)
                        opened = True
                    except (OSError, subprocess.SubprocessError):
                        opened = False
                    send(stream, {"seq": seq, "opened": opened})
                    print("iPhone track link", video, "accepted" if opened else "not opened", flush=True)
            finally:
                close_radio(radio)
    finally:
        subprocess.run(adb + ["forward", "--remove", "tcp:" + port], capture_output=True, timeout=5)


def main():
    lock_path = Path(tempfile.gettempdir()) / ("glovebox-devicehub-%d-%s.lock" % (os.getuid(),args.emulator))
    with lock_path.open("a") as lock:
        try:
            fcntl.flock(lock, fcntl.LOCK_EX | fcntl.LOCK_NB)
        except BlockingIOError:
            return
        deadline = time.monotonic() + 120
        while time.monotonic() < deadline:
            try:
                if capture(adb + ["get-state"], timeout=4).strip() == b"device":
                    deadline = time.monotonic() + 30
                    phone = paired_phone()
                    if phone:
                        serve(phone)
                        deadline = time.monotonic() + 30
            except (OSError, ValueError, KeyError, subprocess.SubprocessError):
                pass
            time.sleep(3)


if __name__ == "__main__":
    main()
