package org.thshsh.cryptman;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.thshsh.crypt.IdedEntity;

@Entity
@Table(schema = CryptmanModel.SCHEMA, name = "entity")
public class TestEntity extends IdedEntity {

	
	
}
