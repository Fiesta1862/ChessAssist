# chessassist

Overlay engine assistant for chess.com — reads the board via accessibility,
draws the engine's best move as an arrow on screen. Fully local, no server,
no network permission needed.

## Build

    ./gradlew assembleDebug
    # → app/build/outputs/apk/debug/app-debug.apk

CI: every push to `main` builds and uploads the APK as an artifact.

## Install

1. `adb install app-debug.apk` — or enable "Install unknown apps" and tap it.
2. Open **chessassist** → **Grant overlay permission**.
3. Settings → Accessibility → **chessassist board reader** → On.
4. Open chess.com, start a game, tap **Start assist**.
5. Green arrow shows the engine's pick. Depth is set in `Engine.kt`.

## Strength

- Current engine: alpha-beta depth 3, ~1700 Elo ceiling.
- Stronger: JNI-wrap Stockfish (`com.github.bhlangonijr:chesslib` + SF binary), set `depth = 18`.

## Layout

    app/src/main/java/com/olltaterfx/chessassist/
      MainActivity.kt       — setup buttons
      BoardReader.kt        — accessibility → FEN
      Engine.kt             — alpha-beta move search
      ArrowView.kt          — arrow overlay
      OverlayService.kt     — foreground overlay + engine loop

## Notes

- Board coordinates are white-at-bottom; flip handling in `isBoardFlipped()`.
- If arrow is off by a square: check `squareToFraction()` against your board crop.
- `.gitignore`: `.gradle/`, `build/`, `local.properties`, `*.iml`.