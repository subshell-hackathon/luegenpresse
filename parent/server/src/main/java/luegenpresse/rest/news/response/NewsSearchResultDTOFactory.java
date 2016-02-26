package luegenpresse.rest.news.response;

import java.util.stream.Collectors;

import luegenpresse.news.NewsDocument;
import luegenpresse.news.NewsResponse;

public class NewsSearchResultDTOFactory {
	private NewsSearchResultDTOFactory(){}
	
	public static NewsSearchResultDTO create(NewsResponse response) {
		NewsSearchResultDTO dto = new NewsSearchResultDTO();
		if (response != null && response.getDocuments()!=null) {			
			dto.setNews(response.getDocuments().stream()
					.map(NewsSearchResultDTOFactory::create)
					.collect(Collectors.toList()));
		}
		return dto;
	}
	
	private static NewsDTO create(NewsDocument doc) {
		NewsDTO newsDTO = new NewsDTO();
		newsDTO.setUrl(doc.getUrl());
		newsDTO.setHeadLine(doc.getHeadline());
		return newsDTO;
	}

}
