package org.thshsh.crypt.web.view.user;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.ActivityRepository;
import org.thshsh.crypt.repo.UserRepository;
import org.thshsh.crypt.serv.MailService;
import org.thshsh.crypt.web.security.SecurityUtils;
import org.thshsh.crypt.web.view.AppEntityGrid;
import org.thshsh.vaadin.UIUtils;
import org.thshsh.vaadin.entity.ConfirmDialogs;
import org.thshsh.vaadin.entity.EntityDescriptor;
import org.thshsh.vaadin.entity.EntityOperation;
import org.vaadin.addons.thshsh.easyrender.TemporalRenderer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class UserGrid extends AppEntityGrid<User> {

	@Autowired
	UserRepository userRepo;

	@Autowired
	ActivityRepository actRepo;
	
	@Autowired
	MailService mailService;
	
	public UserGrid() {
		super(UserDialog.class, FilterMode.Example);
		this.appendButtonColumn = true;
		this.showEditButton = SecurityUtils.hasAccess(User.class, Access.ReadWrite);
		this.showDeleteButton = SecurityUtils.hasAccess(User.class, Access.ReadWriteDelete);
	}

	@Override
	public PagingAndSortingRepository<User, Long> getRepository() {
		return userRepo;
	}
	
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		if(this.buttonColumn!=null) this.buttonColumn.setFlexGrow(1);
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
		
		/*	grid
			.addColumn(new TemporalRenderer<>( user -> {
				Activity a = actRepo.findTopByUserAndTypeOrderByTimestampDesc(user, ActivityType.Login);
				return a!=null?a.getTimestamp():null;
			}, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT) ))
			.setHeader("Last Login")
			.setSortable(false)
			.setWidth("150px")
			.setFlexGrow(0)
			;*/
		
		grid
		.addColumn(new TemporalRenderer<>(User::getLastLogin,DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)))
		.setHeader("Last Login")
		.setWidth("150px")
		.setFlexGrow(0)
		.setSortProperty("lastLogin")
		;
		
		grid
		.addColumn(User::getConfirmed)
		.setHeader("Confirmed")
		.setWidth("100px")
		.setFlexGrow(0)
		.setSortProperty("confirmed")
		;
		
		//this.buttonColumn.setFlexGrow(1);
		
	}

	
	
	
	
	@Override
	protected void createOperations() {
		super.createOperations();
		
		operations.add(EntityOperation.<User>create()
				.withIcon(VaadinIcon.CHECK)
				.withName("Send Confirmation Email")
				//.withDisplay(!showEditButton)
				.withConfirm(true)
				.withSingularOperation((e) -> {
					mailService.sendAccountConfirmEmail(e, e.getConfirmToken());
				}))
				;
		
	}

	/*@Override
	public void addButtonColumn(HorizontalLayout buttons, User e) {
	
		super.addButtonColumn(buttons, e);
		
		{
			Button confirmEmail = new Button(VaadinIcon.CHECK.create());
			confirmEmail.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			buttons.add(confirmEmail);
			UIUtils.setTitle(confirmEmail, "Confirm");
			confirmEmail.addClickListener(click -> {
				ConfirmDialogs.yesNoDialog("Send Confirmation Email?", () -> {
					LOGGER.info("clicked yes to send confirm email");
					mailService.sendAccountConfirmEmail(e, e.getConfirmToken());
				}).open();
			});
		}
	}*/



	@Override
	@Autowired
	public void setDescriptor(EntityDescriptor<User, Long> descriptor) {
		super.setDescriptor(descriptor);
	}

	@Override
	@Autowired
	public void setRepository(Repository<User, Long> repository) {
		super.setRepository(repository);
	}

	
	
}
