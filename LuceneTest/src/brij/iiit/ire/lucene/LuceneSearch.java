package brij.iiit.ire.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class LuceneSearch {

	private static final String INDEX_DIRECTORY = "/home/brij/Documents/IIIT/IRE/major/lucene/indices/enwiki/index";
	private int hitsPerPage = 20;

	public LuceneSearch() {
		// TODO Auto-generated constructor stub
	}

	public List<String> query(String qStr, int resCount) {
		List<String> results = new ArrayList<String>();
		hitsPerPage = resCount;
		Directory directory = null;
		IndexReader indexReader = null;
		try {
			directory = FSDirectory.open(new File(INDEX_DIRECTORY));
			indexReader = DirectoryReader.open(directory);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		IndexSearcher searcher = new IndexSearcher(indexReader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(
				hitsPerPage, true);

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_49);
		MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
				Version.LUCENE_49, new String[] { "body", "doctitle" },
				analyzer);
		Query query;
		try {
			query = queryParser.parse(qStr);
			searcher.search(query, collector);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		//System.out.println("Found " + hits.length + " hits.");
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = null;
			try {
				d = searcher.doc(docId);
				//System.out.println((i + 1) + ". " + "\t" + d.get("doctitle"));
				results.add(d.get("doctitle").trim());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return results;
	}

	public int getHitsPerPage() {
		return hitsPerPage;
	}

	public void setHitsPerPage(int hitsPerPage) {
		this.hitsPerPage = hitsPerPage;
	}

}
