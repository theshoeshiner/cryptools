package org.thshsh.crypt.web.view;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.vaadin.artur.spring.dataprovider.PageableDataProvider;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;

@SuppressWarnings("serial")
@Component()
@Scope("prototype")
public class HasSymbolDataProvider<T> extends PageableDataProvider<T, String> {

	public static final Logger LOGGER = LoggerFactory.getLogger(HasSymbolDataProvider.class);

	protected HasSymbolRepository<T> repo;

	HasSymbolDataProvider(HasSymbolRepository<T> repo) {
		this.repo = repo;
	}

	@Override
	protected Page<T> fetchFromBackEnd(Query<T, String> q, Pageable p) {
		String f = q.getFilter().orElse("").toLowerCase();
		//Page<T> page = repo.findBySymbolContainsIgnoreCaseOrNameContainsIgnoreCase(f,f, p);
		Page<T> page = repo.findByString(f, p);
		LOGGER.info("page: {}",page.getSize());
		return page;
	}

	@Override
	protected List<QuerySortOrder> getDefaultSortOrders() {
		return QuerySortOrder.asc("name").build();
	}

	@Override
	protected int sizeInBackEnd(Query<T, String> q) {
		String f = q.getFilter().orElse("").toLowerCase();
		//int count = repo.countBySymbolContainsIgnoreCaseOrNameContainsIgnoreCase(f,f);
		int count = repo.countByString(f);
		LOGGER.info("count: {}",count);
		return count;
	}
}