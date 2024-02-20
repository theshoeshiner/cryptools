package org.thshsh.crypt.web.view.portfolio;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.PortfolioAlert;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.PortfolioAlertRepository;
import org.thshsh.crypt.web.security.SecurityUtils;
import org.thshsh.crypt.web.view.AppEasyRender;
import org.thshsh.crypt.web.view.AppEntityGrid;
import org.thshsh.crypt.web.view.ManagePortfolioView;
import org.thshsh.vaadin.BinderUtils;
import org.thshsh.vaadin.entity.EntityDescriptor;
import org.vaadin.addons.thshsh.easyrender.RouterLinkRenderer;
import org.vaadin.addons.thshsh.easyrender.TemporalRenderer;

import com.vaadin.flow.component.grid.Grid;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioAlertGrid extends AppEntityGrid<PortfolioAlert>{


	public PortfolioAlertGrid() {
		super(null, FilterMode.Example);
		this.appendButtonColumn = SecurityUtils.hasAccess(Feature.Portfolio, Access.Super);
		this.defaultSortOrderProperty="timestamp";
		this.defaultSortAsc=false;
	}



	@Override
	public void setupColumns(Grid<PortfolioAlert> grid) {
		

		
		grid
		.addColumn(
				//new RouterLinkRenderer<>(ManagePortfolioView.class, BinderUtils.nestedValue(PortfolioAlert::getPortfolio, Portfolio::getName), BinderUtils.nestedValue(PortfolioAlert::getPortfolio, Portfolio::getId))
				AppEasyRender.router(ManagePortfolioView.class, BinderUtils.nestedValue(PortfolioAlert::getPortfolio, Portfolio::getId),  BinderUtils.nestedValue(PortfolioAlert::getPortfolio, Portfolio::getName))
				)
		.setHeader("Portfolio")
		.setAutoWidth(true)
		.setFlexGrow(0)
		.setSortProperty("portfolio.name")
		;
		
		grid.addColumn(BinderUtils.nestedValue(PortfolioAlert::getPortfolio, Portfolio::getUser, User::getEmail))
		.setHeader("User")
		.setWidth("250px")
		.setFlexGrow(0);
		
		grid.addColumn(new TemporalRenderer<>(PortfolioAlert::getTimestamp,DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)))
		.setHeader("Timestamp")
		.setWidth("150px")
		.setFlexGrow(0)
		;
		
		grid.addColumn(PortfolioAlert::getRepeat)
		.setHeader("Repeat")
		.setWidth("75px")
		.setFlexGrow(0)
		;
		

		grid.addColumn(PortfolioAlert::getMuted)
		.setHeader("Muted")
		.setWidth("75px")
		.setFlexGrow(0)
		;
		
	}



	@Override
	@Autowired
	public void setDescriptor(EntityDescriptor<PortfolioAlert, Long> descriptor) {
		super.setDescriptor(descriptor);
	}



	@Override
	@Autowired
	public void setRepository(Repository<PortfolioAlert, Long> repository) {
		super.setRepository(repository);
	}
	
	

}
