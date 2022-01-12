package org.thshsh.crypt;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Message extends IdedEntity {
	
	@ManyToOne
	MessageThread thread;
	
	@Column(columnDefinition = "text")
	String text;
	
	@Column
	ZonedDateTime timestamp;
	
	@ManyToOne
	User from;

}
