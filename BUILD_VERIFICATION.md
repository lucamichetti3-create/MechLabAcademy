# BUILD_VERIFICATION — MechLab Academy 2.0.0

**Data:** 22 luglio 2026

## Stato noto

La versione 1.0 del progetto è stata realmente compilata con GitHub Actions, ha superato test e lint ed è stata installata e avviata su un dispositivo Android. La versione 2.0 aggiunge Media3, video locali, nuove schermate, ricerca estesa, aggiornamento seed e portale PWA.

Nel container usato per produrre questa consegna non erano disponibili Android SDK, Gradle completo e cache delle dipendenze Android. Per questo motivo **non si dichiara una compilazione Android 2.0 locale riuscita**. Il workflow GitHub Actions è configurato per effettuare la verifica completa dopo il caricamento nel repository.

## Controlli realmente eseguiti sulla 2.0

- parsing di tutti i JSON seed;
- quantità minime, ID univoci e riferimenti non orfani;
- controllo delle sorgenti video locali e HTTPS;
- parsing di tutti gli XML;
- controllo statico di struttura, versioni, package, route e parentesi;
- compilazione Kotlin della logica pura `StudyLogic.kt` e `VideoSources.kt`;
- test manuale del resolver video locale/YouTube;
- `node --check` su JavaScript del portale e del service worker;
- parsing del catalogo PWA;
- verifica FFprobe di tutti i 10 MP4: H.264 video, AAC audio, risoluzione 960×540;
- generazione del catalogo web dai seed Android;
- verifica della pipeline video Python tramite `py_compile`;
- creazione e riproducibilità di storyboard, copioni e sottotitoli.

Esito dataset:

```text
subjects 12
lessons 840
quiz 2200
exercises 1200
flashcards 2100
glossary 2500
maps 200
labs 200
videos 29
tools 150
Dataset, IDs, unique content fields, foreign references, map edges, video sources and XML: OK
```

## Verifica completa da eseguire in CI

```bash
gradle --console=plain --stacktrace testDebugUnitTest
gradle --console=plain --stacktrace lintDebug assembleDebug
```

Il workflow `.github/workflows/android.yml` pubblica l’APK solo se queste fasi riescono.

## Configurazione

- Android Gradle Plugin: 9.3.0;
- Gradle CI: 9.5.0;
- JDK: 17;
- compileSdk: 37;
- targetSdk: 36;
- minSdk: 23;
- Media3: 1.10.1;
- Room: 2.8.4;
- Navigation Compose: 2.9.8;
- WorkManager: 2.11.2.
