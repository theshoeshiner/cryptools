package org.thshsh.crypt.web.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.thshsh.crypt.serv.PortfolioHistoryService;
import org.thshsh.crypt.web.views.main.MainLayout;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route(value = "system", layout = MainLayout.class)
@PageTitle("System")
public class SystemView extends VerticalLayout {
	
	@Autowired
	PortfolioHistoryService histService;

	@PostConstruct
	public void postConstruct() {
		
		Button runHist = new Button("Run History Job");
		add(runHist);
		runHist.addClickListener(click -> {
			histService.runHistoryJob();
		});
	}
	
}
