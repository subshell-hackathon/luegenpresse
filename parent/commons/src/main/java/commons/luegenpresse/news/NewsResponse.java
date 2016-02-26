package commons.luegenpresse.news;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class NewsResponse {
	
	//TODO: facets?
	
	private final ImmutableList<NewsDocument> documents;
	
	public NewsResponse(List<NewsDocument> documents) {
		this.documents = ImmutableList.copyOf(documents);
	}
	
	public ImmutableList<NewsDocument> getDocuments() {
		return documents;
	}

}
