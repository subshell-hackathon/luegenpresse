package luegenpresse.rest.news.response;

import java.util.stream.Collectors;

import commons.luegenpresse.news.NewsDocument;
import commons.luegenpresse.news.NewsResponse;

public class NewsSearchResultDTOFactory {
	private NewsSearchResultDTOFactory(){}
	
	public static NewsSearchResultDTO create(NewsResponse response) {
		NewsSearchResultDTO dto = new NewsSearchResultDTO();
		dto.setNews(response.getDocuments().stream()
				.map(NewsSearchResultDTOFactory::create)
				.collect(Collectors.toList()));
		return dto;
	}
	
	private static NewsDTO create(NewsDocument doc) {
		NewsDTO newsDTO = new NewsDTO();
		newsDTO.setUrl(doc.getUrl());
		//TODO: somethin more?
		return newsDTO;
	}

}
