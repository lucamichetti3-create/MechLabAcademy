# Limiti reali e roadmap editoriale

## Limiti della 1.0.0

1. **APK non prodotto localmente.** Nel container non erano disponibili Android SDK, Gradle e cache delle dipendenze; la build Android è demandata a GitHub Actions o ad Android Studio.
2. **Videolezioni.** Sono pubblicate 19 risorse controllate singolarmente. Non sono stati inventati 1.000 URL: `video_research_backlog.json` contiene 1.000 attività di ricerca, non video verificati.
3. **Profondità didattica.** Le 330 lezioni marcate `COMPLETE` hanno tutti i campi principali e contenuti coerenti con la disciplina, ma sono un primo passaggio editoriale sintetico, non 330 capitoli di manuale già revisionati da docenti.
4. **Ricerca globale.** La prima versione cerca nelle lezioni; l’indicizzazione unificata di quiz, esercizi, glossario, mappe, video, appunti e laboratori è ancora da sviluppare.
5. **Disegno tecnico.** Sono presenti programma, esercizi e tipi di attività, ma non le 200 tavole grafiche originali complete richieste.
6. **Mappe e laboratori.** I dati sono reali e navigabili; mancano schede nodo avanzate, collegamenti diretti multipli e simulazioni fisiche/grafiche.
7. **Pianificatore.** Piano, preferenze, modalità e carico settimanale di base sono presenti; ripianificazione intelligente, arretrati e grafo dei prerequisiti sono una fase successiva.
8. **Appunti.** Sono disponibili appunti testuali locali e backup; immagini, fotocamera, editor ricco ed esportazione singola non sono implementati.
9. **Strumenti.** Il catalogo contiene 150 schede; 24 calcoli sono realmente interattivi, mentre le altre voci funzionano come formulari o riferimenti.
10. **Norme e impiego reale.** Formule, schede e risultati sono didattici e devono essere verificati rispetto a norme, condizioni, dati del costruttore e responsabilità professionali applicabili.
11. **Distribuzione scolastica.** La sequenza è un nucleo comune nazionale ricostruito; la futura scuola può distribuire o approfondire diversamente gli argomenti.

## Pacchetti successivi previsti

- `content-pack-01-meccanica`: 50 lezioni manualistiche, 300 esercizi numerici e 40 mappe revisionate;
- `content-pack-02-materiali-processi`: materiali, metrologia, lavorazioni, CNC e qualità;
- `content-pack-03-automazione`: pneumatica, oleodinamica, PLC, controllo e robotica;
- `content-pack-04-disegno`: 200 schemi originali SVG/PNG e correzioni guidate;
- `content-pack-05-generali`: italiano, storia, inglese, matematica ed esame di Stato;
- `video-pack-verified`: import progressivo degli URL dopo apertura e verifica individuale;
- `search-v2`: indice locale trasversale a tutte le tipologie di contenuto;
- `planner-v2`: prerequisiti a grafo, carico settimanale, arretrati e suggerimenti adattivi;
- `notes-v2`: immagini, fotocamera, Markdown, esportazione e condivisione.

Ogni pacchetto dovrà superare validazione di ID, riferimenti, fonti, duplicazioni, migrazioni e compatibilità con i progressi esistenti.
