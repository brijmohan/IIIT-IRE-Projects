package brij.iiit.iremajor.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Date;

public class DocumentMapperSecondaryIndexer {
	public static void main(String args[]) {

		String outputFolder = "/home/brij/Documents/IIIT/IRE/major/big/";
		File primaryDocMap = new File(outputFolder + "docIdTitleMap.txt");
		File secondaryDocMap = new File(outputFolder + "sDocMap.txt");
		RandomAccessFile primaryDocMapReader = null;
		PrintWriter secondaryDocMapWriter = null;
		String docMapEntry = null;
		String[] docEntrySplit = null;

		try {
			long start = (new Date()).getTime();
			primaryDocMapReader = new RandomAccessFile(primaryDocMap, "rw");
			secondaryDocMapWriter = new PrintWriter(new BufferedWriter(
					new FileWriter(secondaryDocMap, false)));
			int counter = 0;
			while ((docMapEntry = primaryDocMapReader.readLine()) != null) {
				if ((counter % 100) == 0) {
					docEntrySplit = docMapEntry.split(" ## ");
					secondaryDocMapWriter.println(new StringBuffer(
							docEntrySplit[0]).append(" ")
							.append(primaryDocMapReader.getFilePointer())
							.toString());
				}
				counter++;
			}
			System.out.println("Secondary Indexing Complete in : " + ((new Date()).getTime() - start) + " ms.");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (primaryDocMapReader != null) {
					primaryDocMapReader.close();
				}
				if (secondaryDocMapWriter != null) {
					secondaryDocMapWriter.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
