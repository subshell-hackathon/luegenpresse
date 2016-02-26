package luegenpresse.indexer.ts;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
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

@Component
public class TagesschauIndexer implements IIndexer {
	private URL TS_FEED_URL;

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
			NewsDocument doc = new NewsDocument();

			JsonNode jsonDate = node.get("date");
			DateTime jsonParsedDate = ISODateTimeFormat.dateTime().parseDateTime(jsonDate.textValue());
			doc.setDate(jsonParsedDate);

			doc.setId("tagesschau-" + node.get("sophoraId").textValue());

			repository.add(doc);
		}
	}

	@Override
	public void ingestOnce(INewsRepository repository, Map<String, Object> attributes) {
		// TODO Auto-generated method stub

	}
}
