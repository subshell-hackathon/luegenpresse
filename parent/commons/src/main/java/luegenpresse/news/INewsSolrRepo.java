package luegenpresse.news;

import org.springframework.data.solr.repository.SolrCrudRepository;

public interface INewsSolrRepo extends SolrCrudRepository<NewsDocument, String> {
	
}
