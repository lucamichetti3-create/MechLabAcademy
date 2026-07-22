# MechLab Studio — guida alla produzione video

## Struttura di uno storyboard

```json
{
  "slug": "forze_momenti",
  "title": "Forze, momenti e leve",
  "module": "Statica",
  "outputName": "mechlab_forze_momenti.mp4",
  "language": "it",
  "voiceSpeed": 148,
  "voicePitch": 45,
  "burnSubtitles": true,
  "scenes": [
    {
      "title": "Titolo scena",
      "body": "Testo visibile",
      "voice": "Testo completo della narrazione",
      "kind": "formula"
    }
  ]
}
```

Tipi grafici disponibili: `intro`, `force`, `formula`, `lever`, `example`, `balance`, `recap`.

## Generazione

```bash
python tools/mechlab_studio/generate_pilot.py \
  --storyboard studio/video_vettori/storyboard.json
```

Output:

- MP4 in `app/src/main/res/raw/`;
- SRT nella cartella dello storyboard;
- sottotitoli suddivisi in frasi brevi e incorporati nell’MP4 per la visione offline, salvo `"burnSubtitles": false`;
- file intermedi eliminati automaticamente, salvo `--keep-build`.

## Collegamento all’app

Aggiungere in `videos.json`:

```json
{
  "url": "raw:mechlab_nome_video",
  "platform": "MECHLAB_LOCAL",
  "linkStatus": "OFFLINE_INCLUDED"
}
```

Il nome dopo `raw:` deve coincidere con il file MP4, senza estensione.

## Pubblicazione nel portale

Copiare l’MP4 in `portal/media/`, quindi eseguire:

```bash
python tools/build_portal_catalog.py
```

## Qualità editoriale

Prima di pubblicare una videolezione verificare:

- correttezza delle formule;
- unità e ipotesi;
- pronuncia di simboli e parole tecniche;
- leggibilità su smartphone;
- assenza di materiale protetto copiato;
- sottotitoli;
- collegamento alla lezione corretta;
- esercizio e quiz coerenti.
