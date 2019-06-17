package me.cbitler.raidbot.raids;

/**
 * Represents a raid user This class is not commented as the method names should
 * be self-explanatory
 * 
 * @author Christopher Bitler
 */
public class RaidUser {
	public final String id;
	public final String name;
	public final String spec;
	public final String role;
	private String signupStatus;

	public RaidUser(String id, String name, String spec, String role) {
		this.id = id;
		this.name = name;
		this.spec = spec;
		this.role = role;
		this.signupStatus = "0";
	}
	
	public RaidUser(String id, String name, String spec, String role, String signupStatus) {
		this.id = id;
		this.name = name;
		this.spec = spec;
		this.role = role;
		this.signupStatus = signupStatus;
	}

	public boolean isBenched() {
		if(signupStatus.equals("1")){
			return true;
		}
		return false;
	}

	public boolean isAccepted() {
		if(signupStatus.equals("2")){
			return true;
		}
		return false;
	}

}
