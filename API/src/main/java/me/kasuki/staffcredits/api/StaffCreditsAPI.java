package me.kasuki.staffcredits.api;

import lombok.Getter;
import me.kasuki.staffcredits.api.profile.IProfileHandler;

public class StaffCreditsAPI {

    @Getter
    private static StaffCreditsAPI instance;
    private IProfileHandler profileHandler;

    public StaffCreditsAPI() {
        instance = this;
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
