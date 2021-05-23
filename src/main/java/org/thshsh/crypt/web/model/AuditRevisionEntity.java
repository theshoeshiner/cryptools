package org.thshsh.crypt.web.model;

import java.text.DateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;


@Entity
@RevisionEntity(AuditRevisionListener.class)
@Table(name = "revinfo")
public class AuditRevisionEntity  {

	@Column()
	Long userId;

	@Id
	@SequenceGenerator(name="revision_id",initialValue = 1,allocationSize = 50,sequenceName = "revision_id")
	@GeneratedValue(generator = "revision_id",strategy = GenerationType.SEQUENCE)
	@RevisionNumber
	@Column(name = "rev")
	private int id;

	@RevisionTimestamp
	@Column(name = "revtstmp")
	private long timestamp;

	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Transient
	public Date getRevisionDate() {
		return new Date( timestamp );
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( !(o instanceof AuditRevisionEntity) ) {
			return false;
		}

		final AuditRevisionEntity that = (AuditRevisionEntity) o;
		return id == that.id
				&& timestamp == that.timestamp;
	}

	@Override
	public int hashCode() {
		int result;
		result = id;
		result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "AuditRevisionEntity(id = " + id
				+ ", revisionDate = " + DateFormat.getDateTimeInstance().format( getRevisionDate() ) + ")";
	}
	

}
