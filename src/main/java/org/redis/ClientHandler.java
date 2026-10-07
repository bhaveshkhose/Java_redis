package org.redis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final CommandExecutor commandExecutor;
    private final RespParser respParser;

    public ClientHandler(Socket socket, CommandExecutor commandExecutor) {
        this.socket = socket;
        this.commandExecutor = commandExecutor;
        this.respParser = new RespParser();
    }

    @Override
    public void run() {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            OutputStream out = socket.getOutputStream()
        ) {
            List<String> tokens;
            while ((tokens = respParser.parseCommand(reader)) != null) {
                if (tokens.isEmpty()) {
                    continue;
                }

                System.out.println("Command Received from " + socket.getInetAddress() + ": " + tokens);
                String response = commandExecutor.handleTokens(tokens);

                out.write(response.getBytes(StandardCharsets.UTF_8));
                out.flush();
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
