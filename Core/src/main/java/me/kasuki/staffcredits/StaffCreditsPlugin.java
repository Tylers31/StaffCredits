package me.kasuki.staffcredits;

import cc.insidious.fethmusmioma.CommandHandler;
import com.samjakob.spigui.SpiGUI;
import lombok.Getter;
import me.kasuki.staffcredits.api.StaffCreditsAPI;
import me.kasuki.staffcredits.api.database.mongodb.MongoHandler;
import me.kasuki.staffcredits.commands.CommandStaffCredits;
import me.kasuki.staffcredits.commands.CommandAdminStaffCredits;
import me.kasuki.staffcredits.config.MainConfig;
import me.kasuki.staffcredits.listener.ChatListener;
import me.kasuki.staffcredits.profile.listener.ProfileListener;
import me.kasuki.staffcredits.profile.ProfileHandler;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class StaffCreditsPlugin extends JavaPlugin {
    public StaffCreditsPlugin instance;
    private SpiGUI spiGUI;

    private StaffCreditsAPI staffCreditsAPI;

    private MongoHandler mongoHandler;

    private MainConfig mainConfig;


    public List<UUID> pendingRequests = new ArrayList<>();


    @Override
    public void onEnable() {
        instance = this;

        this.registerConfig();

        this.mongoHandler = new MongoHandler(MainConfig.MONGO_HOST, MainConfig.MONGO_PORT,
                MainConfig.MONGO_AUTH, MainConfig.MONGO_USERNAME, MainConfig.MONGO_PASSWORD,
                MainConfig.MONGO_DATABASE, MainConfig.MONGO_URI_MODE, MainConfig.MONGO_URI);

        this.registerCommands();
        this.registerListeners();

        this.staffCreditsAPI = new StaffCreditsAPI();
        this.spiGUI = new SpiGUI(this);

        this.staffCreditsAPI.setProfileHandler(new ProfileHandler(this));
    }

    @Override
    public void onDisable() {
        StaffCreditsAPI api = this.staffCreditsAPI;
        api.getProfileHandler().unload();
    }

    public void registerCommands(){
        CommandHandler handler = new CommandHandler(this, "staffcredits");
        handler.registerCommand(new CommandStaffCredits(this));
        handler.registerCommand(new CommandAdminStaffCredits(this));
    }

    public void registerListeners(){
        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new ProfileListener(this), this);
        manager.registerEvents(new ChatListener(this), this);
    }

    public void registerConfig(){
        this.mainConfig = new MainConfig(this, "config");
    }
}
