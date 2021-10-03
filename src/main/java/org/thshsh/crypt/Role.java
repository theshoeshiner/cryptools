package org.thshsh.crypt;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Table(schema = CryptModel.SCHEMA, name = "role")
@Entity
public class Role extends IdedEntity{
	
	public static final Logger LOGGER = LoggerFactory.getLogger(Role.class);


	@ManyToMany(mappedBy = "roles")
	Set<User> users;
	
	@Column(columnDefinition = "text")
	String name;
	
	@Column(columnDefinition = "text")
	String key;
	
	@OneToMany(orphanRemoval = true,mappedBy = "role",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	Set<Permission> permissions;

	@Column
	Integer priority;
	
	public Role() {}
	
	public Role(String name) {
		this.name = name;
		this.key = name.toLowerCase().replaceAll("\\s", "_"); 
	}

	public void update(Role r) {
		LOGGER.info("update: {} to {}",this,r);
		this.key = r.key;
		this.name = r.name;
		//this.setPermissions(r.getPermissions());
		this.updatePermissions(r.getPermissions().toArray(new Permission[0]));
		LOGGER.info("permissions: {}",getPermissions());
		this.priority = r.priority;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

	public Set<User> getUsers() {
		if(users == null) users = new HashSet<>();
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public Permission getPermission(Feature feature) {
		for(Permission p : getPermissions()) {
			if(p.getFeature() == feature) return p;
		}
		return null;
	}
	
	public Permission getOrCreatePermission(Feature feature) {
		Permission p = getPermission(feature);
		if(p == null) {
			p = new Permission(feature, null);
			p.setRole(this);
			getPermissions().add(p);
		}
		return p;
	}

	public Set<Permission> getPermissions() {
		if(permissions == null) permissions = new HashSet<>();
		return permissions;
	}

	public void setPermissions(Set<Permission> p) {
		p.forEach(pm -> pm.setRole(this));
		getPermissions().clear();
		getPermissions().addAll(p);
		
	}
	
	/*public void addPermissions(Set<Permission> ps) {
		ps.forEach(p->p.setRole(this));
		getPermissions().addAll(ps);
	}*/
	
	public void setPermissions(Collection<Permission> p) {
		if( !(permissions instanceof Set)) p = new HashSet<>(permissions);
		this.permissions = (Set<Permission>) p;
		if(permissions != null) permissions.forEach(pm -> pm.setRole(this));
	}
	
	/*public Map<Feature,Access> getPermissionsMap() {
		Map<Feature,Access> permMap = new HashMap<>();
		getPermissions().forEach(perm -> {
			if(!permMap.containsKey(perm.getFeature())) permMap.put(perm.getFeature(), perm.getAccess());
			else if(!permMap.get(perm.getFeature()).isGreaterThanOrEqual(perm.getAccess())) permMap.put(perm.getFeature(), perm.getAccess());
		});
		return permMap;
	}*/
	
	public void updateAllPermissions(Access a) {
		for(Feature f : Feature.values()) {
			getOrCreatePermission(f).setAccess(a);
		}
	}
	
	public void updatePermissions(Permission... ps) {
		Set<Permission> newset= new HashSet<>();
		for(Permission p : ps) {
			Permission current = getPermission(p.getFeature());
			if(current != null) {
				current.setAccess(p.getAccess());
				newset.add(current);
			}
			else {
				newset.add(p);
			}
		}
		setPermissions(newset);
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@Override
	public String toString() {
		return "[name=" + name + ", key=" + key + ", priority=" + priority + "]";
	}

	//TODO fix the multiselect and remove this
	//hashcodes generate collisions and causes selection issues
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	

	
}
