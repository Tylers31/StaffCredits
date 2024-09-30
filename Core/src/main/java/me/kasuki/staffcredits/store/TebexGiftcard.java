package me.kasuki.staffcredits.store;

import me.kasuki.staffcredits.StaffCreditsPlugin;
import me.kasuki.staffcredits.config.MainConfig;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TebexGiftcard {
    public StaffCreditsPlugin instance;

    public TebexGiftcard(StaffCreditsPlugin instance) {
        this.instance = instance;
    }

    public String generateTebexGiftcard(int amount, String currency, String expirationDate){
        try {
            URL url = new URL("https://plugin.tebex.io/gift-cards");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Setup connection
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("X-Tebex-Secret", "069452c9a21feedefd84f52a2300c304d926f94d"); // Use the dynamically fetched secret key

            // JSON request payload with dynamic values
            String jsonInputString = String.format(
                    "{\"amount\":%d,\"currency\":\"%s\",\"expires_at\":\"%s\"}",
                    amount, currency, expirationDate
            );

            // Write the JSON payload to the request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the response code and handle accordingly
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return "Gift card created successfully!";
            } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                return "Error 400: Bad request. Check the request payload.";
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                return "Error 403: Forbidden. Check API key or permissions.";
            } else {
                return "Error: Received response code " + responseCode;
            }
        }catch (Exception e){
            e.printStackTrace();
            return "An issue has occured";
        }
    }
}
