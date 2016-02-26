package luegenpresse.news;

import java.util.Set;

import org.apache.solr.client.solrj.beans.Field;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@SolrDocument(solrCoreName="news")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id")
@ToString
public class NewsDocument {

	@Id
	private String id;
	@Field
	private String url;
	@Field
	private String source;
	@Field
	private DateTime date;
	@Field
	private Set<String> keywords;
	@Field
	private Set<String> geoTags;
	@Field
	private String headLine;
	@Field
	private String shortText;
	@Field
	private String fullText;
	
}
