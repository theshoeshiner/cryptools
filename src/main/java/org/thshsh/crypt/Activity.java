package org.thshsh.crypt;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(schema = CryptModel.SCHEMA)
public class Activity extends IdedEntity {
	
	@ManyToOne
	User user;
	
	@Column
	ZonedDateTime timestamp;
	
	@Column
	ActivityType type;

	public Activity() {}
	
	public Activity(User user, ActivityType type, ZonedDateTime ts) {
		super();
		this.user = user;
		this.type = type;
		this.timestamp = ts;
	}
	
	public Activity(User user, ActivityType type) {
		super();
		this.user = user;
		this.type = type;
		this.timestamp = ZonedDateTime.now();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public ActivityType getType() {
		return type;
	}

	public void setType(ActivityType type) {
		this.type = type;
	}

	
	
}
