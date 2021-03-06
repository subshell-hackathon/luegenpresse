package luegenpresse.rest.news;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import luegenpresse.news.INewsRepository;
import luegenpresse.news.NewsResponse;
import luegenpresse.rest.news.request.NewsRequestDTO;
import luegenpresse.rest.news.request.NewsRequestFactory;
import luegenpresse.rest.news.response.NewsSearchResultDTO;
import luegenpresse.rest.news.response.NewsSearchResultDTOFactory;

@RestController
@RequestMapping(NewsResource.NEWS_PATH)
public class NewsResource {
	
	@Autowired
	protected INewsRepository newsRepo;
	
	public static final String NEWS_PATH = "/news";
	
	@RequestMapping(value="/find", method=RequestMethod.POST)
	public NewsSearchResultDTO findNews(@RequestBody NewsRequestDTO request) {
		NewsResponse found = newsRepo.findBy(NewsRequestFactory.create(request));
		NewsSearchResultDTO response = NewsSearchResultDTOFactory.create(found);
		return response;
	}
	
	@RequestMapping(value="/find/{buzzword}", method=RequestMethod.GET)
	public NewsSearchResultDTO findNewsByBuzzword(@PathVariable String buzzword){
		NewsResponse found = newsRepo.findBy(NewsRequestFactory.create(buzzword));
		NewsSearchResultDTO response = NewsSearchResultDTOFactory.create(found);
		return response;
	}
	
}
