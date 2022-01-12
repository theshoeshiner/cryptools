package org.thshsh.crypt.web.view;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.MessageThread;
import org.thshsh.crypt.repo.MessageThreadRepository;
import org.thshsh.vaadin.ZonedDateTimeRenderer;

import com.vaadin.flow.component.grid.Grid;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class MessageThreadGrid extends AppEntityGrid<MessageThread> {

	@Autowired
	MessageThreadRepository threadRepo;
	
	public MessageThreadGrid() {
		super(MessageThread.class, null, null);
	}

	@Override
	public PagingAndSortingRepository<MessageThread, Long> getRepository() {
		return threadRepo;
	}

	@Override
	public void setupColumns(Grid<MessageThread> grid) {
		
		grid
		.addColumn(MessageThread::getSubject)
		.setHeader("Subject");
		
		
		grid
		.addColumn(new ZonedDateTimeRenderer<>(MessageThread::getLastTimestamp, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)))
		.setHeader("Last Message");
	}
	
	

}
