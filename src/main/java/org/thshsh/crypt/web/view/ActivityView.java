package org.thshsh.crypt.web.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.thshsh.crypt.Activity;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.vaadin.entity.EntityGridView;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route(value = "activities", layout = MainLayout.class)
@PageTitle("Activities")
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
