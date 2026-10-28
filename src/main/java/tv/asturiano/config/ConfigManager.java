package tv.asturiano.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigManager {

    private static final Logger LOGGER = Logger.getLogger("HyAstur");
    private File configFile;
    
    private int maxClaimsPerPlayer = 10;
    private int reviveTimerSeconds = 60;
    private boolean enablePartySharing = true;
    private String databaseFile = "hyastur_data.db";

    public void loadConfig() {
        File configDir = new File("config/hyastur");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        configFile = new File(configDir, "hyastur-config.json");

        if (!configFile.exists()) {
            LOGGER.info("[HyAstur] Archivo de configuración no encontrado. Creando uno por defecto...");
            saveDefaultConfig();
        } else {
            LOGGER.info("[HyAstur] Cargando configuración existente...");
            loadFromJsonFile();
        }
    }

    private void saveDefaultConfig() {
        String defaultJson = "{\n" +
                "  \"max_claims_per_player\": " + maxClaimsPerPlayer + ",\n" +
                "  \"revive_timer_seconds\": " + reviveTimerSeconds + ",\n" +
                "  \"enable_party_sharing\": " + enablePartySharing + ",\n" +
                "  \"database_file\": \"" + databaseFile + "\"\n" +
                "}";

        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write(defaultJson);
            LOGGER.info("[HyAstur] Configuración por defecto creada en " + configFile.getPath());
        } catch (IOException e) {
            LOGGER.severe("[HyAstur] No se pudo crear el archivo de configuración: " + e.getMessage());
        }
    }

    private void loadFromJsonFile() {
        try {
            String content = new String(Files.readAllBytes(configFile.toPath()));
            maxClaimsPerPlayer = parseInt(content, "\"max_claims_per_player\"", maxClaimsPerPlayer);
            reviveTimerSeconds = parseInt(content, "\"revive_timer_seconds\"", reviveTimerSeconds);
            enablePartySharing = parseBoolean(content, "\"enable_party_sharing\"", enablePartySharing);
            databaseFile = parseString(content, "\"database_file\"", databaseFile);
            LOGGER.info("[HyAstur] Configuración cargada correctamente (Max Claims: " + maxClaimsPerPlayer + ")");
        } catch (IOException e) {
            LOGGER.warning("[HyAstur] Error leyendo el archivo de configuración, usando valores por defecto: " + e.getMessage());
        }
    }

    private int parseInt(String json, String key, int defaultValue) {
        try {
            Pattern pattern = Pattern.compile(key + "\\s*:\\s*(\\d+)");
            Matcher matcher = pattern.matcher(json);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception ignored) {}
        return defaultValue;
    }

    private boolean parseBoolean(String json, String key, boolean defaultValue) {
        try {
            Pattern pattern = Pattern.compile(key + "\\s*:\\s*(true|false)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(json);
            if (matcher.find()) {
                return Boolean.parseBoolean(matcher.group(1));
            }
        } catch (Exception ignored) {}
        return defaultValue;
    }

    private String parseString(String json, String key, String defaultValue) {
        try {
            Pattern pattern = Pattern.compile(key + "\\s*:\\s*\"([^\"]*)\"");
            Matcher matcher = pattern.matcher(json);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception ignored) {}
        return defaultValue;
    }

    public int getMaxClaimsPerPlayer() {
        return maxClaimsPerPlayer;
    }

    public int getReviveTimerSeconds() {
        return reviveTimerSeconds;
    }

    public boolean isEnablePartySharing() {
        return enablePartySharing;
    }

    public String getDatabaseFile() {
        return databaseFile;
    }
}
