package org.redis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class RedisServer {

    static int port = 6379 ;


    public static  void main(String[] args){

        try(ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("java Redis server on port "+port);

            while(true){
                Socket socket = serverSocket.accept();
                System.out.println("Client connected "+socket.getInetAddress());

                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()
                        )
                );


                String command ;

                while((command = bufferedReader.readLine()) != null){
                    System.out.println("Command Received "+command);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


