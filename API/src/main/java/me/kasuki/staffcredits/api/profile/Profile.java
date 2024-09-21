package me.kasuki.staffcredits.api.profile;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter @Setter
public class Profile {

    @SerializedName("_id")
    private final UUID uniqueId;

    private double credits;
    private double lifetimeCredits;

    public Profile(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.credits = 0;
        this.lifetimeCredits = 0;
    }
}