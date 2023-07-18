package org.thshsh.crypt.web.view.exchange;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.Grade;
import org.thshsh.crypt.web.UiComponents;
import org.thshsh.crypt.web.security.SecurityUtils;
import org.thshsh.crypt.web.view.AppEntityGrid;
import org.thshsh.crypt.web.view.ManagePortfolioView;
import org.thshsh.vaadin.entity.EntityDescriptor;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class ExchangesGrid extends AppEntityGrid<Exchange> {

	public ExchangesGrid() {
		super(ExchangeDialog.class, FilterMode.Example);
		this.appendButtonColumn = true;
		this.showEditButton = SecurityUtils.hasAccess(Exchange.class, Access.ReadWrite);
		this.showDeleteButton = SecurityUtils.hasAccess(Exchange.class, Access.ReadWriteDelete);
	}

	@Override
	public void setupColumns(Grid<Exchange> grid) {

		Column<?> col = grid.addComponentColumn(entry -> {
			if (entry.getImageUrl() != null) {
				String imageUrl = "/image/" + entry.getImageUrl();
				Image image = new Image(imageUrl, "Icon");
				image.setWidth(ManagePortfolioView.ICON_SIZE);
				image.setHeight(ManagePortfolioView.ICON_SIZE);
				return image;
			} else
				return new Span();

		});
		UiComponents.iconColumn(col);

		Column<?> eName = grid.addColumn(Exchange::getName).setHeader("Name").setSortProperty("name").setSortable(true)
				.setWidth("150px").setFlexGrow(0);
		UiComponents.iconLabelColumn(eName);

		grid.addColumn(Exchange::getGrade).setHeader("Grade").setWidth("200px").setSortable(true)
				.setSortProperty("grade").setFlexGrow(0);

		grid.addColumn((e) -> {
			return "";
		});
	}

	/*	@Override
		public void addButtonColumn(HorizontalLayout buttons, Exchange e) {
			super.addButtonColumn(buttons, e);
	
			Button manageButton = new Button(VaadinIcon.FOLDER_OPEN.create());
			manageButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			buttons.add(manageButton);
			UIUtils.setTitle(manageButton, "Manage");
			manageButton.addClickListener(click -> manage(e));
		}*/

	/*protected void manage(Portfolio e) {
		String route = RouteConfiguration.forSessionScope().getUrl(ManagePortfolioView.class);
		QueryParameters queryParameters = new QueryParameters(Collections.singletonMap("id",Arrays.asList(e.getId().toString())));
		UI.getCurrent().navigate(route, queryParameters);
	}*/

	@Override
	public void setFilter(String text) {
		filterEntity.setName(text);
		filterEntity.setKey(text);
		filterEntity.setGrade(EnumUtils.getEnumIgnoreCase(Grade.class, text));
	}

	@Override
	@Autowired
	public void setDescriptor(EntityDescriptor<Exchange, Long> descriptor) {
		super.setDescriptor(descriptor);
	}

	@Override
	@Autowired
	public void setRepository(Repository<Exchange, Long> repository) {
		super.setRepository(repository);
	}

	@Override
	public void clearFilter() {
		this.filterEntity.setName(null);
		this.filterEntity.setKey(null);
		filterEntity.setGrade(null);
	}

}
