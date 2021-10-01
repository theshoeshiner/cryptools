package org.thshsh.crypt.web;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

/**
 * HttpSessionRequestCache that avoids saving internal framework requests.
 */
class CustomRequestCache extends HttpSessionRequestCache {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(CustomRequestCache.class);
	
	/**
	 * {@inheritDoc}
	 *
	 * If the method is considered an internal request from the framework, we skip
	 * saving it.
	 *
	 * @see SecurityUtils#isFrameworkInternalRequest(HttpServletRequest)
	 */
	@Override
	public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
		
		
		if (!SecurityUtils.isFrameworkInternalRequest(request)) {
			LOGGER.info("save request: {}",request.getRequestURL());
			super.saveRequest(request, response);
		}
		else {
			LOGGER.info("NOT saving request: {}",request.getRequestURL());
			
		}
	}

}