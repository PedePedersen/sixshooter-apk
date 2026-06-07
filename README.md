# Six Shooter — Android (WebView-wrapper)

Fullscreen WebView-skal omkring `sixshooter.html` fra
[sixshooter](https://github.com/PedePedersen/sixshooter)-repoet.
App-navn "Six Shooter", package `dk.dustytales.sixshooter`, portrait-only,
fullscreen immersive, ingen internet-permission (100% offline).

## Build

Kræver JDK 17 + Android SDK (platform-34, build-tools 34.0.0). På denne
maskine ligger begge brugerlokalt:

```
echo "sdk.dir=$HOME/android-sdk" > local.properties   # første gang
JAVA_HOME=~/jdk17 ./gradlew assembleDebug
# → app/build/outputs/apk/debug/app-debug.apk
```

## Opdatering af web-indholdet

HTML + assets ligger som Android-assets og synces MANUELT fra
`~/sixshooter/` (kopiér igen efter ændringer dér, og bump `versionCode`
i `app/build.gradle` før installation på telefon):

```
cp ~/sixshooter/sixshooter.html app/src/main/assets/
cp ~/sixshooter/assets/{Rye-Regular.ttf,OldNewspaperTypes.ttf,paper.webp,spin.mp3,bang.mp3,klik.mp3} app/src/main/assets/assets/
```

## Release-signering & udgivelse

Release-builds signeres med en **stabil** upstream-nøgle (ikke debug-nøglen)
— F-Droid kræver en konstant signatur for at kunne verificere og opdatere.

- **Keystore:** `~/sixshooter-apk-release.keystore` (alias `sixshooter`,
  gyldig 30 år). Ligger UDEN FOR repoet. ⚠️ **Tag backup** — mistes nøglen,
  kan der ikke udgives opdateringer F-Droid/Android vil acceptere.
- **Adgangskode:** i `keystore.properties` i repo-roden (gitignored sammen
  med `*.keystore`). `app/build.gradle` læser den derfra; mangler filen
  (fx på F-Droids byggeserver), bygges release blot usigneret.
- **Cert-fingeraftryk (SHA-256):**
  `505876e346edfe7dcf4b4e3b9ab567e6bdb54c106b20a070a1d07df224ff42e7`
  — står i `metadata/…yml` som `AllowedAPKSigningKeys`.

Udgiv en ny version (efter versionCode/Name-bump):

```
JAVA_HOME=~/jdk17 ./gradlew assembleRelease
cp app/build/outputs/apk/release/app-release.apk /tmp/sixshooter-<v>.apk
git tag -a v<v> -m "Six Shooter <v>" && git push origin v<v>
# Opret GitHub release på tag v<v> og vedhæft assetet sixshooter-<v>.apk
# (asset-navnet SKAL matche Binaries-mønstret i metadata/…yml: sixshooter-%v.apk)
```

F-Droid-metadataen (`metadata/dk.dustytales.sixshooter.yml`) henter den
vedhæftede release-APK via `Binaries:` og verificerer den mod et kildekode-
build + signaturen. Tilføj et nyt `Builds:`-entry og bump
`CurrentVersion`/`CurrentVersionCode` ved hver udgivelse.

## WebView-konfiguration (vigtige detaljer)

- `setDomStorageEnabled(true)` — PÅKRÆVET for localStorage (hold +
  seneste tilstand) på `file://`; fejler ellers stille.
- `layoutInDisplayCutoutMode = SHORT_EDGES` (API 28+) — uden den
  letterboxer Android fullscreen-vinduer udenom kamerahul/notch, og
  HTML'ens `viewport-fit=cover` får aldrig noget areal at male på.
- `setMediaPlaybackRequiresUserGesture(false)`, `setAllowFileAccess(true)`,
  `setTextZoom(100)`, `setBlockNetworkLoads(true)`.
