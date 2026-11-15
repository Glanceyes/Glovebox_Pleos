"""Run the Mac Bluetooth controller for the iPhone attached to this emulator."""
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import subprocess
import tempfile


def compile_radio():
    src = Path(__file__).with_name("GloveboxPhoneRemote.m")
    digest = hashlib.sha256(src.read_bytes()).hexdigest()[:16]
    cache = Path.home() / "Library" / "Caches" / "Glovebox" / "PhoneRemote"
    cache.mkdir(parents=True, mode=0o700, exist_ok=True)
    program = cache / ("remote-" + digest)
    if not program.is_file():
        with tempfile.TemporaryDirectory(dir=cache) as tmp:
            output = Path(tmp) / "remote"
            subprocess.run(["/usr/bin/xcrun", "clang", "-fobjc-arc", "-framework", "Foundation",
                            "-framework", "IOBluetooth", str(src), "-o", str(output)],
                           check=True, capture_output=True, timeout=30)
            os.replace(output, program)
    return program


def start_radio(usb_id, lookup):
    if not lookup:
        return None
    # Pairing identity comes from this same USB phone. No nearby name matching.
    device = subprocess.run([lookup, "lockdown", "info"], capture_output=True, timeout=12, check=True)
    details = json.loads(device.stdout)
    if details.get("UniqueDeviceID", "").replace("-", "").lower() != usb_id:
        return None
    address = details.get("BluetoothAddress", "")
    if not re.fullmatch(r"(?:[0-9a-fA-F]{2}:){5}[0-9a-fA-F]{2}", address):
        return None
    return start_controller(address)


def start_controller(address):
    proc = subprocess.Popen([str(compile_radio())], stdin=subprocess.PIPE, stdout=subprocess.PIPE, bufsize=0)
    try:
        proc.stdin.write((address + "\n").encode("ascii"))
        return proc
    except OSError:
        close_radio(proc)
        raise


def close_radio(proc):
    if not proc:
        return
    try:
        proc.stdin.close()
        proc.wait(timeout=2)
    except (OSError, subprocess.TimeoutExpired):
        proc.kill()
        proc.wait()
    finally:
        proc.stdout.close()
