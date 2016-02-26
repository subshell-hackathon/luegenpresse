package commons.luegenpresse.news;

public interface INewsRepository {
	
	void add(NewsDocument doc);
	
	void delete(String id);
	
	NewsResponse findBy(NewsRequest request);

}
