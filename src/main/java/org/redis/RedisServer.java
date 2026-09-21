package org.redis;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RedisServer {

    static int port = 6379;

    public static void main(String[] args) {

        RedisStore redisStore = new RedisStore();
        CommandExecutor commandExecutor = new CommandExecutor(redisStore);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Java Redis server running on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(socket, commandExecutor);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
