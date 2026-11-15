package io.memorydrive.pleos;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.SharedPreferences;
import java.util.List;
import static io.memorydrive.pleos.AlbumPlayer.call;
import static io.memorydrive.pleos.AlbumPlayer.cls;

/** Read the same account-scoped album and trip records used by the recommendation engine. */
final class MusicShelf {
    static SharedPreferences albumSettings(Context context,Object session){
        String namespace=session==null?"local":String.valueOf(call(session,"getNamespace"));
        return context.getSharedPreferences("listening-history-"+namespace,Context.MODE_PRIVATE);
    }
    static JSONArray tripEvents(Object journal) throws Exception {
        JSONArray rows=new JSONArray();
        for(Object trip:(List<?>)call(journal,"getTrips")){
            for(Object listen:(List<?>)call(trip,"getListens")){
                rows.put(new JSONObject().put("kind","drive").put("title",call(listen,"getTitle"))
                    .put("provider",call(listen,"getProvider")).put("at",call(listen,"getAtMs"))
                    .put("until",call(listen,"getObservedUntilMs")).put("trip",call(trip,"getId")));
            }
        }
        return rows;
    }
    static JSONArray albumEvents(Context context,Object session) throws Exception {
        Object history=cls("ListeningHistoryStore").getConstructor(Context.class,cls("PlatformSession")).newInstance(context,session);
        JSONArray rows=new JSONArray();
        for(Object event:(List<?>)call(history,"recent",300)){
            if(!"vehicle_player".equals(String.valueOf(call(event,"getSource"))))continue;
            rows.put(new JSONObject().put("kind","album").put("title",call(event,"getTitle"))
                .put("artist",call(event,"getChannel")).put("at",call(event,"getAtMs")).put("video",call(event,"getId")));
        }
        return rows;
    }
}
