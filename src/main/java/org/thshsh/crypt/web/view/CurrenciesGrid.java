package org.thshsh.crypt.web.view;

import java.util.Arrays;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Grade;
import org.thshsh.crypt.repo.CurrencyRepository;
import org.thshsh.crypt.serv.MarketRateService;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.crypt.web.UiComponents;
import org.thshsh.crypt.web.security.SecurityUtils;
import org.thshsh.vaadin.UIUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class CurrenciesGrid extends AppEntityGrid<Currency,Long> {


	@Autowired
	CurrencyRepository repo;
	
	@Autowired
	MarketRateService rateService;

	public CurrenciesGrid() {
		super(Currency.class, null, FilterMode.Example);
		this.showButtonColumn=true;
		this.showEditButton = SecurityUtils.hasAccess(Currency.class, Access.ReadWrite);
		this.showDeleteButton = SecurityUtils.hasAccess(Currency.class, Access.ReadWriteDelete);
	}

	@Override
	public PagingAndSortingRepository<Currency, Long> getRepository() {
		return repo;
	}

	@Override
	public void setupColumns(Grid<Currency> grid) {

		Column<?> col = grid.addComponentColumn(entry -> {
			if(entry.getImageUrl()!=null) {
				String imageUrl = "/image/"+entry.getImageUrl();
				Image image = new Image(imageUrl,"Icon");
				image.setWidth(ManagePortfolioView.ICON_SIZE);
				image.setHeight(ManagePortfolioView.ICON_SIZE);
				return image;
			}
			else return new Span();

		});
		UiComponents.iconColumn(col);

		Column<?> eName = grid.addColumn(Currency::getName)
		.setHeader("Name")
		.setSortProperty("name")
		.setSortable(true)
		.setWidth("150px")
		.setFlexGrow(0)
		;
		UiComponents.iconLabelColumn(eName);

		grid
		.addColumn(Currency::getKey)
		.setHeader("Symbol")
		.setWidth("125px")
		.setFlexGrow(0)
		;
		
		
		grid
		.addColumn(Currency::getActive)
		.setWidth("125px")
		.setFlexGrow(0)
		.setHeader("Active");
		
		grid.addColumn(Currency::getGrade).setHeader("Grade")
		.setWidth("125px")
		.setSortable(true)
		.setSortProperty("grade")
		.setFlexGrow(0);
		
		grid.addColumn(Currency::getPlatformType).setHeader("Platform")
		.setWidth("125px")
		.setFlexGrow(0);
		
		grid.addComponentColumn(c -> {
			Anchor a = new Anchor("https://www.cryptocompare.com/coins/"+c.getKey(),"https://www.cryptocompare.com/coins/"+c.getKey());
			return a;
		})
		.setFlexGrow(1)
		.setHeader("Link");
		

		
		
	
		
		//grid.addColumn(FunctionUtils.nestedValue(Currency::getBuiltOn, Currency::getName)).setHeader("Built On");
		
		/*grid.addColumn((e) -> {
			return "";
		})
		;*/
	}

	@Override
	public void addButtonColumn(HorizontalLayout buttons, Currency e) {
		super.addButtonColumn(buttons, e);

		Button refresh = new Button(VaadinIcon.REFRESH.create());
		refresh.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		buttons.add(refresh);
		UIUtils.setTitle(refresh, "Refresh");
		refresh.addClickListener(click -> refreshRate(e));
	}

	protected void refreshRate(Currency c) {
		rateService.getUpToDateMarketRates(AppSession.getCurrentUser().getApiKey(), Arrays.asList(c));
	}
	
	/*protected void manage(Portfolio e) {
		String route = RouteConfiguration.forSessionScope().getUrl(ManagePortfolioView.class);
		QueryParameters queryParameters = new QueryParameters(Collections.singletonMap("id",Arrays.asList(e.getId().toString())));
		UI.getCurrent().navigate(route, queryParameters);
	}*/

	@Override
	public String getEntityName(Currency t) {
		return t.getName();
	}

	

	@Override
	public void setFilter(String text) {
		this.filterEntity.setName(text);
		this.filterEntity.setKey(text);
		filterEntity.setGrade(EnumUtils.getEnumIgnoreCase(Grade.class, text));
	}

	@Override
	public void clearFilter() {
		this.filterEntity.setName(null);
		this.filterEntity.setKey(null);
		filterEntity.setGrade(null);
		
	}
	
}
