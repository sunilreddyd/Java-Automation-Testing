package com.cai.qa.core;

public enum SleepTime {
	TOO_HIGH (20), 	HIGH (15), 	MEDIUM(10), LOW(5), TOO_LOW(1);
	//TOO_HIGH (40), 	HIGH (25), 	MEDIUM(20), LOW(15), TOO_LOW(5);

	private final double sleepTimeValue;
	
	SleepTime(double sleepTimeValue){
		this.sleepTimeValue = sleepTimeValue;
	}
	public double getSleepTimeValue(){
		return this.sleepTimeValue;
	}
}
