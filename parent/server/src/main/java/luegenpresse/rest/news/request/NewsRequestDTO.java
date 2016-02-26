package luegenpresse.rest.news.request;

import org.joda.time.DateTime;

public class NewsRequestDTO {

	private DateTime date;
	private String text;
	private String source;

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
