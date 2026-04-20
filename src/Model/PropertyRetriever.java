package Model;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertyRetriever {
    private static Properties prop = new Properties();
    static {
        try(FileInputStream in = new FileInputStream("src/Model/DB.properties")) {
            prop.load(in);
        } catch (Exception e) {
            System.out.println("Error loading properties");
        }
    }
    public static String getUrl(){
        return prop.getProperty("url");
    }
    public static String getUser(){
        return prop.getProperty("DBuserId");
    }
    public static String getPassword() {
        return prop.getProperty("DBpassword");
    }
}