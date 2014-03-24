
public class Index {

	public static void main(String[] args) {
		Parser parser = new Parser();
		try {
			parser.parseData(args[0], args[1]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
