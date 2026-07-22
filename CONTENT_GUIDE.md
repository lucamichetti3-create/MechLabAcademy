# CONTENT_GUIDE

I contenuti iniziali sono separati dal codice in `app/src/main/assets/seed`. Al primo avvio, `SeedImporter` importa i JSON in Room. I dati personali e i progressi restano in tabelle separate.

## Regole comuni

- Ogni record deve avere un `id` stabile e univoco.
- Non cambiare un ID già pubblicato: i progressi lo usano come chiave.
- Usare UTF-8.
- Non inserire URL non verificati in `videos.json`.
- Eseguire sempre `python tools/validate_dataset.py`.
- Incrementare `contentVersion` quando si modifica sostanzialmente una lezione.
- Una futura importazione incrementale deve usare una migrazione/contenuto versionato; non cancellare il database dell’utente.

## Aggiungere una materia

Modificare `subjects.json` con:

```json
{
  "id": "unique_subject",
  "code": "ABC",
  "name": "Nome materia",
  "category": "GENERAL",
  "years": [3, 4, 5],
  "sortOrder": 13,
  "description": "..."
}
```

Aggiungere poi almeno una lezione valida riferita allo stesso `subjectId`.

## Aggiungere o modificare una lezione

File: `lessons.json`. Campi principali:

- gerarchia: `year`, `subjectId`, `macroarea`, `module`, `chapter`;
- metadati: `durationMinutes`, `difficulty`, `status`, `sortOrder`, `contentVersion`;
- didattica: `prerequisites`, `objectives`, `introduction`, `explanation`, `formulas`, `symbols`, esempi, errori, riepilogo, autoverifica, esercizi, soluzioni, fonti.

Stati ammessi dal progetto editoriale:

- `COMPLETE`
- `DRAFT`
- `TO_DEEPEN`
- `TO_VERIFY`
- `VIRTUAL_LAB`
- `SCHOOL_DEPENDENT`
- `TO_DEVELOP`

Le formule sono stringhe leggibili, accompagnate da simboli e unità. Dichiarare ipotesi e limiti.

## Quiz

File: `quiz.json`.

Ogni domanda deve riferirsi a una lezione e una materia esistenti. Tipi previsti nei dati: `MULTIPLE_CHOICE`, `TRUE_FALSE`, `COMPLETION`, `MATCHING`, `NUMERIC`, `FORMULA`, `GRAPH`, `DRAWING`, `SCENARIO`, `ORDERING`, `COMPONENT_RECOGNITION`.

## Esercizi

File: `exercises.json`. Categorie iniziali: `NUMERIC`, `DRAWING`, `AUTOMATION`, `TECHNICAL_ENGLISH`, `HUMANITIES`. Inserire dati, risultato atteso, soluzione, passaggi e fonte.

## Video

File pubblicato nell’app: `videos.json`.

Campi obbligatori: titolo, autore/canale, URL diretto, piattaforma, lingua, durata, livello, argomento, `lessonId`, `subjectId`, descrizione, motivo, `lastVerified`, `linkStatus`.

Procedura:

1. aprire personalmente l’URL;
2. verificare titolo, autore e pertinenza;
3. controllare che non richieda chiavi private o download;
4. salvare con `linkStatus: "VERIFIED"` e data ISO;
5. ricontrollare periodicamente.

`video_research_backlog.json` contiene attività di ricerca, **non video pubblicati né URL inventati**.

## Mappe

File: `maps.json`.

- `nodes`: id locale, etichetta, coordinate, categoria, lezione collegata;
- `edges`: `from`, `to`, etichetta;
- gli ID dei nodi devono essere unici all’interno della mappa.

## Flashcard

File: `flashcards.json`. Il progresso SRS non è nel seed; viene creato localmente in `flashcard_progress`.

## Glossario

File: `glossary.json`. Inserire termine italiano/inglese, definizione, uso, simbolo, unità, sinonimi, errori, materia, esempio e fonte.

## Laboratori virtuali

File: `labs.json`. Ogni scenario deve contenere obiettivo, strumenti, DPI, teoria, procedura, ordine, controlli, rischi, errori, risultato, domande, simulazione e relazione.

## Strumenti tecnici

File: `tools.json`. `key` collega la scheda al motore `TechnicalCalculators`. Se si aggiunge un calcolatore realmente interattivo, aggiungere un ramo in `domain/StudyLogic.kt` e un test unitario.

## Appunti dimostrativi e piano

- `notes.json`: esempi iniziali; i nuovi appunti vengono salvati in Room.
- `study_plan.json`: piano dimostrativo; i cambiamenti dell’utente non modificano l’asset.

## Aggiornamenti senza perdita di progressi

Strategia consigliata per v2:

1. introdurre una tabella `content_metadata` con versione seed;
2. importare con `@Upsert` solo tabelle editoriali;
3. non cancellare tabelle progressi, tentativi, note e piano;
4. aggiungere migrazioni Room in `AcademyDatabase.ALL_MIGRATIONS` quando cambia lo schema;
5. testare un database v1 reale prima della pubblicazione della v2;
6. esportare un backup prima di qualsiasi reset.

## Rigenerazione e validazione editoriale

`tools/enrich_seed.py` è uno strumento di sviluppo usato per arricchire il seed generato mantenendo invariati ID e riferimenti. Non va eseguito automaticamente durante la build e non va usato su contenuti già revisionati senza prima creare una copia di sicurezza, perché riscrive diversi campi editoriali.

Dopo ogni modifica eseguire:

```bash
python tools/validate_project.py
python tools/validate_dataset.py
```

Il secondo comando controlla quantità, ID, riferimenti, archi delle mappe, URL pubblicati e duplicati esatti nei principali campi. Non sostituisce una revisione didattica umana: correttezza, livello scolastico, fonti e chiarezza devono essere controllati argomento per argomento.

## Pacchetti progressivi

Per aggiungere grandi quantità di contenuti senza perdere i progressi:

1. mantenere gli ID esistenti;
2. assegnare un identificatore e una versione a ogni pacchetto;
3. usare `@Upsert` sulle sole tabelle editoriali;
4. non sovrascrivere progressi, note, tentativi e cronologia;
5. registrare le modifiche in un changelog del pacchetto;
6. validare l’intero grafo prima della distribuzione;
7. provare l’aggiornamento su una copia di un database già utilizzato.

## Video Academy 2.0

### Video locale incluso nell’APK

1. Creare o copiare un MP4 H.264/AAC in `app/src/main/res/raw/`.
2. Usare un nome Android valido: minuscole, numeri e underscore.
3. Aggiungere la scheda a `app/src/main/assets/seed/videos.json` con:

```json
{
  "url": "raw:mechlab_nome_video",
  "platform": "MECHLAB_LOCAL",
  "linkStatus": "OFFLINE_INCLUDED"
}
```

4. Incrementare `CONTENT_VERSION` in `SeedImporter.kt` se l’aggiornamento deve essere importato anche dagli utenti che possiedono già il database.
5. Copiare il file anche in `portal/media/` e rigenerare il catalogo con `python tools/build_portal_catalog.py`.

### YouTube

Inserire esclusivamente l’URL ufficiale del singolo video. L’app riconosce URL `watch`, `youtu.be`, `embed` e `shorts`. Se l’incorporamento non è disponibile, il pulsante “Apri fonte” avvia l’app YouTube o il browser.

### Aggiornamento senza perdere i progressi

Le entità statiche vengono importate con `@Upsert`. I progressi sono tabelle separate; preferiti e stato visto dei video esistenti vengono preservati durante il refresh editoriale.
