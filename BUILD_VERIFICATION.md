# BUILD_VERIFICATION

**Data:** 21 luglio 2026

## Ambiente disponibile

- Java presente: JDK 21 nel container;
- compilatore Kotlin CLI presente;
- Android SDK: non presente;
- Gradle: non presente;
- cache completa delle dipendenze Android: non presente;
- download dei binari Android/Gradle non disponibile in modo affidabile nel container.

## Controlli realmente eseguiti

1. Validazione della struttura del progetto, delle versioni dichiarate, dei package e delle route.
2. Parsing di tutti i JSON seed.
3. Verifica dell’unicità degli ID.
4. Verifica dell’unicità dei principali campi editoriali: titoli, domande, esercizi, flashcard, termini, mappe e laboratori.
5. Verifica dei riferimenti materia/lezione e assenza di riferimenti orfani nei dataset principali.
6. Verifica dei nodi e degli archi delle mappe.
7. Verifica delle quantità minime, salvo il requisito dei 1.000 video verificati, esplicitamente non raggiunto.
8. Controllo della forma HTTPS e dello stato `VERIFIED` dei video pubblicati.
9. Parsing XML di manifest, temi, backup rules, data extraction rules e icona vettoriale.
10. Compilazione con `kotlinc` della logica Kotlin pura `StudyLogic.kt`.
11. Esecuzione dello smoke test su calcolatori, progressi e ripetizione dilazionata.
12. Ricerca di testi segnaposto, delimitatori sbilanciati e package inattesi.
13. Generazione del manifest dei file e del checksum SHA-256 dell’archivio finale.

Esiti dell’ultima esecuzione:

```text
Project structure, versions, packages, routes, placeholders and source delimiters: OK
Dataset, IDs, unique content fields, foreign references, map edges, video URL shape and XML: OK
Pure Kotlin domain smoke tests: OK
```

## Controlli non eseguiti localmente

- sincronizzazione Gradle Android;
- elaborazione KSP/Room;
- compilazione Compose;
- lint Android;
- test strumentali;
- installazione su dispositivo;
- generazione dell’APK.

Questo documento **non dichiara che il progetto Android sia già stato compilato**.

## Configurazione predisposta

- Android Gradle Plugin: 9.3.0;
- Gradle CI: 9.5.0;
- JDK CI: 17;
- compileSdk/targetSdk: 37;
- minSdk: 23;
- Kotlin e plugin Compose: 2.4.10;
- Compose BOM: 2026.06.00;
- Room: 2.8.4;
- Navigation Compose: 2.9.8;
- WorkManager: 2.11.2;
- DataStore: 1.2.1;
- KSP: 2.3.10.

AGP 9 usa Kotlin integrato: il plugin `org.jetbrains.kotlin.android` non è applicato. Il progetto include un launcher `gradlew` testuale che usa un’installazione Gradle disponibile; il JAR binario standard del wrapper non è incluso. GitHub Actions installa esplicitamente Gradle 9.5.0 prima della build.

## Ripetere la verifica

```bash
python tools/validate_project.py
python tools/validate_dataset.py
kotlinc app/src/main/java/it/lucamichetti/mechlabacademy/domain/StudyLogic.kt \
  tools/kotlin-smoke/SmokeMain.kt -include-runtime -d mechlab-smoke.jar
java -jar mechlab-smoke.jar
gradle --stacktrace testDebugUnitTest
gradle --stacktrace lintDebug assembleDebug
```

In GitHub: aprire **Actions → Android CI → Run workflow**. Il workflow pubblica `MechLabAcademy-debug-apk` soltanto se la build riesce e conserva i report disponibili anche in caso di errore.
