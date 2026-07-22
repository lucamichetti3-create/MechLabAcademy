# Aggiornamento da MechLab Academy 1.0 a 2.0

Questa versione mantiene lo stesso package Android:

```text
it.lucamichetti.mechlabacademy
```

La versione passa da `versionCode 1` a `versionCode 2`. Installando l'APK 2.0 sopra l'app 1.0, senza disinstallare prima l'app, Android tratta il file come un aggiornamento. Il sistema di seed usa inoltre `content_version = 2` per aggiornare i contenuti statici conservando progressi, appunti e stato visto/preferito dei video.

## Prima di iniziare

1. Nell'app 1.0 apri **Profilo → Esporta backup** e conserva il file JSON.
2. Non disinstallare l'app dal telefono.
3. Conserva una copia del vecchio APK finché la 2.0 non è stata provata.

## Aggiornare il repository con GitHub Desktop

1. Scarica ed estrai `MechLabAcademy-2.0.0-source.zip`.
2. Apri GitHub Desktop e seleziona il repository `MechLabAcademy` già collegato.
3. Premi **Repository → Show in Explorer**.
4. Nella cartella locale del repository elimina tutti gli elementi visibili, ma non eliminare la cartella nascosta `.git`.
5. Copia nella cartella del repository tutto il contenuto della cartella estratta `MechLabAcademy-2.0.0`, compresa `.github`.
6. In GitHub Desktop scrivi come riepilogo:

```text
Aggiornamento MechLab Academy 2.0
```

7. Premi **Commit to main**, poi **Push origin**.

## Generare l'APK 2.0

1. Apri il repository nel browser.
2. Vai su **Actions → Android CI**.
3. Apri l'esecuzione più recente.
4. Attendi la spunta verde.
5. In fondo alla pagina, nella sezione **Artifacts**, scarica `MechLabAcademy-debug-apk`.
6. Estrai lo ZIP e recupera `app-debug.apk`.

Questa consegna è stata validata staticamente, ma non è stata compilata in locale perché il container non disponeva dell'ambiente Android completo. La prima esecuzione di GitHub Actions è quindi la verifica reale della build 2.0. In caso di errore, apri il primo passaggio rosso e conserva le righe iniziali e finali del log.

## Installare senza perdere i dati

1. Trasferisci `app-debug.apk` sul telefono.
2. Apri il file e scegli **Aggiorna** o **Installa**.
3. Non scegliere di disinstallare la versione precedente.
4. Avvia MechLab Academy e attendi il completamento dell'aggiornamento dei contenuti.
5. Controlla appunti, lezioni completate e preferiti.
6. Se necessario, usa **Profilo → Importa backup** con il file esportato prima dell'aggiornamento.

## Pubblicare il portale web

Dopo il push:

1. Nel repository apri **Settings → Pages**.
2. In **Build and deployment**, seleziona **GitHub Actions**.
3. Torna su **Actions**.
4. Seleziona **Deploy MechLab Portal**.
5. Premi **Run workflow**.
6. Al termine, apri l'indirizzo mostrato nel riepilogo del job.

Il portale può essere installato dal browser come PWA. I suoi progressi restano separati da quelli dell'app Android; usa i pulsanti di esportazione/importazione presenti nel portale per spostarli tra browser.
