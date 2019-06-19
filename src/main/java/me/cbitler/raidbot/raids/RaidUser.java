package me.cbitler.raidbot.raids;

import java.sql.Timestamp;
import java.util.Comparator;

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
	public String signupStatus;
	public final Timestamp signupTime;

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
		if (signupStatus.equals("1")) {
			return true;
		}
		return false;
	}

	public boolean isAccepted() {
		if (signupStatus.equals("2")) {
			return true;
		}
		return false;
	}

}
