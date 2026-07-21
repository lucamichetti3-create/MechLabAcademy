# Architettura

## Flusso dati

`assets/seed/*.json` → `SeedImporter` → Room → `AcademyDao` → `AcademyRepository` → `MainViewModel` → schermate Compose.

Le preferenze sono gestite da DataStore. Progressi e dati personali sono separati dai contenuti editoriali. Coroutines e Flow mantengono l’interfaccia reattiva.

## Dipendenze

È usata una **dependency injection manuale** tramite `AppContainer`. La scelta evita code generation aggiuntiva e mantiene trasparente un progetto personale a modulo singolo. Hilt potrà essere introdotto quando il progetto verrà diviso in più moduli o aumenteranno i componenti con lifecycle complessi.

## Backup

`BackupManager` serializza soltanto dati personali e progressi in un formato JSON versionato. I contenuti seed non vengono duplicati nel backup.

## Contenuti multimediali

I video non sono copiati né scaricati. L’app apre l’URL ufficiale con un `Intent`. Tutti i testi restano offline.

## Evoluzione database

La versione iniziale è 1. Ogni modifica futura dello schema deve aggiungere una `Migration` esplicita e testarla su un database esistente. Gli aggiornamenti editoriali possono usare `@Upsert` senza cancellare le tabelle di progressi.
