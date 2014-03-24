
public class DocumentAttributes {

	private String ID;
	private String attributes;
	
	public DocumentAttributes (String id, String attrs) {
		setID(id);
		attributes = attrs;
	}
	
	public String getAttributes() {
		return attributes;
	}
	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the iD
	 */
	public String getID() {
		return ID;
	}

	/**
	 * @param iD the iD to set
	 */
	public void setID(String iD) {
		ID = iD;
	}
}
