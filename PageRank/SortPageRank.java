import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by neel on 7/4/14.
 */
public class SortPageRank {
    public static void main(String[] args) {
        try {
            Map<Integer,Float> pageRank=new HashMap<Integer,Float>();
            BufferedReader brPageRank=new BufferedReader(new FileReader(args[1]));
            String line;
            while ((line=brPageRank.readLine())!=null){
                int id=Integer.parseInt(line.substring(0,line.indexOf(':')));
                float val=Float.parseFloat(line.substring(line.indexOf(':')+1));
                pageRank.put(id,val);
            }

            BufferedReader bufferedReader=new BufferedReader(new FileReader(args[0]));
            BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(args[0]+"op"));
            while ((line=bufferedReader.readLine())!=null){
                String[] arr=line.split(";");
                bufferedWriter.write(arr[0].substring(0,arr[0].lastIndexOf('/')));
                int docId=Integer.parseInt(arr[0].substring(arr[0].lastIndexOf('/')+1, arr[0].indexOf('-')));
                bufferedWriter.write("/"+docId+"-");
                String temp=arr[0].substring(arr[0].indexOf('-')+1);
                if(temp.contains("b"))
                    bufferedWriter.write("b");
                if(temp.contains("t"))
                    bufferedWriter.write("t");
                if(temp.contains("i"))
                    bufferedWriter.write("i");
                if(temp.contains("e"))
                    bufferedWriter.write("e");
                if(temp.contains("c"))
                    bufferedWriter.write("c");
                bufferedWriter.write(":"+pageRank.get(docId)+";");
                int len=arr.length;
                if(len>=1000)
                    len=1000;
                for(int i=1;i<len;i++){
                    docId=Integer.parseInt(arr[i].substring(0,arr[i].indexOf('-')));
                    bufferedWriter.write(docId+"-");
                    temp=arr[i].substring(arr[i].indexOf('-')+1);
                    if(temp.contains("b"))
                        bufferedWriter.write("b");
                    if(temp.contains("t"))
                        bufferedWriter.write("t");
                    if(temp.contains("i"))
                        bufferedWriter.write("i");
                    if(temp.contains("e"))
                        bufferedWriter.write("e");
                    if(temp.contains("c"))
                        bufferedWriter.write("c");
                    bufferedWriter.write(":"+pageRank.get(docId)+";");
                }
                bufferedWriter.write("\n");
            }
            bufferedWriter.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
