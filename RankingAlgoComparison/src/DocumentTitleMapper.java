import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

public class DocumentTitleMapper {

	private String indexOutputFolder;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	public void addDocIdMap(long docID, String docTitle) {
		String docIdMapFile = new StringBuffer(indexOutputFolder).append(File.separator).append("docIdTitleMap.txt").toString();
		try {
			File secFile = new File(docIdMapFile);
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(secFile, true)));

			out.println(new StringBuffer(String.valueOf(docID)).append(" ").append(docTitle.trim()).toString());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getDocumentTitle(String docId) {
		String docIdMapFile = new StringBuffer(indexOutputFolder).append(File.separator).append("docIdTitleMap.txt").toString();
		String title = null;
		Search search = new Search();
		RandomAccessFile docMap = null;
		try {
			docMap = new RandomAccessFile(docIdMapFile, "rw");
			long offset = search.findNearestOffset(docMap, Long.parseLong(docId));
			docMap.seek(offset);
			String line = docMap.readLine();
			if (line != null) {
				title = line.substring(line.indexOf(' '), line.length());
			}
			docMap.close();
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
		return title;
	}

	public String getIndexOutputFolder() {
		return indexOutputFolder;
	}

	public void setIndexOutputFolder(String indexOutputFolder) {
		this.indexOutputFolder = indexOutputFolder;
	}
}
