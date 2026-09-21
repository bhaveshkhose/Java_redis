package org.redis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final CommandExecutor commandExecutor;

    public ClientHandler(Socket socket, CommandExecutor commandExecutor) {
        this.socket = socket;
        this.commandExecutor = commandExecutor;
    }

    @Override
    public void run() {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String command;
            while ((command = reader.readLine()) != null) {
                System.out.println("Command Received from " + socket.getInetAddress() + ": " + command);
                String response = commandExecutor.handle(command);
                writer.print(response);
                writer.flush();
            }
        } catch (IOException e) {
            System.out.println("Client disconnected (" + socket.getInetAddress() + "): " + e.getMessage());
        } finally {
            try {
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
