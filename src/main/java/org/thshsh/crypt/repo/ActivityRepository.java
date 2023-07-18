package org.thshsh.crypt.repo;

import org.thshsh.crypt.Activity;
import org.thshsh.crypt.ActivityType;
import org.thshsh.crypt.User;
import org.thshsh.vaadin.data.QueryByExampleRepository;

public interface ActivityRepository extends BaseRepository<Activity, Long>, QueryByExampleRepository<Activity, Long> {
	
	public Activity findTopByUserAndTypeOrderByTimestampDesc(User user, ActivityType type);

}
