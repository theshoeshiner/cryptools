package org.cryptools;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.Test;
import org.ocpsoft.prettytime.PrettyTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PrettyTimeTest {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(PrettyTimeTest.class);

	@Test
	public void test() {
		
		LocalDateTime now = LocalDateTime.now();
		PrettyTime prettyTime = new PrettyTime(now);

		
		Duration d = Duration.ofHours(2);
		LocalDateTime then = now.plus(d);
		org.ocpsoft.prettytime.Duration pd =prettyTime.approximateDuration(then);
		
		LOGGER.info("pd: {}",pd);
		LOGGER.info("pd: {}",prettyTime.format(pd));
		
		
	}
	
}
