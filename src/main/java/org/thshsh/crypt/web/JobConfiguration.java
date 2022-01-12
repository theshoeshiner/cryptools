package org.thshsh.crypt.web;

public class JobConfiguration {
	
	Boolean history = true;
	
	Long minutes = 30l;

	public Boolean getHistory() {
		return history;
	}

	public void setHistory(Boolean history) {
		this.history = history;
	}

	public Long getMinutes() {
		return minutes;
	}

	public void setMinutes(Long minutes) {
		this.minutes = minutes;
	}
	
	

}
