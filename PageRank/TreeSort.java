import java.io.*;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by neel on 1/4/14.
 */
public class TreeSort {
    public void sort(String path,String fileName){
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader(path+fileName));
            String line;
            Map<Integer,Float> treeMap=new TreeMap<Integer,Float>();
            while ((line=bufferedReader.readLine())!=null){
                int id=Integer.parseInt(line.substring(0,line.indexOf(':')));
                float value=Float.parseFloat(line.substring(line.indexOf(':')+1));
                treeMap.put(id,value);
            }
            BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(path+"/pageRank"));
            for(Map.Entry<Integer,Float> entry:treeMap.entrySet()){
                bufferedWriter.write(entry.getKey()+":"+entry.getValue()+"\n");
            }
            bufferedWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
