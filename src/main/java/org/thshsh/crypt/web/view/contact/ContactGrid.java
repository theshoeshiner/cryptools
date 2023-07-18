package org.thshsh.crypt.web.view.contact;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Contact;
import org.thshsh.crypt.ContactType;
import org.thshsh.crypt.User;
import org.thshsh.crypt.web.view.AppEntityGrid;
import org.thshsh.vaadin.BinderUtils;
import org.thshsh.vaadin.entity.EntityDescriptor;
import org.vaadin.addons.thshsh.easyrender.TemporalRenderer;

import com.vaadin.flow.component.grid.Grid;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class ContactGrid extends AppEntityGrid<Contact> {


	public ContactGrid() {
		super(ContactDialog.class, FilterMode.Example);
	}

	@Override
	public void postConstruct() {
		this.appendButtonColumn = true;
		this.showCreateButton = false;
		super.postConstruct();
	}

	@Override
	public void setupColumns(Grid<Contact> grid) {
		
		grid.addColumn(BinderUtils.nestedValue(Contact::getUser, User::getId))
		.setHeader("User Id")
		.setFlexGrow(0)
		.setWidth("100px")
		.setSortProperty("user.id")
		;
		
		grid.addColumn(BinderUtils.nestedValue(Contact::getUser, User::getUserName))
		.setHeader("Username")
		.setWidth("150px")
		.setFlexGrow(0)
		.setSortProperty("user.userName")
		;
		
		grid.addColumn(BinderUtils.nestedValue(Contact::getUser, User::getEmail))
		.setHeader("User Email")
		.setWidth("250px")
		.setFlexGrow(0)
		.setSortProperty("user.email")
		;
		
		
		
		grid.addColumn(new TemporalRenderer<>(Contact::getCreated, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)))
		.setHeader("Time")
		.setFlexGrow(0)
		.setWidth("150px")
		.setSortProperty("created")
		;
		
		grid.addColumn(Contact::getType)
		.setHeader("Type")
		.setFlexGrow(0)
		.setWidth("100px")
		.setSortProperty("type")
		;
		
		grid.addColumn(Contact::getAnswered)
		.setHeader("Answered")
		.setFlexGrow(0)
		.setWidth("100px")
		.setSortProperty("answered")
		;
		
		grid.addColumn(Contact::getText)
		.setHeader("Text")
		.setFlexGrow(1)
		.setSortProperty("text")
		;
	}

	@Override
	public void setFilter(String text) {
		filterEntity.setText(text);
		filterEntity.setUser(new User(text,text,text));
		filterEntity.setType(EnumUtils.getEnumIgnoreCase(ContactType.class, text));
		if(NumberUtils.isParsable(text)) filterEntity.setId(NumberUtils.toLong(text));
	}

	@Override
	public void clearFilter() {
		filterEntity.setText(null);
		filterEntity.setUser(null);
		filterEntity.setType(null);
		filterEntity.setId(null);
	}

	@Override
	@Autowired
	public void setDescriptor(EntityDescriptor<Contact, Long> descriptor) {
		super.setDescriptor(descriptor);
	}

	@Override
	@Autowired
	public void setRepository(Repository<Contact, Long> repository) {
		super.setRepository(repository);
	}
	
	

}
