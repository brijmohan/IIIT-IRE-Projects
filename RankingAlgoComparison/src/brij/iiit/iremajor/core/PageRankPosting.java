package brij.iiit.iremajor.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PageRankPosting implements Comparator<PageRankPosting>{

	private String token;
	private List<DocumentScore> docIDList;
	private boolean fielded = false;
	private String field = "";
	
	public PageRankPosting() {
		// TODO Auto-generated constructor stub
	}
	
	public PageRankPosting(String term, String posting) {
		System.out.println(term + " : " + posting);
		token = term;
		docIDList = new ArrayList<DocumentScore>();
		String[] split = posting.split(";");
		int loop = split.length;//(split.length < 500) ? split.length : 500;
		DocumentScore documentScore = null;

		for (int i = 0; i < loop; i++) {

			double score = 0.0;

			String[] documentsComponents = split[i].split("-");

			documentScore = new DocumentScore();
			documentScore.setID(documentsComponents[0]);

			if (documentsComponents[1] != null) {
				score = Double.parseDouble(documentsComponents[1].split(":")[1]);
			}
			
			documentScore.setTfidf(score);

			docIDList.add(documentScore);
		}
	}

	public PageRankPosting(String term, String posting, boolean fielded,
			String field) {
		System.out.println(term + " : " + field + " - " + posting);
		token = term;
		this.fielded = fielded;
		this.field = field;
		docIDList = new ArrayList<DocumentScore>();
		String[] split = posting.split(";");
		int loop = split.length;//(split.length < 500) ? split.length : 500;
		DocumentScore documentScore = null;

		for (int i = 0; i < loop; i++) {

			double score = 0.0;

			// Both constructors differ only in this condition
			if (split[i] != null && split[i].indexOf(field) != -1) {

				String[] documentsComponents = split[i].split("-");

				documentScore = new DocumentScore();
				documentScore.setID(documentsComponents[0]);

				if (documentsComponents[1] != null) {
					score = Double.parseDouble(documentsComponents[1].split(":")[1]);
				}
				
				documentScore.setTfidf(score);

				docIDList.add(documentScore);
			}
		}
	}

	public List<DocumentScore> getDocIDList() {
		return docIDList;
	}

	public void setDocIDList(List<DocumentScore> docIDList) {
		this.docIDList = docIDList;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isFielded() {
		return fielded;
	}

	public void setFielded(boolean fielded) {
		this.fielded = fielded;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	@Override
	public int compare(PageRankPosting o1, PageRankPosting o2) {
		return 0;
	}

}
