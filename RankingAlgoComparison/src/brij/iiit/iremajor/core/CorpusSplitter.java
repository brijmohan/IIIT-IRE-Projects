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

public class CorpusSplitter extends DefaultHandler {

	public void parseData(String wikiInputFile, final String outputFolder)
			throws IOException {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = saxParserFactory.newSAXParser();
			DefaultHandler defaultHandler = new DefaultHandler() {

				private static final String TAG_PAGE = "page";
				private static final String TAG_TEXT = "text";
				private static final String TAG_TITLE = "title";

				private String currentTag;

				private StringBuffer currentPageContent;
				private StringBuffer currentPageTitle;

				Long start = 0L;

				BufferedWriter out = null;

				public void startDocument() throws SAXException {
					start = (new Date()).getTime();
				}

				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException {

					currentTag = qName;
					if (TAG_PAGE.equals(qName)) {
						currentPageContent = new StringBuffer("");
						currentPageTitle = new StringBuffer("");
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
					try {
						if (TAG_TITLE.equalsIgnoreCase(qName)) {
							String fileName = currentPageTitle.toString().replaceAll("[^a-zA-Z]", " ").trim();
							out = new BufferedWriter(new FileWriter(new File(
									outputFolder + File.separator
											+ fileName + ".txt")));
						}
						if (TAG_PAGE.equalsIgnoreCase(qName)) {
							out.write(currentPageContent.toString());
							if (out != null) {
								out.close();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				public void endDocument() throws SAXException {
					System.out.println((new Date()).getTime() - start);
				}

			};

			saxParser.parse(wikiInputFile, defaultHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		CorpusSplitter parser = new CorpusSplitter();
		try {
			parser.parseData(args[0], args[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
