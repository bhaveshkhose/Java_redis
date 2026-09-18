package org.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class RedisStore {

    private final static Map<String , Object> data = new ConcurrentHashMap<>();

    public Object set(String key , String value){
        return data.put(key , value);
    }

    public Object get(String key){
        return data.get(key);
    }

    public boolean exists(String key){
        return data.containsKey(key);
    }

    public boolean delete(String key){
        return data.remove(key) != null ;
    }


    public void printdata(){
        System.out.println(data);
    }

}
