package luegenpresse.rest.news.response;

public class NewsDTO {
	
	private String url;
	private String headLine;
	//TODO: teaser?
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getHeadLine() {
		return headLine;
	}
	
	public void setHeadLine(String headLine) {
		this.headLine = headLine;
	}

}
