package mintey.raidbot.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
public class EnvVariables {
    private final HashMap<String,String> variables = new HashMap<>();
    public void loadFromEnvFile() throws IOException {
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
    public String getValue(String key) {
        return variables.get(key);
    }
}
