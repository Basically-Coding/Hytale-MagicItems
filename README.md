# Hytale MagicItems

A Hytale server plugin that adds magic items to the game (starting with boots of speed). Scaffolded
from the [HytaleModding plugin-template](https://github.com/HytaleModding/plugin-template).

## How to start?

1. Copy the template by downloading it or using the "Use this template" button.
2. [Configure or Install the Java SDK](https://hytalemodding.dev/en/docs/guides/plugin/setting-up-env)
   to use the latest 25 from JetBrains or similar.
3. Open the project in your favorite IDE, we
   recommend [IntelliJ IDEA](https://www.jetbrains.com/idea/download).
4. Optionally, run `./gradlew` if your IDE does not automtically synchronizes.
5. Run the devserver with the Run Configuration created, or `./gradlew devServer`.

> On Windows, use `.\gradlew.bat` instead of `./gradlew`, this script is here to run the
> Gradle without you needing to install the tooling itself, only the Java is required.

With that you will be prompted in the output to authorize your server, and then you can start
developing your plugin while the server is live reloading the code changes.

From here,
the [HytaleModding guides](https://hytalemodding.dev/en/docs/guides/plugin/build-and-test) cover
more details!

## Scaffoldit Plugin

While there are multiple plugins made for Hytale, the template currently uses a zero-boilerplate one
where you only need the absolute minimum to start. However, you do have access to everything as
normal if you know what you are doing.

For in-depth configuration, you can visit the [ScaffoldIt Plugin Docs](https://scaffoldit.dev).

## Configuration

The mod reads a small JSON config from the per-plugin data directory Hytale assigns it (typically under `%APPDATA%\Hytale\UserData\` — the exact path is logged at first launch). On first run, the file is created with sensible defaults; edit it and restart Hytale to apply changes.

| Field | Default | Effect |
|---|---|---|
| `SpeedMultiplier` | `2.5` | Multiplier applied to forward-run, sprint, backward-run, and strafe speeds while the Pants of Speed are equipped. Hytale clamps movement multipliers at `15`; values above that desync the client. |

Example `config.json`:

```json
{
    "SpeedMultiplier": 4.0
}
```

The config is in your platform's per-user data directory and lives outside the mod's git repo by design — your tweaks survive mod updates, and committing them isn't necessary unless you want to share defaults across machines.

## Troubleshooting

- **Gradle sync fails in IntelliJ** –
  _Check that Java 25 is installed and configured under File → Project Structure → SDKs._
- **Build fails with missing dependencies** –
  _Run `./gradlew build --refresh-dependencies`. Make sure you have internet access!_
- **Permission denied on `./gradlew`** –
  _Run `chmod +x gradlew` (macOS/Linux)._
- **Hot-reload doesn't work** –
  _Verify you're using JetBrains Runtime, not a regular JDK._

## Resources

- [Hytale Modding Guides](https://hytalemodding.dev)
- [Hytale Modding Discord](https://discord.gg/hytalemodding)
- [ScaffoldIt Plugin Docs](https://scaffoldit.dev)

## License

[MIT](LICENSE).
