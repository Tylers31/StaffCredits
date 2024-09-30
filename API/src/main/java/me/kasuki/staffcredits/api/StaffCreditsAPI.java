package me.kasuki.staffcredits.api;

import lombok.Getter;
import me.kasuki.staffcredits.api.profile.IProfileHandler;
import me.kasuki.staffcredits.api.tebex.TebexAPI;

public class StaffCreditsAPI {

    @Getter
    private static StaffCreditsAPI instance;
    private IProfileHandler profileHandler;

    private TebexAPI tebexAPI;

    public StaffCreditsAPI() {
        instance = this;
    }

    public void setupTebexAPI(String apiKey, String secretKey){
        tebexAPI = new TebexAPI(apiKey, secretKey);
    }

    public TebexAPI getTebexAPI() {
        if (this.tebexAPI == null) {
            throw new NullPointerException("The TebexAPI has not been initialized");
        }

        return this.tebexAPI;
    }


    public void setProfileHandler(IProfileHandler profileHandler) {
        if (this.profileHandler != null) {
            throw new IllegalStateException("The ProfileHandler has already been initialized.");
        }

        if (profileHandler == null) {
            throw new NullPointerException("The passed ProfileHandler is currently null.");
        }

        this.profileHandler = profileHandler;
    }

    public IProfileHandler getProfileHandler() {
        if (this.profileHandler == null) {
            throw new NullPointerException("The ProfileHandler has not been initialized");
        }

        return this.profileHandler;
    }
}
