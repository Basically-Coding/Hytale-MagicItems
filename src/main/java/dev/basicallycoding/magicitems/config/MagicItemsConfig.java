package dev.basicallycoding.magicitems.config;

public record MagicItemsConfig(float speedMultiplier) {
    public static final MagicItemsConfig DEFAULT = new MagicItemsConfig(2.5f);
}
