package org.thshsh.crypt.web.view;

import org.thshsh.crypt.MessageThread;
import org.thshsh.vaadin.entity.EntityGrid;
import org.thshsh.vaadin.entity.EntityGridView;


public class MessageThreadView extends EntityGridView<MessageThread, Long>{

	public MessageThreadView() {
		super(MessageThreadGrid.class);
	}

}
