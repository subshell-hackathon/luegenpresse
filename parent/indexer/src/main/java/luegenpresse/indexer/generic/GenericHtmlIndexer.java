package luegenpresse.indexer.generic;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import luegenpresse.indexer.IIndexer;
import luegenpresse.news.INewsRepository;
import luegenpresse.news.NewsDocument;
import luegenpresse.news.NewsDocument.NewsDocumentBuilder;

public class GenericHtmlIndexer implements IIndexer {
	private final class ArticleContentExtractor implements NodeVisitor {
		private StringBuilder content = new StringBuilder();
		private final Set<String> STOPWORDS = Sets.newHashSet("kommentieren", "Kommentieren", "Kommentare");
		private boolean stopParsing = false;
		
		@Override
		public void head(Node node, int depth) {
			if (node instanceof Element) {
				if (stopParsing) {
					return;
				}
				Element e = (Element) node;
				if (STOPWORDS.contains(e.text())) {
					stopParsing = true;
					return;
				}
				if ("p".equals(e.tagName())) {
					content.append(e.text());
					content.append("\n");
				}
			}
		}

		@Override
		public void tail(Node node, int depth) {
		}
		
		public String getArticleBody() {
			return content.toString();
		}
	}

	private static Logger log = LoggerFactory.getLogger(GenericHtmlIndexer.class);

	public GenericHtmlIndexer() {
	}

	@Override
	public void runPeriodically(INewsRepository repository) {
	}

	private String stripTags(String paragraphText) {
		return Jsoup.clean(paragraphText, Whitelist.none());
	}

	@Override
	public void ingestOnce(INewsRepository repository, Map<String, Object> attributes) {
		String urlString = (String) attributes.get("url");
		String source = (String) attributes.get("source");
		
		if (StringUtils.isBlank(urlString)) {
			log.error("url attribute is empty, no document indexed.");
			return;
		}
		
		URL url;
		try {
			url = new URL(urlString);
			Document doc = Jsoup.parse(url, 20 * 1000);
			
			NewsDocumentBuilder docBuilder = NewsDocument.builder();
			docBuilder.url(urlString);
			docBuilder.id(urlString);
			
			// Try retrieving OpenGraph information.
			Elements date = doc.select("head > meta[name=date]");
			if (date.isEmpty()) {
				date = doc.select("head > meta[name=last-modified]");
			}
			if (date.isEmpty()) {
				log.error("No OpenGraph date found.");
				return;
			}
			String dateString = date.get(0).attr("content");
			DateTime parsedDate = null;
			try {
				parsedDate = ISODateTimeFormat.dateTimeNoMillis().parseDateTime(dateString);
			} catch (IllegalArgumentException e) {
			}
			if (parsedDate == null) {
				try {
					parsedDate = DateTime.parse(dateString);
				} catch (IllegalArgumentException e) {
				}
			}
			if (parsedDate == null) {
				try {
					parsedDate = DateTimeFormat.forPattern("E, d MMM yyyy H:m:s z").parseDateTime(dateString);
				} catch (IllegalArgumentException e) {
				}
			}
			if (parsedDate == null) {
				// Fall back to a universal date parser.
				Parser parser = new Parser();
				List<DateGroup> groups = parser.parse(dateString);
				if (!groups.isEmpty() && !groups.get(0).getDates().isEmpty()) {
					parsedDate = new DateTime(groups.get(0).getDates().get(0));
				}

			}
			if (parsedDate == null) {
				log.error("Unable to parse OpenGraph date.");
				return;
			}
			docBuilder.date(parsedDate);

			Elements title = doc.select("head > meta[property=og:title]");
			if (!title.isEmpty()) {
				docBuilder.headline(title.get(0).attr("content"));
			}
			Elements description = doc.select("head > meta[property=og:description]");
			if (!description.isEmpty()) {
				docBuilder.shortText(description.get(0).attr("content"));
			}
			Elements image = doc.select("head > meta[property=og:image]");
			if (!image.isEmpty()) {
				docBuilder.imageUrl(image.get(0).attr("content"));
			}			
			
			Elements siteName = doc.select("head > meta[property=og:site_name]");
			if (!siteName.isEmpty()) {
				source = siteName.get(0).attr("content");
			}			
			if (StringUtils.isBlank(source)) {
				log.error("source attribute is not set, no document indexed.");
				return;
			}
			docBuilder.source(source);
			
			ArticleContentExtractor visitor = new ArticleContentExtractor();
			doc.traverse(visitor);
			docBuilder.fullText(visitor.getArticleBody());

			NewsDocument document = docBuilder.build();
			log.debug("Adding document " + document);
			repository.add(document);
		} catch (IOException e) {
			log.error("Exception while retrieving or parsing document.", e);
		}
		
	}
}
