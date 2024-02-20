package org.thshsh.crypt.web.view.history;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.PortfolioHistory;
import org.thshsh.crypt.repo.PortfolioHistoryRepository;
import org.thshsh.crypt.web.view.AppEasyRender;
import org.thshsh.crypt.web.view.AppEntityGrid;
import org.thshsh.crypt.web.view.ManagePortfolioView;
import org.thshsh.crypt.web.view.portfolio.PortfolioEntryGrid;
import org.thshsh.vaadin.BinderUtils;
import org.thshsh.vaadin.UIUtils;
import org.thshsh.vaadin.entity.EntityDescriptor;
import org.vaadin.addons.thshsh.easyrender.RouterLinkRenderer;
import org.vaadin.addons.thshsh.easyrender.TemporalRenderer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.NumberRenderer;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioHistoryGrid extends AppEntityGrid<PortfolioHistory>{

	
	@Autowired
	ApplicationContext context;
	
	public PortfolioHistoryGrid() {
		super(null, FilterMode.Example);
		this.defaultSortOrderProperty="timestamp";
		this.defaultSortAsc=false;
		this.appendButtonColumn = true;
	}


	@Override
	public void setupColumns(Grid<PortfolioHistory> grid) {
		
		/*grid
		.addColumn(
				//new RouterLinkRenderer<>(ManagePortfolioView.class, BinderUtils.nestedValue(PortfolioHistory::getPortfolio, Portfolio::getId), BinderUtils.nestedValue(PortfolioHistory::getPortfolio, Portfolio::getId))
				AppEasyRender.router(ManagePortfolioView.class, BinderUtils.nestedValue(PortfolioHistory::getPortfolio, Portfolio::getId), BinderUtils.nestedValue(PortfolioHistory::getPortfolio, Portfolio::getName))
				)
		.setHeader("Portfolio")
		.setAutoWidth(true)
		.setFlexGrow(0)
		.setSortProperty("portfolio.id")
		;*/
		
		grid
		.addColumn(
				//new RouterLinkRenderer<>(ManagePortfolioView.class, BinderUtils.nestedValue(PortfolioHistory::getPortfolio, Portfolio::getName), BinderUtils.nestedValue(PortfolioHistory::getPortfolio, Portfolio::getId))
				AppEasyRender.router(ManagePortfolioView.class, BinderUtils.nestedValue(PortfolioHistory::getPortfolio, Portfolio::getId), BinderUtils.nestedValue(PortfolioHistory::getPortfolio, Portfolio::getName))
				)
		.setHeader("Portfolio")
		.setAutoWidth(true)
		.setFlexGrow(0)
		.setSortProperty("portfolio.name")
		;
		
		grid.addColumn(new TemporalRenderer<>(PortfolioHistory::getTimestamp,DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)))
		.setHeader("Timestamp")
		.setWidth("150px")
		.setFlexGrow(0)
		;
		
		Column<?> valueColumn = grid.addColumn(new NumberRenderer<>(PortfolioHistory::getValue, PortfolioEntryGrid.ReserveFormat))
				.setHeader("Value")
				.setTextAlign(ColumnTextAlign.END)
				.setSortProperty("value")
				//.setComparator(Comparator.comparing(PortfolioEntryHistory::getValueReserve))
				.setWidth("110px")
				.setFlexGrow(0);
	}
	
	@Override
	public void setFilter(String text) {
		
		this.filterEntity.setPortfolio(new Portfolio(text));
		if(NumberUtils.isParsable(text)) this.filterEntity.getPortfolio().setId(Long.parseLong(text));
	}

	@Override
	public void clearFilter() {
		this.filterEntity.setPortfolio(null);
	}
	
	

	//FIXME TODO
	/*@Override
	public void addButtonColumn(HorizontalLayout buttons, PortfolioHistory e) {
		
		{
		Button button = new Button(VaadinIcon.FOLDER_OPEN.create());
		button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		buttons.add(button);
		UIUtils.setTitle(button, "Manage");
		button.addClickListener(click -> view(e));
		}
		
		super.addButtonColumn(buttons, e);
	}*/
	
	protected void view(PortfolioHistory h) {
		
		PortfolioHistorySummaryDialog d = context.getBean(PortfolioHistorySummaryDialog.class,h);
		d.open();
		
		
	}

	@Override
	@Autowired
	public void setDescriptor(EntityDescriptor<PortfolioHistory, Long> descriptor) {
		super.setDescriptor(descriptor);
	}

	@Override
	@Autowired
	public void setRepository(Repository<PortfolioHistory, Long> repository) {
		super.setRepository(repository);
	}

	
	
}
