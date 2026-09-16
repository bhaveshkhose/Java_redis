package org.redis;

public class CommandExecutor {

    private final RedisStore redisStore ;

    public CommandExecutor(RedisStore redisStore){
        this.redisStore = redisStore;
    }

    public String execute(String command){
        String[] parts = command.trim().split("\\s+");

        if(parts[0].isEmpty()){
            return "Error Empty Command";
        }
        String operation = parts[0].toLowerCase();

        if(operation.equalsIgnoreCase("set")){
            if(parts.length != 3){
                return "Error ! wrong number of arguments";
            }

            String key = parts[1];
            String value = parts[2];

            redisStore.set(key , value);

            return "OK";
        }

        return "ERR unknown command";
    }

}
