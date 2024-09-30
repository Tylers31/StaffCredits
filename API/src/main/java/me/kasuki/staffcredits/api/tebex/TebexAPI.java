package me.kasuki.staffcredits.api.tebex;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.bukkit.Bukkit;

public class TebexAPI {

  private static final String BASE_URL = "https://plugin.tebex.io";
  private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json");

  private final String apiKey;
  private final String secretKey;
  private final OkHttpClient client;

  public TebexAPI(String apiKey, String secretKey) {
    this.apiKey = apiKey;
    this.secretKey = secretKey;
    this.client = new OkHttpClient();
  }

  public String createVoucher(double amount, int expiryDays, String note) throws IOException {
    String url = BASE_URL + "/gift-cards";
    String expiryDate = calculateExpiryDate(expiryDays);

    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("amount", amount);
    jsonObject.addProperty("expires_at", expiryDate);
    if (note != null && !note.isEmpty()) {
      jsonObject.addProperty("note", note);
    }

    RequestBody body = RequestBody.create(jsonObject.toString(), JSON_MEDIA_TYPE);
    Request request = new Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("X-Tebex-Secret", secretKey)
            .post(body)
            .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        handleErrorResponse(response, "Failed to create gift card.");
      }
      return parseGiftCardCode(response.body().string());
    }
  }

  public void updateVoucherAmount(String voucherCode, double newAmount) throws IOException {
    String url = BASE_URL + "/gift-cards/" + voucherCode;

    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("amount", newAmount);

    RequestBody body = RequestBody.create(jsonObject.toString(), JSON_MEDIA_TYPE);
    Request request = new Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("X-Tebex-Secret", secretKey)
            .put(body)
            .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        handleErrorResponse(response, "Failed to update gift card.");
      }
    }
  }

  public JsonObject lookupGiftCardByCode(String code) throws IOException {
    String url = BASE_URL + "/gift-cards/lookup/" + code;

    Request request = new Request.Builder()
            .url(url)
            .addHeader("X-Tebex-Secret", secretKey)
            .get()
            .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        handleErrorResponse(response, "Failed to lookup gift card.");
      }
      return JsonParser.parseString(response.body().string()).getAsJsonObject();
    }
  }

  public boolean deleteGiftCardByCode(String code) {
    try {
      JsonObject giftCardDetails = lookupGiftCardByCode(code);
      if (giftCardDetails == null || !giftCardDetails.has("data")) {
        Bukkit.getLogger().severe("Failed to fetch gift card details. No data found.");
        return false;
      }
      int giftCardId = giftCardDetails.getAsJsonObject("data").get("id").getAsInt();
      return deleteGiftCard(giftCardId);
    } catch (IOException e) {
      Bukkit.getLogger().severe("Failed to delete gift card by code: " + e.getMessage());
      return false;
    }
  }

  private boolean deleteGiftCard(int giftCardId) throws IOException {
    String url = BASE_URL + "/gift-cards/" + giftCardId;

    Request request = new Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("X-Tebex-Secret", secretKey)
            .delete()
            .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        handleErrorResponse(response, "Failed to delete gift card.");
        return false;
      }
      return true;
    }
  }

  private String calculateExpiryDate(int expiryDays) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    sdf.setTimeZone(TimeZone.getTimeZone("EST"));
    Date expiryDate = new Date(System.currentTimeMillis() + expiryDays * 86400000L); // 86400000 = 24 * 60 * 60 * 1000
    return sdf.format(expiryDate);
  }

  private String parseGiftCardCode(String responseBody) throws IOException {
    JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
    if (json.has("data") && json.getAsJsonObject("data").has("code")) {
      return json.getAsJsonObject("data").get("code").getAsString();
    } else {
      throw new IOException("Gift card code not found in 'data' field.");
    }
  }

  private void handleErrorResponse(Response response, String logMessage) throws IOException {
    String responseBody = (response.body() != null) ? response.body().string() : "No response body";
    Bukkit.getLogger().severe(logMessage + " Response code: " + response.code());
    Bukkit.getLogger().severe("Response message: " + response.message());
    Bukkit.getLogger().severe("Response body: " + responseBody);
    throw new IOException("Unexpected code " + response);
  }
}
