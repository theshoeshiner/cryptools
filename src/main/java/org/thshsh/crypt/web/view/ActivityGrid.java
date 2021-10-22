package org.thshsh.crypt.web.view;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Activity;
import org.thshsh.crypt.ActivityType;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.ActivityRepository;
import org.thshsh.vaadin.FunctionUtils;
import org.thshsh.vaadin.ZonedDateTimeRenderer;

import com.vaadin.flow.component.grid.Grid;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class ActivityGrid extends AppEntityGrid<Activity, Long> {

	@Autowired
	ActivityRepository actRepo;
	
	public ActivityGrid() {
		super(Activity.class, null, FilterMode.Example);
		this.defaultSortOrderProperty = "timestamp";
		this.defaultSortAsc=false;
	}

	@Override
	public PagingAndSortingRepository<Activity, Long> getRepository() {
		return actRepo;
	}

	@Override
	public void setupColumns(Grid<Activity> grid) {


		grid
		.addColumn(new ZonedDateTimeRenderer<>(Activity::getTimestamp, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT) ))
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
		.addColumn(FunctionUtils.nestedValue(Activity::getUser, User::getUserName))
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
	
	

}
