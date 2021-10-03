package org.thshsh.crypt.web.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.ilay.NavigationAnnotation;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Feature;


@NavigationAnnotation(FeatureAccessEvaluator.class) 
@Retention(RetentionPolicy.RUNTIME)
public @interface SecuredByFeatureAccess {
    Feature feature() ;  
    Access access();
}
