package brij.iiit.iremajor.core;

import java.io.File;
import java.io.RandomAccessFile;

public class PageRankDocumentMapper {

	public PageRankDocumentMapper() {
		// TODO Auto-generated constructor stub
	}

	private String indexOutputFolder;
	private static final String TOKEN_SEPARATOR = ";";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	public String getLineWithDocID(String docId) {
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
			long offset = search.findNearestOffsetPR(secDocMap,
					Long.parseLong(docId));
			secDocMap.seek(offset);
			String line = secDocMap.readLine();
			if (line != null) {

				// check if the recieved docid matches the requested one
				String[] secDocSplit = line.split(";");
				if (docId.equalsIgnoreCase(secDocSplit[0])) {
					// if it matches goto primary file and read the title
					docMap.seek(Long.parseLong(secDocSplit[1]));
					requiredLine = docMap.readLine();
				} else {
					secDocMap.seek(search.getPreviousLineStart(offset,
							secDocMap));
					docMap.seek(Long
							.parseLong(secDocMap.readLine().split(";")[1]));
					boolean matchFound = false;
					int iter = 0;
					while ((line = docMap.readLine()) != null && !matchFound && iter < 50) {
						if (docId
								.equalsIgnoreCase(line.split(TOKEN_SEPARATOR)[0])) {
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
			title = lineWithDocID.substring(lineWithDocID.indexOf(TOKEN_SEPARATOR) + 1);
		}
		return title;
	}

	public String getIndexOutputFolder() {
		return indexOutputFolder;
	}

	public void setIndexOutputFolder(String indexOutputFolder) {
		this.indexOutputFolder = indexOutputFolder;
	}

}
