package org.thshsh.crypt.web.view;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.thshsh.cryptman.HasNameRepository;
import org.vaadin.artur.spring.dataprovider.PageableDataProvider;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;

@SuppressWarnings("serial")
@Component()
@Scope("prototype")
public class HasSymbolDataProvider<T> extends PageableDataProvider<T, String> {

	protected HasSymbolRepository<T> repo;

	HasSymbolDataProvider(HasSymbolRepository<T> repo) {
		this.repo = repo;
	}

	@Override
	protected Page<T> fetchFromBackEnd(Query<T, String> q, Pageable p) {
		return repo.findBySymbolContainsIgnoreCase(q.getFilter().orElse(""), p);
	}

	@Override
	protected List<QuerySortOrder> getDefaultSortOrders() {
		return QuerySortOrder.asc("name").build();
	}

	@Override
	protected int sizeInBackEnd(Query<T, String> q) {
		return repo.countBySymbolContainsIgnoreCase(q.getFilter().orElse(""));
	}
}