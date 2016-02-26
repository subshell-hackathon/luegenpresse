package luegenpresse;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.TaskScheduler;

import commons.luegenpresse.news.INewsRepository;
import luegenpresse.indexer.ts.TagesschauIndexer;

@SpringBootApplication
public class IndexerApplication {
	@Autowired
	private TagesschauIndexer tsIndexer;
	
	@Autowired
	private TaskScheduler scheduler;
	
	private INewsRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(IndexerApplication.class, args);
	}
	
	@PostConstruct
	private void init() {
		scheduler.scheduleWithFixedDelay(() -> tsIndexer.runPeriodically(repository), 1* 60 * 1000);
	}
}
