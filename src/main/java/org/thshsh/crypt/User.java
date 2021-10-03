package org.thshsh.crypt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Table(schema = CryptModel.SCHEMA, name = "user_account")
@Entity
public class User extends IdedEntity {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(User.class);

	/*@Column
	String firstName;
	
	@Column
	String lastName;*/

	@Column
	String displayName;

	@Column
	String userName;

	@Column
	String email;
	
	@Column
	String apiKey;

	@Column
	String password;
	
	@Column
	Boolean confirmed;
	
	@Column
	String confirmToken;
	
	@ManyToMany()
	@JoinTable(schema = CryptModel.SCHEMA,name="user_account_role",
	joinColumns = @JoinColumn(name="user_id"),inverseJoinColumns = @JoinColumn(name="role_id"))
	Set<Role> roles;
	
	@Transient
	Map<Feature,Access> permissionsMap;

	public User() {}

	public User(String name, String email, String un, String pass) {
		super();
		this.displayName = name;
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

	/*public String getFirstName() {
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
	}*/

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}




	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String cryptoCompareApiKey) {
		this.apiKey = cryptoCompareApiKey;
	}

	public Boolean getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

	public String getConfirmToken() {
		return confirmToken;
	}

	public void setConfirmToken(String confirmToken) {
		this.confirmToken = confirmToken;
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
	
	

	public Set<Role> getRoles() {
		if(roles == null) roles = new HashSet<>();
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	

	public Map<Feature,Access> getPermissionsMap() {
		LOGGER.info("getPermissionsMap");
		if(permissionsMap == null) {
			permissionsMap = new HashMap<>();
			getRoles().forEach(role -> {
				LOGGER.info("role: {}",role);
				role.getPermissions().forEach(perm -> {
					LOGGER.info("perm: {}",perm);
					if(!permissionsMap.containsKey(perm.getFeature())) permissionsMap.put(perm.getFeature(), perm.getAccess());
					else if(!permissionsMap.get(perm.getFeature()).isGreaterThanOrEqual(perm.getAccess())) permissionsMap.put(perm.getFeature(), perm.getAccess());
				});
			});
		}
		return permissionsMap;
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
