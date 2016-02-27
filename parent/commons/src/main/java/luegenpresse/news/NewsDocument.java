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
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@SolrDocument
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id")
@ToString
public class NewsDocument {

	@Id
	@NonNull
	private String id;
	@Field
	@NonNull
	private String url;
	@Field
	@NonNull
	private String source;
	@Field
	@NonNull
	private	DateTime date;
	@Field
	private Set<String> keywords;
	@Field
	private Set<String> geoTags;
	@Field
	private String headline;
	@Field
	private String shortText;
	@Field
	private String fullText;
	@Field
	private String imageUrl;
}
