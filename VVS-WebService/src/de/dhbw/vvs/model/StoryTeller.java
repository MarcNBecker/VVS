package de.dhbw.vvs.model;

public class StoryTeller {
	
	private String say;
	
	public StoryTeller(String story) {
		this.say = story;
	}
	
	public String saySomething() {
		return say;
	}
	
}
