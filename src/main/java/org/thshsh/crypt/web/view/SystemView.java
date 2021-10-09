package org.thshsh.crypt.web.view;

import javax.annotation.PostConstruct;

import org.thshsh.crypt.web.views.main.MainLayout;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route(value = "system", layout = MainLayout.class)
@PageTitle("System")
public class SystemView extends VerticalLayout {

	@PostConstruct
	public void postConstruct() {
		
		Button runHist = new Button("Run History Job");
		
	}
	
}
