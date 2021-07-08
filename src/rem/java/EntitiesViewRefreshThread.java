package org.thshsh.crypt.web.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.StoppableThread;
import org.thshsh.util.concurrent.ThreadUtils;

import com.vaadin.flow.component.UI;

@Component
@Scope("prototype")
public class EntitiesViewRefreshThread extends StoppableThread {

	public static final Logger LOGGER = LoggerFactory.getLogger(EntitiesViewRefreshThread.class);

	protected EntitiesList<?, ?> entitiesList;
	protected UI ui;
	protected Long wait;

	public EntitiesViewRefreshThread(EntitiesList<?, ?> view, UI ui,Long wait) {
		super();
		this.entitiesList = view;
		this.ui = ui;
		this.wait = wait;
	}

	public Long getWait() {
		return wait;
	}

	public void setWait(Long wait) {
		this.wait = wait;
	}

	public void run() {
		while(!isStopped()) {
			//clear interrupted status
			Thread.interrupted();
			//LOGGER.info("ui attached: {}",ui.isAttached());
			//if(!ui.isAttached()) {
				ui.access(() -> {
					//LOGGER.info("ui attached: {}",ui.isAttached());
					//LOGGER.info("entitiesList attached: {}",entitiesList.isAttached());
					try {
						entitiesList.refresh();
						ui.push();
					}
					catch(RuntimeException re) {
						LOGGER.warn("Refresh thread stopping due to exception",re);
						setStopped();
					}
				});
				sleepSafe(wait);
				/*}
				else {
					LOGGER.info("UI is no longer attached, stopping refresh thread");
					this.setStopped();
				}*/
		}
	}

	public void refreshIn(Long wait) {
		Thread t = new Thread(() -> {
			ThreadUtils.sleepSafe(wait);
			this.interrupt();
		});
		t.start();
	}

}