package org.thshsh.crypt;

public enum Grade {
	
	AA,A,BB,B,C,D,E,F,NA;
	
	public int ordinalReverse() {
		return (Grade.values().length-1) - this.ordinal();
	}
	
	public static Grade from(org.thshsh.crypt.cryptocompare.Grade g) {
		return Grade.valueOf(g.name());
	}
	

}
