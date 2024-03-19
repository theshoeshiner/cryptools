package org.thshsh.crypt.web.view.activity;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Activity;
import org.thshsh.crypt.ActivityType;
import org.thshsh.crypt.User;
import org.thshsh.crypt.web.view.AppEntityGrid;
import org.thshsh.vaadin.BinderUtils;
import org.thshsh.vaadin.entity.EntityDescriptor;
import org.vaadin.addons.thshsh.easyrender.TemporalRenderer;

import com.vaadin.flow.component.grid.Grid;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class ActivityGrid extends AppEntityGrid<Activity> {

	
	public ActivityGrid() {
		super(null, FilterMode.Example);
		this.defaultSortOrderProperties = Arrays.asList("timestamp");
		this.defaultSortAsc=false;
	}



	@Override
	public void setupColumns(Grid<Activity> grid) {


		grid
		.addColumn(new TemporalRenderer<>(Activity::getTimestamp, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT) ))
		.setHeader("Timestamp")
		.setSortProperty("timestamp")
		.setWidth("150px")
		.setFlexGrow(0)
		;
		
		grid.addColumn(Activity::getType)
		.setHeader("Type")
		.setSortProperty("type")
		.setWidth("100px")
		.setFlexGrow(0);
		
		grid
		.addColumn(BinderUtils.nestedValue(Activity::getUser, User::getUserNameOrEmail))
		.setHeader("User")
		.setSortProperty("user.userName")
		;

	}

	@Override
	public void setFilter(String text) {
		if(EnumUtils.isValidEnumIgnoreCase(ActivityType.class, text)){
			filterEntity.setType(EnumUtils.getEnumIgnoreCase(ActivityType.class, text));
		}
		filterEntity.setUser(new User(text,text,text));
	}

	@Override
	public void clearFilter() {
		filterEntity.setUser(null);
		filterEntity.setType(null);
	}



	@Override
	@Autowired
	public void setDescriptor(EntityDescriptor<Activity, Long> descriptor) {
		super.setDescriptor(descriptor);
	}



	@Override
	@Autowired
	public void setRepository(Repository<Activity, Long> repository) {
		super.setRepository(repository);
	}
	
	

}
