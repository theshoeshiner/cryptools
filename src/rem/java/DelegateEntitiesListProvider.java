package org.thshsh.crypt.web.view;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;

public abstract class DelegateEntitiesListProvider<T,ID extends Serializable> implements EntitiesListProvider<T, ID> {

	@Autowired
	ApplicationContext context;

	EntitiesList<T,ID> list;

	public DelegateEntitiesListProvider() {}

	@Override
	public DataProvider<T, ?> createDataProvider() {
		return list.createDataProvider();
	}

	@Override
	public T createFilterEntity() {
		return list.createFilterEntity();
	}



	@Override
	public void refresh() {
		list.refresh();
	}

	@Override
	public void updateCount() {
		list.updateCount();
	}




	@Override
	public void delete(T t) {
		list.delete(t);
	}

	@Override
	public void changeFilter(String text) {
		list.changeFilter(text);
	}

	@Override
	public void clickNew(ClickEvent<Button> click) {
		list.clickNew(click);
	}

	@Override
	public void clickEdit(ClickEvent<Button> click, T entity) {
		list.clickEdit(click, entity);
	}

	@Override
	public void addButtonColumn(HorizontalLayout buttons, T e) {
		list.addButtonColumn(buttons, e);
	}

	@Override
	public List<QuerySortOrder> getDefaultSortOrder() {
		return list.getDefaultSortOrder();
	}

	@Override
	public void shortcutDetails(T e) {
		// TODO Auto-generated method stub

	}



}
