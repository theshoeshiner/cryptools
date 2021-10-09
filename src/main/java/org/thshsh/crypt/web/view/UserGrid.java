package org.thshsh.crypt.web.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.UserRepository;
import org.thshsh.crypt.web.security.SecurityUtils;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class UserGrid extends AppEntityGrid<User, Long> {

	@Autowired
	UserRepository userRepo;
	
	public UserGrid() {
		super(User.class, UserDialog.class, FilterMode.Example);
		this.showButtonColumn = true;
		this.showEditButton = SecurityUtils.hasAccess(User.class, Access.ReadWrite);
		this.showDeleteButton = SecurityUtils.hasAccess(User.class, Access.ReadWriteDelete);
	}

	@Override
	public PagingAndSortingRepository<User, Long> getRepository() {
		return userRepo;
	}

	@Override
	public void setupColumns(Grid<User> grid) {
		
		grid
		.addColumn(User::getId)
		.setHeader("Id")
		.setWidth("100px")
		.setFlexGrow(0)
		;
		
		grid
		.addColumn(User::getDisplayName)
		.setHeader("Name");
		
		grid
		.addColumn(User::getUserName)
		.setHeader("Username")
		
		;
		
		grid
		.addColumn(User::getEmail)
		.setHeader("Email");
		
		grid
		.addColumn(User::getConfirmed)
		.setHeader("Confirmed")
		.setWidth("100px")
		.setFlexGrow(0)
		;
		
		
	}

	@Override
	public Dialog createDialog(User entity) {
		UserDialog cd = appCtx.getBean(UserDialog.class,entity);
		return cd;
	}

	
	
}
