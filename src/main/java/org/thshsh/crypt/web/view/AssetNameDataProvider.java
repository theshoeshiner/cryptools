package org.thshsh.crypt.web.view;

import java.util.List;

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
final class AssetNameDataProvider extends PageableDataProvider<Currency, String> {

	@Autowired
	CurrencyRepository repo;


	AssetNameDataProvider() {}


	@Override
	protected Page<Currency> fetchFromBackEnd(Query<Currency, String> q, Pageable p) {
		return repo.findByNameContainsIgnoreCase(q.getFilter().orElse(""), p);
	}

	@Override
	protected List<QuerySortOrder> getDefaultSortOrders() {
		return QuerySortOrder.asc("name").build();
	}

	@Override
	protected int sizeInBackEnd(Query<Currency, String> q) {
		return repo.countByNameContainsIgnoreCase(q.getFilter().orElse(""));
	}
}