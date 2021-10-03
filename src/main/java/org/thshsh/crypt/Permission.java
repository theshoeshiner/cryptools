package org.thshsh.crypt;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

@Table(schema = CryptModel.SCHEMA,indexes = @Index(columnList = "role_id"))
@Entity
public class Permission extends IdedEntity {
	
	@ManyToOne(optional = false)
	Role role;


	@Column(columnDefinition = "text",nullable = false)
	Feature feature;
	
	@Column(columnDefinition = "text",nullable = false)
	Access access;	
	
	public Permission(Feature feature, Access access) {
		super();
		this.feature = feature;
		this.access = access; 
	}

	public Permission() {}
	
	public Feature getFeature() {
		return feature;
	}
	public void setFeature(Feature feature) {
		this.feature = feature;
	}
	public Access getAccess() {
		return access;
	}
	public void setAccess(Access access) {
		this.access = access;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "[feature=" + feature + ", access=" + access + "]";
	}

	public static Permission of(Feature f, Access a) {
		return new Permission(f,a);
	}
	
}
