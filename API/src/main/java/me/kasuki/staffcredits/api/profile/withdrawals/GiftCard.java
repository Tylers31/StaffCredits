package me.kasuki.staffcredits.api.profile.withdrawals;

import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@Getter
public class GiftCard {
  private final String code;
  
  private final double amount;
  
  private final UUID playerUUID;
  
  private final Date receivedDate;
  
  private final Date expiryDate;
  
  private double amountLeft;
  
  public GiftCard(String code, double amount, UUID playerUUID) {
    this.code = code;
    this.amount = amount;
    this.playerUUID = playerUUID;
    this.receivedDate = new Date();
    this.expiryDate = new Date(System.currentTimeMillis() + 172800000L);
    this.amountLeft = amount;
  }
  
  public void useAmount(double amount) {
    if (amount <= this.amountLeft)
      this.amountLeft -= amount; 
  }
  
  public boolean isExpired() {
    return (new Date()).after(this.expiryDate);
  }
}
