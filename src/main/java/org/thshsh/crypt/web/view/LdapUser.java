package org.thshsh.crypt.web.view;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

@Entry(objectClasses = "person")
public class LdapUser {
	
	@Id
	Name id;

	@org.springframework.ldap.odm.annotations.Attribute
	String givenName;
	
	@org.springframework.ldap.odm.annotations.Attribute(name = "sn")
	String surName;
	
	@org.springframework.ldap.odm.annotations.Attribute(name = "sAMAccountName")
	String accountName;
	
	@org.springframework.ldap.odm.annotations.Attribute(name = "cn")
	String commonName;
	
	@Attribute(name = "userPrincipalName")
	String email;
	
	public LdapUser() {}
	
	public String getDisplayName() {
		return givenName+" "+surName;
	}

	public Name getId() {
		return id;
	}

	public void setId(Name id) {
		this.id = id;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getSurName() {
		return surName;
	}

	public void setSurName(String surName) {
		this.surName = surName;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "[id=" + id + ", email=" + email + "]";
	}
	
	
}