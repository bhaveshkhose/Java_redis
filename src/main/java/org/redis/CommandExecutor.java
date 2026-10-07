package org.redis;

import java.util.Arrays;
import java.util.List;

public class CommandExecutor {

    private final RedisStore store;

    public CommandExecutor(RedisStore store) {
        this.store = store;
    }

    /**
     * Executes a raw command line string (backwards compatibility).
     */
    public String handle(String command) {
        if (command == null || command.trim().isEmpty()) {
            return RespParser.toError("empty command");
        }
        String[] parts = command.trim().split("\\s+");
        return handleTokens(Arrays.asList(parts));
    }

    /**
     * Executes a tokenized command list parsed by RespParser.
     */
    public String handleTokens(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return RespParser.toError("empty command");
        }

        String operation = tokens.get(0).toUpperCase();

        switch (operation) {
            case "PING":
                return handlePing(tokens);

            case "ECHO":
                return handleEcho(tokens);

            case "SET":
                return handleSet(tokens);

            case "GET":
                return handleGet(tokens);

            case "EXISTS":
                return handleExists(tokens);

            case "DEL":
                return handleDelete(tokens);

            default:
                return RespParser.toError("unknown command '" + tokens.get(0) + "'");
        }
    }

    private String handlePing(List<String> tokens) {
        if (tokens.size() == 1) {
            return RespParser.toSimpleString("PONG");
        } else if (tokens.size() == 2) {
            return RespParser.toBulkString(tokens.get(1));
        } else {
            return RespParser.toError("wrong number of arguments for 'ping' command");
        }
    }

    private String handleEcho(List<String> tokens) {
        if (tokens.size() != 2) {
            return RespParser.toError("wrong number of arguments for 'echo' command");
        }
        return RespParser.toBulkString(tokens.get(1));
    }

    private String handleSet(List<String> tokens) {
        if (tokens.size() != 3) {
            return RespParser.toError("wrong number of arguments for 'set' command");
        }

        store.set(tokens.get(1), tokens.get(2));
        return RespParser.toSimpleString("OK");
    }

    private String handleGet(List<String> tokens) {
        if (tokens.size() != 2) {
            return RespParser.toError("wrong number of arguments for 'get' command");
        }

        Object value = store.get(tokens.get(1));
        if (value == null) {
            return RespParser.toNullBulkString();
        }

        return RespParser.toBulkString(value.toString());
    }

    private String handleExists(List<String> tokens) {
        if (tokens.size() != 2) {
            return RespParser.toError("wrong number of arguments for 'exists' command");
        }

        return store.exists(tokens.get(1)) ? RespParser.toInteger(1) : RespParser.toInteger(0);
    }

    private String handleDelete(List<String> tokens) {
        if (tokens.size() != 2) {
            return RespParser.toError("wrong number of arguments for 'del' command");
        }

        boolean exists = store.exists(tokens.get(1));
        store.delete(tokens.get(1));

        return exists ? RespParser.toInteger(1) : RespParser.toInteger(0);
    }
}