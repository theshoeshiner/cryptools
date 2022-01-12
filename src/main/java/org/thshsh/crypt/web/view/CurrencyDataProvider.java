package org.thshsh.crypt.web.view;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.repo.CurrencyRepository;
import org.vaadin.artur.spring.dataprovider.PageableDataProvider;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;

@SuppressWarnings("serial")
@Component()
@Scope("prototype")
public class CurrencyDataProvider extends PageableDataProvider<Currency, String> {

	public static final Logger LOGGER = LoggerFactory.getLogger(CurrencyDataProvider.class);

	@Autowired
	protected CurrencyRepository repo;

	

	@Override
	protected Page<Currency> fetchFromBackEnd(Query<Currency, String> q, Pageable p) {
		String f = q.getFilter().orElse("").toLowerCase();
		//Page<T> page = repo.findBySymbolContainsIgnoreCaseOrNameContainsIgnoreCase(f,f, p);
		Page<Currency> page = repo.findByString(f, p);
		LOGGER.info("page: {}",page.getSize());
		return page;
	}

	@Override
	protected List<QuerySortOrder> getDefaultSortOrders() {
		//return QuerySortOrder.asc("grade").thenAsc("name").build();
		return QuerySortOrder.desc("rank").thenAsc("name").build();
		
	}

	@Override
	protected int sizeInBackEnd(Query<Currency, String> q) {
		String f = q.getFilter().orElse("").toLowerCase();
		//int count = repo.countBySymbolContainsIgnoreCaseOrNameContainsIgnoreCase(f,f);
		int count = repo.countByString(f);
		LOGGER.info("count: {}",count);
		return count;
	}
}