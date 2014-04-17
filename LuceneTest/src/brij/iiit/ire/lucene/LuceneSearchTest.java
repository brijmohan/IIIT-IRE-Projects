package brij.iiit.ire.lucene;

import java.util.List;


public class LuceneSearchTest {

	public static void main(String[] args) {
		LuceneSearch search = new LuceneSearch();
		List<String> query = search.query("brij mohan", 10);
		for(String doc : query) {
			System.out.println(doc);
		}

	}

}
