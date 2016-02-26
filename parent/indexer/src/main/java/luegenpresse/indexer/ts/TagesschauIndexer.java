package luegenpresse.indexer.ts;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import luegenpresse.indexer.IIndexer;
import luegenpresse.indexer.JsonNodeWrapper;
import luegenpresse.news.INewsRepository;
import luegenpresse.news.NewsDocument;
import luegenpresse.news.NewsDocument.NewsDocumentBuilder;

public class TagesschauIndexer implements IIndexer {
	private URL tsFeedUrl;
	private static Logger log = LoggerFactory.getLogger(TagesschauIndexer.class);

	public TagesschauIndexer() {
		try {
			tsFeedUrl = new URL("http://www.tagesschau.de/xml/rss2");
		} catch (MalformedURLException e) {
			log.error("Error in URL. This should not happen.", e);
		}
	}

	@Override
	public void runPeriodically(INewsRepository repository) {
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed;
		try {
			feed = input.build(new XmlReader(tsFeedUrl));
		} catch (IllegalArgumentException | FeedException | IOException e1) {
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		for (SyndEntry syndEntry : feed.getEntries()) {
			String link = syndEntry.getLink();
			if (!link.startsWith("http://www.tagesschau.de/")) {
				// Do not process external links.
				continue;
			}
			link = link.replace("http://www.tagesschau.de/", "http://www.tagesschau.de/api/");
			link = link.replaceFirst(".html\\z", ".json");
			JsonNodeWrapper node;
			try {
				node = new JsonNodeWrapper(mapper.readTree(new URL(link)));
			} catch (IOException e) {
				// Ignore it if we cannot read it.
				log.info("Cannot read or parse '" + link + "'.", e);
				continue;
			}

			NewsDocumentBuilder docBuilder = NewsDocument.builder();

			node.get("date").ifPresent(value -> {
				DateTime jsonParsedDate = ISODateTimeFormat.dateTime().parseDateTime(value.textValue());
				docBuilder.date(jsonParsedDate);
			});
			node.get("sophoraId").ifPresent(value -> docBuilder.id("tagesschau-" + value.textValue()));
			List<String> headlineParts = new ArrayList<>();
			node.get("topline").ifPresent(value -> headlineParts.add(value.textValue()));
			node.get("headline").ifPresent(value -> headlineParts.add(value.textValue()));
			String headline = StringUtils.join(headlineParts, " - ");
			if (StringUtils.isNotBlank(headline)) {
				docBuilder.headline(headline);
			}
			node.get("shorttext").ifPresent(value -> docBuilder.shortText(value.textValue()));
			node.get("detailsWeb").ifPresent(value -> docBuilder.url(value.textValue()));
			docBuilder.source("Tagesschau");

			StringBuilder copytext = new StringBuilder();
			node.get("copytext").ifPresent( value -> {
				Iterator<JsonNode> paragraphs = value.elements();
				while (paragraphs.hasNext()) {
					JsonNode paragraph = paragraphs.next();
					String paragraphText = paragraph.get("text").textValue();
					paragraphText = stripTags(paragraphText);
					copytext.append(paragraphText);
					copytext.append("\n");
				}
				docBuilder.fullText(copytext.toString());
			});

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
