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

    public final long MIN_DELAY = 1000;
    public final long MAX_DELAY = 5000;

    public Connection(Socket socket, ConnectionEventListener eventListener) throws IOException {
        this.socket = socket;
        this.eventListener = eventListener;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));


        mainThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    eventListener.onConnect(Connection.this);

                    List<String> dataContent = Server.getInstance().getDataContent();
                    while (!mainThread.isInterrupted()) {
                        Random rand = new Random();
                        long delay = (long)(rand.nextDouble() * (MAX_DELAY - MIN_DELAY)) + MIN_DELAY;

                        //Message from data file
                        Random generator = new Random();
                        int randomIndex = generator.nextInt(dataContent.size());
                        String msg = dataContent.get(randomIndex);

                        sendMessage(msg);
                        Thread.sleep(delay);
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
