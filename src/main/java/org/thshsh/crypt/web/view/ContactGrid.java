package org.thshsh.crypt.web.view;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Contact;
import org.thshsh.crypt.ContactType;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.ContactRepository;
import org.thshsh.vaadin.FunctionUtils;
import org.thshsh.vaadin.ZonedDateTimeRenderer;

import com.vaadin.flow.component.grid.Grid;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class ContactGrid extends AppEntityGrid<Contact, Long> {

	@Autowired
	ContactRepository contactRepo;
	
	public ContactGrid() {
		super(Contact.class, ContactDialog.class, FilterMode.Example);
	}

	@Override
	public PagingAndSortingRepository<Contact, Long> getRepository() {
		return contactRepo;
	}
	
	@Override
	public void postConstruct() {
		this.showButtonColumn = true;
		this.showCreateButton = false;
		super.postConstruct();
	}

	@Override
	public void setupColumns(Grid<Contact> grid) {
		
		grid.addColumn(FunctionUtils.nestedValue(Contact::getUser, User::getId))
		.setHeader("User Id")
		.setFlexGrow(0)
		.setWidth("100px")
		.setSortProperty("user.id")
		;
		
		grid.addColumn(FunctionUtils.nestedValue(Contact::getUser, User::getUserName))
		.setHeader("Username")
		.setWidth("150px")
		.setFlexGrow(0)
		.setSortProperty("user.userName")
		;
		
		grid.addColumn(FunctionUtils.nestedValue(Contact::getUser, User::getEmail))
		.setHeader("User Email")
		.setWidth("250px")
		.setFlexGrow(0)
		.setSortProperty("user.email")
		;
		
		
		
		grid.addColumn(new ZonedDateTimeRenderer<>(Contact::getCreated, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)))
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
	
	

}
