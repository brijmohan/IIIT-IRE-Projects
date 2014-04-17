import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * Created by neel on 23/3/14.
 */
public class LoadInMemory {
    String PATH;
    public LoadInMemory(String arg) {
        PATH=arg;
    }

    public void load(Map<String, Integer> titleToId) {
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader(PATH+"/titleToDocId"));
            String line;
            while ((line=bufferedReader.readLine())!=null){
                String title=line.substring(0,line.lastIndexOf(';'));
                Integer id=Integer.parseInt(line.substring(line.lastIndexOf(';')+1));
                //System.out.println(title+" "+id);
                titleToId.put(title,id);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
