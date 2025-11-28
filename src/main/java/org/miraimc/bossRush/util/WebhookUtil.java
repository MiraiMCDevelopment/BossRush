package org.miraimc.bossRush.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;
import org.miraimc.bossRush.BossRush;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@UtilityClass
public class WebhookUtil {

    private static final Gson gson = new Gson();
    private static final BossRush plugin = BossRush.getInstance();

    public static File loadFile(String fileName) {
        return new File(plugin.getDataFolder(), "webhooks/" + fileName);
    }

    public static CompletableFuture<Void> sendWebhook(File jsonFile,
                                                      String webhookUrl, Map<String, String> placeholders) {
        return CompletableFuture.runAsync(() -> {
            try {
                sendWebhookSync(jsonFile, webhookUrl, placeholders);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to send webhook: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    private static void sendWebhookSync(File jsonFile, String webhookUrl,
                                        Map<String, String> placeholders) throws IOException {
        // Load full JSON file
        JsonObject fullJson = JsonParser.parseReader(new FileReader(jsonFile)).getAsJsonObject();

        // Get Json Object
        JsonObject webhookJson = fullJson.getAsJsonObject();
        if (webhookJson == null) {
            throw new IllegalArgumentException("Section not found in webhook JSON");
        }

        // JSON 문자열로 직렬화
        String jsonString = gson.toJson(webhookJson);

        // Placeholder
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            jsonString = jsonString.replace(entry.getKey(), entry.getValue());
        }

        sendRawJsonToDiscord(webhookUrl, jsonString);
    }

    private static void sendRawJsonToDiscord(String webhookUrl, String json) throws IOException {
        URL url = new URL(webhookUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Java Discord Webhook");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != 204 && responseCode != 200) {
            throw new IOException("Failed to send webhook: HTTP " + responseCode);
        }
    }


}