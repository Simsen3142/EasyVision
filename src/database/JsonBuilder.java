package database;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;

public class JsonBuilder {
	public static JsonElement toJSONElement(Object o){
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(o);
        
        return jsonElement;
    }
	
	public static String toJSON(Object o){
        Gson gson = new Gson();
        return gson.toJson(o);
    }
	

	public static String toJSON(JsonElement jelement){
        Gson gson = new Gson();
        return gson.toJson(jelement);
    }

    public static Object fromJSONtoObject(String json, Type type){
        Gson gson = new Gson();
        Object ret = gson.fromJson(json, type);
        
        return ret;
    }
}
