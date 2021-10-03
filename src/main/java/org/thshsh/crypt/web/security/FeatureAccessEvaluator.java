package org.thshsh.crypt.web.security;

import org.ilay.Access;
import org.ilay.AccessEvaluator;
import org.thshsh.crypt.web.SecurityUtils;

import com.vaadin.flow.router.Location;

public class FeatureAccessEvaluator implements AccessEvaluator<SecuredByFeatureAccess> {
		
    @Override
    public Access evaluate(Location location, Class<?> navigationTarget, SecuredByFeatureAccess ann) { 
        if(!SecurityUtils.hasAccess(ann.feature(), ann.access())) {
        	return Access.restricted(UnauthorizedException.class);
        }
        return Access.granted(); 
    }
}
