package luegenpresse.indexer.ts;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
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

public class NDRIndexer implements IIndexer {
	private URL feedUrl;
	private static Logger log = LoggerFactory.getLogger(NDRIndexer.class);
	private static Set<String> paragraphTypes = new HashSet<>(Arrays.asList("text", "h3"));

	public NDRIndexer(URL rssFeedUrl) {
		if (rssFeedUrl == null) {
			throw new IllegalArgumentException("rssFeedUrl must not be null.");
		}
		feedUrl = rssFeedUrl;
	}

	@Override
	public void runPeriodically(INewsRepository repository) {
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed;
		try {
			feed = input.build(new XmlReader(feedUrl));
		} catch (IllegalArgumentException | FeedException | IOException e1) {
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		for (SyndEntry syndEntry : feed.getEntries()) {
			String link = syndEntry.getLink();
			if (!link.startsWith("http://www.ndr.de/")) {
				// Do not process external links.
				continue;
			}
			link = link.replaceFirst(".html\\z", "-app.json");
			JsonNodeWrapper node;
			try {
				node = new JsonNodeWrapper(mapper.readTree(new URL(link)));
			} catch (IOException e) {
				// Ignore it if we cannot read it.
				log.info("Cannot read or parse '" + link + "'.", e);
				continue;
			}

			NewsDocumentBuilder docBuilder = NewsDocument.builder();

			node.get("t").ifPresent(value -> docBuilder.date(new DateTime(value.getJsonNode().asLong() * 1000L)));
			node.get("id").ifPresent(value -> docBuilder.id("ndr-" + value.getJsonNode().textValue()));
			node.get("h1").ifPresent(value -> docBuilder.headline(value.getJsonNode().textValue()));
			node.get("text").ifPresent(value -> docBuilder.shortText(value.getJsonNode().textValue()));
			docBuilder.url(syndEntry.getLink());
			docBuilder.source("NDR");

			StringBuilder copytext = new StringBuilder();
			node.get("content").ifPresent(value -> {
				Iterator<JsonNode> paragraphs = value.getJsonNode().elements();
				while (paragraphs.hasNext()) {
					JsonNode paragraph = paragraphs.next();
					JsonNode paragraphType = paragraph.get("type");
					if (paragraphType == null || !paragraphTypes.contains(paragraphType.textValue())) {
						continue;
					}
					String paragraphText = paragraph.get("content").textValue();
					paragraphText = stripTags(paragraphText);
					copytext.append(paragraphText);
					copytext.append("\n");
				}
				docBuilder.fullText(copytext.toString());
			});

//			node.get("images").ifPresent(images -> 
//			images.get(0).ifPresent(image -> 
//				image.get("variants").ifPresent(variants ->
//					variants.getJsonNode().elements().forEachRemaining(variant -> {
//						JsonNode videowebs = variant.get("videowebs");
//						if (videowebs != null) {
//							docBuilder.imageUrl(videowebs.textValue());
//						}
//					}))));

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
