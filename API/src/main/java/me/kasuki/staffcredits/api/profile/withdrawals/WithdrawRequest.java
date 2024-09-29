package me.kasuki.staffcredits.api.profile.withdrawals;

import lombok.Getter;
import lombok.Setter;
import me.kasuki.staffcredits.utilities.enums.WithdrawalStates;

import java.util.UUID;

@Getter @Setter
public class WithdrawRequest {
    private UUID owner;
    private WithdrawalStates state;
    private double amount;
    private long date;

    public WithdrawRequest(UUID owner, WithdrawalStates state, double amount, long date) {
        this.owner = owner;
        this.state = state;
        this.amount = amount;
        this.date = date;
    }
}
