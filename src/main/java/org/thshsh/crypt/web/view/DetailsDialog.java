package org.thshsh.crypt.web.view;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class DetailsDialog<T,ID extends Serializable> extends Dialog {

	T entity;
	EntitiesList<T,ID> list;
	VerticalLayout layout;

	public DetailsDialog(T entity,EntitiesList<T,ID> el){
		this.entity = entity;
		this.list = el;
	}

	@PostConstruct
	public void postConstruct() {
		this.setWidth("800px");
		layout = new VerticalLayout();
		add(layout);

		ID id = list.getEntityId(entity);
		String name = list.getEntityName(entity);


		layout.add(new Span(new Span("Id: "),new Span(Objects.toString(id))));
		layout.add(new Span(new Span("Name: "),new Span(Objects.toString(name))));
	}

}
