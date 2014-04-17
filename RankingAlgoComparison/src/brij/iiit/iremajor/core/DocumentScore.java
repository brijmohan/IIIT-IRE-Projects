package brij.iiit.iremajor.core;

import org.json.simple.JSONObject;

public class DocumentScore {
	private String ID;
	private double tfidf;
	
	public DocumentScore (String id, double score) {
		ID = id;
		tfidf = score;
	}
	
	public DocumentScore() {
		// TODO Auto-generated constructor stub
	}

	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public double getTfidf() {
		return tfidf;
	}
	public void setTfidf(double tfidf) {
		this.tfidf = tfidf;
	}
	
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", ID);
		jsonObject.put("score", tfidf);
		return jsonObject;
	}
}
