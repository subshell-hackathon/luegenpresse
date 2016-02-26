package commons.luegenpresse.news;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

/**
 * In memory implementation for testing purposes
 */
public class NewsRepositoryInMemory implements INewsRepository {
	
	private final HashMap<String, NewsDocument> news = new HashMap<>();

	@Override
	public void add(NewsDocument doc) {
		news.put(doc.getId(), doc);
	}

	@Override
	public void delete(String id) {
		news.remove(id);
	}

	@Override
	public NewsResponse findBy(NewsRequest request) {
		Stream<NewsDocument> stream = news.values().stream();
		if (request.getText()!=null) {
			stream = stream.filter(news -> 
			StringUtils.equalsIgnoreCase(request.getText(), news.getFullText())||
			StringUtils.equalsIgnoreCase(request.getText(), news.getShortText())||
			StringUtils.equalsIgnoreCase(request.getText(), news.getHeadLine())
			);
		}
		if (request.getDate() != null) {
			stream = stream.filter(news -> request.getDate().equals(news.getDate()));
		}
		List<NewsDocument> newslist = stream.collect(Collectors.toList());
		NewsResponse resp = new NewsResponse(newslist);
		return resp;
	}

}
