package org.thshsh.crypt.audit;

import org.hibernate.envers.RevisionListener;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.crypt.web.ApplicationContextService;

public class AuditRevisionListener implements RevisionListener {

	@Override
	public void newRevision(Object revisionEntity) {
		AuditRevisionEntity entity = (AuditRevisionEntity) revisionEntity;
		AppSession session = ApplicationContextService.getBean(AppSession.class);
		//entity.setUserId(session.getUser().getId());
	}

}
