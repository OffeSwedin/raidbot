package me.cbitler.raidbot.raids;

/**
 * Represents a role that is available in a raid
 * @author Christopher Bitler
 */
public class RaidRole {
    public final int amount;
    public final String name;

    /**
     * Create a new RaidRole object
     * @param amount The max amount of the role
     * @param name The name of the role
     */
    public RaidRole(int amount, String name) {
        this.amount = amount;
        this.name = name;
    }
}
