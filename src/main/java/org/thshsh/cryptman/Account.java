package org.thshsh.cryptman;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.thshsh.crypt.CryptModel;
import org.thshsh.crypt.IdedEntity;
import org.thshsh.crypt.User;

@Entity
@Table(schema = CryptmanModel.SCHEMA, name = CryptmanModel.TABLE_PREFIX+"account")
public class Account extends IdedEntity {

	@ManyToOne
	Exchange exchange;

	@ManyToOne
	User user;



}
