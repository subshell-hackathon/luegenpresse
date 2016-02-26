package luegenpresse;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import commons.luegenpresse.news.INewsRepository;
import commons.luegenpresse.news.NewsRepositoryInMemory;
import luegenpresse.indexer.ts.TagesschauIndexer;

@SpringBootApplication
public class IndexerApplication {
	@Autowired
	private TagesschauIndexer tsIndexer;
	
	private ThreadPoolTaskScheduler scheduler;
	
	private INewsRepository repository = new NewsRepositoryInMemory();

	public static void main(String[] args) {
		SpringApplication.run(IndexerApplication.class, args);
	}
	
	@PostConstruct
	private void init() {
		scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(1);
		scheduler.setThreadNamePrefix("IndexerScheduler-");
		scheduler.initialize();

		scheduler.scheduleWithFixedDelay(() -> tsIndexer.runPeriodically(repository), 1* 60 * 1000);
	}
	
	@PreDestroy
	private void destroy() {
		scheduler.shutdown();
	}
}
