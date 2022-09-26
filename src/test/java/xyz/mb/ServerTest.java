package xyz.mb;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.Assert.*;

public class ServerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void main() {
    }

    @Test
    public void clientConnect() throws Exception {
        //new TestClient("127.0.0.1", Server.PORT);

    }

    private class TestClient implements Closeable{

        private Socket socket;
        private BufferedReader input;
        private BufferedWriter output;


        public TestClient(String ip, int port)  {
            try {
                socket = new Socket(ip, port);
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                String msg;
                while ( (msg = input.readLine()) != null ) {
                    System.out.println(msg + "\r\n");
                }

            }catch (IOException e) {
                e.printStackTrace();
            }

        }


        @Override
        public void close() throws IOException {

        }
    }

}