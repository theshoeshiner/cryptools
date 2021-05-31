package org.thshsh.crypt;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.thshsh.cryptman.CryptmanModel;

/**
 * This is a system wide entity
 * @author TheShoeShiner
 *
 */
@Entity
@Table(schema = CryptModel.SCHEMA, name = "currency")
public class Currency extends IdedEntity {


	@Column
	String name;

	@Column
	String symbol;

	@Column
	String remoteName;

	@Column
	String remoteId;

	@ManyToOne
	Currency builtOn;

	public Currency() {}


	public Currency(String name, String symbol, String remoteId) {
		super();
		this.name = name;
		this.symbol = symbol;
		this.remoteId = remoteId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
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

	public Currency getBuiltOn() {
		return builtOn;
	}

	public void setBuiltOn(Currency builtOn) {
		this.builtOn = builtOn;
	}


	@Override
	public String toString() {
		return "[name=" + name + ", symbol=" + symbol + ", remoteName=" + remoteName + ", remoteId=" + remoteId
				+ "]";
	}



}
