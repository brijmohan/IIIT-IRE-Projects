package brij.iiit.iremajor.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Query {

	private static final int SKIPS = 100;
	static Search search = new Search();
	private QueryCache<String, String> QUERY_CACHE = new QueryCache<String, String>(
			100);
	//public static MaxentTagger tagger = new MaxentTagger(
	//		"/home/brij/workspace/IRE-Major-Project/RankingAlgoComparison/lib/english-left3words-distsim.tagger");
	public static long N;
	public static boolean normalize = false;
	public static DocumentTitleMapper titleMapper = new DocumentTitleMapper();
	public String indexFolder;
	public static int NUM_OF_RESULTS = 20;

	public Query(String indexFolder) {

		// provide index folder
		this.indexFolder = indexFolder;

		// initialize title mapper
		titleMapper.setIndexOutputFolder(indexFolder);

		// things to do while initializing query module
		// Read collection size value - N
		RandomAccessFile confFile;
		try {
			confFile = new RandomAccessFile(indexFolder + "/conf.txt", "r");
			String conf = confFile.readLine();
			if (conf != null) {
				N = Long.parseLong(conf.split(" ")[1]);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		Query query = new Query(args[0]);

		Long start = (new Date()).getTime();
		int num = 0, i, j = 0;
		String queryStr;
		try {

			// For Reading console inputs for search queries
			BufferedReader consoleInput = new BufferedReader(
					new InputStreamReader(System.in));

			// First input in the file will indicate the number of search
			// queries
			num = Integer.parseInt(consoleInput.readLine());
			for (i = 0; i < num; i++) {

				queryStr = consoleInput.readLine().trim();
				List<DocumentScore> results = query.find(queryStr);
				j = 1;
				System.out.println("Results :");
				for (DocumentScore res : results) {
					System.out.println(j++ + ". " + res.getID() + " : "
							+ res.getTfidf());
				}

				System.out.println((new Date()).getTime() - start);

				// Adding next line to give default space after query result
				System.out.println();
			}

		} catch (Exception e) {// Catch exception if any
			e.printStackTrace();
		}

		System.out.println((new Date()).getTime() - start);
	}

	public List<DocumentScore> find(String queryStr) {

		List<DocumentScore> queryResults = new ArrayList<DocumentScore>();

		int postingListSize = 0, titleCounter = 0;
		String posting = null;
		Posting fieldPosting = null;
		String[] fieldQuery = null, postingSplit = null;
		List<String> queryWords = null;

		RandomAccessFile praf = null, sraf = null;

		try {

			praf = new RandomAccessFile(indexFolder + File.separator
					+ "index.txt", "rw");
			// To access secondary index file
			File secondaryFile = new File(indexFolder + File.separator
					+ "sindex.txt");
			sraf = new RandomAccessFile(secondaryFile, "rw");

			queryStr = queryStr.trim();
			System.out.println("Query : " + queryStr);
			queryWords = this.preprocessQuery(queryStr);

			// iterate over query words to find their postings
			List<Posting> postingsList = new ArrayList<Posting>();
			for (String queryWord : queryWords) {

				fieldQuery = this.getFieldQuery(queryWord);

				posting = null;
				fieldPosting = null;
				if (fieldQuery == null) {
					posting = this.getPostingFor(queryWord, sraf, praf);
					if (posting != null) {
						postingSplit = posting.split("/");
						postingsList.add(new Posting(postingSplit[0],
								postingSplit[1], postingSplit[2]));
					}
				} else {
					fieldPosting = this.getFieldPostingFor(fieldQuery[1],
							fieldQuery[0], sraf, praf);
					if (fieldPosting != null) {
						postingsList.add(fieldPosting);
					}
				}

			}
			postingListSize = postingsList.size();
			// Process postings
			if (postingListSize == 0) {

				// No results found

			} else if (postingListSize == 1) {

				// Single query - get top NUM_OF_RESULTS posting values
				List<DocumentScore> postings = postingsList.get(0)
						.getDocIDList();
				titleCounter = 0;
				for (DocumentScore docid : postings) {
					titleCounter++;
					queryResults
							.add(new DocumentScore(titleMapper
									.getDocumentTitle(docid.getID()), docid
									.getTfidf()));
					if (titleCounter == NUM_OF_RESULTS)
						break;
				}

			} else if (postingListSize > 1) {

				// Phrase queries - get top 10 merged results
				// TODO implement comparator chain

				// To sort postings based on their DF - important if
				// implementing conjunctive model
				// Collections.sort(postingsList, new Posting());

				double effectiveScore = 1.0;
				Map<String, Integer> documentFreqMap = new HashMap<String, Integer>();
				Map<String, Double> documentScoreMap = new HashMap<String, Double>();
				for (Posting postingObj : postingsList) {

					for (DocumentScore documentScore : postingObj.getDocIDList()) {
						if (documentScoreMap.containsKey(documentScore.getID())) {
							effectiveScore = documentScoreMap.get(documentScore
									.getID()) + (documentScore.getTfidf());
							documentScoreMap.put(documentScore.getID(),
									effectiveScore);
							documentFreqMap.put(documentScore.getID(), documentFreqMap.get(documentScore.getID()) + 1);
						} else {
							effectiveScore = documentScore.getTfidf();
							documentScoreMap.put(documentScore.getID(),
									effectiveScore);
							documentFreqMap.put(documentScore.getID(), 1);
						}
					}
				}
				documentFreqMap = Query.reverseSortByValue(documentFreqMap);
				
				int lastFreqValue = 1000, docCounter = 1, docLimit = documentFreqMap.size();
				Map<String, Double> tempScoreMap = new HashMap<String, Double>();
				Map<String, Double> finalDocMap = new LinkedHashMap<String, Double>();
				
				for (Entry<String, Integer> entry : documentFreqMap.entrySet()) {
					if (entry.getValue() == lastFreqValue && docCounter < docLimit) {
						tempScoreMap.put(entry.getKey(), documentScoreMap.get(entry.getKey()));
					} else {
						lastFreqValue = entry.getValue();
						tempScoreMap = Query.reverseSortByValue(tempScoreMap);
						for (Entry<String, Double> scoreEntry : tempScoreMap.entrySet()) {
							finalDocMap.put(scoreEntry.getKey(), scoreEntry.getValue());
						}
						tempScoreMap.clear();
						tempScoreMap.put(entry.getKey(), documentScoreMap.get(entry.getKey()));
					}
					docCounter++;
				}
				
				// RAW INTERSECTION IMPLEMENTED
				// for (k = 1; k < postingListSize; k++) {
				// combined = query.intersect(combined, postingsList.get(k)
				// .getDocIDList());
				// }

				// Ranking postings based on aggregate tf-idf weights

				// If combined has less than 10 results pick from most priority
				// word
				// TODO - fielded queries should be given more weightage
				// int csize = combined.size();
				// if (csize < 10) {
				// int toCover = 10 - csize;
				// for (String toCoverDocs : postingsList.get(0)
				// .getDocIDList()) {
				// if (!combined.contains(toCoverDocs)) {
				// combined.add(toCoverDocs);
				// toCover--;
				// }
				// if (toCover == 0) {
				// break;
				// }
				// }
				// }

				titleCounter = 0;
				// for (String docid : combined) {
				// titleCounter++;
				// System.out.println(titleCounter + ". "
				// + titleMapper.getDocumentTitle(docid));
				// queryResults.add(titleMapper.getDocumentTitle(docid));
				// if (titleCounter == 10)
				// break;
				// }
				for (Entry<String, Double> entry : finalDocMap.entrySet()) {

					if (titleCounter >= NUM_OF_RESULTS)
						break;

					queryResults
							.add(new DocumentScore(titleMapper
									.getDocumentTitle(entry.getKey()), entry
									.getValue()));
					titleCounter++;
				}

			}
			// Adding next line to give default space after query result
			System.out.println();
		} catch (Exception e) {

		} finally {
			try {
				praf.close();
				sraf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return queryResults;
	}

	@Deprecated
	private List<String> getCommonDocuments(List<Posting> postingsList) {
		List<String> commonDocs = new ArrayList<String>();
		int i, matchCount;
		String docToMatch;
		for (DocumentScore firstPostingDoc : postingsList.get(0).getDocIDList()) {
			i = 1;
			docToMatch = firstPostingDoc.getID();
			matchCount = 0;
			while (i < postingsList.size()) {

				for (DocumentScore ithPostingDoc : postingsList.get(i)
						.getDocIDList()) {
					if (ithPostingDoc.getID().equalsIgnoreCase(docToMatch)) {
						matchCount++;
					}
				}

				i++;
			}
			if (matchCount == (postingsList.size() - 1)) {
				commonDocs.add(docToMatch);
			}
		}

		return commonDocs;
	}

	private Posting getFieldPostingFor(String queryWord, String field,
			RandomAccessFile sraf, RandomAccessFile praf) {
		Posting fieldPosting = null;
		String[] split = null;
		String posting = getPostingFor(queryWord, sraf, praf);
		if (posting != null) {
			split = posting.split("/");
			fieldPosting = new Posting(split[0], split[1], split[2], true,
					field);
		}
		return fieldPosting;
	}

	private String getPostingFor(String queryWord, RandomAccessFile sraf,
			RandomAccessFile praf) {

		if (QUERY_CACHE.containsKey(queryWord)) {
			return QUERY_CACHE.get(queryWord);
		}

		String posting = null;
		byte b;
		StringBuffer sbuf = null;

		// Find the nearest match in secondary index and tokenize
		// to get address of primary index file
		long secondaryOffset;
		try {
			secondaryOffset = search.findNearestOffset(sraf, queryWord);

			sraf.seek(secondaryOffset);
			String aa = sraf.readLine();
			String[] secLine = aa.split(" ");

			// If exact matching token is not found then goto previous token
			// and do
			// a linear search to find the exact match in primary file
			if (!queryWord.equalsIgnoreCase(secLine[0])) {

				// Moving secondary offset pointer to the start of previous line
				sraf.seek(search.getPreviousLineStart(secondaryOffset, sraf));
				secLine = sraf.readLine().split(" ");

				// Moving primary offset pointer to previous matching vocabulary
				// word
				praf.seek(Long.parseLong(secLine[1]));
				boolean matchFound = false;
				int iter = 0;

				// Iterating linearly over the primary posting list
				// to find the exact matching keyword from vocabulary
				while (!matchFound && iter <= SKIPS) {
					// posting = praf.readLine();
					sbuf = new StringBuffer();
					while ((b = praf.readByte()) != 47) {
						sbuf.append((char) b);
					}
					if (queryWord.equalsIgnoreCase(sbuf.toString())) {
						posting = sbuf.append("/").append(praf.readLine())
								.toString();
						matchFound = true;
					} else {
						praf.seek(search.getNextLineStart(
								praf.getFilePointer(), praf));
					}
					iter++;
				}

			} else {
				praf.seek(Long.parseLong(secLine[1]));
				posting = praf.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Put in cache
		QUERY_CACHE.put(queryWord, posting);

		return posting;
	}

	private List<String> preprocessQuery(String queryStr) {
		List<String> qWords = null;
		if (!queryStr.equals("")) {
			qWords = new ArrayList<String>();
			String[] split = queryStr.split(" ");
			for (int i = 0; i < split.length; i++) {
				if (Util.isValidString(split[i])) {
					// Format the query string (lowercase, stemming, etc.)
					String[] fieldQuery = getFieldQuery(split[i]);
					if (fieldQuery == null) {
						qWords.add(Stemmer.parse(split[i].toLowerCase()));
					} else {
						qWords.add(new StringBuffer(fieldQuery[0])
								.append(":")
								.append(Stemmer.parse(fieldQuery[1]
										.toLowerCase())).toString());
					}
				}
			}
		}
		return qWords;
	}

	private String[] getFieldQuery(String str) {
		int end;
		String[] arr = null;
		if ((end = str.indexOf(':', 0)) > 0) {
			arr = new String[2];
			arr[0] = str.substring(0, end);
			arr[1] = str.substring(end + 1, str.length());
		}
		return arr;
	}

	public List<String> intersect(List<String> a, List<String> b) {
		HashSet<String> hs = new HashSet<String>();
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < b.size(); i++) {
			hs.add(b.get(i));
		}

		for (int i = 0; i < a.size(); i++) {
			if (hs.contains(a.get(i))) {
				result.add(a.get(i));
			}
		}
		return result;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> reverseSortByValue(
			Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
