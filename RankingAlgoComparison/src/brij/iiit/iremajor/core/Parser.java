package brij.iiit.iremajor.core;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Parser extends DefaultHandler {

	public void parseData(String wikiInputFile, final String outputFolder)
			throws IOException {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = saxParserFactory.newSAXParser();
			DefaultHandler defaultHandler = new DefaultHandler() {

				private static final String TAG_PAGE = "page";
				private static final String TAG_TEXT = "text";
				private static final String TAG_ID = "id";
				private static final String TAG_TITLE = "title";

				Util utility = new Util();
				DocumentTitleMapper titleMapper = new DocumentTitleMapper();

				private String currentTag;

				private StringBuffer currentPageContent;
				private StringBuffer currentPageTitle;
				private long currentPageID = 0L;

				Long start = 0L;

				public void startDocument() throws SAXException {
					start = (new Date()).getTime();
					utility.setOutputFolder(outputFolder);
					titleMapper.setIndexOutputFolder(outputFolder);
				}

				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException {

					currentTag = qName;
					if (TAG_PAGE.equals(qName)) {
						currentPageContent = new StringBuffer("");
						currentPageTitle = new StringBuffer("");
						currentPageID++;
						Util.incrementDocumentCounter();
					}
				}

				public void characters(char ch[], int start, int length)
						throws SAXException {

					if (TAG_TITLE.equals(currentTag)) {
						currentPageTitle = currentPageTitle.append(ch, start,
								length);
					} else if (TAG_TEXT.equals(currentTag)) {
						currentPageContent = currentPageContent.append(ch,
								start, length);
					}
				}

				public void endElement(String uri, String localName,
						String qName) throws SAXException {

					if (TAG_ID.equalsIgnoreCase(qName)) {
					}
					if (TAG_PAGE.equalsIgnoreCase(qName)) {
						try {
							int documentTokenCount = utility.formatAndStore(currentPageTitle.toString(),
									currentPageID,
									currentPageContent.toString());
							// Add document title to mapper file
							titleMapper.addDocIdMap(currentPageID,
									currentPageTitle.toString(), documentTokenCount);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				public void endDocument() throws SAXException {
					// If something is remaining in map dump it too
					if (Util.wordMap.size() > 0) {
						utility.dumpMapToFile();
					}
					try {
						// Write document count to a conf file

						BufferedWriter out = new BufferedWriter(new FileWriter(
								new File(outputFolder + "/conf.txt")));
						out.write("N " + Util.getDocumentCounter());
						out.close();

						ExternalSort.setOutputFolder(outputFolder);
						ExternalSort.mergeSortedFiles(Util.primaryFileList,
								new File(outputFolder + "/sorted_index.txt"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println((new Date()).getTime() - start);
				}

			};

			saxParser.parse(wikiInputFile, defaultHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Parser parser = new Parser();
		try {
			parser.parseData("H:\\Labs\\IIIT\\sample.xml", "H:\\Labs\\IIIT");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
