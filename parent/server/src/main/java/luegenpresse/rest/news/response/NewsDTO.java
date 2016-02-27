package luegenpresse.rest.news.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"source", "headline"})
public class NewsDTO {
	
	private String url;
	private String headline;
	private String source;
	private String imageUrl;
	private String shortText;
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public String getHeadline() {
		return headline;
	}
	
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public String getShortText() {
		return shortText;
	}
	
	public void setShortText(String shortText) {
		this.shortText = shortText;
	}

}
