package org.thshsh.crypt;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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
public class Currency extends IdedEntity implements HasImage  {

	@Column
	String name;

	@Column(name = "symbol")
	String key;

	@Column
	String remoteName;

	@Column
	String remoteId;

	@Column
	String imageUrl;

	//@Column
	//Boolean fiat;

	@Column
	@Enumerated(EnumType.STRING)
	PlatformType platformType;

	@ManyToOne(fetch = FetchType.LAZY)
	Currency builtOn;

	/*
	 13:19:56.186 [main] INFO  org.cryptax.CryptoCompareTest - platformtypes: [null, blockchain, derivative, token]
13:19:56.186 [main] INFO  org.cryptax.CryptoCompareTest - builton: [null, XLM,ETH, Mainnet,BNB, NAS, TRX,ETH, VET, RSK Network, SOL,ETH, ETH,BNB,FTM, mainnet,BNB, HT, WAVES, ETH/BNB, mainnet,ETH, ONT, XLM, ETH,BNB,mainnet, 2017-10-25, TRX, QTUM, Mainnet,NEO,ETH, LUNA, luniverse, ETH,WAN, KAVA, IOST, BNB,TRX,ETH, ETH,BNB,NEO,NULS, EOS, GO, WAVES,ETH, STRAX, ETH,BTC,EOS,TRX,ALGO,SLP,OMG, ETH,BNB, ETH,BNB,DOT,ALGO,ADA, BNB, ETH, NEO, ETH,ZIL, OMNI, XMR]
	 */

	public Currency() {}

	public Currency(String name, String symbol, PlatformType type) {
		super();
		this.name = name;
		this.key = symbol;
		this.platformType = type;
	}


	public Currency(String name, String symbol, String remoteId) {
		super();
		this.name = name;
		this.key = symbol;
		this.remoteId = remoteId;
	}

	public String getDisplayName() {
		return name+" ("+key+")";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String symbol) {
		this.key = symbol;
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




	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public PlatformType getPlatformType() {
		return platformType;
	}



	public void setPlatformType(PlatformType platformType) {
		this.platformType = platformType;
	}


	@Override
	public String toString() {
		return "[name=" + name + ", symbol=" + key + ", remoteName=" + remoteName + ", remoteId=" + remoteId
				+ "]";
	}



}
