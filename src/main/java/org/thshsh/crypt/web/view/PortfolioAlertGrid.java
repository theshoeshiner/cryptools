package org.thshsh.crypt.web.view;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.PortfolioAlert;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.PortfolioAlertRepository;
import org.thshsh.crypt.web.security.SecurityUtils;
import org.thshsh.vaadin.FunctionUtils;
import org.thshsh.vaadin.RouterLinkRenderer;
import org.thshsh.vaadin.ZonedDateTimeRenderer;

import com.vaadin.flow.component.grid.Grid;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioAlertGrid extends AppEntityGrid<PortfolioAlert>{
	
	@Autowired
	PortfolioAlertRepository alertRepo;

	public PortfolioAlertGrid() {
		super(PortfolioAlert.class, null, FilterMode.Example);
		this.showButtonColumn = SecurityUtils.hasAccess(Feature.Portfolio, Access.Super);
		this.defaultSortOrderProperty="timestamp";
		this.defaultSortAsc=false;
	}

	@Override
	public PagingAndSortingRepository<PortfolioAlert, Long> getRepository() {
		return alertRepo;
	}

	@Override
	public void setupColumns(Grid<PortfolioAlert> grid) {
		

		
		grid
		.addColumn(new RouterLinkRenderer<>(ManagePortfolioView.class, FunctionUtils.nestedValue(PortfolioAlert::getPortfolio, Portfolio::getName), FunctionUtils.nestedValue(PortfolioAlert::getPortfolio, Portfolio::getId)))
		.setHeader("Portfolio")
		.setAutoWidth(true)
		.setFlexGrow(0)
		.setSortProperty("portfolio.name")
		;
		
		grid.addColumn(FunctionUtils.nestedValue(PortfolioAlert::getPortfolio, Portfolio::getUser, User::getEmail))
		.setHeader("User")
		.setWidth("250px")
		.setFlexGrow(0);
		
		grid.addColumn(new ZonedDateTimeRenderer<>(PortfolioAlert::getTimestamp,DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)))
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

}
