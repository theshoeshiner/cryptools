package org.thshsh.crypt.web.security;

import org.springframework.data.spel.spi.EvaluationContextExtension;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.web.AppSession;

@Component
public class SessionEvaluationContextExtension implements EvaluationContextExtension {

	  @Override
	  public String getExtensionId() {
	    return "session";
	  }

	  @Override
	  public AppSession getRootObject() {
	    return AppSession.getCurrent();
	  }
	}
