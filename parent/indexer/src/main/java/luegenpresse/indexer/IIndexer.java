package luegenpresse.indexer;

import java.util.Map;

import commons.luegenpresse.news.INewsRepository;

public interface IIndexer {
	/**
	 * Called periodically. Implementors should read their sources and add corresponding
	 * documents to the given repository.
	 */
	public void runPeriodically(INewsRepository repository);
	
	/**
	 * Called on user request. Implementors should read in a source and add corresponding
	 * documents to the given repository.
	 * @param attributes the attributes given by the user
	 */
	public void ingestOnce(INewsRepository repository, Map<String, Object> attributes);
}
