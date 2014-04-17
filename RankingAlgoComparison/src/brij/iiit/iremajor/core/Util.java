package brij.iiit.iremajor.core;
import java.io.BufferedReader;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

	private String outputFolder;
	private static long fileCounter = 0;
	private static long documentCounter = 0;

	public static List<File> primaryFileList = new ArrayList<File>();

	public static final Map<String, Map<Long, Map<String, MutableInt>>> wordMap = new TreeMap<String, Map<Long, Map<String, MutableInt>>>();
	private static final long WORDMAP_LIMIT = 50000;

	private static final String EXTERNAL_LINK_REGEX = "(?mi)^\\s*==\\s*external\\s*links\\s*==\\s*$";
	private static final Pattern EXTERNAL_LINK = Pattern
			.compile(EXTERNAL_LINK_REGEX);

	private static final String CATEGORY_REGEX = "(?mi)\\[\\[Category:(.*?)\\]\\]";
	private static final Pattern CATEGORY_PATTERN = Pattern
			.compile(CATEGORY_REGEX);

	private static final String INFOBOX_CONST_STR = "{{Infobox";

	public static final String[] stopWords = { "a", "able", "about", "above",
			"according", "accordingly", "across", "actually", "after",
			"afterwards", "again", "against", "all", "allow", "all", "almost",
			"alone", "along", "already", "also", "although", "always", "am",
			"among", "amongst", "an", "and", "another", "any", "anybody",
			"anyhow", "anyone", "anything", "anyway", "anyways", "anywhere",
			"apart", "appear", "appreciate", "appropriate", "are", "around",
			"as", "aside", "ask", "asking", "associated", "at", "available",
			"away", "awfully", "b", "be", "became", "because", "become",
			"becomes", "becoming", "been", "before", "beforehand", "behind",
			"being", "believe", "below", "beside", "besides", "best", "better",
			"between", "beyond", "both", "brief", "but", "by", "c", "came",
			"can", "cannot", "cant", "cause", "causes", "certain", "certainly",
			"changes", "clearly", "co", "com", "come", "comes", "concerning",
			"consequently", "consider", "considering", "contain", "containing",
			"contains", "corresponding", "could", "course", "currently", "d",
			"definitely", "described", "despite", "did", "different", "do",
			"does", "doing", "done", "down", "downwards", "during", "e",
			"each", "edu", "eg", "eight", "either", "else", "elsewhere",
			"enough", "entirely", "especially", "et", "etc", "even", "ever",
			"every", "everybody", "everyone", "everything", "everywhere", "ex",
			"exactly", "example", "except", "f", "far", "few", "fifth",
			"first", "five", "followed", "following", "follows", "for",
			"former", "formerly", "forth", "four", "from", "further",
			"furthermore", "g", "get", "gets", "getting", "given", "gives",
			"go", "goes", "going", "gone", "got", "gotten", "greetings", "h",
			"had", "happens", "hardly", "has", "have", "having", "he", "hello",
			"help", "hence", "her", "here", "hereafter", "hereby", "herein",
			"hereupon", "hers", "herself", "hi", "him", "himself", "his",
			"hither", "hopefully", "how", "howbeit", "however", "i", "ie",
			"if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed",
			"indicate", "indicated", "indicates", "inner", "insofar",
			"instead", "into", "inward", "is", "it", "its", "itself", "j",
			"just", "k", "keep", "keeps", "kept", "know", "knows", "known",
			"l", "last", "lately", "later", "latter", "latterly", "least",
			"less", "lest", "let", "like", "liked", "likely", "little", "ll",
			"look", "looking", "looks", "ltd", "m", "mainly", "many", "may",
			"maybe", "me", "mean", "meanwhile", "merely", "might", "more",
			"moreover", "most", "mostly", "much", "must", "my", "myself", "n",
			"name", "namely", "nd", "near", "nearly", "necessary", "need",
			"needs", "neither", "never", "nevertheless", "new", "next", "nine",
			"no", "nobody", "non", "none", "noone", "nor", "normally", "not",
			"nothing", "novel", "now", "nowhere", "o", "obviously", "of",
			"off", "often", "oh", "ok", "okay", "old", "on", "once", "one",
			"ones", "only", "onto", "or", "other", "others", "otherwise",
			"ought", "our", "ours", "ourselves", "out", "outside", "over",
			"overall", "own", "p", "particular", "particularly", "per",
			"perhaps", "placed", "please", "plus", "possible", "presumably",
			"probably", "provides", "q", "que", "quite", "qv", "r", "rather",
			"rd", "re", "really", "reasonably", "regarding", "regardless",
			"regards", "relatively", "respectively", "right", "s", "said",
			"same", "saw", "say", "saying", "says", "second", "secondly",
			"see", "seeing", "seem", "seemed", "seeming", "seems", "seen",
			"self", "selves", "sensible", "sent", "serious", "seriously",
			"seven", "several", "shall", "she", "should", "since", "six", "so",
			"some", "somebody", "somehow", "someone", "something", "sometime",
			"sometimes", "somewhat", "somewhere", "soon", "sorry", "specified",
			"specify", "specifying", "still", "sub", "such", "sup", "sure",
			"t", "take", "taken", "tell", "tends", "th", "than", "thank",
			"thanks", "thanx", "that", "thats", "the", "their", "theirs",
			"them", "themselves", "then", "thence", "there", "thereafter",
			"thereby", "therefore", "therein", "theres", "thereupon", "these",
			"they", "think", "third", "this", "thorough", "thoroughly",
			"those", "though", "three", "through", "throughout", "thru",
			"thus", "to", "together", "too", "took", "toward", "towards",
			"tried", "tries", "truly", "try", "trying", "twice", "two", "u",
			"un", "under", "unfortunately", "unless", "unlikely", "until",
			"unto", "up", "upon", "us", "use", "used", "useful", "uses",
			"using", "usually", "uucp", "v", "value", "various", "ve", "very",
			"via", "viz", "vs", "w", "want", "wants", "was", "way", "we",
			"welcome", "well", "went", "were", "what", "whatever", "when",
			"whence", "whenever", "where", "whereafter", "whereas", "whereby",
			"wherein", "whereupon", "wherever", "whether", "which", "while",
			"whither", "who", "whoever", "whole", "whom", "whose", "why",
			"will", "willing", "wish", "with", "within", "without", "wonder",
			"would", "would", "x", "y", "yes", "yet", "you", "your", "yours",
			"yourself", "yourselves", "z", "zero", "nbsp", "lt", "gt", "www",
			"com", "jpg", "jpeg", "http", "aa", "aaa", "aaaa", "aaaaa", "zzz",
			"png", "txt", "avi", "mpg", "mpeg", "pdf", "redirect" };

	private static final HashSet<String> stopWordSet = new HashSet<String>(
			Arrays.asList(stopWords));

	public int formatAndStore(String title, long currentPageID, String text) {

		// To store total number of tokens in a document
		int documentTokenCount = 0;
					
		try {
			
			// =========== PROCESS TITLE START ===============
			String[] words = basicFormatting(title).split(" ");
			Word[] wordFrequency = WordFrequencyCounter.getFrequentWords(words);
			for (int k = 0; k < wordFrequency.length; k++) {
				documentTokenCount += wordFrequency[k].count;
			}
			processWords(wordFrequency, currentPageID, "t");
			// =========== PROCESS TITLE END ===============

			// =========== PROCESS EXTERNAL LINK START ===============
			// Finding external links in text
			Matcher matcher = EXTERNAL_LINK.matcher(text);
			StringBuffer sbuf = new StringBuffer();
			if (matcher.find()) {
				BufferedReader bufReader = new BufferedReader(new StringReader(
						text.substring(matcher.end() + 1)));
				sbuf = new StringBuffer();
				String line = null;
				while ((line = bufReader.readLine()) != null) {
					if (line.trim().equals("")) {
						break;
					}
					text = text.replace(line, "");
					line = line.replaceAll("[^a-zA-Z]", " ").trim()
							.replaceAll(" +", " ");
					sbuf.append(line).append(" ");
				}
			}
			// Process external links
			words = basicFormatting(sbuf.toString().trim()).split(" ");
			wordFrequency = WordFrequencyCounter.getFrequentWords(words);
			for (int k = 0; k < wordFrequency.length; k++) {
				documentTokenCount += wordFrequency[k].count;
			}
			processWords(wordFrequency, currentPageID, "e");

			// Remove external links from main text
			text = text.replaceAll(EXTERNAL_LINK_REGEX, "");
			// =========== PROCESS EXTERNAL LINKS END ===============

			// =========== PROCESS CATEGORIES START ===============
			// Finding all the categories
			matcher = CATEGORY_PATTERN.matcher(text);
			sbuf = new StringBuffer();
			while (matcher.find()) {
				String[] temp = matcher.group(1).split("\\|");
				sbuf.append(temp[0]).append(" ");
			}

			// Process categories
			words = basicFormatting(sbuf.toString()).split(" ");
			wordFrequency = WordFrequencyCounter.getFrequentWords(words);
			for (int k = 0; k < wordFrequency.length; k++) {
				documentTokenCount += wordFrequency[k].count;
			}
			processWords(wordFrequency, currentPageID, "c");

			// Remove categories from main text
			text = text.replaceAll(CATEGORY_REGEX, "");
			// =========== PROCESS CATEGORIES END ===============

			// =========== PROCESS INFOBOX START ===============
			String infoBoxText = "";
			int startPos = text.indexOf(INFOBOX_CONST_STR), endPos = 0;
			if (startPos >= 0) {
				int bracketCount = 2;
				endPos = startPos + INFOBOX_CONST_STR.length();
				for (; endPos < text.length(); endPos++) {
					switch (text.charAt(endPos)) {
					case '}':
						bracketCount--;
						break;
					case '{':
						bracketCount++;
						break;
					default:
					}
					if (bracketCount == 0)
						break;
				}
				if (endPos + 1 < text.length()) {
					infoBoxText = text.substring(startPos, endPos + 1);
				}
			}

			// Process categories
			words = basicFormatting(infoBoxText).split(" ");
			wordFrequency = WordFrequencyCounter.getFrequentWords(words);
			for (int k = 0; k < wordFrequency.length; k++) {
				documentTokenCount += wordFrequency[k].count;
			}
			processWords(wordFrequency, currentPageID, "i");

			// Remove infobox from main text
			text = text.replace(infoBoxText, "");
			// =========== PROCESS INFOBOX END ===============

			// =========== PROCESS BODY TEXT START ===============
			// Process remaining body text
			words = basicFormatting(text).split(" ");
			wordFrequency = WordFrequencyCounter.getFrequentWords(words);
			for (int k = 0; k < wordFrequency.length; k++) {
				documentTokenCount += wordFrequency[k].count;
			}
			processWords(wordFrequency, currentPageID, "b");
			// =========== PROCESS BODY TEXT END ===============

			// If hashmap size increased more than MAX then dump and clean the
			// map
			if (wordMap.size() >= WORDMAP_LIMIT) {
				dumpMapToFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return documentTokenCount;
	}

	private void processWords(Word[] wordFrequency, long currentPageID,
			String category) {
		int i = 0;
		Word word = null;
		Map<Long, Map<String, MutableInt>> list = null;
		Map<String, MutableInt> categoryMap = null;
		MutableInt categoryCount = null;
		// iterating over words to form posting list
		for (i = 0; i < wordFrequency.length; i++) {
			// Storing the word along with the document id as set
			word = wordFrequency[i];
			list = wordMap.get(word.word);
			if (list == null || list.size() == 0) {
				wordMap.put(word.word,
						list = new HashMap<Long, Map<String, MutableInt>>());
			}
			categoryMap = list.get(currentPageID);
			if (categoryMap == null) {
				list.put(currentPageID,
						categoryMap = new HashMap<String, MutableInt>());
			}
			categoryCount = categoryMap.get(category);
			if (categoryCount == null) {
				categoryMap.put(category, categoryCount = new MutableInt(
						word.count));
			} else {
				categoryCount.incrementBy(word.count);
			}
		}
	}

	public void dumpMapToFile() {
		writeInvertedIndexToFile(wordMap, getFileCounter());
		wordMap.clear();
		incrementFileCounter();
	}

	public static boolean isValidString(String str) {
		return (str != null && !stopWordSet.contains(str)
				&& !str.matches("\\d+") && str.length() < 21);
	}

	private void writeInvertedIndexToFile(
			Map<String, Map<Long, Map<String, MutableInt>>> wordmap2,
			long counter) {

		try {
			File f = new File(new StringBuffer(outputFolder).append("/index")
					.append(counter).append(".txt").toString());
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			primaryFileList.add(f);

			String word;
			StringBuffer sb = null;
			Set<Entry<Long, Map<String, MutableInt>>> docIterator = null;
			Set<Entry<String, MutableInt>> categoryIterator = null;
			int docListCount = 0, j = 0, k = 0, categoryCount = 0;
			for (Entry<String, Map<Long, Map<String, MutableInt>>> entry : wordmap2
					.entrySet()) {
				sb = new StringBuffer();
				docIterator = entry.getValue().entrySet();
				docListCount = docIterator.size();
				word = entry.getKey();
				j = 0;
				for (Entry<Long, Map<String, MutableInt>> documentData : docIterator) {
					j++;
					sb.append(documentData.getKey()).append("-");
					categoryIterator = documentData.getValue().entrySet();
					k = 0;
					categoryCount = categoryIterator.size();
					for (Entry<String, MutableInt> categoryData : categoryIterator) {
						k++;
						sb.append(categoryData.getKey()).append(":")
								.append(categoryData.getValue().get());
						if (k != categoryCount) {
							sb.append("&");
						}
					}
					if (j != docListCount) {
						sb.append(";");
					}
				}

				raf.writeBytes(new StringBuffer(word).append("/")
						.append(sb.toString()).append("\n").toString());

			}
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String basicFormatting(String str) {
		// String text = str.replaceAll("&gt;", ">");
		// text = text.replaceAll("&lt;", "<");
		// text = text.replaceAll("<ref>.*?</ref>", " ");
		// text = text.replaceAll("</?.*?>", " ");
		// text = text.replaceAll("\\{\\{.*?\\}\\}", " ");
		// text = text.replaceAll("\\[\\[.*?:.*?\\]\\]", " ");
		// String text = str.replaceAll("\\[\\[(.*?)\\]\\]", "$1");
		// text = text.replaceAll("\\s(.*?)\\|(\\w+\\s)", " $2");
		// text = text.replaceAll("\\[.*?\\]", " ");
		// text = text.replaceAll("\\'+", "");
		return str.replaceAll("[^a-zA-Z]", " ").trim().replaceAll(" +", " ");
	}

	/**
	 * @return the outputFolder
	 */
	public String getOutputFolder() {
		return outputFolder;
	}

	/**
	 * @param outputFolder
	 *            the outputFolder to set
	 */
	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}

	/**
	 * @return the fileCounter
	 */
	public long getFileCounter() {
		return fileCounter;
	}

	/**
	 * @param fileCounter
	 *            the fileCounter to set
	 */
	public void setFileCounter(int fileCounter) {
		Util.fileCounter = fileCounter;
	}

	/**
	 * increments the file counter
	 */
	public void incrementFileCounter() {
		Util.fileCounter = Util.fileCounter + 1;
	}

	/**
	 * @return the documentCounter
	 */
	public static long getDocumentCounter() {
		return documentCounter;
	}

	/**
	 * @param documentCounter
	 *            the documentCounter to set
	 */
	public static void setDocumentCounter(int documentCounter) {
		Util.documentCounter = documentCounter;
	}

	/**
	 * @param documentCounter
	 *            the documentCounter to set
	 */
	public static void incrementDocumentCounter() {
		Util.documentCounter++;
	}

}
