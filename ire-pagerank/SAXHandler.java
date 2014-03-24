import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by neel on 18/3/14.
 */
public class SAXHandler extends DefaultHandler {

    String PATHTOINDEX,title="";
    int task=0,docId=0;
    boolean sRevision=false,sContri=false,sText=false,sTitle=false,sId=false;
    StringBuilder stringBuilder=new StringBuilder();
    StringBuilder stringBuilderDocId=new StringBuilder();
    File file1,file2;
    FileWriter fileWriter1,fileWriter2;
    BufferedWriter bufferedWriter1,bufferedWriter2;
    Map<Integer,List<Integer>> inLinks=new TreeMap<Integer,List<Integer>>();
    int k=0;
    Map<String,Integer> titleToId;
    BufferedWriter countWriter;

    public SAXHandler(int type, String args){
        PATHTOINDEX=args;
        task=type;
    }

    public SAXHandler(int i, String arg, Map<String, Integer> titleToId) {
        PATHTOINDEX=arg;
        task=i;
        this.titleToId=titleToId;
    }


    public void startElement(String uri, String localname,String qName, Attributes attr) throws SAXException {
        if(qName.equalsIgnoreCase("title")){
            sTitle=true;
            title="";
            stringBuilder.delete(0,stringBuilder.length());
        }else if(qName.equalsIgnoreCase("text")){
            sText=true;
            stringBuilder.delete(0,stringBuilder.length());
        }else if(qName.equalsIgnoreCase("id")){
            sId=true;
            stringBuilderDocId.delete(0,stringBuilderDocId.length());
        }else if(qName.equalsIgnoreCase("revision")){
            sRevision=true;
        }else if(qName.equalsIgnoreCase("contributor")){
            sContri=true;
        }
    }

    public void endElement(String uri, String localName,String qName) throws SAXException {
        if(qName.equalsIgnoreCase("title")){
            sTitle=false;
            title=stringBuilder.toString();
        }else if(qName.equalsIgnoreCase("text")){
            sText=false;
            if(task==2){
                Links links=new Links(PATHTOINDEX);
                links.extract(stringBuilder,docId,inLinks,titleToId,countWriter);
                if(inLinks.size()>35000){
                    try {
                        BufferedWriter mapWriter=new BufferedWriter(new FileWriter(PATHTOINDEX+"/inLink"+k));
                        for(Map.Entry<Integer,List<Integer>> entry: inLinks.entrySet()){
                            String list=entry.getValue().toString().replace("[","").replace("]","").replaceAll(" ","");
                            mapWriter.write(entry.getKey()+":"+list+"\n");
                        }
                        mapWriter.close();
                        k++;
                        inLinks.clear();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else if(qName.equalsIgnoreCase("id")){
            sId=false;
            if(!sRevision && !sContri){
                try {
                    docId=Integer.parseInt(stringBuilderDocId.toString());
                    if(task==1){
                        bufferedWriter1.write(title+";"+docId+"\n");
                        bufferedWriter2.write(docId+";"+title+"\n");
                        //new Code
                        titleToId.put(title,docId);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }else if(qName.equalsIgnoreCase("revision")){
            sRevision=false;
        }else if(qName.equalsIgnoreCase("contributor")){
            sContri=false;
        }
    }

    public void characters(char ch[], int start, int length) throws SAXException {
        if(sTitle){
            stringBuilder.append(ch,start,length);
        }else if(sId && !sRevision && !sContri){
            stringBuilderDocId.append(ch,start,length);
        }else if(sText){
            stringBuilder.append(ch,start,length);
        }
    }

    public void startDocument(){
        if(task==1){
            file1=new File(PATHTOINDEX+"/titleToDocId");
            file2=new File(PATHTOINDEX+"/docIdToTitle");
            try {
                file1.createNewFile();
                file2.createNewFile();
                fileWriter1=new FileWriter(file1.getAbsoluteFile());
                fileWriter2=new FileWriter(file2.getAbsoluteFile());
                bufferedWriter1=new BufferedWriter(fileWriter1);
                bufferedWriter2=new BufferedWriter(fileWriter2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(task==2){
            try {
                countWriter=new BufferedWriter(new FileWriter(PATHTOINDEX+"/outLinkCount"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void endDocument(){
        if(task==1){
            try {
                bufferedWriter1.close();
                bufferedWriter2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(task==2){
            if(inLinks.size()>0){
                try {
                    BufferedWriter mapWriter=new BufferedWriter(new FileWriter(PATHTOINDEX+"/inLink"+k));
                    for(Map.Entry<Integer,List<Integer>> entry: inLinks.entrySet()){
                        String list=entry.getValue().toString().replace("[","").replace("]","").replaceAll(" ","");
                        mapWriter.write(entry.getKey()+":"+list+"\n");
                    }
                    mapWriter.close();
                    k++;
                    inLinks.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                countWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ExtMergeSort extMergeSort=new ExtMergeSort(PATHTOINDEX,"/inLink");
            extMergeSort.sort1(k);

        }
    }
}
