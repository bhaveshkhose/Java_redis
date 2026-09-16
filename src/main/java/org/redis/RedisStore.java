package org.redis;

import java.util.HashMap;
import java.util.Map;

public class RedisStore {

    private final Map<String , String> data = new HashMap<>();


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
}
