package arduino.messages;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class JSONMessage {
    private String type;

    public String getType() {
        return type;
    }

    public JSONMessage(String type){
        this.type=type;
    }

//    public static void sendWifiMessageJSONToServer(WifiMessage wifimessage){
//        String message=JSONParser.toJSON(wifimessage);
//    }


    public static String fromWifMessageToJSON(JSONMessage wifimessage){
        return JSONParser.toJSON(wifimessage);
    }

    public static JSONMessage fromJSONtoWifiMessage(String json, Type type){
        Gson gson = new Gson();
        JSONMessage msg = gson.fromJson(json, type);
        return msg;
    }

}
