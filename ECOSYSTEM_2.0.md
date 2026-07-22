# Architettura dell’ecosistema MechLab 2.0

## Componenti

### 1. App Android

È il centro dello studio quotidiano: contenuti testuali offline, progressi, quiz, esercizi, flashcard, appunti, laboratori, mappe, strumenti e Video Academy.

### 2. Portale PWA

È il compagno per computer e tablet. Usa un catalogo compatto generato dagli stessi seed dell’app. I progressi sono conservati nel `localStorage` del browser e possono essere esportati in JSON.

### 3. MechLab Studio

Trasforma uno storyboard in:

```text
storyboard.json → slide → voce → segmenti → MP4 → subtitles.srt
```

La pipeline è locale, non richiede API a pagamento e non copia video di terzi.

### 4. Repository contenuti

Le entità didattiche sono file JSON separati. `content_version` permette all’app di aggiornare i contenuti statici alla prima apertura della nuova versione senza eliminare appunti, completamenti, tentativi e stato di apprendimento.

## Flusso didattico

```text
Studio di oggi
  ↓
Lezione strutturata
  ↓
Videolezione o fonte autorevole
  ↓
Esercizio guidato
  ↓
Flashcard
  ↓
Quiz e analisi degli errori
  ↓
Laboratorio / mappa / strumento
```

## Evoluzione consigliata

La fase successiva non deve aggiungere soltanto quantità. Deve completare un modulo per volta con qualità editoriale, immagini originali, esercizi graduati, video, verifiche e laboratorio. Il modello già pronto è il primo blocco di Meccanica del terzo anno.
