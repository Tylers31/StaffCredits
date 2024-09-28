package me.kasuki.staffcredits.api.profile.withdrawals;

import lombok.Getter;
import lombok.Setter;
import me.kasuki.staffcredits.utilities.enums.WithdrawalStates;

@Getter @Setter
public class WithdrawRequest {
    private WithdrawalStates state;
    private double amount;
    private long date;

    public WithdrawRequest(WithdrawalStates state, double amount, long date) {
        this.state = state;
        this.amount = amount;
        this.date = date;
    }
}
