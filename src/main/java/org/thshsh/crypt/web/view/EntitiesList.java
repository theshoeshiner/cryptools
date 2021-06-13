package org.thshsh.crypt.web.view;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.atteo.evo.inflector.English;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.ExampleMatcher;
import org.thshsh.vaadin.ExampleFilterDataProvider;
import org.thshsh.vaadin.ExampleFilterRepository;
import org.thshsh.vaadin.UIUtils;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteConfiguration;

/**
 * This is a component that can list entities in a table with a button column for operations
 * It needs a "Provider" implementation to be passed to it to to delegate parts of the component
 *
 *
 * @author daniel.watson
 *
 * @param <T>
 * @param <ID>
 */
@SuppressWarnings("serial")
public class EntitiesList<T,ID extends Serializable>  extends VerticalLayout implements EntitiesListProvider<T, ID> {

	public static final Logger LOGGER = LoggerFactory.getLogger(EntitiesList.class);

	@Autowired
	ApplicationContext appCtx;

	EntitiesListProvider<T, ID> provider;
	ExampleFilterRepository<T, ID> repository;
	ExampleFilterDataProvider<T, ID> dataProvider;
	T filterEntity;
	Class<? extends Component> entityView;
	Class<T> entityClass;
	Grid<T> grid;
	Boolean defaultSortAsc = true;
	String defaultSortOrderProperty = "id";
	String entityName;
	String entityNamePlural;
	Boolean showButtonColumn = false;
	Boolean showEditButton = true;
	Boolean showDeleteButton = true;
	Boolean showHeader = true;
	Span count;
	TextField filter;
	Column<?> buttonColumn;
	String createText = "New";

	Button editButton;
	Button deleteButton;

	public EntitiesList(Class<T> c,
			Class<? extends Component> ev) {
		this.entityClass = c;
		this.entityView = ev;
	}

	public EntitiesList(Class<T> c,
			Class<? extends Component> ev,
			EntitiesListProvider<T, ID> provider) {
		this.entityClass = c;
		this.entityView = ev;
		this.provider = provider;

	}

	public EntitiesList(Class<T> c,
			Class<? extends Component> ev,
			DelegateEntitiesListProvider<T, ID> provider) {
		this.entityClass = c;
		this.entityView = ev;
		provider.list = this;
		this.provider = provider;

	}

	@SuppressWarnings("deprecation")
	public void postConstruct(ApplicationContext appCtx) {

		LOGGER.info("postConstruct {}",provider);

		this.appCtx = appCtx;

		this.repository = provider.getRepository();
		this.addClassName("entities-view");

		if(entityName == null) entityName = entityClass.getSimpleName();
		if(entityNamePlural == null) entityNamePlural = English.plural(entityName);
		dataProvider = provider.createDataProvider();
		filterEntity = provider.createFilterEntity();
		dataProvider.setFilter(filterEntity);

		if(showHeader) {
			HorizontalLayout header = new HorizontalLayout();
			header.setSpacing(true);
			header.setWidth("100%");
			header.setAlignItems(Alignment.CENTER);
			this.add(header);

			count = new Span();
			count.addClassName("count");
			header.addAndExpand(count);

			filter = new TextField();
			filter.setClearButtonVisible(true);

			filter.setPlaceholder("Search");
			filter.addValueChangeListener(change -> provider.changeFilter(change.getValue()));
			header.add(filter);

			if(entityView!=null) {
				Button add =new Button(createText+" "+entityName,VaadinIcon.PLUS.create());
				add.addClickListener(provider::clickNew);
				add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
				header.add(add);
			}
		}

		grid = new Grid<T>(entityClass,false);
		grid.setDataProvider(dataProvider);
		grid.addThemeVariants(
				//GridVariant.LUMO_NO_ROW_BORDERS,
		        GridVariant.LUMO_NO_BORDER,
		        GridVariant.LUMO_ROW_STRIPES
		        );
		grid.addClassName("borderless");
		grid.setHeight("100%");


		//dataProvider = provider.createDataProvider();
		//filterEntity = provider.createFilterEntity();
		//dataProvider.setFilter(filterEntity);
		//grid.setDataProvider(dataProvider);

		provider.setupColumns(grid);

		if(showButtonColumn) {

			grid.addClassName("button-column");
			buttonColumn = grid.addComponentColumn(e -> {

				HorizontalLayout buttons = new HorizontalLayout();
				buttons.addClassName("grid-buttons");
				buttons.setWidthFull();
				buttons.setJustifyContentMode(JustifyContentMode.END);

				provider.addButtonColumn(buttons, e);

				return buttons;
			})
			.setFlexGrow(0)
			.setClassNameGenerator(val -> {
				return "grid-buttons-column";
			})
			.setWidth("250px")
			;

		}

		grid.addItemClickListener(click -> {
			LOGGER.info("Clicked item: {}",click.getItem());
		});

		this.add(grid);

		provider.updateCount();

	}

	public void refresh() {
		dataProvider.refreshAll();
		provider.updateCount();
	}

	public void addButtonColumn(HorizontalLayout buttons, T e) {
		if(showEditButton) {
			editButton = new Button(VaadinIcon.PENCIL.create());
			editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			buttons.add(editButton);
			UIUtils.setTitle(editButton, "Edit");
			editButton.addClickListener(click -> provider.clickEdit(click, e));
		}
		if(showDeleteButton) {
			deleteButton = new Button(VaadinIcon.TRASH.create());
			deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			buttons.add(deleteButton);
			UIUtils.setTitle(deleteButton, "Delete");
			deleteButton.addClickListener(click -> {
				clickDelete(e);
			});
		}

	}

	public void clickDelete(T e) {

		ConfirmDialogs.deleteDialog(entityName+" \""+provider.getEntityName(e) +"\"", () -> {
			provider.delete(e);
		}).open();
		/*
		ConfirmDialog cd = new ConfirmDialog(null,"Delete "+entityName+" \""+provider.getEntityName(e)+"\" ?",VaadinIcon.TRASH.create());
		cd.withYesButton()
		.withVariants(ButtonVariant.LUMO_PRIMARY)

		.with(null,() -> {
			provider.delete(e);
		});
		cd.withNoButton().withIcon(null);
		cd.open(); */
	}

	public void clickEdit(ClickEvent<Button> click,T entity) {
		if(Dialog.class.isAssignableFrom(entityView)) {
			Dialog cd = createDialog(entity);
			cd.addOpenedChangeListener(change -> {
				provider.refresh();
			});
			cd.open();
		}
		else {
			//Class<V> hup = (Class<V>) entityView;

			String route = RouteConfiguration.forSessionScope().getUrl(entityView);

			//RouteParameters rpParameters;
			QueryParameters queryParameters = new QueryParameters(Collections.singletonMap("id", Arrays.asList(provider.getEntityId(entity).toString())));


			//LOGGER.info("entityView: {}",entityView);
			//UI.getCurrent().navigate(hup,provider.getEntityId(entity));
			UI.getCurrent().navigate(route,queryParameters);

		}
	}



	public ExampleFilterDataProvider<T,ID> createDataProvider(){
		ExampleFilterDataProvider<T,ID> dataProvider = new ExampleFilterDataProvider<T,ID>(
				repository,
				ExampleMatcher.matchingAny()
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
				.withIgnoreCase().withIgnoreNullValues()
				, provider.getDefaultSortOrder()
				);

		return dataProvider;
	}

	public List<QuerySortOrder> getDefaultSortOrder() {
		if(defaultSortAsc) return QuerySortOrder.asc(defaultSortOrderProperty).build();
		else return QuerySortOrder.desc(defaultSortOrderProperty).build();
	}

	public T createFilterEntity() {
		try {
			return entityClass.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException("Could not instantiate class "+entityClass);
		}
	}

	public void updateCount() {
		if(showHeader) {
			long full = repository.count();
			int shown = dataProvider.size(new Query<>());
			count.setText("Showing "+shown+" of "+full);
		}
	}

	public void changeFilter(String text) {
		if(StringUtils.isBlank(text)) provider.clearFilter();
		else provider.setFilter(text);
		dataProvider.refreshAll();
		provider.updateCount();
	}

	public void clickNew(ClickEvent<Button> click) {
		if(Dialog.class.isAssignableFrom(entityView)) {
			Dialog cd = createDialog(null);
			cd.open();
			cd.addOpenedChangeListener(change -> {
				if(cd instanceof EntityDialog) {
					EntityDialog<?> ed = (EntityDialog<?>) cd;
					if(ed.getSaved()) provider.refresh();
				}
				else provider.refresh();
			});
		}
		else {
			UI.getCurrent().navigate(entityView);
		}
	}

	public Dialog createDialog(T entity) {
		Dialog cd = (Dialog) appCtx.getBean(entityView,entity);
		return cd;
	}
	/*
		@Override
		public void setupColumns(Grid<T> grid) {
			throw new NotImplementedException();
		}

		@Override
		public void setFilter(String text) {
			throw new NotImplementedException();
		}

		@Override
		public void clearFilter() {
			throw new NotImplementedException();
		}

		@Override
		public ExampleFilterRepository<T, ID> getRepository() {
			throw new NotImplementedException();
		}

		@Override
		public String getEntityName(T t) {
			throw new NotImplementedException();
		}

		@Override
		public void delete(T t) {
			throw new NotImplementedException();
		}*/

	@Override
	public ID getEntityId(T entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setupColumns(Grid<T> grid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFilter(String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearFilter() {

	}



	@Override
	public ExampleFilterRepository<T, ID> getRepository() {
		return null;
	}

	@Override
	public String getEntityName(T t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(T t) {


	}
}
