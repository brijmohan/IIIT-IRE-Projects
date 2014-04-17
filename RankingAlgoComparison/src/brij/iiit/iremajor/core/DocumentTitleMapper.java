package brij.iiit.iremajor.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

public class DocumentTitleMapper {

	private String indexOutputFolder;
	private static final String TOKEN_SEPARATOR = " ## ";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	public void addDocIdMap(long docID, String docTitle, int tokenCount) {
		String docIdMapFile = new StringBuffer(indexOutputFolder)
				.append(File.separator).append("docIdTitleMap.txt").toString();
		try {
			File secFile = new File(docIdMapFile);
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(secFile, true)));

			out.println(new StringBuffer(String.valueOf(docID))
					.append(TOKEN_SEPARATOR).append(docTitle.trim())
					.append(TOKEN_SEPARATOR).append(tokenCount).toString());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getLineWithDocID (String docId) {
		String requiredLine = null;
		String docIdMapFile = new StringBuffer(indexOutputFolder)
				.append(File.separator).append("docIdTitleMap.txt").toString();
		String secDocIdMapFile = new StringBuffer(indexOutputFolder)
				.append(File.separator).append("sDocMap.txt").toString();
		Search search = new Search();
		RandomAccessFile docMap = null;
		RandomAccessFile secDocMap = null;
		try {
			docMap = new RandomAccessFile(docIdMapFile, "rw");
			secDocMap = new RandomAccessFile(secDocIdMapFile, "rw");
			
			// Find doc id in secondary file
			long offset = search.findNearestOffset(secDocMap,
					Long.parseLong(docId));
			secDocMap.seek(offset);
			String line = secDocMap.readLine();
			if (line != null) {
				
				// check if the recieved docid matches the requested one
				String[] secDocSplit = line.split(" ");
				if (docId.equalsIgnoreCase(secDocSplit[0])) {
					// if it matches goto primary file and read the title
					docMap.seek(Long.parseLong(secDocSplit[1]));
					requiredLine = docMap.readLine();
				} else {
					secDocMap.seek(search.getPreviousLineStart(offset, secDocMap));
					docMap.seek(Long.parseLong(secDocMap.readLine().split(" ")[1]));
					boolean matchFound = false;
					int iter = 0;
					while((line = docMap.readLine()) != null && !matchFound && iter < 100) {
						if (docId.equalsIgnoreCase(line.split(TOKEN_SEPARATOR)[0])){
							requiredLine = line;
							matchFound = true;
							iter++;
						}
					}
				}
			}
			
			secDocMap.close();
			docMap.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (docMap != null) {
					docMap.close();
				}
				if (secDocMap != null) {
					secDocMap.close();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return requiredLine;
	}

	public String getDocumentTitle(String docId) {
		String title = null;
		String lineWithDocID = getLineWithDocID(docId);
		if (lineWithDocID != null) {
			title = lineWithDocID.split(TOKEN_SEPARATOR)[1];
		}
		return title;
	}

	public int getDocumentTokenCount(String docId) {
		int countnum = 0;
		String lineWithDocID = getLineWithDocID(docId);
		if (lineWithDocID != null) {
			countnum = Integer.parseInt(lineWithDocID.split(TOKEN_SEPARATOR)[2]);
		}
		return countnum;
	}

	public Map<String, Integer> initTokenCountMap() {
		Map<String, Integer> tokenCountMap = new HashMap<String, Integer>();
		String docIdMapFile = new StringBuffer(indexOutputFolder)
				.append(File.separator).append("docIdTitleMap.txt").toString();
		String docMapLine, count;
		RandomAccessFile docMap = null;
		Integer countnum = 0;
		try {
			docMap = new RandomAccessFile(docIdMapFile, "rw");
			while ((docMapLine = docMap.readLine()) != null) {
				String[] docMapValues = docMapLine.split(TOKEN_SEPARATOR);
				count = docMapValues[2];
				if (count != null) {
					countnum = Integer.parseInt(count);
				}
				tokenCountMap.put(docMapValues[0], countnum);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (docMap != null) {
					docMap.close();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return tokenCountMap;
	}

	public String getIndexOutputFolder() {
		return indexOutputFolder;
	}

	public void setIndexOutputFolder(String indexOutputFolder) {
		this.indexOutputFolder = indexOutputFolder;
	}
}
