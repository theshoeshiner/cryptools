package org.thshsh.crypt.web.view;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.serv.PortfolioHistoryService;
import org.thshsh.crypt.web.security.SecuredByFeatureAccess;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.vaadin.entity.ConfirmDialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route(value = "system", layout = MainLayout.class)
@PageTitle("System")
@SecuredByFeatureAccess(feature=Feature.System,access=Access.Super)
public class SystemView extends VerticalLayout {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(SystemView.class);
	
	
	
	@Autowired
	PortfolioHistoryService histService;
	
	@Autowired
	ApplicationContext context;

	@Autowired
	AsyncTaskExecutor executor;
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
	TransactionTemplate template;
	
	@PostConstruct
	public void postConstruct() {
		
		template = new TransactionTemplate(transactionManager);
		
		Button runHist = new Button("Run History Job");
		add(runHist);
		runHist.addClickListener(click -> {
			histService.runHistoryJob();
		});
		
		Button shutdown = new Button("Shutdown", VaadinIcon.POWER_OFF.create(), click -> {
			ConfirmDialogs.yesNoDialog("Shutdown system?", () -> {
				int exitCode = SpringApplication.exit(context, () -> 0);
				System.exit(exitCode);
			}).open();
			;
		});
		
		

		add(shutdown);
	}
	
}
