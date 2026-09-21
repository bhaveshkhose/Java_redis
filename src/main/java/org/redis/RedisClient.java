package org.redis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RedisClient {

    public static void main(String[] args) {

        try (
                Socket socket = new Socket("localhost", 6379);

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        socket.getInputStream()
                                )
                        );

                PrintWriter writer =
                        new PrintWriter(
                                socket.getOutputStream(),
                                true
                        );

                BufferedReader console =
                        new BufferedReader(
                                new InputStreamReader(System.in)
                        )
        ) {

            System.out.println("Connected to Redis server.");
            System.out.println("Type commands (or 'exit' to quit):");

            while (true) {

                System.out.print("> ");

                String command = console.readLine();

                if (command == null || command.equalsIgnoreCase("exit")) {
                    break;
                }

                if (command.trim().isEmpty()) {
                    continue;
                }

                writer.println(command);

                String response = reader.readLine();

                if (response == null) {
                    System.out.println("Server closed connection.");
                    break;
                }

                System.out.println(response);

                // Handle multi-line RESP Bulk String responses (e.g. "$5\r\nhello")
                if (response.startsWith("$") && !response.equals("$-1")) {
                    String valueLine = reader.readLine();
                    if (valueLine != null) {
                        System.out.println(valueLine);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}
