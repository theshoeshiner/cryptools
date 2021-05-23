package org.thshsh.cryptman;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.thshsh.crypt.IdedEntity;

/**
 * System wide entity
 * @author TheShoeShiner
 *
 */
@Entity
@Table(schema = CryptmanModel.SCHEMA, name = CryptmanModel.TABLE_PREFIX+"exchange")
public class Exchange extends IdedEntity {

	@Column
	String name;

	@Column
	String remoteName;


}
