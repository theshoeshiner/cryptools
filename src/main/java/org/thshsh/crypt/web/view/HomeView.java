package org.thshsh.crypt.web.view;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.thshsh.crypt.web.views.main.MainLayout;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = HomeView.PATH, layout = MainLayout.class)
@PageTitle(HomeView.TITLE)
@RouteAlias(value = "", layout = MainLayout.class)
public class HomeView extends VerticalLayout {

	public static final Logger LOGGER = LoggerFactory.getLogger(HomeView.class);

	public static final String PATH = "dashboard";
	public static final String TITLE = "Dashboard";


	@Autowired
	Breadcrumbs breadcrumbs;

	//@Autowired
	//@PersistenceContext
	//AuditReader auditReader;

	//@Autowired
	//EntityManagerFactory entityManagerFactory;

    public HomeView() {

    }


    @PostConstruct
    @Transactional
    public void postConstruct() {
    	breadcrumbs.resetBreadcrumbs().addBreadcrumb("Home", HomeView.class);
		/*
		    	AuditQuery query =  auditReader
		    		    .createQuery()
		    		    .forRevisionsOfEntity( ProfileConfiguration.class, true, false )
		    		    .addOrder( AuditEntity.revisionProperty("timestamp").desc() );
		    	 query.add(AuditEntity.revisionNumber().maximize().computeAggregationInInstanceContext());


		    	List<?> results =  query.getResultList();


		    	results.forEach(o -> {
		    		LOGGER.info("entity: {}",o);

		    	});
		    	*/


    }
}
