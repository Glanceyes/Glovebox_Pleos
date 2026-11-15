package io.memorydrive.pleos;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import static io.memorydrive.pleos.AlbumPlayer.call;
import static io.memorydrive.pleos.AlbumPlayer.cls;

/** Starts the existing device authorization on first use. Only Google can issue the grant. */
final class YouTubeLink {
    private final Handler main = new Handler(Looper.getMainLooper());
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private final SharedPreferences settings;
    private Future<?> pending;
    private int generation;
    private Object owner, store;
    private String scope = "";
    private JSONObject state = new JSONObject();

    YouTubeLink(Context app) {
        settings = app.getSharedPreferences("youtube-first-connection", Context.MODE_PRIVATE);
    }

    JSONObject observe(Object sync) throws Exception {
        if (owner != sync) {
            cancel();
            owner = sync;
            store = member("access$getTokens$p");
            scope = (String)call(store, "getAccountScope");
            call(sync, "refreshIfDue", false);
            if (call(store, "getConnectionId") != null) settings.edit().putBoolean(scope, true).apply();
            else if (!settings.getBoolean(scope, false)) begin();
        }
        if (Boolean.TRUE.equals(call(sync, "getConnected")) && state.length() > 0) cancel();
        return state;
    }

    private Object member(String name) throws Exception {
        Class<?> type = cls("YouTubePreferenceSync");
        return type.getMethod(name, type).invoke(null, owner);
    }

    void dismiss() {
        settings.edit().putBoolean(scope, true).apply();
        cancel();
    }

    void begin() throws Exception {
        if (owner == null || store == null || call(store, "getConnectionId") != null) return;
        if (pending != null && !pending.isDone()) return;
        int request = ++generation;
        Object sync = owner, credentials = member("access$getCredentials$p");
        state = new JSONObject().put("phase", "loading");
        pending = worker.submit(() -> authorize(request, sync, credentials));
    }

    private void authorize(int request, Object sync, Object credentials) {
        try {
            Class<?> apiType = cls("YouTubeAccountApi");
            Object api = apiType.getConstructor(cls("YouTubeCredentials"), cls("YouTubeTransport"),
                Class.forName("kotlin.jvm.functions.Function0"), int.class,
                Class.forName("kotlin.jvm.internal.DefaultConstructorMarker"))
                .newInstance(credentials, null, null, 6, null);
            Object challenge = call(api, "begin");
            String url = (String)call(challenge, "getVerificationUrl");
            JSONObject prompt = new JSONObject().put("phase", "waiting")
                .put("url", url).put("code", call(challenge, "getUserCode"));
            publish(request, prompt);
            long expires = (Long)call(challenge, "getExpiresAtMs");
            long interval = Math.max(1000L, (Long)call(challenge, "getIntervalMs"));
            while (System.currentTimeMillis() < expires) {
                Thread.sleep(interval);
                if (System.currentTimeMillis() >= expires) break;
                Object result;
                try {
                    result = call(api, "poll", challenge);
                } catch (RuntimeException failure) {
                    if (!networkFailure(failure)) throw failure;
                    interval = Math.min(interval * 2, 60000L);
                    continue;
                }
                if (cls("YouTubePoll$Authorized").isInstance(result)) {
                    Object grant = call(result, "getTokens");
                    main.post(() -> accept(request, sync, grant));
                    return;
                }
                if (Boolean.TRUE.equals(call(result, "getSlowDown"))) interval += 5000L;
            }
            message(request, "코드가 만료되었습니다.");
        } catch (InterruptedException ended) {
            Thread.currentThread().interrupt();
        } catch (Exception failure) {
            String reason = "Google에 연결하지 못했습니다. 인터넷 연결을 확인해 주세요.";
            for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
                if (cls("YouTubeAccountError").isInstance(cause)) {
                    reason = cause.getMessage();
                    break;
                }
            }
            message(request, reason);
        }
    }

    private static boolean networkFailure(Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause())
            if (cause instanceof IOException) return true;
        return false;
    }

    private void accept(int request, Object sync, Object grant) {
        if (request != generation || sync != owner) return;
        try {
            if (Boolean.TRUE.equals(member("access$current")) && call(store, "getConnectionId") == null) {
                call(store, "save", grant);
                settings.edit().putBoolean(scope, true).apply();
            }
            call(sync, "refreshIfDue", true);
            cancel();
        } catch (Exception error) {
            message(request, "계정 연결을 저장하지 못했습니다. 다시 시도해 주세요.");
        }
    }

    private void publish(int request, JSONObject update) {
        main.post(() -> { if (generation == request) state = update; });
    }

    private void message(int request, String text) {
        JSONObject result = new JSONObject();
        try { result.put("phase", "error").put("message", text); }
        catch (org.json.JSONException impossible) { throw new IllegalStateException(impossible); }
        publish(request, result);
    }

    private void cancel() {
        generation++;
        if (pending != null) pending.cancel(true);
        pending = null;
        state = new JSONObject();
    }

    void close() {
        cancel();
        worker.shutdownNow();
    }
}
