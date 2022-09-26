package xyz.mb;

import xyz.mb.data.Storage;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class Server implements ConnectionEventListener {

    public static final int PORT = 8801;

    Logger log = Logger.getLogger("com.elkey.friendi.logger");
    private  final List<Connection> connections = new ArrayList<>();

    private static Server instance;
    private Storage storage;

    private List<String> dataContent;

    public static void main(String[] args) {
        new Server();
    }

    private Server() {
        instance = this;
        storage = new Storage(getClass().getClassLoader().getResourceAsStream("messages.txt"));

        dataContent = storage.getContent();

        log.info("Server running!");

        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try {
                    new Connection(serverSocket.accept(), this);
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }

        } catch (IOException e){
            throw  new RuntimeException(e);
        }
    }

    @Override
    public void onConnect(Connection connection) {
        connections.add(connection);
        log.info("Client connected: " + connection.toString());
    }

    @Override
    public void onDisconnect(Connection connection) {
        connections.remove(connection);
        log.info("Client disconnected: " + connection.toString());
    }

    @Override
    public void onException(Connection tcpConnection, Exception e) {
        log.info("Connection exception: " + e);
    }

    public Storage getStorage() {
        return this.storage;
    }

    public List<String> getDataContent() {
        return dataContent;
    }

    public static Server getInstance() {
        return instance;
    }
}
