package org.thshsh.crypt.web.view.user;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.User;
import org.thshsh.crypt.web.security.SecuredByFeatureAccess;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.vaadin.entity.EntityGridView;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
@Route(value = "users", layout = MainLayout.class)
@PageTitle("Users")
@SecuredByFeatureAccess(feature=Feature.User,access=Access.Read)
public class UsersView extends EntityGridView<User, Long>{

	public UsersView() {
		super(UserGrid.class);
	}

	@Override
	public void postConstruct() {
		super.postConstruct();
	}

	
}
