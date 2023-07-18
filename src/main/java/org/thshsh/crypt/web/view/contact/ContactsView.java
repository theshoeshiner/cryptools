package org.thshsh.crypt.web.view.contact;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Contact;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.web.security.SecuredByFeatureAccess;
import org.thshsh.crypt.web.view.Breadcrumbs;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.vaadin.entity.EntityGridView;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@SuppressWarnings("serial")
@Route(value = "contacts", layout = MainLayout.class)
@PageTitle("Contacts")
@SecuredByFeatureAccess(feature=Feature.System,access=Access.Super)
public class ContactsView extends EntityGridView<Contact, Long> {

	@Autowired
	Breadcrumbs breadcrumbs;

	public ContactsView() {
		super(ContactGrid.class);
	}

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		
		 breadcrumbs.resetBreadcrumbs()
		    .addBreadcrumb("Contacts", ContactsView.class)
		    ;
	}


}

