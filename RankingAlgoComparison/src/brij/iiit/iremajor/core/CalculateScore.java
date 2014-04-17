package brij.iiit.iremajor.core;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class CalculateScore {

	private static long N;
	private static final int MAX_POSTING_SIZE = 1000;
	public static Map<String, Double> WEIGHTS = new HashMap<String, Double>();
	static {
		WEIGHTS.put("t", 0.99);
		WEIGHTS.put("b", 0.001);
		WEIGHTS.put("i", 0.005);
		WEIGHTS.put("c", 0.002);
		WEIGHTS.put("e", 0.002);
	}
	private static Map<String, Integer> TOKEN_COUNT_MAP = null;

	public static void main(String[] args) {

		long start = (new Date()).getTime();

		CalculateScore calculateScore = new CalculateScore();

		String outputFolder = args[0], conf = null, sortedPostings, scoredPostings;
		long counter = 0;
		DocumentTitleMapper titleMapper = new DocumentTitleMapper();
		titleMapper.setIndexOutputFolder(outputFolder);
		TOKEN_COUNT_MAP = titleMapper.initTokenCountMap();

		File sortedIndex = new File(outputFolder + "/sorted_index.txt");
		File primaryIndex = new File(outputFolder + "/index.txt");
		File secondaryIndex = new File(outputFolder + "/sindex.txt");

		RandomAccessFile confFile = null, sortedIndexReader = null, primaryIndexWriter = null;
		PrintWriter secondaryIndexWriter = null;
		try {

			// Read collection size value - N
			confFile = new RandomAccessFile(outputFolder + "/conf.txt", "r");
			conf = confFile.readLine();
			if (conf != null) {
				N = Long.parseLong(conf.split(" ")[1]);
			}

			sortedIndexReader = new RandomAccessFile(sortedIndex, "rw");
			primaryIndexWriter = new RandomAccessFile(primaryIndex, "rw");
			secondaryIndexWriter = new PrintWriter(new BufferedWriter(
					new FileWriter(secondaryIndex, false)));

			while ((sortedPostings = sortedIndexReader.readLine()) != null) {
				// If skipped 100 lines, write to secondary index
				if (counter % 100 == 0) {
					secondaryIndexWriter.println(new StringBuffer(
							sortedPostings.substring(0,
									sortedPostings.indexOf('/'))).append(" ")
							.append(primaryIndexWriter.getFilePointer()));
				}
				scoredPostings = calculateScore
						.calculatePostingScore(sortedPostings);
				if (scoredPostings != null) {
					primaryIndexWriter.writeBytes(new StringBuffer(
							scoredPostings).append("\n").toString());
					counter++;
				}
			}

			// Delete sorted index file
			// sortedIndex.delete();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (sortedIndexReader != null) {
					sortedIndexReader.close();
				}
				if (primaryIndexWriter != null) {
					primaryIndexWriter.close();
				}
				if (secondaryIndexWriter != null) {
					secondaryIndexWriter.close();
				}
				if (confFile != null) {
					confFile.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println((new Date()).getTime() - start);
	}

	private String calculatePostingScore(String sortedPostings) {
		
		int wordEndPos = sortedPostings.indexOf('/');
		String word = sortedPostings.substring(0, wordEndPos);
		if (word == null || word.equalsIgnoreCase("redirect") || getLongestRepetition(word) > 3)
			return null;
		String postingDocsStr = sortedPostings.substring(wordEndPos + 1,
				sortedPostings.length());
		double score, TF;
		String[] docDetailsArr = null, docAttrsArr = null, attr = null;
		String docId, docAttrs;
		Map<Double, List<DocumentAttributes>> newPostingMap = new TreeMap<Double, List<DocumentAttributes>>(
				Collections.reverseOrder());
		List<DocumentAttributes> docList = null;
		int i = 0, j = 0;
		int postingStrLength = postingDocsStr.length();

		int counter = 0;
		for (i = 0; i < postingDocsStr.length(); i++) {
			if (postingDocsStr.charAt(i) == ';') {
				counter++;
			}
		}
		long DF = counter + 1;

		// return if DF is too large
		if (DF > 1000000)
			return null;

		double IDF = 1 + Math.log(N / DF);

		StringBuffer newPosting = new StringBuffer(word).append("/").append(DF)
				.append("/");

		int pos = 0, end, docTokenCount;
		while ((end = postingDocsStr.indexOf(';', pos)) >= 0) {
			docDetailsArr = postingDocsStr.substring(pos, end).split("-");
			docId = docDetailsArr[0];
			docAttrs = docDetailsArr[1];
			docAttrsArr = docAttrs.split("&");
			// Calculate TF
			TF = 0.0;
			docTokenCount = TOKEN_COUNT_MAP.get(docId);
			docTokenCount = (docTokenCount == 0) ? 1 : docTokenCount;
			for (j = 0; j < docAttrsArr.length; j++) {
				attr = docAttrsArr[j].split(":");
				// TF(w) = W(w) * (F(w) / TC) - For normalization of TF
				TF += (WEIGHTS.get(attr[0]) * Double.valueOf(attr[1]) / docTokenCount );
			}
			score = TF * IDF;
			docList = newPostingMap.get(score);
			if (docList == null) {
				newPostingMap.put(score,
						docList = new ArrayList<DocumentAttributes>());
			}
			docList.add(new DocumentAttributes(docId, docAttrs));
			pos = end + 1;
		}

		if (pos < postingStrLength) {
			docDetailsArr = postingDocsStr.substring(pos, postingStrLength)
					.split("-");
			docId = docDetailsArr[0];
			docAttrs = docDetailsArr[1];
			docAttrsArr = docAttrs.split("&");
			TF = 0.0;
			for (j = 0; j < docAttrsArr.length; j++) {
				attr = docAttrsArr[j].split(":");
				TF += (WEIGHTS.get(attr[0]) * Double.valueOf(attr[1]));
			}
			score = TF * IDF;
			docList = newPostingMap.get(score);
			if (docList == null) {
				newPostingMap.put(score,
						docList = new ArrayList<DocumentAttributes>());
			}
			docList.add(new DocumentAttributes(docId, docAttrs));
		}

		Set<Entry<Double, List<DocumentAttributes>>> postingIterator = newPostingMap
				.entrySet();
		i = 0;
		for (Entry<Double, List<DocumentAttributes>> entry : postingIterator) {
			if (i == MAX_POSTING_SIZE) {
				break;
			}
			docList = entry.getValue();
			for (DocumentAttributes doc : docList) {
				newPosting.append(doc.getID()).append("-")
						.append(doc.getAttributes()).append(";");
			}
			i++;
		}

		newPostingMap.clear();

		return newPosting.toString();
	}

	/**
	 * To find out if the word is of form aaahello, jjjjgore
	 * @param str
	 * @return
	 */
	private int getLongestRepetition(String str) {
		int i = 1;
		char b = str.charAt(0);
		while (b == str.charAt(i)) {
			b = str.charAt(i);
			i++;
			if ((i > 3) || (i == str.length()))
				break;
		}
		return i;
	}
}
