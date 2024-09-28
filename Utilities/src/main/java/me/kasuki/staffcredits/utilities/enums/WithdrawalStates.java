package me.kasuki.staffcredits.utilities.enums;

public enum WithdrawalStates {
    DECLINED("&c&lDECLINED"),
    ACCEPTED("&a&lACCEPTED"),
    PENDING("&e&lPENDING");

    public String display;

    WithdrawalStates(String display) {
        this.display = display;
    }
}
