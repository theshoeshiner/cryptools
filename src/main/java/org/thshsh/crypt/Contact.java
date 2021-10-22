package org.thshsh.crypt;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Contact extends IdedEntity {
	
	
	
	@ManyToOne
	User user;
	
	@Column(columnDefinition = "text")
	String text;
	
	@Column
	ContactType type;
	
	@Column()
	Boolean answered;
	
	@Column()
	ZonedDateTime created;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ContactType getType() {
		return type;
	}

	public void setType(ContactType type) {
		this.type = type;
	}

	public Boolean getAnswered() {
		return answered;
	}

	public void setAnswered(Boolean answered) {
		this.answered = answered;
	}

	public ZonedDateTime getCreated() {
		return created;
	}

	public void setCreated(ZonedDateTime created) {
		this.created = created;
	}
	
	
	

}
