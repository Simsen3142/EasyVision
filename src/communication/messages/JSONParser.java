package communication.messages;

import com.google.gson.Gson;
import java.lang.reflect.Type;

public class JSONParser {
    public static String toJSON(Object o){
        Gson gson = new Gson();
        String json = gson.toJson(o);
        return json+"\n";
    }

    public static JSONMessage fromJSONtoObject(String json, Type type){
        Gson gson = new Gson();
        JSONMessage msg = gson.fromJson(json, type);
        return msg;
    }
}
