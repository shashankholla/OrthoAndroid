package com.sharcodes.ortho.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class JSONHelper {

    public static JSONObject mapToJSON(Map<String, Object> map) throws JSONException {
        JSONObject obj = new JSONObject();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                Map<String, Object> subMap = (Map<String, Object>) value;
                obj.put(key, mapToJSON(subMap));
            } else if (value instanceof List) {
                obj.put(key, listToJSONArray((List) value));
            } else {
                obj.put(key, value);
            }
        }
        return obj;
    }

    public static JSONArray listToJSONArray(List<Object> list) throws JSONException {
        JSONArray arr = new JSONArray();
        for (Object obj : list) {
            if (obj instanceof Map) {
                arr.put(mapToJSON((Map) obj));
            } else if (obj instanceof List) {
                arr.put(listToJSONArray((List) obj));
            } else {
                arr.put(obj);
            }
        }
        return arr;
    }

}
