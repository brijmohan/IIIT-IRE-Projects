package brij.iiit.iremajor.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Posting implements Comparator<Posting> {
	private String token;
	private long DF;
	private List<DocumentScore> docIDList;
	private boolean fielded = false;
	private String field = "";

	public Posting() {

	}

	public Posting(String term, String df, String posting) {
		double dampingFactor = 0.0001;
		token = term;
		DF = Long.parseLong(df);
		docIDList = new ArrayList<DocumentScore>();
		String[] split = posting.split(";");
		int loop = split.length;//(split.length < 500) ? split.length : 500;
		DocumentScore documentScore = null;

		double IDF = 1 + Math.log(Query.N / DF);

		for (int i = 0; i < loop; i++) {

			double score = 0.0, TF = 0.0;

			String[] documentsComponents = split[i].split("-");

			documentScore = new DocumentScore();
			documentScore.setID(documentsComponents[0]);

			if (documentsComponents[1] != null) {
				String[] docAttrs = documentsComponents[1].split("&");
				for (int k = 0; k < docAttrs.length; k++) {
					String[] attrVals = docAttrs[k].split(":");
					TF += (CalculateScore.WEIGHTS.get(attrVals[0]) * Integer
							.parseInt(attrVals[1]));
				}
			}
			
			// to normalize documents for length
			int documentTokenCount = (Query.normalize) ? Query.titleMapper.getDocumentTokenCount(documentsComponents[0]) : 1;
			
			score = (TF / documentTokenCount) * IDF;
			documentScore.setTfidf(score * dampingFactor);

			docIDList.add(documentScore);
		}
	}

	public Posting(String term, String df, String posting, boolean fielded,
			String field) {
		double dampingFactor = CalculateScore.WEIGHTS.get(field);
		token = term;
		this.fielded = fielded;
		this.field = field;
		DF = Long.parseLong(df);
		docIDList = new ArrayList<DocumentScore>();
		String[] split = posting.split(";");
		int loop = split.length;//(split.length < 500) ? split.length : 500;
		DocumentScore documentScore = null;

		double IDF = 1 + Math.log(Query.N / DF);

		for (int i = 0; i < loop; i++) {

			double score = 0.0, TF = 0.0;

			// Both constructors differ only in this condition
			if (split[i] != null && split[i].indexOf(field + ":") != -1) {

				String[] documentsComponents = split[i].split("-");

				documentScore = new DocumentScore();
				documentScore.setID(documentsComponents[0]);

				if (documentsComponents[1] != null) {
					String[] docAttrs = documentsComponents[1].split("&");
					for (int k = 0; k < docAttrs.length; k++) {
						String[] attrVals = docAttrs[k].split(":");
						TF += (CalculateScore.WEIGHTS.get(attrVals[0]) * Integer
								.parseInt(attrVals[1]));
					}
				}
				
				// to normalize documents for length
				int documentTokenCount = (Query.normalize) ? Query.titleMapper.getDocumentTokenCount(documentsComponents[0]) : 1;
				
				score = (TF / documentTokenCount) * IDF;
				documentScore.setTfidf(score * dampingFactor);

				docIDList.add(documentScore);
			}
		}
	}

	public long getDF() {
		return DF;
	}

	public void setDF(long dF) {
		DF = dF;
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
	public int compare(Posting o1, Posting o2) {
		if (o1.isFielded() ^ o2.isFielded()) {
			return (int) (o1.getDF() - o2.getDF());
		} else {
			if (o1.isFielded() && !o2.isFielded()) {
				return 1;
			} else {
				return -1;
			}
		}
	}
}
