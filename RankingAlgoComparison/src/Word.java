
public class Word implements Comparable<Word> {
	String word;
	int count;

	public Word(String word, int count) {
		this.word = word;
		this.count = count;
	}

	public int compareTo(Word otherWord) {
		if (this.count == otherWord.count) {
			return this.word.compareTo(otherWord.word);
		}
		return otherWord.count - this.count;
	}
}
