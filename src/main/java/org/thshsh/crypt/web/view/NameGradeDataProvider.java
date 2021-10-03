package org.thshsh.crypt.web.view;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.repo.HasNameAndGradeRepository;
import org.thshsh.crypt.repo.HasNameRepository;
import org.vaadin.artur.spring.dataprovider.PageableDataProvider;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;

@SuppressWarnings("serial")
@Component()
@Scope("prototype")
public class NameGradeDataProvider<T> extends PageableDataProvider<T, String> {

	protected HasNameAndGradeRepository<T> repo;

	NameGradeDataProvider(HasNameAndGradeRepository<T> repo) {
		this.repo = repo;
	}

	@Override
	protected Page<T> fetchFromBackEnd(Query<T, String> q, Pageable p) {
		return repo.findByNameContainsIgnoreCase(q.getFilter().orElse(""), p);
	}

	@Override
	protected List<QuerySortOrder> getDefaultSortOrders() {
		return QuerySortOrder.asc("name").build();
	}

	@Override
	protected int sizeInBackEnd(Query<T, String> q) {
		return repo.countByNameContainsIgnoreCase(q.getFilter().orElse(""));
	}
	
}
