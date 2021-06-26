package org.thshsh.crypt.web.view;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.thshsh.crypt.IdedEntity;
import org.thshsh.crypt.web.view.EntitiesList.FilterMode;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;

@SuppressWarnings("serial")
public abstract class EntitiesView<T,ID extends Serializable> extends VerticalLayout  implements EntitiesListProvider<T, ID> {

	public static final Logger LOGGER = LoggerFactory.getLogger(EntitiesView.class);

	@Autowired
	Breadcrumbs breadcrumbs;

	@Autowired
	ApplicationContext appCtx;

	EntitiesList<T,ID> entitiesList;

	public EntitiesView(Class<T> c, Class<? extends Component> ev) {
		entitiesList = new EntitiesList<T,ID>(c, ev, this,FilterMode.Example);
	}

	@PostConstruct
	public void postConstruct() {

		this.setHeight("100%");


		entitiesList.postConstruct(appCtx);
		entitiesList.setHeight("100%");
		add(entitiesList);

		breadcrumbs
		.resetBreadcrumbs()
		.addBreadcrumb("Home", HomeView.class)
		.addBreadcrumb(entitiesList.entityNamePlural, this.getClass());


	}

	public T getFilterEntity() {
		return entitiesList.filterEntity;
	}

	public void addButtonColumn(HorizontalLayout buttons, T e) {

		entitiesList.addButtonColumn(buttons, e);

	}


	public void delete(T entity) {
		getRepository().delete(entity);
		refresh();
	}

	public DataProvider<T,?> createDataProvider(){
		return entitiesList.createDataProvider();

	}

	public void clickNew(ClickEvent<Button> click) {
		entitiesList.clickNew(click);

	}

	public void clickEdit(ClickEvent<Button> click,T entity) {
		entitiesList.clickEdit(click, entity);

	}

	public T createFilterEntity() {
		return entitiesList.createFilterEntity();

	}

	public List<QuerySortOrder> getDefaultSortOrder() {
		return entitiesList.getDefaultSortOrder();
	}

	public void updateCount() {
		entitiesList.updateCount();

	}

	public void changeFilter(String text) {
		entitiesList.changeFilter(text);

	}

	public void refresh() {
		entitiesList.refresh();
	}

	public abstract void setupColumns(Grid<T> grid);

	@Override
	public abstract void setFilter(String text);

	@Override
	public abstract void clearFilter();

	@Override
	public ID getEntityId(T entity) {
		if(entity instanceof IdedEntity) return (ID) ((IdedEntity)entity).getId();
		else return null;
	}



}
