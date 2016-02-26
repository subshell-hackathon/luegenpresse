package commons.luegenpresse.news;

import java.util.Set;

import org.apache.solr.client.solrj.beans.Field;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName="news")
public class NewsDocument {

	@Id
	private String id;
	
	private String url;
	private String source;
	private DateTime date;
	private Set<String> keywords;
	private Set<String> geoTags;
	private String headLine;
	private String shortText;
	private String fullText;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	public DateTime getDate() {
		return date;
	}
	
	public void setDate(DateTime date) {
		this.date = date;
	}

	public Set<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}

	public Set<String> getGeoTags() {
		return geoTags;
	}

	public void setGeoTags(Set<String> geoTags) {
		this.geoTags = geoTags;
	}

	public String getHeadLine() {
		return headLine;
	}

	public void setHeadLine(String headLine) {
		this.headLine = headLine;
	}

	public String getShortText() {
		return shortText;
	}

	public void setShortText(String shortText) {
		this.shortText = shortText;
	}

	public String getFullText() {
		return fullText;
	}

	public void setFullText(String fullText) {
		this.fullText = fullText;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NewsDocument other = (NewsDocument) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NewsDocument [id=" + id + "]";
	}

}
