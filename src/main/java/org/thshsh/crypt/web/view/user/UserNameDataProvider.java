package org.thshsh.crypt.web.view.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.UserRepository;
import org.vaadin.artur.spring.dataprovider.PageableDataProvider;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;

@SuppressWarnings("serial")
@Component()
@Scope("prototype")
final class UserNameDataProvider extends PageableDataProvider<User, String> {

	@Autowired
	UserRepository userRepo;

	UserNameDataProvider() {}
	

	@Override
	protected Page<User> fetchFromBackEnd(Query<User, String> q, Pageable p) {
		return userRepo.findByDisplayNameContainsIgnoreCase(q.getFilter().orElse(""), p);
	}

	@Override
	protected List<QuerySortOrder> getDefaultSortOrders() {
		return QuerySortOrder.asc("displayName").build();
	}

	@Override
	protected int sizeInBackEnd(Query<User, String> q) {
		return userRepo.countByDisplayNameContainsIgnoreCase(q.getFilter().orElse(""));
	}
}