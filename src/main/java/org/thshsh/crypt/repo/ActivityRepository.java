package org.thshsh.crypt.repo;

import org.thshsh.crypt.Activity;
import org.thshsh.crypt.ActivityType;
import org.thshsh.crypt.User;

public interface ActivityRepository extends BaseRepository<Activity, Long>{
	
	public Activity findTopByUserAndTypeOrderByTimestampDesc(User user, ActivityType type);

}
