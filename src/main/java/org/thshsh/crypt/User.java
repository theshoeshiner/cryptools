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

@Table(schema = CryptModel.SCHEMA, name = CryptModel.TABLE_PREFIX+"user_account")
@Entity
public class User extends IdedEntity {

	@Column
	String firstName;

	@Column
	String lastName;

	@Column
	String displayName;

	@Column
	String email;

	@Column
	String directoryId;



	@Transient
	LdapUser ldapUser;

	public User() {}

	public User(String firstName, String lastName, String email) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.displayName = firstName+" "+lastName;
		this.email = email;
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

	public String getDirectoryId() {
		return directoryId;
	}

	public void setDirectoryId(String directoryId) {
		this.directoryId = directoryId;
	}



	public LdapUser getLdapUser() {
		return ldapUser;
	}

	public void setLdapUser(LdapUser ldapUser) {
		this.ldapUser = ldapUser;
	}

	public void update(LdapUser ldap) {
		this.directoryId = ldap.getId().toString();
		this.displayName = ldap.getDisplayName();
		this.email = ldap.getEmail().toLowerCase();
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
