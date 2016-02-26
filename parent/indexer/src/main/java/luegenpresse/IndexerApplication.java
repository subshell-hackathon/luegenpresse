package luegenpresse;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import luegenpresse.indexer.ts.TagesschauIndexer;
import luegenpresse.news.INewsRepository;
import luegenpresse.news.NewsDocument;
import luegenpresse.news.NewsRepositoryInMemory;

@SpringBootApplication
public class IndexerApplication {
	@Autowired
	private TagesschauIndexer tsIndexer;
	
	private ThreadPoolTaskScheduler scheduler;
	
	public static void main(String[] args) {
		SpringApplication.run(IndexerApplication.class, args);
	}
	
	@PostConstruct
	private void init() {
		scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(1);
		scheduler.setThreadNamePrefix("IndexerScheduler-");
		scheduler.initialize();

		scheduler.scheduleWithFixedDelay(() -> tsIndexer.runPeriodically(repo), 1* 60 * 1000);
	}
	
	@PreDestroy
	private void destroy() {
		scheduler.shutdown();
	}
	
	@Autowired
	private INewsRepository repo;
	
	@PostConstruct
	public void start() {
		NewsDocument doc = NewsDocument.builder().date(new DateTime()).id("test100").build();
		repo.add(doc);
	}
}
