# MechLab Academy 2.0 — ecosistema di studio tecnico

MechLab Academy è un progetto personale per studiare il triennio dell’Istituto Tecnico Tecnologico, indirizzo **Meccanica, Meccatronica ed Energia**. La versione 2.0 trasforma la prima app Android in un ecosistema composto da:

1. **app Android nativa**, utilizzabile soprattutto offline;
2. **Video Academy**, con player Media3, YouTube incorporato quando consentito e fonti esterne;
3. **10 videolezioni originali offline**, con voce italiana e sottotitoli incorporati;
4. **portale web/PWA**, installabile da browser, con catalogo, video, checklist, ricerca e laboratorio interattivo;
5. **MechLab Studio**, pipeline locale che genera MP4, copione e sottotitoli da storyboard JSON;
6. **dataset didattico separato dal codice**, aggiornabile senza perdere i progressi personali.

> Stato della consegna: **sorgenti 2.0.0 validati staticamente**. La versione 1.0 è stata realmente compilata da GitHub Actions e installata su un dispositivo Android. Questa nuova versione 2.0 contiene dipendenze e codice aggiuntivi e deve essere ricompilata tramite il workflow incluso; nel container di generazione non era disponibile un ambiente Android completo, quindi non viene dichiarata una build 2.0 già riuscita. Vedere `BUILD_VERIFICATION.md`.

## Novità principali della 2.0

- modalità **Studio di oggi**: teoria → video → esercizi → flashcard → quiz;
- hub risorse dentro ogni lezione;
- ricerca globale su lezioni, video, glossario, esercizi, laboratori e strumenti;
- videolezioni locali riprodotte con AndroidX Media3;
- video YouTube visualizzati in WebView con base URL e fallback alla fonte ufficiale;
- stato video visto/preferito e filtri dedicati;
- aggiornamento dei seed tramite `content_version`, preservando progressi, appunti e stato dei video;
- card e griglie più adattive per ridurre sovrapposizioni sugli schermi piccoli;
- primo percorso editoriale approfondito di Meccanica: grandezze, vettori, sistemi di forze, momenti, equilibrio, vincoli, attrito, moto uniforme, potenza e energia;
- portale PWA con laboratorio grafico sul momento di una forza e backup locale dei progressi web;
- pipeline ripetibile per produrre nuove videolezioni originali.

## Contenuti iniziali

| Contenuto | Quantità |
|---|---:|
| Materie | 12 |
| Lezioni | 840 |
| Lezioni marcate complete | 330 |
| Quiz | 2.200 |
| Esercizi | 1.200 |
| Flashcard | 2.100 |
| Glossario bilingue | 2.500 termini |
| Mappe concettuali | 200 |
| Laboratori/scenari | 200 |
| Strumenti e formulari | 150 |
| Video totali | 29 |
| Videolezioni originali offline | 10 |
| Risorse esterne verificate | 19 |

Le quantità e i riferimenti sono verificati da `tools/validate_dataset.py`.

## Le 10 videolezioni offline incluse

- Grandezze fisiche e unità SI;
- Vettori e componenti;
- Sistemi di forze e risultante;
- Forze, momenti e leve;
- Equilibrio statico;
- Vincoli e reazioni vincolari;
- Attrito statico e dinamico;
- Moto rettilineo uniforme;
- Potenza e rendimento;
- Lavoro ed energia.

I file MP4 sono in `app/src/main/res/raw/`. Copioni, storyboard e sottotitoli sono in `studio/`.

## Aggiornamento dalla versione 1.0

Le istruzioni complete per sostituire il progetto nel repository, compilare e installare la 2.0 senza disinstallare l’app sono in [`UPGRADE_FROM_1.0.md`](UPGRADE_FROM_1.0.md). Prima dell’aggiornamento è consigliato esportare un backup dall’app 1.0.

## Compilazione dell’APK con GitHub Actions

1. Sostituire nel repository GitHub il contenuto della versione precedente con quello di questa cartella, mantenendo la cartella nascosta `.git` se si usa GitHub Desktop.
2. Eseguire commit e push sul ramo `main`.
3. Aprire **Actions → Android CI**.
4. Attendere che `validate-build` diventi verde.
5. Aprire l’esecuzione e scaricare l’artifact **MechLabAcademy-debug-apk**.
6. Estrarre `app-debug.apk` e installarlo sul telefono.

Il workflow usa JDK 17, Gradle 9.5.0, Android API 37.0, Build Tools 37.0.0, test JVM, lint e `assembleDebug`.

## Apertura in Android Studio

- JDK: 17;
- compileSdk: 37;
- targetSdk: 36;
- minSdk: 23;
- package: `it.lucamichetti.mechlabacademy`;
- versione: `2.0.0` (`versionCode 2`).

APK atteso dopo la build:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Avvio del portale web

Non aprire direttamente `index.html` con doppio clic, perché browser e service worker richiedono un server locale.

```bash
cd portal
python -m http.server 8080
```

Aprire quindi `http://localhost:8080`. Il workflow `portal-pages.yml` permette anche una pubblicazione manuale su GitHub Pages dopo aver abilitato Pages nelle impostazioni del repository.

## Rigenerare il catalogo del portale

```bash
python tools/build_portal_catalog.py
```

## Generare una videolezione con MechLab Studio

```bash
python tools/mechlab_studio/generate_pilot.py \
  --storyboard studio/pilot_forze_momenti/storyboard.json
```

Requisiti locali: Python, Pillow, `espeak`, `ffmpeg` e `ffprobe`. Il generatore produce MP4 e sottotitoli SRT. Il formato degli storyboard è documentato in `VIDEO_STUDIO.md`.

## Struttura essenziale

```text
app/                         app Android
app/src/main/assets/seed/    contenuti JSON
app/src/main/res/raw/        videolezioni offline
portal/                      PWA complementare
studio/                      storyboard, copioni e sottotitoli
tools/mechlab_studio/        generatore video
tools/                       validatori e generatori dati
.github/workflows/           build APK e deploy portale
```

## Limiti dichiarati

MechLab Academy non sostituisce scuola, docenti, laboratori reali, diploma, norme tecniche, manuali aggiornati o responsabilità professionali. Molte lezioni del catalogo restano seed editoriali sintetici; il primo percorso di Meccanica è stato approfondito, mentre il resto dovrà essere revisionato progressivamente. Il portale e l’app non sincronizzano ancora automaticamente i progressi tra dispositivi: ciascuno mantiene dati locali e backup separati.
