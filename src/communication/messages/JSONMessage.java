package communication.messages;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class JSONMessage {
    private int type;

    public int getType() {
        return type;
    }

    public JSONMessage(int type){
        this.type=type;
    }

//    public static void sendWifiMessageJSONToServer(WifiMessage wifimessage){
//        String message=JSONParser.toJSON(wifimessage);
//    }


    public static String fromMessageToJSON(JSONMessage wifimessage){
        return JSONParser.toJSON(wifimessage);
    }

    public static JSONMessage fromJSONtoMessage(String json, Type type){
        Gson gson = new Gson();
        JSONMessage msg = gson.fromJson(json, type);
        return msg;
    }

}
