package org.redis;

public class RespParser {

    public String parseSimpleString(String input){

        if(!input.startsWith("+")) {
            return "Invalid RESP";
        }
        return input.substring(1, input.length() - 2);
    }
}
