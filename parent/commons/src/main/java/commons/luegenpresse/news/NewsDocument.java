package commons.luegenpresse.news;

import java.util.Set;

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
	private String url;
	private String source;
	private DateTime date;
	private Set<String> keywords;
	private Set<String> geoTags;
	private String headLine;
	private String shortText;
	private String fullText;
	
}
