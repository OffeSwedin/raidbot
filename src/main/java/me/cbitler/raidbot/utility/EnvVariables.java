package me.cbitler.raidbot.utility;

import java.io.*;
import java.util.HashMap;

/**
 * Class for loading variables from .env file
 * @author Christopher Bitler
 */
public class EnvVariables {
    private static EnvVariables instance;
    private final HashMap<String,String> variables = new HashMap<>();

    /**
     * Load variables from .env file
     * @throws IOException IOException
     */
    public void loadFromEnvFile() throws IOException {
        instance = this;

        File file = new File(".env");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while((line = reader.readLine()) != null) {
            String[] parts = line.split("=");
            if(parts.length >= 2) {
                String name = parts[0].trim();
                String value = parts[1].trim();
                variables.put(name, value);
            }
        }
    }

    /**
     * Get a variable that was set in the .env file
     * @param key The variable name to get the value of
     * @return The value of the variable
     */
    public String getValue(String key) {
        return variables.get(key);
    }

    public boolean isTestEnvironment(){ return variables.get("ENVIRONMENT").equalsIgnoreCase("TEST"); }

    public static EnvVariables getInstance(){ return instance; }
}
