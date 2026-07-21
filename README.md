# MechLab Academy — Accademia Meccanica

Applicazione Android nativa personale per preparare il **triennio dell’Istituto Tecnico Tecnologico, indirizzo Meccanica, Meccatronica ed Energia**.

> Stato del progetto: **sorgenti 1.0.0**. Il pacchetto contiene architettura Android, database locale, interfaccia Compose, dataset iniziale, documentazione, validatori e workflow CI. Nel container non erano disponibili Android SDK e Gradle completi: non viene quindi allegato un APK locale e non viene dichiarata una compilazione Android già riuscita. Vedere `BUILD_VERIFICATION.md`.

## Funzioni principali

- Kotlin, Jetpack Compose, Material 3, MVVM e Navigation Compose;
- Room con importazione dei contenuti JSON al primo avvio;
- DataStore per tema, anno, dimensione testo, ore settimanali e promemoria;
- lezioni offline strutturate per anno → materia → macroarea → modulo → capitolo → lezione;
- progressi, preferiti, appunti per lezione, quiz, esercizi e flashcard con ripetizione dilazionata;
- video verificati aperti tramite URL ufficiale, senza download o redistribuzione;
- mappe concettuali basate su nodi e archi;
- laboratori virtuali guidati;
- glossario tecnico bilingue;
- strumenti tecnici, formulari e cronologia dei calcoli;
- piano dimostrativo, statistiche di base e modalità “Studio di oggi”;
- promemoria locale con WorkManager e gestione del permesso notifiche;
- backup e ripristino tramite Storage Access Framework;
- test JVM, test Room predisposti, validatori dataset e GitHub Actions.

## Contenuti iniziali

| Tipo | Quantità |
|---|---:|
| Materie | 12 |
| Lezioni | 840 |
| Lezioni marcate complete | 330 |
| Quiz | 2.200 |
| Esercizi | 1.200 |
| Flashcard | 2.100 |
| Termini di glossario | 2.500 |
| Mappe | 200 |
| Laboratori/scenari | 200 |
| Strumenti/formulari | 150 |
| Video realmente verificati | 19 |
| Attività di ricerca video ancora da svolgere | 1.000 |

I quantitativi e i collegamenti sono controllati da `tools/validate_dataset.py`. Lo script verifica anche l’assenza di duplicati esatti nei principali campi editoriali. `tools/enrich_seed.py` documenta il passaggio usato per rendere i seed iniziali coerenti con ciascuna disciplina, mantenendo stabili ID e relazioni.

I contenuti costituiscono un **seed editoriale iniziale**. Le 330 lezioni marcate complete hanno struttura, spiegazione, esempi, domande e attività, ma molte sono ancora sintetiche e richiedono approfondimento e revisione progressiva. Non sono presentate come equivalenti a tre anni di manuali e didattica certificata.

## Apertura in Android Studio

1. Installare una versione di Android Studio compatibile con AGP 9.3.
2. Aprire la cartella `MechLabAcademy`.
3. Selezionare JDK 17 per Gradle.
4. Installare Android SDK Platform 37 e Build Tools 36.0.0.
5. Attendere la sincronizzazione Gradle.
6. Eseguire la configurazione `app` su emulatore o dispositivo Android 6.0 o successivo.

Il progetto non include binari di terze parti. Il file `gradlew` incluso è un launcher testuale che delega a Gradle già installato; il JAR binario standard del Gradle Wrapper non è incluso. In alternativa usare `scripts/bootstrap-gradle.sh`, Android Studio o il workflow CI, che installa Gradle 9.5.0 tramite `gradle/actions/setup-gradle`.

## Comandi

```bash
python tools/validate_project.py
python tools/validate_dataset.py
./scripts/bootstrap-gradle.sh
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
```

APK atteso dopo una compilazione riuscita:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Struttura

```text
app/src/main/java/.../data       Room, repository, seed, backup e DataStore
app/src/main/java/.../domain     logica pura, SRS e calcolatori
app/src/main/java/.../ui         tema, navigazione, ViewModel e schermate Compose
app/src/main/java/.../worker     promemoria WorkManager
app/src/main/assets/seed         dataset JSON modificabile
app/src/test                     test JVM
app/src/androidTest              test Room su Android
.github/workflows/android.yml    validazione, test, lint e APK CI
tools/validate_dataset.py        integrità, quantità, duplicati e riferimenti
tools/validate_project.py        struttura, versioni e controlli statici
docs/reference                   riferimento visivo fornito dall’utente
```

## Uso e limiti

MechLab Academy è uno strumento personale di preparazione. Non sostituisce iscrizione scolastica, docenti, laboratori, diploma, manuali aggiornati, norme tecniche o verifiche professionali. Per dimensionamenti e attività reali occorre usare norme applicabili, dati del produttore e responsabilità di un tecnico abilitato quando richiesta.

## Documentazione

- `PROGRAMMA_FONTI.md`: ricostruzione del nucleo nazionale e fonti;
- `CONTENT_GUIDE.md`: modifica e ampliamento dei contenuti;
- `BUILD_VERIFICATION.md`: controlli realmente eseguiti e controlli rinviati alla CI;
- `IMPLEMENTED_FEATURES.md`: matrice delle funzioni complete e parziali;
- `LIMITS_AND_ROADMAP.md`: limiti reali e prossimi pacchetti;
- `VALIDATION_REPORT.txt`: output dell’ultima validazione;
- `FILE_MANIFEST.md` e `FILE_MANIFEST.json`: inventario con dimensioni e checksum.
