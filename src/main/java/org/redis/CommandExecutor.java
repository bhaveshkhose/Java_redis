package org.redis;

public class CommandExecutor {

    private final RedisStore redisStore ;

    public CommandExecutor(RedisStore redisStore){
        this.redisStore = redisStore;
    }

    public String execute(String command){
        String[] parts = command.trim().split("\\s+");

        if (parts.length == 0 || parts[0].isEmpty()) {
            return "ERR empty command";
        }

        String operation = parts[0].toLowerCase();

        switch (operation) {

            case "set":
                if (parts.length != 3) {
                    return "ERR wrong number of arguments";
                }

                String key = parts[1];
                String value = parts[2];

                redisStore.set(key, value);

                redisStore.printdata();

                return "OK";

            case "get":
                if (parts.length != 2) {
                    return "ERR wrong number of arguments";
                }

                return redisStore.get(parts[1]);

            case "del":
                if (parts.length != 2) {
                    return "ERR wrong number of arguments";
                }

                redisStore.delete(parts[1]);

                return "OK";

            case "exists":
                if(parts.length != 2){
                    return "Err wrong number of arguments";
                }

                redisStore.exists(parts[1]);

                return "OK";


            default:
                return "ERR unknown command";
        }
    }

}
