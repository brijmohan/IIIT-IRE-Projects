import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Query {

	private static final int SKIPS = 100;
	static Search search = new Search();
	private Map<String, String> QUERY_CACHE = new HashMap<String, String>();

	public static void main(String[] args) {

		Query query = new Query();

		Long start = (new Date()).getTime();
		int num = 0, i = 0, postingListSize = 0, k = 0, titleCounter = 0;
		String queryStr, posting = null;
		Posting fieldPosting = null;
		String[] fieldQuery = null, postingSplit = null;
		List<String> queryWords = null;

		DocumentTitleMapper titleMapper = new DocumentTitleMapper();
		titleMapper.setIndexOutputFolder(args[0]);
		RandomAccessFile praf = null, sraf = null;

		try {

			// For Reading console inputs for search queries
			BufferedReader consoleInput = new BufferedReader(
					new InputStreamReader(System.in));
			// To access primary index file
			praf = new RandomAccessFile(args[0] + File.separator + "index.txt",
					"rw");
			// To access secondary index file
			File secondaryFile = new File(args[0] + File.separator
					+ "sindex.txt");
			sraf = new RandomAccessFile(secondaryFile, "rw");

			// First input in the file will indicate the number of search
			// queries
			num = Integer.parseInt(consoleInput.readLine());
			for (i = 0; i < num; i++) {
				queryStr = consoleInput.readLine().trim();
				System.out.println("Query : " + queryStr);
				queryWords = query.preprocessQuery(queryStr);

				// iterate over query words to find their postings
				List<Posting> postingsList = new ArrayList<Posting>();
				for (String queryWord : queryWords) {
					
					fieldQuery = query.getFieldQuery(queryWord);
					
					posting = null;
					fieldPosting = null;
					if (fieldQuery == null) {
						posting = query.getPostingFor(queryWord, sraf, praf);
						if (posting != null) {
							postingSplit = posting.split("/");
							postingsList.add(new Posting(postingSplit[0],
									postingSplit[1], postingSplit[2]));
						}
					} else {
						fieldPosting = query.getFieldPostingFor(fieldQuery[1],
								fieldQuery[0], sraf, praf);
						if (fieldPosting != null) {
							postingsList.add(fieldPosting);
						}
					}

				}
				postingListSize = postingsList.size();
				System.out.println("Results :");
				// Process postings
				if (postingListSize == 0) {

					// No results found

				} else if (postingListSize == 1) {

					// Single query - get top 10 posting values
					List<String> postings = postingsList.get(0).getDocIDList();
					titleCounter = 0;
					for (String docid : postings) {
						titleCounter++;
						System.out.println(titleCounter + ". " + titleMapper.getDocumentTitle(docid));
						if (titleCounter == 10)
							break;
					}

				} else if (postingListSize > 1) {

					// Phrase queries - get top 10 merged results
					// TODO implement comparator chain
					Collections.sort(postingsList, new Posting());
					List<String> combined = postingsList.get(0).getDocIDList();
					for (k = 1; k < postingListSize; k++) {
						combined = query.intersect(combined, postingsList
								.get(k).getDocIDList());
					}
					
					// If combined has less than 10 results pick from most priority word
					//TODO - fielded queries should be given more weightage
					int csize = combined.size();
					if (csize < 10) {
						int toCover = 10 - csize;
						for (String toCoverDocs : postingsList.get(0).getDocIDList()) {
							if (!combined.contains(toCoverDocs)) {
								combined.add(toCoverDocs);
								toCover--;
							}
							if (toCover == 0) {
								break;
							}
						}
					}
					
					titleCounter = 0;
					for (String docid : combined) {
						titleCounter++;
						System.out.println(titleCounter + ". " + titleMapper.getDocumentTitle(docid));
						if (titleCounter == 10)
							break;
					}

				}
				System.out.println((new Date()).getTime() - start);

				// Adding next line to give default space after query result
				System.out.println();
			}

		} catch (Exception e) {// Catch exception if any
			e.printStackTrace();
		} finally {
			try {
				praf.close();
				sraf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println((new Date()).getTime() - start);
	}

	private Posting getFieldPostingFor(String queryWord, String field,
			RandomAccessFile sraf, RandomAccessFile praf) {
		List<String> docIds = null;
		Posting fieldPosting = null;
		String[] split = null;
		String posting = getPostingFor(queryWord, sraf, praf), docAttr = null;
		if (posting != null) {
			fieldPosting = new Posting();
			docIds = new ArrayList<String>();
			split = posting.split("/");
			
			fieldPosting.setToken(split[0]);
			fieldPosting.setDF(Long.parseLong(split[1]));
			fieldPosting.setFielded(true);
			
			posting = split[2];
			int pos = 0, end, docCount = 0;
			while ((end = posting.indexOf(';', pos)) >= 0 && docCount < 100) {
				docAttr = posting.substring(pos, end);
				if (docAttr.indexOf(field + ":") != -1) {
					docIds.add(docAttr.split("-")[0]);
					docCount++;
				}
				pos = end + 1;
			}
			fieldPosting.setDocIDList(docIds);
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
		
		//Put in cache
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
}
