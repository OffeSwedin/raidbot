package me.cbitler.raidbot.utility;

import java.util.Comparator;

import me.cbitler.raidbot.raids.RaidUser;

public class TimestampComparator implements Comparator<RaidUser> {
	@Override
	public int compare(RaidUser r1, RaidUser r2) {
		if (r1.signupTime.equals(r2.signupTime)) {
			return 0;
		} else if (r1.signupTime.after(r2.signupTime)) {
			return 1;
		} else {
			return -1;
		}
	}
}
