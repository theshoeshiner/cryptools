package org.thshsh.crypt;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * System wide entity
 * @author TheShoeShiner
 *
 */
@Entity
@Table(schema = CryptModel.SCHEMA, name = "exchange")
public class Exchange extends IdedEntity implements HasImage {

	@Column
	String name;

	@Column(unique = true)
	String key;

	@Column
	String remoteName;

	@Column
	String remoteId;

	@Column
	String imageUrl;
	
	@Column
	@Enumerated(EnumType.ORDINAL)
	Grade grade;

	public Exchange() {}


	public Exchange(String name, String remoteId, String remoteName, String image,Grade g) {
		super();
		this.name = name;
		this.remoteId = remoteId;
		this.remoteName = remoteName;
		this.imageUrl = image;
		this.grade = g;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemoteName() {
		return remoteName;
	}

	public void setRemoteName(String remoteName) {
		this.remoteName = remoteName;
	}

	public String getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(String remoteId) {
		this.remoteId = remoteId;
	}



	public String getImageUrl() {
		return imageUrl;
	}


	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}


	@Override
	public String toString() {
		return "[name=" + name + ", remoteName=" + remoteName + ", remoteId=" + remoteId + "]";
	}


	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}


	public Grade getGrade() {
		return grade;
	}


	public void setGrade(Grade grade) {
		this.grade = grade;
	}



}
