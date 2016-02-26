package luegenpresse.news;

import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Component;

public interface INewsSolrRepo extends SolrCrudRepository<NewsDocument, String> {

}
