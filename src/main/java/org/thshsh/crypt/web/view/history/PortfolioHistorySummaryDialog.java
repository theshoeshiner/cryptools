package org.thshsh.crypt.web.view.history;

import java.util.Comparator;

import javax.annotation.PostConstruct;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.PortfolioEntryHistory;
import org.thshsh.crypt.PortfolioHistory;
import org.thshsh.crypt.repo.PortfolioHistoryRepository;
import org.thshsh.crypt.serv.ImageService;
import org.thshsh.crypt.web.UiComponents;
import org.thshsh.crypt.web.view.ManagePortfolioView;
import org.thshsh.crypt.web.view.portfolio.PortfolioEntryGrid;
import org.thshsh.vaadin.BinderUtils;
import org.vaadin.addons.thshsh.easyrender.ImageRenderer;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.FooterRow.FooterCell;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.NumberRenderer;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioHistorySummaryDialog extends Dialog {

	@Autowired
	ImageService imageService;
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Autowired
	PortfolioHistoryRepository historyRepo;
	
	TransactionTemplate template;
	
	PortfolioHistory history;
	
	public PortfolioHistorySummaryDialog(PortfolioHistory h) {
		super();
		this.history = h;
	}
	
	@PostConstruct
	public void postConstruct() {
		
		this.template = new TransactionTemplate(transactionManager);
		
		template.executeWithoutResult(action -> {
			
			PortfolioHistory history = historyRepo.findById(this.history.getId()).get();
			Hibernate.initialize(history.getEntries());

		
			this.setWidth("600px");
			VerticalLayout layout = new VerticalLayout();
			layout.setWidthFull();
			this.add(layout);
			
			Grid<PortfolioEntryHistory> grid = new Grid<>();
			grid.setWidthFull();
			grid.setItems(history.getEntries());
			layout.add(grid);
			
			Column<?> curCol = grid.addColumn(new ImageRenderer<>(entry -> {
				return imageService.getImageUrl(entry.getCurrency());
			}, null, ManagePortfolioView.ICON_SIZE, ManagePortfolioView.ICON_SIZE));
	
			UiComponents.iconColumn(curCol);
	
			Column<?> sym = grid.addColumn(BinderUtils.nestedValueDefault(PortfolioEntryHistory::getCurrency, Currency::getKey,"Unallocated"))
					.setHeader("Currency")
					.setWidth("90px")
					.setFlexGrow(0)
					.setSortProperty("currency.symbol");
			UiComponents.iconLabelColumn(sym);
			
			grid.addColumn(PortfolioEntryHistory::getBalance)
			.setSortProperty("balance")
			.setTextAlign(ColumnTextAlign.END)
			.setComparator(Comparator.comparing(PortfolioEntryHistory::getBalance))
			.setHeader("Balance ©")
			.setAutoWidth(true)
			.setWidth("125px")
			.setFlexGrow(0);
			
			Column<?> valueColumn = grid.addColumn(new NumberRenderer<>(PortfolioEntryHistory::getValueReserve, PortfolioEntryGrid.ReserveFormat))
					.setHeader("Value $")
					.setTextAlign(ColumnTextAlign.END)
					.setSortProperty("value")
					.setComparator(Comparator.comparing(PortfolioEntryHistory::getValueReserve))
					.setWidth("110px")
					.setFlexGrow(0);
			
			
			
			
			FooterRow footer = grid.appendFooterRow();
			FooterCell totalValueCell = footer.getCell(valueColumn);
			
			totalValueCell.setText(PortfolioEntryGrid.ReserveFormat.format(history.getValue()));

		});
		
	}
}
