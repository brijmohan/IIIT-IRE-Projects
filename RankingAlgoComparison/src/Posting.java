import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Posting implements Comparator<Posting> {
	private String token;
	private long DF;
	private List<String> docIDList;
	private boolean fielded = false;
	
	public Posting() {
		
	}
	
	public Posting (String term, String df, String posting) {
		token = term;
		DF = Long.parseLong(df);
		docIDList = new ArrayList<String>();
		String[] split = posting.split(";");
		int loop = (split.length < 500) ? split.length : 500;
		for (int i = 0; i < loop; i++) {
			docIDList.add(split[i].split("-")[0]);
		}
	}
	
	public long getDF() {
		return DF;
	}
	public void setDF(long dF) {
		DF = dF;
	}
	public List<String> getDocIDList() {
		return docIDList;
	}
	public void setDocIDList(List<String> docIDList) {
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

	@Override
	public int compare(Posting o1, Posting o2) {
		if (o1.isFielded() ^ o2.isFielded()) {
			return (int)(o1.getDF() - o2.getDF());
		} else {
			if (o1.isFielded() && !o2.isFielded()) {
				return 1;
			} else {
				return -1;
			}
		}
	}
}
