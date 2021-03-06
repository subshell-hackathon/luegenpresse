package luegenpresse.news;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;

@Configuration
public class SolrConfig {
	
	@Value("${spring.data.solr.host}")
	private String solrUrl;
	
	@Bean
	public SolrServer solrServer() {
		return new HttpSolrServer(solrUrl);
	}

	@Bean
	public SolrTemplate solrTemplate() {
		return new SolrTemplate(solrServer(), "news");
	}
}
