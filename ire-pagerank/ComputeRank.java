import com.sun.java_cup.internal.runtime.virtual_parse_stack;

import javax.management.remote.rmi._RMIConnection_Stub;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by neel on 23/3/14.
 */
public class ComputeRank {
    String PATH;
    public ComputeRank(String arg) {
        PATH=arg;
    }

    public void compute() {
        Map<Integer,Float> pageRank=new HashMap<Integer,Float>();
        Map<Integer,Integer> outLink=new HashMap<Integer,Integer>();

        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader(PATH+"/inLink"));
            BufferedReader bufferedReader1=new BufferedReader(new FileReader(PATH+"/outLinkCount"));
            /*String line=bufferedReader.readLine();
            System.out.println(line);
            while ((line=bufferedReader.readLine())!=null){
                int id=Integer.parseInt(line.substring(0, line.indexOf(':')));
                pageRank.put(id,(float)1);
            }*/
            String line;
            while ((line=bufferedReader1.readLine())!=null){
                int id=Integer.parseInt(line.substring(0,line.indexOf(':')));
                pageRank.put(id,(float)1);
                int count=Integer.parseInt(line.substring(line.indexOf(':')+1));
                outLink.put(id,count);
            }
            int N=outLink.size();
            bufferedReader.close();
            bufferedReader1.close();
            int i=0;
            while (true){
                int flag=0;
                BufferedReader bufferedReader2=new BufferedReader(new FileReader(PATH+"/inLink"));
                line=bufferedReader2.readLine();

                while ((line=bufferedReader2.readLine())!=null){
                    String[] linkId=line.substring(line.indexOf(':')+1).split(",");
                    int k=linkId.length;
                    float val=0;
                    for(int j=0;j<k;j++){
                        int id=Integer.parseInt(linkId[j]);
                        val=val+(float)(pageRank.get(id)/outLink.get(id));
                    }

                    val= (float) (val*0.85+(float)(0.15/N));
                    int pageId=Integer.parseInt(line.substring(0, line.indexOf(':')));
                    if(pageRank.get(pageId)!=val){
                        flag=1;
                        pageRank.put(pageId,val);
                    }
                }
                //System.out.println(i);
                i++;
                if (flag==0)
                    break;
            }
            //System.out.println(pageRank);

            BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(PATH+"/pageRank"));
            //DataOutputStream dataOutputStream=new DataOutputStream(new FileOutputStream(PATH+"/pageRank"));

            Iterator it=pageRank.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry p=(Map.Entry)it.next();
                bufferedWriter.write(p.getKey()+":"+p.getValue()+"\n");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
