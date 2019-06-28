package me.cbitler.raidbot.raids;

import java.sql.Timestamp;

/**
 * Represents a raid user This class is not commented as the method names should
 * be self-explanatory
 * 
 * @author Christopher Bitler
 */
public class RaidUser {
	public final String id;
	public final String spec;
	public final String role;
	public final Timestamp signupTime;
	public String name;
	public String signupStatus;

	public RaidUser(String id, String name, String spec, String role, Timestamp signupTime) {
		this.id = id;
		this.name = name;
		this.spec = spec;
		this.role = role;
		this.signupStatus = "0";
		this.signupTime = signupTime;
	}

	public RaidUser(String id, String name, String spec, String role, String signupStatus, Timestamp signupTime) {
		this.id = id;
		this.name = name;
		this.spec = spec;
		this.role = role;
		this.signupStatus = signupStatus;
		this.signupTime = signupTime;
	}

	public void bench() {
		signupStatus = "1";
	}

	public void accept() {
		signupStatus = "2";
	}

	public boolean isBenched() {
		return signupStatus.equals("1");
	}

	public boolean isAccepted() {
		return signupStatus.equals("2");
	}

}
