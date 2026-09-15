package org.redis;

import java.io.*;
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
            System.out.println("Type commands:");

            String command;

            while (true) {

                System.out.print("> ");

                command = console.readLine();

                if (command == null ||
                        command.equalsIgnoreCase("exit")) {
                    break;
                }

                writer.println(command);

                String response = reader.readLine();

                System.out.println(response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
