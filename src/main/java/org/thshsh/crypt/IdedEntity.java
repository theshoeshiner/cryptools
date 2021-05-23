package org.thshsh.crypt;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;

@MappedSuperclass
public abstract class IdedEntity {
	
	@Id
	@Column(nullable = false)
	@SequenceGenerator(name="entity_id",initialValue = 1,allocationSize = 50,sequenceName = "entity_id")
	@GeneratedValue(generator = "entity_id",strategy = GenerationType.SEQUENCE)
	protected Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		/*final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;*/
		return 31;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdedEntity other = (IdedEntity) obj;
		if (id == null) {
			//if (other.id != null)
			return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
}
