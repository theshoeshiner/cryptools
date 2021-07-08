package org.thshsh.crypt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.thshsh.crypt.web.model.LoaderModel;
import org.thshsh.crypt.web.view.LdapUser;

@Table(schema = CryptModel.SCHEMA, name = "user_account")
@Entity
public class User extends IdedEntity {

	@Column
	String firstName;

	@Column
	String lastName;

	@Column
	String displayName;

	@Column
	String userName;

	@Column
	String email;
	
	@Column
	String cryptoCompareApiKey;

	@Column
	String password;

	public User() {}

	public User(String firstName, String lastName, String email, String un, String pass) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.userName = un;
		this.email = email;
		this.password = pass;
	}


	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}




	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "[id=" + id + ", email=" + email + "]";
	}

	//TODO fix the multiselect and remove this
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

}