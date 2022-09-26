package xyz.mb;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Connection {

    private final Socket socket;
    private final Thread mainThread;
    private final ConnectionEventListener eventListener;
    private final BufferedReader input;
    private final BufferedWriter output;


    public Connection(Socket socket, ConnectionEventListener eventListener) throws IOException {
        this.socket = socket;
        this.eventListener = eventListener;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        //List<String> dataContent = Server.getInstance().getStorage().getContent();

        mainThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    eventListener.onConnect(Connection.this);
                    sendMessage("HELLO!");
                    /*repeatRandom(new Timer(), 1000, 5000, 40, new Runnable() {
                        @Override
                        public void run() {
                            sendMessage("AAAAA");
                        }
                    });*/
                    List<String> dataContent = Server.getInstance().getDataContent();
                    while (!mainThread.isInterrupted()) {
                        //String msg = input.readLine();
                        Random rand = new Random();
                        long delay = (long)(rand.nextDouble() * (5000 - 1000)) + 1000;
                        /*try {
                            Thread.sleep(3*1000);
                            sendMessage("LLLL");
                        } catch (InterruptedException e) {
                            eventListener.onDisconnect(Connection.this);
                            throw new RuntimeException(e);
                        }*/

                        //Message from data file

                        Random generator = new Random();
                        int randomIndex = generator.nextInt(dataContent.size());
                        String msg = dataContent.get(randomIndex);

                        sendMessage(msg);
                        Thread.sleep(3*1000);

                        //eventListener.onReceiveString(Connection.this, msg);

                    }
                }catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    eventListener.onDisconnect(Connection.this);
                }
            }
        });
        mainThread.start();
    }

    public synchronized void sendMessage(String msg) {
        try {
            output.write(msg + "\r\n");
            output.flush();
        } catch (IOException e) {
            eventListener.onException(this, e);
            disconnect();
        }
    }

    public synchronized void disconnect(){
        mainThread.interrupt();
        try {
            eventListener.onDisconnect(Connection.this);
            socket.close();
        } catch (IOException e) {
            eventListener.onException(this, e);
        }
    }


    @Override
    public String toString() {
        return "connection: " + socket.getInetAddress() + ": " + socket.getPort();
    }


    private static void repeatRandom(Timer timer, long min, long max, int count, Runnable r) {
        Random rand = new Random();

        if(count < 1) {
            timer.cancel();
            return;
        }
        long delay = (long)(rand.nextDouble() * (max - min)) + min;
        timer.schedule(new TimerTask() {
            public void run() {
                r.run();
                repeatRandom(timer, min, max, count - 1, r);
            }
        }, delay);
    }



}
