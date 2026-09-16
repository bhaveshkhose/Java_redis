package org.redis;

import java.util.HashMap;
import java.util.Map;

public class RedisStore {

    private final static Map<String , String> data = new HashMap<>();

    public String set(String key , String value){
        return data.put(key , value);
    }

    public String get(String key){
        return data.get(key);
    }

    public boolean exists(String key){
        return data.containsKey(key);
    }

    public boolean delete(String key){
        return data.remove(key) != null ;
    }


    public static void main(String[] b ){
        System.out.println(data.get("name"));
    }
}
