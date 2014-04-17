package brij.iiit.iremajor.core;
import java.util.HashMap;

public class WordFrequencyCounter {

	public static Word[] getFrequentWords(String words[]) {
		HashMap<String, Word> map = new HashMap<String, Word>();
		Word w = null;
		for (String s : words) {
			s = Stemmer.parse(s.toLowerCase());
			if (Util.isValidString(s) && s.length() > 2) {
				w = map.get(s);
				if (w == null)
					w = new Word(s, 1);
				else
					w.count++;
				map.put(s, w);
			}
		}
		Word[] list = map.values().toArray(new Word[] {});
		//Arrays.sort(list);
		return list;
	}
}