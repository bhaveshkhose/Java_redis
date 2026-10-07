package org.redis;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * REdis Serialization Protocol (RESP) Parser and Encoder.
 * Supports RESP Arrays (*), Bulk Strings ($), Simple Strings (+), Integers (:), and Errors (-),
 * as well as legacy Inline commands (space-delimited text).
 */
public class RespParser {

    /**
     * Parses the next command from a BufferedReader.
     * Supports both RESP Array commands (*3\r\n$3\r\nSET...) and Inline text commands (SET k v).
     *
     * @param reader the BufferedReader connection input stream
     * @return List of command tokens, or null if end of stream reached
     * @throws IOException if a network or stream read error occurs
     */
    public List<String> parseCommand(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null; // Client disconnected / EOF
        }

        line = line.trim();
        if (line.isEmpty()) {
            return new ArrayList<>();
        }

        char firstChar = line.charAt(0);

        if (firstChar == '*') {
            // RESP Array command
            return parseArray(line, reader);
        } else if (firstChar == '$') {
            // Single Bulk String
            String val = parseBulkStringContent(line, reader);
            List<String> tokens = new ArrayList<>();
            if (val != null) {
                tokens.add(val);
            }
            return tokens;
        } else {
            // Legacy / Inline Command (e.g. "SET key value")
            return parseInlineCommand(line);
        }
    }

    private List<String> parseArray(String headerLine, BufferedReader reader) throws IOException {
        int count;
        try {
            count = Integer.parseInt(headerLine.substring(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid RESP array length: " + headerLine);
        }

        if (count <= 0) {
            return new ArrayList<>();
        }

        List<String> tokens = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String elemLine = reader.readLine();
            if (elemLine == null) {
                break;
            }
            elemLine = elemLine.trim();
            if (elemLine.startsWith("$")) {
                String val = parseBulkStringContent(elemLine, reader);
                if (val != null) {
                    tokens.add(val);
                }
            } else if (elemLine.startsWith("+") || elemLine.startsWith(":") || elemLine.startsWith("-")) {
                tokens.add(elemLine.substring(1));
            } else {
                tokens.add(elemLine);
            }
        }
        return tokens;
    }

    private String parseBulkStringContent(String headerLine, BufferedReader reader) throws IOException {
        int length;
        try {
            length = Integer.parseInt(headerLine.substring(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid RESP bulk string length: " + headerLine);
        }

        if (length == -1) {
            return null; // Null bulk string
        }

        String data = reader.readLine();
        return data;
    }

    private List<String> parseInlineCommand(String line) {
        // Splits plain text by whitespace, preserving quoted strings if needed
        String[] parts = line.split("\\s+");
        return new ArrayList<>(Arrays.asList(parts));
    }

    // --- RESP Response Encoders ---

    public static String toSimpleString(String message) {
        return "+" + message + "\r\n";
    }

    public static String toError(String message) {
        return "-ERR " + message + "\r\n";
    }

    public static String toInteger(long value) {
        return ":" + value + "\r\n";
    }

    public static String toBulkString(String value) {
        if (value == null) {
            return "$-1\r\n";
        }
        return "$" + value.getBytes().length + "\r\n" + value + "\r\n";
    }

    public static String toNullBulkString() {
        return "$-1\r\n";
    }

    public static String toArray(List<String> elements) {
        if (elements == null) {
            return "*-1\r\n";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("*").append(elements.size()).append("\r\n");
        for (String elem : elements) {
            sb.append(toBulkString(elem));
        }
        return sb.toString();
    }
}
