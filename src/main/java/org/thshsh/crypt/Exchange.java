package org.thshsh.crypt;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.thshsh.cryptman.CryptmanModel;

/**
 * System wide entity
 * @author TheShoeShiner
 *
 */
@Entity
@Table(schema = CryptModel.SCHEMA, name = "exchange")
public class Exchange extends IdedEntity {

	@Column
	String name;

	@Column
	String remoteName;

	@Column
	String remoteId;

	public Exchange() {}


	public Exchange(String name, String remoteId, String remoteName) {
		super();
		this.name = name;
		this.remoteId = remoteId;
		this.remoteName = remoteName;
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


	@Override
	public String toString() {
		return "[name=" + name + ", remoteName=" + remoteName + ", remoteId=" + remoteId + "]";
	}



}
