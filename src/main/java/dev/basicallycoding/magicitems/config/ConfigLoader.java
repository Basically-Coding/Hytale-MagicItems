package dev.basicallycoding.magicitems.config;

import com.hypixel.hytale.logger.HytaleLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ConfigLoader {
    // JSON keys are PascalCase to match Hytale's modding ecosystem convention.
    private static final Pattern SPEED_MULTIPLIER_PATTERN = Pattern.compile(
        "\"SpeedMultiplier\"\\s*:\\s*([-+]?[0-9]*\\.?[0-9]+)");

    private ConfigLoader() {}

    public static MagicItemsConfig loadOrCreate(Path path, HytaleLogger logger) {
        if (Files.exists(path)) {
            try {
                String content = Files.readString(path);
                return parse(content, logger);
            } catch (IOException e) {
                logger.at(Level.WARNING).log(
                    "[MagicItemsConfig] failed to read " + path + " (" + e.getMessage()
                    + "); using defaults this session, file untouched");
                return MagicItemsConfig.DEFAULT;
            }
        }

        // First-load case: create the file with defaults so the user can find and edit it.
        try {
            Path parent = path.getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.writeString(path, serialize(MagicItemsConfig.DEFAULT));
            logger.at(Level.INFO).log(
                "[MagicItemsConfig] no config found; wrote defaults to " + path);
        } catch (IOException e) {
            logger.at(Level.WARNING).log(
                "[MagicItemsConfig] could not write default config to " + path + " ("
                + e.getMessage() + "); using in-memory defaults this session");
        }
        return MagicItemsConfig.DEFAULT;
    }

    static MagicItemsConfig parse(String content, HytaleLogger logger) {
        float speedMultiplier = MagicItemsConfig.DEFAULT.speedMultiplier();
        Matcher m = SPEED_MULTIPLIER_PATTERN.matcher(content);
        if (m.find()) {
            try {
                speedMultiplier = Float.parseFloat(m.group(1));
            } catch (NumberFormatException e) {
                logger.at(Level.WARNING).log(
                    "[MagicItemsConfig] SpeedMultiplier '" + m.group(1)
                    + "' isn't a number; using default " + speedMultiplier);
            }
        } else {
            logger.at(Level.WARNING).log(
                "[MagicItemsConfig] no SpeedMultiplier key found in config; using default "
                + speedMultiplier);
        }
        return new MagicItemsConfig(speedMultiplier);
    }

    static String serialize(MagicItemsConfig config) {
        return "{\n    \"SpeedMultiplier\": " + config.speedMultiplier() + "\n}\n";
    }
}
