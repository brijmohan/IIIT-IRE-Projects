import java.io.*;

/**
 * Created by neel on 20/3/14.
 */
public class SecondaryIndex {

    String pathToFile,fileName,dPath;
    public SecondaryIndex(String path, String titleToDocId) {
        pathToFile=path+"/"+titleToDocId;
        fileName=titleToDocId;
        dPath=path;
    }


    public void createIndex() {
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader(pathToFile));
            BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(dPath+"/sec_"+fileName));
            String line;
            int i=0,offset=0;
            while ((line=bufferedReader.readLine())!=null){
                if(i%50==0){
                    bufferedWriter.write(line.substring(0,line.indexOf(';'))+";"+offset+"\n");
                }
                i++;
                offset=offset+line.getBytes().length+1;
            }
            bufferedWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
