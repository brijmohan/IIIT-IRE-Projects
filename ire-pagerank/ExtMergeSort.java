import java.io.*;
import java.util.*;

/**
 * Created by neel on 19/3/14.
 */
public class ExtMergeSort {

    private String filePath,indexPath;
    public ExtMergeSort(String s, String titleToDocId) {
        filePath=s+titleToDocId;
        indexPath=s;
    }

    private int k=0,limit=5000;
    public void sort(){
        String line;
        int i=0;
        List<String> titles=new ArrayList<String>();
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader(filePath));
            while ((line = bufferedReader.readLine())!=null){
                titles.add(line);
                if(titles.size()>limit){
                    Collections.sort(titles);
                    BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(indexPath+"/temp"+k));
                    for(String temp:titles){
                        bufferedWriter.write(temp+"\n");
                    }
                    bufferedWriter.close();
                    titles.clear();
                    k++;
                }
            }
            Collections.sort(titles);
            BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(indexPath+"/temp"+k));
            for(String temp:titles){
                bufferedWriter.write(temp+"\n");
            }
            bufferedWriter.close();
            titles.clear();
            k++;

            Comparator<String> comparator=new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            };

            BufferedReader[] bufferedReaders=new BufferedReader[k];
            PriorityQueue<String> priorityQueue=new PriorityQueue<String>(k,comparator);

            for(i=0;i<k;i++){
                bufferedReaders[i]=new BufferedReader(new FileReader(indexPath+"/temp"+i));
                priorityQueue.add(bufferedReaders[i].readLine()+"="+i);
            }
            String temp;
            int pIndex;
            BufferedWriter bufferedWriter1=new BufferedWriter(new FileWriter(filePath));
            while (!priorityQueue.isEmpty()){
                temp=priorityQueue.remove();

                pIndex=Integer.parseInt(temp.substring(temp.indexOf('=')+1));
                bufferedWriter1.write(temp.replaceAll("=.*$","")+"\n");
                if((temp=bufferedReaders[pIndex].readLine())!=null){

                    priorityQueue.add(temp+"="+pIndex);
                }
            }
            bufferedWriter1.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sort1(int l){
        int i;
        try {
            Comparator<String> comparator=new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    int i1=Integer.parseInt(o1.substring(0,o1.indexOf(':')));
                    int i2=Integer.parseInt(o2.substring(0,o2.indexOf(':')));
                    if(i1>i2)
                        return 1;
                    else if(i1<i2)
                        return -1;
                    else
                        return 0;
                    //return o1.compareTo(o2);
                }
            };

            BufferedReader[] bufferedReaders=new BufferedReader[l];
            PriorityQueue<String> priorityQueue=new PriorityQueue<String>(l,comparator);

            for(i=0;i<l;i++){
                bufferedReaders[i]=new BufferedReader(new FileReader(indexPath+"/inLink"+i));
                priorityQueue.add(bufferedReaders[i].readLine()+"="+i);

            }
            String temp,prev="";
            int pIndex;
            BufferedWriter bufferedWriter1=new BufferedWriter(new FileWriter(filePath));

            int count=0;
            while (!priorityQueue.isEmpty()){
                temp=priorityQueue.remove();
                if(prev.equals(temp.substring(0,temp.indexOf(':')))){
                    bufferedWriter1.write(","+temp.substring(temp.indexOf(':')+1,temp.indexOf('=')));
                }else{
                    bufferedWriter1.write("\n"+temp.replaceAll("=.*$",""));
                }
                count++;
                pIndex=Integer.parseInt(temp.substring(temp.indexOf('=')+1));
                prev=temp.substring(0,temp.indexOf(':'));
                if((temp=bufferedReaders[pIndex].readLine())!=null){

                    priorityQueue.add(temp+"="+pIndex);
                }
            }

            bufferedWriter1.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
