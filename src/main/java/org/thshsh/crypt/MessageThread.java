package org.thshsh.crypt;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class MessageThread extends IdedEntity {
	
	@ManyToOne
	User to;
	
	@Column
	String subject;
	
	@Column
	ZonedDateTime lastTimestamp;

	public User getTo() {
		return to;
	}

	public void setTo(User to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String title) {
		this.subject = title;
	}

	public ZonedDateTime getLastTimestamp() {
		return lastTimestamp;
	}

	public void setLastTimestamp(ZonedDateTime lastTimestamp) {
		this.lastTimestamp = lastTimestamp;
	}
	
	

}
