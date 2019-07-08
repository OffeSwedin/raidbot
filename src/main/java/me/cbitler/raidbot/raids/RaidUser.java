package me.cbitler.raidbot.raids;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.database.Database;
import me.cbitler.raidbot.database.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Represents a raid user This class is not commented as the method names should
 * be self-explanatory
 * 
 * @author Christopher Bitler
 */
public class RaidUser {
	private static final Logger log = LoggerFactory.getLogger(RaidUser.class);

	public final String id;
	public final String spec;
	public final Timestamp signupTime;
	public final String raidId;
	public String name;
	public String role;
	public String signupStatus;

	public RaidUser(String id, String name, String spec, String role, Timestamp signupTime, String raidId) {
		this.id = id;
		this.name = name;
		this.spec = spec;
		this.role = role;
		this.signupStatus = "0";
		this.signupTime = signupTime;
		this.raidId = raidId;
	}

	public RaidUser(String id, String name, String spec, String role, String signupStatus, Timestamp signupTime, String raidId) {
		this.id = id;
		this.name = name;
		this.spec = spec;
		this.role = role;
		this.signupStatus = signupStatus;
		this.signupTime = signupTime;
		this.raidId = raidId;
	}

	public void bench() {
		signupStatus = "1";
	}

	public void accept() {
		signupStatus = "2";
	}

	public void noShow() { signupStatus = "3"; }

	public boolean isBenched() {
		return signupStatus.equals("1");
	}

	public boolean isAccepted() {
		return signupStatus.equals("2");
	}

	public boolean isNoShow() { return signupStatus.equals("3"); }

	/**
	 * Save the raiduser in the database
	 */
	public boolean save() {
		RaidBot bot = RaidBot.getInstance();
		Database db = bot.getDatabase();

		try {
			QueryResult results = db.query("SELECT COUNT(*) FROM `raidUsers` WHERE `userId` = ? AND `raidId` = ?", new String[] { id, raidId });
			results.getResults().next();
			int count = Integer.parseInt(results.getResults().getString("COUNT(*)"));

			results.getResults().close();
			results.getStmt().close();

			if (count == 0) {
				db.update("INSERT INTO `raidUsers` (`userId`, `username`, `spec`, `role`, `raidId`, `signupStatus`, `signupTime`)"
						+ " VALUES (?,?,?,?,?,?,?)", new String[]{id, name, spec, role, raidId, signupStatus, Long.toString(signupTime.getTime())});
			} else {
				db.update("UPDATE `raidUsers` SET `signupStatus` = ?, `username` = ?, `role` = ? WHERE `userId` = ? AND `raidId` = ?",
						new String[]{signupStatus, name, role, id, raidId});
			}
		} catch (SQLException e) {
			log.error("Could not update user in raid. ", e);
			return false;
		}

		return true;
	}
}
