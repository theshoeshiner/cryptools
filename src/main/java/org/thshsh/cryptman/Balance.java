package org.thshsh.cryptman;

import java.math.BigDecimal;

import javax.persistence.BigDecimalToStringConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.thshsh.crypt.IdedEntity;

@Entity
@Table(schema = CryptmanModel.SCHEMA, name = CryptmanModel.TABLE_PREFIX+"balance")
public class Balance extends IdedEntity {

	@ManyToOne
	Account account;

	@ManyToOne
	Asset asset;

	@Column
	@Convert(converter = BigDecimalToStringConverter.class)
	BigDecimal balance;

}
