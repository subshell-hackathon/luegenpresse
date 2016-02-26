package luegenpresse.indexer;

import commons.luegenpresse.news.INewsRepository;

public interface IIndexer {
	public void run(INewsRepository repository);
}
