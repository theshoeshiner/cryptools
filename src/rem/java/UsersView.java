package org.thshsh.crypt.web.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.thshsh.crypt.User;
import org.thshsh.crypt.web.repo.AppUserRepository;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.vaadin.ExampleFilterRepository;
import org.thshsh.vaadin.UIUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route(value = "users", layout = MainLayout.class)
@PageTitle("Users")
public class UsersView extends EntitiesView<User, Long>{

	@Autowired
	ApplicationContext appCtx;

	@Autowired
	AppUserRepository userRepo;

	public UsersView() {
		super(User.class, UserDialog.class);
	}

	@PostConstruct
	public void postConstruct() {
		entitiesList.showEditButton = false;
		entitiesList.showButtonColumn = true;
		entitiesList.defaultSortOrderProperty = "displayName";
		entitiesList.showDeleteButton=false;
		entitiesList.createText="Add";
		super.postConstruct();

	}

	@Override
	public ExampleFilterRepository<User, Long> getRepository() {
		return userRepo;
	}

	@Override
	public String getEntityName(User t) {
		return t.getDisplayName();
	}

	@Override
	public void setupColumns(Grid<User> grid) {

		grid
		.addColumn(User::getDisplayName)
		.setHeader("Name")
		.setWidth("250px")
		.setSortable(true)
		.setSortProperty("displayName")
		.setFlexGrow(0)
		;

		grid
		.addColumn(User::getEmail)
		.setHeader("Email")
		.setWidth("250px")
		.setSortable(true)
		.setSortProperty("email")
		.setFlexGrow(0)
		;

		/*grid.addComponentColumn(user -> {
			//Set<Role> roles = user.getRoles();
			Span rs= new Span();
			rs.addClassName("roles-column-list");
			user.getRoles().forEach(r->{
				Span rname = new Span(r.getName());
				rname.addClassName(r.getKey());

				rs.add(rname);
			});
			return rs;
		})
		.setSortable(false)

		.setHeader("Roles")
		.setFlexGrow(1)
		;*/

	}

	@Override
	public void setFilter(String text) {
		User filterEntity = getFilterEntity();
		filterEntity.setFirstName(text);
		filterEntity.setLastName(text);
		filterEntity.setEmail(text);
	}

	@Override
	public void clearFilter() {
		User filterEntity = getFilterEntity();
		filterEntity.setFirstName(null);
		filterEntity.setLastName(null);
		filterEntity.setEmail(null);
	}

	@Override
	public void addButtonColumn(HorizontalLayout buttons, User e) {
		super.addButtonColumn(buttons, e);

		Button groupsButton = new Button(VaadinIcon.GROUP.create());
		groupsButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		buttons.add(groupsButton);
		UIUtils.setTitle(groupsButton, "Edit Roles");
		groupsButton.addClickListener(click -> editGroups(e));
		//groupsButton.setEnabled(!e.isRunning());

	}

	protected void editGroups(User user) {
		/*UserRolesDialog d = appCtx.getBean(UserRolesDialog.class,user);
		d.addOpenedChangeListener(closed -> {
			if(!closed.isOpened()) {
				refresh();
			}
		});
		d.open();
		 */
	}


}
