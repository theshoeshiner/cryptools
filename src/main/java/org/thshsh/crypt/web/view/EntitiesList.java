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
import org.springframework.data.repository.PagingAndSortingRepository;
import org.thshsh.vaadin.ChunkRequest;
import org.thshsh.vaadin.ExampleFilterDataProvider;
import org.thshsh.vaadin.ExampleFilterRepository;
import org.thshsh.vaadin.StringSearchDataProvider;
import org.thshsh.vaadin.UIUtils;

import com.google.common.primitives.Ints;
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
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
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
public class EntitiesList<T, ID extends Serializable> extends VerticalLayout {

	public static final Logger LOGGER = LoggerFactory.getLogger(EntitiesList.class);

	public static enum FilterMode {
		String, Example, None;
	}

	@Autowired
	ApplicationContext appCtx;

	EntitiesListProvider<T, ID> listOperationProvider;

	PagingAndSortingRepository<T, ID> repository;
	DataProvider<T, ?> dataProvider;

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
	Boolean showCreateButton = true;
	Boolean showHeader = true;
	Span count;
	TextField filter;
	Column<?> buttonColumn;
	String createText = "New";
	HorizontalLayout header;

	//holds a temporary reference to the edit button, which is replaced as we are iterating over the rows
	Button editButton;
	Button deleteButton;

	FilterMode filterMode;

	Boolean caseSensitive = false;

	public EntitiesList(Class<T> c,Class<? extends Component> ev) {
		this.entityClass = c;
		this.entityView = ev;
		this.filterMode=FilterMode.Example;
	}

	public EntitiesList(Class<T> c, Class<? extends Component> ev,FilterMode fm) {
		this.entityClass = c;
		this.entityView = ev;
		this.filterMode = fm;
	}

	public EntitiesList(Class<T> c, Class<? extends Component> ev, EntitiesListProvider<T, ID> provider,
			FilterMode fm) {
		this.entityClass = c;
		this.entityView = ev;
		this.listOperationProvider = provider;
		this.filterMode = fm;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public void postConstruct(ApplicationContext appCtx) {

		LOGGER.info("postConstruct {}", listOperationProvider);

		this.appCtx = appCtx;

		this.repository = listOperationProvider.getRepository();
		this.addClassName("entities-view");

		if (entityName == null)
			entityName = entityClass.getSimpleName();
		if (entityNamePlural == null)
			entityNamePlural = English.plural(entityName);

		dataProvider = listOperationProvider.createDataProvider();

		LOGGER.info("dataProvider: {}",dataProvider);

		if (filterMode == FilterMode.Example) {
			filterEntity = listOperationProvider.createFilterEntity();
			((ExampleFilterDataProvider<T, ID>) dataProvider).setFilter(filterEntity);
		}

		if (showHeader) {
			header = new HorizontalLayout();
			header.setSpacing(true);
			header.setWidth("100%");
			header.setAlignItems(Alignment.CENTER);
			this.add(header);

			count = new Span();
			count.addClassName("count");
			header.addAndExpand(count);

			filter = new TextField();
			filter.setClearButtonVisible(true);

			filter.setPlaceholder("Filter");
			filter.addValueChangeListener(change -> listOperationProvider.changeFilter(change.getValue()));
			header.add(filter);

			if (entityView != null && showCreateButton) {
				Button add = new Button(createText + " " + entityName, VaadinIcon.PLUS.create());
				add.addClickListener(listOperationProvider::clickNew);
				add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
				header.add(add);
			}
		}

		grid = new Grid<T>(entityClass, false);
		grid.setDataProvider(dataProvider);
		grid.addThemeVariants(
				//GridVariant.LUMO_NO_ROW_BORDERS,
				GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
		grid.addClassName("borderless");
		grid.setHeight("100%");

		//dataProvider = provider.createDataProvider();
		//filterEntity = provider.createFilterEntity();
		//dataProvider.setFilter(filterEntity);
		//grid.setDataProvider(dataProvider);

		listOperationProvider.setupColumns(grid);

		if (showButtonColumn) {

			grid.addClassName("button-column");
			buttonColumn = grid.addComponentColumn(e -> {

				HorizontalLayout buttons = new HorizontalLayout();
				buttons.addClassName("grid-buttons");
				buttons.setPadding(true);
				buttons.setWidthFull();
				buttons.setJustifyContentMode(JustifyContentMode.END);

				listOperationProvider.addButtonColumn(buttons, e);

				return buttons;
			}).setFlexGrow(0).setClassNameGenerator(val -> {
				return "grid-buttons-column";
			}).setWidth("250px");

		}

		grid.addItemClickListener(click -> {
			LOGGER.info("Clicked item: {}", click.getItem());
		});

		this.add(grid);

		listOperationProvider.updateCount();

	}

	public void refresh() {
		dataProvider.refreshAll();
		listOperationProvider.updateCount();
	}

	public void addButtonColumn(HorizontalLayout buttons, T e) {
		if (showEditButton) {
			editButton = new Button(VaadinIcon.PENCIL.create());
			editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			buttons.add(editButton);
			UIUtils.setTitle(editButton, "Edit");
			editButton.addClickListener(click -> listOperationProvider.clickEdit(click, e));
		}
		if (showDeleteButton) {
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

		ConfirmDialogs.deleteDialog(entityName + " \"" + listOperationProvider.getEntityName(e) +"\"", () -> {
			listOperationProvider.delete(e);
		}).open();

	}

	public void delete(T e) {
		repository.delete(e);
		refresh();
	}

	public void clickEdit(ClickEvent<Button> click, T entity) {
		if(entityView != null) {
			if (Dialog.class.isAssignableFrom(entityView)) {
				Dialog cd = createDialog(entity);
				cd.open();
				cd.addOpenedChangeListener(change -> {
					listOperationProvider.refresh();
				});
			} else {
				Class<? extends Component> hup = entityView;

				String route = RouteConfiguration.forSessionScope().getUrl(hup);

				//RouteParameters rpParameters;
				QueryParameters queryParameters = new QueryParameters(Collections.singletonMap("id",
						Arrays.asList(listOperationProvider.getEntityId(entity).toString())));

				//LOGGER.info("entityView: {}",entityView);
				//UI.getCurrent().navigate(hup,provider.getEntityId(entity));
				UI.getCurrent().navigate(route, queryParameters);

			}
		}
	}

	public DataProvider<T, ?> createDataProvider() {
		LOGGER.info("createDataProvider: {}",filterMode);

		switch (filterMode) {
		case Example: {
			ExampleFilterRepository<T, ID> r = (ExampleFilterRepository<T, ID>) repository;
			ExampleFilterDataProvider<T, ID> dataProvider = new ExampleFilterDataProvider<T, ID>(r,
					ExampleMatcher.matchingAny().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
							.withIgnoreCase().withIgnoreNullValues(),
					listOperationProvider.getDefaultSortOrder());
			return dataProvider;
		}
		case String: {
			//StringSearchRepository<T, ID> r = (StringSearchRepository<T, ID>) repository;
			StringSearchDataProvider<T, ID> dp = new StringSearchDataProvider<>(repository,listOperationProvider.getDefaultSortOrder());
			return dp;
		}
		case None: {
			CallbackDataProvider<T, Void> dataProvider = DataProvider.fromCallbacks(
					q -> repository.findAll(ChunkRequest.of(q, listOperationProvider.getDefaultSortOrder())).getContent().stream(),
					q -> Ints.checkedCast(repository.count()));

			return dataProvider;
		}
		default: throw new IllegalStateException();

		}

	}

	public List<QuerySortOrder> getDefaultSortOrder() {
		if (defaultSortAsc)
			return QuerySortOrder.asc(defaultSortOrderProperty).build();
		else
			return QuerySortOrder.desc(defaultSortOrderProperty).build();
	}

	public T createFilterEntity() {
		try {
			return entityClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException("Could not instantiate class " + entityClass);
		}
	}

	public void updateCount() {

		if (showHeader) {
			Long full = getCountAll();
			Integer shown = dataProvider.size(new Query<>());
			count.setText("Showing " + shown + " of " + full);
		}
	}

	public Long getCountAll() {
		switch(filterMode) {
		case Example: return repository.count();
		case None: return null;
		case String: return ((StringSearchDataProvider<T, Serializable>)dataProvider).countAll();
		default: throw new IllegalStateException();
		}
	}

	@SuppressWarnings("unchecked")
	public void changeFilter(String text) {
		if(!caseSensitive) text = StringUtils.lowerCase(text);
		switch (filterMode) {
		case Example:
			if (StringUtils.isBlank(text))
				listOperationProvider.clearFilter();
			else
				listOperationProvider.setFilter(text);
			break;
		case String:
			((StringSearchDataProvider<T, ID>) dataProvider).setFilter(text);
			break;
		case None:
			break;
		default:
			break;
		}

		dataProvider.refreshAll();
		listOperationProvider.updateCount();
	}

	public void clickNew(ClickEvent<Button> click) {
		if (Dialog.class.isAssignableFrom(entityView)) {
			Dialog cd = createDialog(null);
			cd.open();
			cd.addOpenedChangeListener(change -> {
				if (cd instanceof EntityDialog) {
					EntityDialog<?> ed = (EntityDialog<?>) cd;
					if (ed.getSaved())
						listOperationProvider.refresh();
				} else
					listOperationProvider.refresh();
			});
		} else {
			UI.getCurrent().navigate(entityView);
		}
	}

	public Dialog createDialog(T entity) {
		Dialog cd = (Dialog) appCtx.getBean(entityView,entity);
		return cd;
	}

	protected EntitiesViewRefreshThread refreshThread;

	public void refreshEvery(Long ms) {

		if(ms == null) {
			if(refreshThread != null) {
				refreshThread.setStopped();
				refreshThread = null;
			}
		}
		else {
			if(refreshThread == null) {
				refreshThread = new EntitiesViewRefreshThread(this, UI.getCurrent(), ms);
				UI.getCurrent().addBeforeLeaveListener(before -> {
					refreshThread.setStopped();
				});
				refreshThread.start();
			}
			else refreshThread.setWait(ms);
		}

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

}
