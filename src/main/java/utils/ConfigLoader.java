package utils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties file not found!");
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to get property value
    public static String getPackageName() {
        return properties.getProperty("package.name");
    }
    
    public static String getProjectName() {
        return properties.getProperty("project.name");
    }
    
    public static void main(String[] args) {
    	System.out.println(ConfigLoader.getPackageName());
        System.out.println(ConfigLoader.getProjectName());
    }
}
