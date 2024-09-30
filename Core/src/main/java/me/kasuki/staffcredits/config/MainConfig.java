package me.kasuki.staffcredits.config;

import cc.insidious.config.Config;
import cc.insidious.config.annotation.ConfigAnnotation;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.plugin.java.JavaPlugin;

public class MainConfig extends Config {
    public MainConfig(JavaPlugin plugin, String name) {
        super(plugin, name);
    }

    // Mongo

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

    // Redis
    @ConfigAnnotation(path = "redis.host")
    public static String REDIS_HOST = "127.0.0.1";
    @ConfigAnnotation(path = "redis.port")
    public static int REDIS_PORT = 6379;
    @ConfigAnnotation(path = "redis.password")
    public static String REDIS_PASSWORD = "";
    @ConfigAnnotation(path = "redis.channel")
    public static String REDIS_CHANNEL = "staffcredits";

    // Discord Settings
    @ConfigAnnotation(path = "use_discord_bot")
    public static Boolean USE_DISCORD_BOT = false;

    @ConfigAnnotation(path = "discordbot.token")
    public static String DISCORD_TOKEN = "TOKEN_HERE";

    @ConfigAnnotation(path = "discordbot.status")
    public static String DISCORD_STATUS = "Managing staff credits";

    // General Settings

    @ConfigAnnotation(path = "store.store_provider")
    public static String STORE_PROVIDER = "TEBEX";

    @ConfigAnnotation(path = "store.store_private_key")
    public static String STORE_PRIVATE_KEY = "";

    @ConfigAnnotation(path = "store.store_api_key")
    public static String STORE_API_KEY = "";
}
