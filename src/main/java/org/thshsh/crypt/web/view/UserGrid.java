package org.thshsh.crypt.web.view;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Activity;
import org.thshsh.crypt.ActivityType;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.ActivityRepository;
import org.thshsh.crypt.repo.UserRepository;
import org.thshsh.crypt.web.security.SecurityUtils;
import org.thshsh.vaadin.ZonedDateTimeRenderer;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class UserGrid extends AppEntityGrid<User> {

	@Autowired
	UserRepository userRepo;

	@Autowired
	ActivityRepository actRepo;
	
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
		.setSortProperty("id")
		;
		
		grid
		.addColumn(User::getDisplayName)
		.setHeader("Name")
		.setWidth("200px")
		.setFlexGrow(0)
		.setSortProperty("displayName")
		;
		
		grid
		.addColumn(User::getUserName)
		.setHeader("Username")
		.setWidth("150px")
		.setFlexGrow(0)
		.setSortProperty("userName")
		;
		
		grid
		.addColumn(User::getEmail)
		.setHeader("Email")
		.setWidth("250px")
		.setFlexGrow(0)
		.setSortProperty("email")
		;
		
		/*grid
		.addColumn(user -> {
			actRepo.findTopByUserAndTypeOrderByTimestampDesc(user, ActivityType.Login);
		})
		.setHeader("Last Logim")
		.setWidth("100px")
		.setFlexGrow(0)
		;*/
		
		grid
		.addColumn(new ZonedDateTimeRenderer<>( user -> {
			Activity a = actRepo.findTopByUserAndTypeOrderByTimestampDesc(user, ActivityType.Login);
			return a!=null?a.getTimestamp():null;
		}, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT) ))
		.setHeader("Last Login")
		.setSortable(false)
		.setWidth("150px")
		.setFlexGrow(0)
		;
		
		grid
		.addColumn(User::getConfirmed)
		.setHeader("Confirmed")
		.setWidth("100px")
		.setFlexGrow(0)
		.setSortProperty("confirmed")
		;
		
		
	}

	@Override
	public Dialog createDialog(User entity) {
		UserDialog cd = appCtx.getBean(UserDialog.class,entity);
		return cd;
	}

	
	
}
