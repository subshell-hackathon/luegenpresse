package luegenpresse.indexer.ts;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import luegenpresse.indexer.IIndexer;
import luegenpresse.news.INewsRepository;
import luegenpresse.news.NewsDocument;
import luegenpresse.news.NewsDocument.NewsDocumentBuilder;

@Component
public class TagesschauIndexer implements IIndexer {
	private URL TS_FEED_URL;
	private static Logger log = LoggerFactory.getLogger(TagesschauIndexer.class);

	public TagesschauIndexer() throws MalformedURLException {
		TS_FEED_URL = new URL("http://www.tagesschau.de/xml/rss2");
	}

	@Override
	public void runPeriodically(INewsRepository repository) {
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed;
		try {
			feed = input.build(new XmlReader(TS_FEED_URL));
		} catch (IllegalArgumentException | FeedException | IOException e1) {
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		for (SyndEntry syndEntry : feed.getEntries()) {
			String link = syndEntry.getLink();
			if (!link.startsWith("http://www.tagesschau.de/")) {
				continue;
			}
			link = link.replace("http://www.tagesschau.de/", "http://www.tagesschau.de/api/");
			link = link.replace(".html", ".json");
			JsonNode node;
			try {
				node = mapper.readTree(new URL(link));
			} catch (IOException e) {
				continue; // Ignore it if we cannot read it.
			}
			
			NewsDocumentBuilder docBuilder = NewsDocument.builder();

			JsonNode jsonDate = node.get("date");
			DateTime jsonParsedDate = ISODateTimeFormat.dateTime().parseDateTime(jsonDate.textValue());
			docBuilder.date(jsonParsedDate);
			docBuilder.id("tagesschau-" + node.get("sophoraId").textValue());
			docBuilder.headLine(node.get("topline").textValue() + " - " + node.get("headline").textValue());
			docBuilder.shortText(node.get("shorttext").textValue());
			docBuilder.url(node.get("detailsWeb").textValue());
			docBuilder.source("Tagesschau");
			
			StringBuilder copytext = new StringBuilder();
			Iterator<JsonNode> paragraphs = node.get("copytext").elements();
			while (paragraphs.hasNext()) {
				JsonNode paragraph = paragraphs.next();
				String paragraphText = paragraph.get("text").textValue();
				paragraphText = stripTags(paragraphText);
				copytext.append(paragraphText);
				copytext.append("\n");
			}
			docBuilder.fullText(copytext.toString());
			
			NewsDocument document = docBuilder.build();
			log.debug("Adding document " + document);
			repository.add(document);
		}
	}

	private String stripTags(String paragraphText) {
		return Jsoup.clean(paragraphText, Whitelist.none());
	}

	@Override
	public void ingestOnce(INewsRepository repository, Map<String, Object> attributes) {
		// TODO Auto-generated method stub

	}
}
