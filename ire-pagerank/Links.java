import com.sun.org.apache.regexp.internal.recompile;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by neel on 20/3/14.
 */
public class Links {
    String path;
    public Links(String PATHTOINDEX){
        path=PATHTOINDEX;
    }

    public void extract(StringBuilder stringBuilder, int docId, Map<Integer, List<Integer>> inLinks, Map<String, Integer> titleToId, BufferedWriter countWriter) {
        int len=stringBuilder.length();
        String st=stringBuilder.toString();
        List<String> linksList=new ArrayList<String>();
        for(int i=0;i<len;i++){
            if(st.charAt(i)=='[' && i<len-1 && st.charAt(i+1)=='['){
                i=i+2;
                String link="";
                while (i<len && st.charAt(i)!=']'){
                    link=link+st.charAt(i);
                    i++;
                }
                if(link.contains("|"))
                    linksList.add(link.substring(0,link.indexOf("|")));
                else
                    linksList.add(link);
            }
        }
        try {
            countWriter.write(docId+":"+linksList.size()+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(linksList);
        int d;
        for(String temp:linksList){
            //d=findDocId(temp);
            if(titleToId.get(temp)!=null)
                d=titleToId.get(temp);
            else
                d=0;
            if(d!=0){
                if(inLinks.get(d)==null){
                    List<Integer> list=new ArrayList<Integer>();
                    list.add(docId);
                    inLinks.put(d,list);
                }else {
                    List<Integer> list=inLinks.get(d);
                    list.add(docId);
                    inLinks.put(d,list);
                }
            }
        }



    }

    private int findDocId(String temp) {
        int rValue=0;
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader(path+"/sec_titleToDocId"));
            String line,prev="";
            int offset=0,offset1=0;
            while ((line=bufferedReader.readLine())!=null){
                prev=line;
                if(line.substring(0,line.indexOf(";")).compareTo(temp)>0){
                    offset1=Integer.parseInt(line.substring(line.indexOf(";")+1));
                    break;
                }
                offset=Integer.parseInt(line.substring(line.indexOf(";")+1));
            }

            RandomAccessFile randomAccessFile=new RandomAccessFile(path+"/titleToDocId","r");
            randomAccessFile.seek(offset);
            while ((line=randomAccessFile.readLine())!=null && (offset1==0 || randomAccessFile.getFilePointer()<=offset1)){
                if(line.substring(0,line.indexOf(";")).equalsIgnoreCase(temp)){
                    rValue=Integer.parseInt(line.substring(line.indexOf(";")+1));
                }
            }
            bufferedReader.close();
            randomAccessFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rValue;
    }
}
