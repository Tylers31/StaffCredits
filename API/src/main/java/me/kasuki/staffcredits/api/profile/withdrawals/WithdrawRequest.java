package me.kasuki.staffcredits.api.profile.withdrawals;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WithdrawRequest {
    private double amount;
    private long date;

    public WithdrawRequest(double amount, long date) {
        this.amount = amount;
        this.date = date;
    }
}
