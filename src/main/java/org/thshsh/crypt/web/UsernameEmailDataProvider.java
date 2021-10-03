package org.thshsh.crypt.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class UsernameEmailDataProvider extends PageableDataProvider<User, String> {

	public static final Logger LOGGER = LoggerFactory.getLogger(UsernameEmailDataProvider.class);

	@Autowired
	protected UserRepository repo;

	UsernameEmailDataProvider() {

	}

	@Override
	protected Page<User> fetchFromBackEnd(Query<User, String> q, Pageable p) {
		String f = q.getFilter().orElse("").toLowerCase();
		Page<User> page = repo.findByUserNameContainsOrEmailContains(f,f, p);
		return page;
	}

	@Override
	protected List<QuerySortOrder> getDefaultSortOrders() {
		return QuerySortOrder.asc("userName").build();
	}

	@Override
	protected int sizeInBackEnd(Query<User, String> q) {
		String f = q.getFilter().orElse("").toLowerCase();
		int count = repo.countByUserNameContainsOrEmailContains(f,f);
		return count;
	}
}