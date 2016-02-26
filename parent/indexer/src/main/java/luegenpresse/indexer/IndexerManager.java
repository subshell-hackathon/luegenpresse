package luegenpresse.indexer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import luegenpresse.indexer.ts.NDRIndexer;
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
		indexers = new HashSet<IIndexer>(Arrays.asList(
//					new TagesschauIndexer(),
					new NDRIndexer(new URL("http://www.ndr.de/nachrichten/niedersachsen/index-rss.xml")))
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
