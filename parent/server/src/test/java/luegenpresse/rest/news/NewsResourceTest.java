package luegenpresse.rest.news;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

import commons.luegenpresse.news.INewsRepository;
import commons.luegenpresse.news.NewsDocument;
import commons.luegenpresse.news.NewsRepositoryInMemory;
import luegenpresse.rest.news.request.NewsRequestDTO;
import luegenpresse.rest.news.response.NewsSearchResultDTO;

public class NewsResourceTest {
	
	public INewsRepository repo = new NewsRepositoryInMemory();
	public NewsResource uut = new NewsResource();
	
	@Before
	public void setup() {
		uut.newsRepo = repo;
	}
	
	@Test
	public void testSimpleResponse() {
		NewsDocument doc = NewsDocument.builder().id("1").fullText("Test").url("http://www.test.orc").build();
		repo.add(doc);
		
		NewsRequestDTO request = new NewsRequestDTO();;
		request.setText("Test");
		NewsSearchResultDTO response = uut.findNews(request);
		
		assertThat(response.getNews(), hasSize(1));
		assertThat(response.getNews().get(0).getUrl(), is(doc.getUrl()));
	}
	
}
