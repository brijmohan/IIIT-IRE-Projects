import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by neel on 18/3/14.
 */
public class TitleIndex {
    public static void main(String[] args) {
        SAXParserFactory saxParserFactory=SAXParserFactory.newInstance();
        Map<String,Integer> titleToId=new HashMap<String,Integer>();
        //try {
            /*SAXParser saxParser=saxParserFactory.newSAXParser();

            //create title-docid and docid-title mapping
            SAXHandler saxHandler=new SAXHandler(1,args[1],titleToId);
            saxParser.parse(args[0],saxHandler);

            //sort title-docid mapping
            ExtMergeSort extMergeSort=new ExtMergeSort(args[1],"/titleToDocId");
            extMergeSort.sort();

            //create secondary index on title-docid
            SecondaryIndex secondaryIndex=new SecondaryIndex(args[1],"titleToDocId");
            secondaryIndex.createIndex();

            //create secondary index on docid-title
            SecondaryIndex secondaryIndex1=new SecondaryIndex(args[1],"docIdToTitle");
            secondaryIndex1.createIndex();

            //LoadInMemory loadInMemory=new LoadInMemory(args[1]);
            //loadInMemory.load(titleToId);
            //extract links from document and map the docids with their inlinks
            SAXHandler saxHandler1=new SAXHandler(2,args[1],titleToId);
            saxParser.parse(args[0],saxHandler1);

            ComputeRank computeRank=new ComputeRank(args[1]);
            computeRank.compute();
            */

            //TreeSort treeSort=new TreeSort();
            //treeSort.sort(args[1],"/pageRank80");
        SecondaryIndex secondaryIndex=new SecondaryIndex(args[1],"page_rank_indexop");
        secondaryIndex.createIndex();
        /*} catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }*/
    }
}
