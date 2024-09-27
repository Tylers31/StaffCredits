package me.kasuki.staffcredits.config;

import cc.insidious.config.Config;
import cc.insidious.config.annotation.ConfigAnnotation;
import org.bukkit.plugin.java.JavaPlugin;

public class MainConfig extends Config {
    public MainConfig(JavaPlugin plugin, String name) {
        super(plugin, name);
    }

    @ConfigAnnotation(path = "mongo.host")
    public static String MONGO_HOST = "127.0.0.1";
    @ConfigAnnotation(path = "mongo.port")
    public static int MONGO_PORT = 27017;
    @ConfigAnnotation(path = "mongo.auth")
    public static boolean MONGO_AUTH = false;
    @ConfigAnnotation(path = "mongo.username")
    public static String MONGO_USERNAME = "";
    @ConfigAnnotation(path = "mongo.password")
    public static String MONGO_PASSWORD = "";
    @ConfigAnnotation(path = "mongo.database")
    public static String MONGO_DATABASE = "staffcredits";
    @ConfigAnnotation(path = "mongo.uri_mode")
    public static boolean MONGO_URI_MODE = false;
    @ConfigAnnotation(path = "mongo.uri")
    public static String MONGO_URI = "";
}
