# Funzioni implementate — 1.0.0

## Operative nel codice

- bootstrap dei seed JSON in Room, eseguito in transazione e a blocchi;
- navigazione inferiore Home, Materie, Piano, Esercizi e Profilo;
- percorso materia → lezioni → dettaglio;
- lezione a campi strutturati, stato, completamento, preferiti e appunti personali;
- ricerca offline nelle lezioni su titolo, spiegazione e parole chiave;
- quiz locale con risposta, spiegazione e storico dei tentativi;
- esercizi con soluzione e stato di completamento;
- flashcard fronte/retro con ripetizione dilazionata locale;
- catalogo di video verificati, apertura tramite URL ufficiale, preferiti e stato visto;
- glossario bilingue ricercabile;
- mappe a nodi e archi con zoom, trascinamento e selezione del nodo;
- catalogo e schede del laboratorio virtuale;
- appunti liberi locali con creazione ed eliminazione;
- 24 calcolatori tecnici interattivi con controllo degli input e cronologia; le altre schede sono formulari consultabili;
- statistiche sintetiche su completamento, tempo e serie di studio;
- tema, dimensione testo, anno e ore settimanali tramite DataStore;
- backup e import di progressi, tentativi, note, flashcard e piano tramite Storage Access Framework;
- promemoria giornaliero con WorkManager, interruttore nel Profilo e richiesta del permesso notifiche sui dispositivi recenti;
- test della logica pura, test DAO Room predisposti e workflow CI per validazione, test, lint e APK.

## Implementazione parziale

- la ricerca non interroga ancora in un’unica query tutte le entità: la schermata globale copre attualmente lezioni, formule incorporate e parole chiave;
- il piano iniziale è utilizzabile e memorizzabile, ma la generazione automatica avanzata, il recupero arretrati e la distribuzione adattiva sono ancora da completare;
- le statistiche non hanno ancora grafici analitici completi per materia, mese e prerequisito;
- gli appunti non supportano ancora foto, fotocamera, condivisione, esportazione singola o rich text;
- le mappe non aprono ancora direttamente la lezione o il video dal nodo selezionato;
- il laboratorio è guidato e consultabile, ma non dispone ancora di un motore grafico/fisico di simulazione;
- il disegno tecnico contiene struttura, lezioni ed esercizi testuali, ma non ancora il pacchetto completo di tavole originali zoomabili;
- il promemoria è giornaliero ma l’orario non è ancora configurabile dall’utente;
- accessibilità, test UI e test strumentali estesi sono predisposti come fase successiva.

Questa distinzione evita di presentare come conclusa una funzione soltanto predisposta.
