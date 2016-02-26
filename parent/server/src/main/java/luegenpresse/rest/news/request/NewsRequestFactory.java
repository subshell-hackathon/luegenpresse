package luegenpresse.rest.news.request;

import luegenpresse.news.NewsRequest;

public class NewsRequestFactory {
	private NewsRequestFactory(){}
	
	public static NewsRequest create(NewsRequestDTO dto) {
		NewsRequest request = new NewsRequest();
		request.setDate(dto.getDate());
		request.setText(dto.getText());
		request.setSource(dto.getSource());
		return request;
	}
	
	public static NewsRequest create(String buzzword) {
		NewsRequest request = new NewsRequest();
		request.setText(buzzword);
		return request;
	}

}
