package org.thshsh.cryptman;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.thshsh.crypt.IdedEntity;

/**
 * This is a system wide entity
 * @author TheShoeShiner
 *
 */
@Entity
@Table(schema = CryptmanModel.SCHEMA, name = CryptmanModel.TABLE_PREFIX+"asset")
public class Asset extends IdedEntity {

	@Column
	String name;

	@Column
	String symbol;

	@Column
	String remoteName;

}
