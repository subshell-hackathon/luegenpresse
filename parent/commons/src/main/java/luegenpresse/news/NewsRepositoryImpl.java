package luegenpresse.news;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.DisMaxParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrCallback;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.SolrResultPage;
import org.springframework.stereotype.Component;

@Component
public class NewsRepositoryImpl implements INewsRepository {

	@Autowired
	private INewsSolrRepo repo;

	@Autowired
	private SolrTemplate template;

	@Override
	public void add(NewsDocument doc) {
		repo.save(doc);
	}

	@Override
	public void delete(String id) {
		repo.delete(id);
	}

	@Override
	public NewsResponse findBy(NewsRequest request) {
		final SolrQuery query = new SolrQuery();
		  // edismax query setup
		  query.set("q", request.getText());
		  query.set(DisMaxParams.QF, "keywords^20.0 headline^2.5 ");
		  query.set("defType", "edismax");

		  // paging
		  query.setStart(0);
		  query.setRows(20);
		  Page<NewsDocument> page = template.execute(new SolrCallback<Page<NewsDocument>>() {
		   @Override
		   public Page<NewsDocument> doInSolr(SolrServer solr) throws SolrServerException, IOException {
		       final QueryResponse resp = solr.query(query);
		       final List<NewsDocument> beans = template.convertQueryResponseToBeans(resp, NewsDocument.class);
		       return new SolrResultPage<NewsDocument>(beans);
		   }
		  });
		  return new NewsResponse(page.getContent());
	}

}
