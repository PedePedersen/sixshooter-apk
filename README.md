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

## WebView-konfiguration (vigtige detaljer)

- `setDomStorageEnabled(true)` — PÅKRÆVET for localStorage (hold +
  seneste tilstand) på `file://`; fejler ellers stille.
- `layoutInDisplayCutoutMode = SHORT_EDGES` (API 28+) — uden den
  letterboxer Android fullscreen-vinduer udenom kamerahul/notch, og
  HTML'ens `viewport-fit=cover` får aldrig noget areal at male på.
- `setMediaPlaybackRequiresUserGesture(false)`, `setAllowFileAccess(true)`,
  `setTextZoom(100)`, `setBlockNetworkLoads(true)`.
