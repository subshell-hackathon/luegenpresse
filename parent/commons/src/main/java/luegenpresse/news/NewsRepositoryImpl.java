package luegenpresse.news;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NewsRepositoryImpl implements INewsRepository {
	
	@Autowired
	private INewsSolrRepo repo;

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
		return null;
	}

}
