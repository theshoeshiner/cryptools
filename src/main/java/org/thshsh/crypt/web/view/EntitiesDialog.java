package org.thshsh.crypt.web.view;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.thshsh.crypt.IdedEntity;
import org.thshsh.vaadin.ExampleFilterDataProvider;
import org.thshsh.vaadin.ExampleFilterRepository;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.QuerySortOrder;

@SuppressWarnings("serial")
public abstract class EntitiesDialog<T,ID extends Serializable> extends Dialog implements EntitiesListProvider<T, ID> {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(EntitiesDialog.class);
	
	@Autowired
	ApplicationContext appCtx;
	
	EntitiesList<T,ID> entitiesList;

	VerticalLayout layout;
	
	public EntitiesDialog(Class<T> c, Class<? extends Component> ev) {
		LOGGER.info("creating entities dialog for {}",c);
		entitiesList = new EntitiesList<T,ID>(c, ev, this); 
	}
	

	public void postConstruct() {
		LOGGER.info("postConstruct");
		entitiesList.postConstruct(appCtx);
		entitiesList.setHeight("100%");
		add(entitiesList);

	}
	
	public T getFilterEntity() {
		return entitiesList.filterEntity;
	}
	
	public void clickNew(ClickEvent<Button> click) {
		entitiesList.clickNew(click);
	}
	
	public T createFilterEntity() {
		return entitiesList.createFilterEntity();
	}
	
	public void addButtonColumn(HorizontalLayout buttons, T e) {
		entitiesList.addButtonColumn(buttons, e);
		
	}
	
	public void clickEdit(ClickEvent<Button> click,T entity) {
		entitiesList.clickEdit(click, entity);
	}
	
	public ExampleFilterDataProvider<T,ID> createDataProvider(){
		return entitiesList.createDataProvider();
	}

	public void changeFilter(String text) {
		entitiesList.changeFilter(text);
	}
	
	public void refresh() {
		entitiesList.refresh();
	}
	
	public List<QuerySortOrder> getDefaultSortOrder() {
		return entitiesList.getDefaultSortOrder();
	}
	
	public void updateCount() {
		entitiesList.updateCount();
	}
	

	@Override
	public void delete(T t) {
		//entitiesList.delete(t);
	}

	public abstract void setupColumns(Grid<T> grid);
	
	public abstract void setFilter(String text);
	
	public abstract void clearFilter();
	
	public abstract ExampleFilterRepository<T,ID> getRepository();


	@Override
	public ID getEntityId(T entity) {
		if(entity instanceof IdedEntity) return (ID) ((IdedEntity)entity).getId();
		else return null;
	}
	
	
}
