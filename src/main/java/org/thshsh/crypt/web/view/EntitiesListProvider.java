package org.thshsh.crypt.web.view;

import java.io.Serializable;
import java.util.List;

import org.thshsh.vaadin.ExampleFilterDataProvider;
import org.thshsh.vaadin.ExampleFilterRepository;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.QuerySortOrder;

public interface EntitiesListProvider<T,ID extends Serializable> {

	ExampleFilterDataProvider<T, ID> createDataProvider();
	T createFilterEntity();
	ID getEntityId(T entity);
	void refresh();
	void updateCount();
	void setupColumns(Grid<T> grid);
	void setFilter(String text);
	void clearFilter();
	void changeFilter(String text);
	void clickNew(ClickEvent<Button> click);
	void clickEdit(ClickEvent<Button> click,T entity);
	void addButtonColumn(HorizontalLayout buttons, T e);
	List<QuerySortOrder> getDefaultSortOrder();
	ExampleFilterRepository<T,ID> getRepository();
	String getEntityName(T t);
	void delete(T t);
}
