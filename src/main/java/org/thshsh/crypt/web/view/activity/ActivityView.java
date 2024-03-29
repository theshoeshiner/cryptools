package org.thshsh.crypt.web.view.activity;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Activity;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.web.security.SecuredByFeatureAccess;
import org.thshsh.crypt.web.view.Breadcrumbs;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.vaadin.entity.EntityGridView;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route(value = "activities", layout = MainLayout.class)
@PageTitle("Activities")
@SecuredByFeatureAccess(feature=Feature.System,access=Access.Super)
public class ActivityView extends EntityGridView<Activity, Long>{

	@Autowired
	Breadcrumbs breadcrumbs;
	
	public ActivityView() {
		super(ActivityGrid.class);

	}
	
	
	

	@PostConstruct
	public void postConstruct() {

		super.postConstruct();

		 breadcrumbs.resetBreadcrumbs()
		    .addBreadcrumb("Activities", ActivityView.class)
		    ;
	}
	
	

}
