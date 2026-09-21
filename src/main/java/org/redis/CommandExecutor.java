package org.redis;

public class CommandExecutor {

    private final RedisStore store;

    public CommandExecutor(RedisStore store) {
        this.store = store;
    }

    public String handle(String command) {

        if (command == null || command.trim().isEmpty()) {
            return "-ERR empty command\r\n";
        }

        String[] parts = command.trim().split("\\s+", 3);

        String operation = parts[0].toUpperCase();

        switch (operation) {

            case "SET":
                return handleSet(parts);

            case "GET":
                return handleGet(parts);

            case "EXISTS":
                return handleExists(parts);

            case "DEL":
                return handleDelete(parts);

            default:
                return "-ERR unknown command\r\n";
        }
    }

    private String handleSet(String[] parts) {

        if (parts.length != 3) {
            return "-ERR wrong number of arguments for 'set' command\r\n";
        }

        store.set(parts[1], parts[2]);

        return "+OK\r\n";
    }

    private String handleGet(String[] parts) {

        if (parts.length != 2) {
            return "-ERR wrong number of arguments for 'get' command\r\n";
        }

        Object value = store.get(parts[1]);

        if (value == null) {
            return "$-1\r\n";
        }

        String str = value.toString();

        return "$" + str.length() +
                "\r\n" +
                str +
                "\r\n";
    }

    private String handleExists(String[] parts) {

        if (parts.length != 2) {
            return "-ERR wrong number of arguments for 'exists' command\r\n";
        }

        return store.exists(parts[1]) ? ":1\r\n" : ":0\r\n";
    }

    private String handleDelete(String[] parts) {

        if (parts.length != 2) {
            return "-ERR wrong number of arguments for 'del' command\r\n";
        }

        boolean exists = store.exists(parts[1]);

        store.delete(parts[1]);

        return exists ? ":1\r\n" : ":0\r\n";
    }
}