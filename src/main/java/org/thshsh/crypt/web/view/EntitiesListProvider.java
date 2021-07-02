package org.thshsh.crypt.web.view;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;

public interface EntitiesListProvider<T,ID extends Serializable> {

	DataProvider<T, ?> createDataProvider();
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
	void shortcutDetails(T e);
	void addButtonColumn(HorizontalLayout buttons, T e);
	List<QuerySortOrder> getDefaultSortOrder();
	PagingAndSortingRepository<T, ID> getRepository();
	String getEntityName(T t);
	void delete(T t);
}
