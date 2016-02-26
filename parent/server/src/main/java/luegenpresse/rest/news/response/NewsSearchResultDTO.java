package luegenpresse.rest.news.response;

import java.util.List;

public class NewsSearchResultDTO {
	
	private List<NewsDTO> news;
	
	//TODO: facet hits, meta?
	public List<NewsDTO> getNews() {
		return news;
	}
	
	public void setNews(List<NewsDTO> news) {
		this.news = news;
	}

}
