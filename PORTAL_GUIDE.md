# Portale MechLab PWA

## Avvio locale

```bash
cd portal
python -m http.server 8080
```

Aprire `http://localhost:8080`.

## Funzioni

- installazione come PWA;
- cache offline di interfaccia, catalogo e 10 video originali;
- ricerca e filtri;
- checklist di studio;
- laboratorio interattivo sul momento;
- backup JSON dei progressi del browser.

## GitHub Pages

1. In GitHub aprire **Settings → Pages**.
2. Selezionare **GitHub Actions** come origine.
3. Aprire **Actions → Deploy MechLab Portal**.
4. Avviare manualmente il workflow.

Il portale non contiene segreti, chiavi API o backend. I dati personali restano nel browser.
