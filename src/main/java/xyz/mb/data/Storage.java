package xyz.mb.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Storage {

    private InputStream resourceStream;
    private List<String> content = new ArrayList<>();

    public Storage(InputStream resourceStream){
        this.resourceStream = resourceStream;
        content = makeContent();
    }

    private List<String> makeContent() {
        List<String> lines = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("messages.txt")));
        String line = null;
        try {
            while ( (line = br.readLine()) != null ) {
                content.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }


    public List<String>getContent(){
        return content;
    }
}
