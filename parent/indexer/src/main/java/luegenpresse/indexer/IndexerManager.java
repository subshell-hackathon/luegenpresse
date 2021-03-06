package luegenpresse.indexer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import luegenpresse.indexer.generic.GenericHtmlIndexer;
import luegenpresse.indexer.ndr.NDRIndexer;
import luegenpresse.indexer.ts.TagesschauIndexer;
import luegenpresse.news.INewsRepository;

@Component
public class IndexerManager {

	private static int INDEXER_DELAY_MILLIS = 1 * 60 * 1000;

	@Autowired
	private INewsRepository repo;

	private ThreadPoolTaskScheduler scheduler;

	private Set<IIndexer> indexers;

	@PostConstruct
	private void init() throws MalformedURLException {
		// Generic one-time indexers
		GenericHtmlIndexer htmlIndexer = new GenericHtmlIndexer();
		
		Map<String, String> articles = new LinkedHashMap<>();
				articles.put("http://www.zeit.de/politik/deutschland/2015-11/bundeskriminalamt-fluechtlinge-deutsche-straftaten-vergleich", "Zeit.de");
				articles.put("http://www.sueddeutsche.de/politik/geruechte-geruechte-ueber-fluechtlinge-wut-sucht-wahrheit-1.2836977", "Süddeutsche Zeitung");
				articles.put("http://www.tagesschau.de/inland/fluechtlinge-geruechte-101.html", "tagesschau.de");
				articles.put("https://www.tagesschau.de/inland/fluechtlinge-kriminalitaet-101.html", "tagesschau.de");
				articles.put("https://www.ndr.de/fernsehen/sendungen/zapp/Demo-vorm-Funkhaus-Wo-luegt-die-Luegenpresse,swr208.html", "ndr.de");
				articles.put("http://www.tagesschau.de/inland/sachsen-tillich-101.html", "tagesschau.de");
				articles.put("http://blog.tagesschau.de/2015/08/24/unsere-haltung-beim-thema-fluechtlinge/", "blog.tagesschau.de");
				articles.put("https://www.ndr.de/fernsehen/sendungen/zapp/Wir-versuchen-rauszufinden-was-ist,reschke334.html", "ndr.de");
				articles.put("http://www.sueddeutsche.de/politik/fluechtlinge-umfrage-viele-kommunen-fuehlen-sich-wegen-fluechtlingen-nicht-ueberfordert-1.2881199", "Süddeutsche Zeitung");
				articles.put("http://www.sueddeutsche.de/politik/geruechte-geruechte-ueber-fluechtlinge-wut-sucht-wahrheit-1.2836977", "Süddeutsche Zeitung");
				articles.put("http://www.tagesschau.de/inland/fluechtlinge-geruechte-101.html", "tagesschau.de");
		
		articles.entrySet().forEach(article -> {
			Map<String, Object> attributes = new HashMap<>();
			attributes.put("url", article.getKey());
			attributes.put("source", article.getValue());
			htmlIndexer.ingestOnce(repo, attributes);
		});
		
		// Regular indexers
		indexers = new HashSet<IIndexer>(Arrays.asList(
				new TagesschauIndexer(),
				new NDRIndexer(new URL("http://www.ndr.de/home/index-rss.xml")),
				new NDRIndexer(new URL("http://www.ndr.de/nachrichten/niedersachsen/index-rss.xml")),
				new NDRIndexer(new URL("http://www.ndr.de/nachrichten/schleswig-holstein/index-rss.xml")),
				new NDRIndexer(new URL("http://www.ndr.de/nachrichten/mecklenburg-vorpommern/index-rss.xml")),
				new NDRIndexer(new URL("http://www.ndr.de/nachrichten/hamburg/index-rss.xml")))
				);
		 
		scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(1);
		scheduler.setThreadNamePrefix("IndexerScheduler-");
		scheduler.initialize();

		indexers.forEach(indexer -> scheduler.scheduleWithFixedDelay(() -> indexer.runPeriodically(repo), INDEXER_DELAY_MILLIS));
	}

	@PreDestroy
	private void destroy() {
		scheduler.shutdown();
	}
}
