#!/usr/bin/env python3
"""Rende il seed editoriale coerente con la disciplina senza alterare ID o riferimenti."""
from __future__ import annotations
import json
from pathlib import Path
from collections import defaultdict

ROOT = Path(__file__).resolve().parents[1] / "app/src/main/assets/seed"


def load(name):
    return json.loads((ROOT / name).read_text(encoding="utf-8"))


def save(name, data):
    (ROOT / name).write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def topic_of(lesson):
    title = lesson["title"]
    for prefix in ("Fondamenti di ", "Approfondimento di ", "Applicazioni di ", "Verifica di "):
        if title.startswith(prefix):
            return title[len(prefix):]
    return title


def formula_for(topic: str):
    t = topic.lower()
    mapping = [
        (("linear equation", "equazioni lineari"), ["a·x + b = 0"]),
        (("quadratic", "second degree"), ["a·x² + b·x + c = 0", "x = (-b ± √(b²-4ac))/(2a)"]),
        (("straight line", "retta"), ["y = m·x + q"]),
        (("distance",), ["d = √((x₂-x₁)²+(y₂-y₁)²)"]),
        (("trigon", "sine", "cosine"), ["sin²α + cos²α = 1"]),
        (("exponential",), ["y = aˣ"]),
        (("logarith",), ["logₐ(x·y) = logₐx + logₐy"]),
        (("derivative",), ["f'(x) = lim[h→0] (f(x+h)-f(x))/h"]),
        (("integral",), ["∫ₐᵇ f(x)dx"]),
        (("probability",), ["P(A) = casi favorevoli / casi possibili"]),
        (("mean", "average"), ["x̄ = Σxᵢ/n"]),
        (("vectors",), ["|v| = √(vₓ²+vᵧ²+v_z²)"]),
        (("moment", "moments and couples"), ["M = F·d"]),
        (("static equilibrium", "equilibrium"), ["ΣF = 0", "ΣM = 0"]),
        (("friction",), ["Fₐ = μ·N"]),
        (("uniform rectilinear",), ["s = s₀ + v·t"]),
        (("accelerated",), ["v = v₀ + a·t", "s = s₀ + v₀t + 1/2·a·t²"]),
        (("circular motion",), ["v = ω·r", "a_c = v²/r"]),
        (("work",), ["L = F·s·cosα"]),
        (("power",), ["P = L/t", "P = M·ω"]),
        (("kinetic energy",), ["Eₖ = 1/2·m·v²"]),
        (("potential energy",), ["Eₚ = m·g·h"]),
        (("momentum",), ["p = m·v"]),
        (("stress", "tensione"), ["σ = F/A"]),
        (("strain", "deformation"), ["ε = ΔL/L₀"]),
        (("hooke",), ["σ = E·ε"]),
        (("bending",), ["σ = M/W"]),
        (("torsion",), ["τ = Mₜ/Wₚ"]),
        (("gear ratio", "transmission ratio"), ["i = z₂/z₁ = n₁/n₂"]),
        (("efficiency", "rendimento"), ["η = E_out/E_in"]),
        (("ideal gas",), ["p·V = n·R·T"]),
        (("first law",), ["ΔU = Q - L"]),
        (("heat conduction", "conduction"), ["Q̇ = λ·A·ΔT/L"]),
        (("ohm",), ["V = R·I"]),
        (("hydraulic force", "pressure force"), ["F = p·A"]),
        (("flow",), ["Q = A·v"]),
        (("cutting speed",), ["V_c = π·D·n/1000"]),
        (("spindle", "number of revolutions"), ["n = 1000·V_c/(π·D)"]),
        (("feed",), ["V_f = f_z·z·n"]),
        (("break-even", "break even"), ["Q_BE = C_F/(p-C_Vu)"]),
    ]
    for keys, formulas in mapping:
        if any(k in t for k in keys):
            return formulas
    return []


def lesson_profile(sid: str, topic: str):
    if sid == "italian":
        return {
            "objectives": [
                f"Collocare {topic} nel corretto contesto storico-culturale.",
                "Riconoscere temi, scelte linguistiche e struttura dei testi.",
                "Costruire un'interpretazione argomentata con riferimenti verificabili.",
            ],
            "intro": f"La lezione affronta **{topic}** come nodo della storia letteraria italiana. Il traguardo non è memorizzare un'etichetta, ma saper collegare contesto, testi, temi e forme espressive.",
            "explanation": f"Lo studio di {topic} procede su quattro piani. Primo: il contesto, cioè il periodo, il pubblico, le istituzioni culturali e i problemi a cui autori e opere rispondono. Secondo: i testi, letti distinguendo contenuto, struttura, voce, lessico e figure retoriche. Terzo: l'interpretazione, che deve essere sostenuta da elementi del testo e non da impressioni generiche. Quarto: il confronto con altri autori e con la storia. Nel quaderno conviene costruire una linea del tempo, una scheda dei concetti chiave e un breve paragrafo argomentativo. Per la relazione tecnica e l'esame di Stato lo stesso metodo torna utile: tesi chiara, prove ordinate, lessico preciso e conclusione coerente.",
            "industrial": ["Collegamento alla comunicazione professionale: descrivere un problema, selezionare prove e motivare una scelta senza ambiguità."],
            "practical": ["Leggere un brano e segnare contesto, tesi, parole chiave e scelte stilistiche.", "Scrivere 180 parole con una tesi e almeno due riferimenti al testo."],
            "errors": ["Riassumere senza interpretare.", "Attribuire al testo idee non dimostrate.", "Elencare figure retoriche senza spiegarne l'effetto."],
            "summary": f"{topic}: contesto, testi, temi, stile e interpretazione argomentata.",
            "self": [f"In quale contesto si colloca {topic}?", "Quali elementi del testo sostengono l'interpretazione?", "Quale collegamento storico o tecnico puoi costruire?"],
        }
    if sid == "history":
        return {
            "objectives": [f"Ricostruire cronologia e protagonisti di {topic}.", "Distinguere cause, svolgimento e conseguenze.", "Collegare trasformazioni politiche, economiche, sociali e tecnologiche."],
            "intro": f"La lezione studia **{topic}** come processo storico: non una data isolata, ma una rete di cause, decisioni, conflitti e conseguenze.",
            "explanation": f"Per comprendere {topic} si parte dalla cronologia essenziale e dagli attori coinvolti. Si distinguono poi cause di lungo periodo, fattori scatenanti ed effetti immediati o strutturali. Ogni affermazione va collegata a una fonte, a un dato o a una relazione causale plausibile. Una tabella 'prima–evento–dopo' aiuta a evitare il semplice elenco di date. L'analisi deve includere istituzioni, lavoro, produzione, scambi, energia e innovazioni quando pertinenti. Il collegamento tecnico consiste nel chiedersi come strumenti, infrastrutture e organizzazione della produzione abbiano modificato i rapporti sociali e il potere. La conclusione deve distinguere ciò che cambia da ciò che rimane stabile.",
            "industrial": ["Leggere l'evoluzione di lavoro, impresa, infrastrutture ed energia nel periodo studiato."],
            "practical": ["Costruire una linea del tempo con cinque passaggi motivati.", "Preparare una matrice cause–evento–conseguenze."],
            "errors": ["Confondere successione temporale e rapporto di causa.", "Usare categorie contemporanee senza contestualizzarle.", "Ridurre un processo complesso a un solo protagonista."],
            "summary": f"{topic}: cronologia, attori, cause, conseguenze e trasformazioni di lungo periodo.",
            "self": [f"Quali sono le cause principali di {topic}?", "Quale conseguenza ha avuto maggiore durata?", "Quale ruolo hanno avuto tecnica, produzione o infrastrutture?"],
        }
    if sid == "english":
        return {
            "objectives": [f"Comprendere forma e uso di {topic}.", "Usare la struttura in frasi generali e tecniche.", "Riconoscere errori tipici di grammatica, lessico e pronuncia."],
            "intro": f"This lesson develops **{topic}** through form, meaning, use and technical communication.",
            "explanation": f"The topic {topic} is studied in context. First identify the communicative purpose: describing a fact, an operation, a condition, a component or a safety rule. Then observe the grammatical form or the lexical pattern, including affirmative, negative and interrogative forms when relevant. Technical English requires short sentences, unambiguous verbs, correct units and a stable terminology. Read examples aloud, transform them, and write a short instruction or process description. Always check subject–verb agreement, word order, articles, prepositions and false friends. A useful final task is to explain a workshop operation to a colleague using five target expressions.",
            "industrial": [f"Use {topic} to describe a component, a procedure, a fault or a safety instruction."],
            "practical": ["Write five technical sentences and transform them into negative or question form where possible.", "Read a short datasheet paragraph and collect ten domain terms."],
            "errors": ["Translating Italian word order literally.", "Using a generic word where a precise technical term is required.", "Ignoring units, modal meaning or safety wording."],
            "summary": f"{topic}: form, meaning, controlled practice and technical application.",
            "self": [f"When is {topic} used?", "Can you produce a correct technical example?", "Which error do Italian speakers make most often here?"],
        }
    if sid in {"math", "math_complements"}:
        return {
            "objectives": [f"Definire e rappresentare {topic}.", "Applicare una procedura risolutiva motivata.", "Controllare dominio, unità, ordine di grandezza e significato tecnico."],
            "intro": f"La lezione costruisce **{topic}** passando da definizione e rappresentazione a procedura, esempio e controllo.",
            "explanation": f"Per affrontare {topic} si chiariscono prima oggetti, simboli e condizioni di esistenza. La rappresentazione grafica o geometrica serve a vedere ciò che le formule condensano. Nello svolgimento ogni passaggio deve conservare l'equivalenza e indicare le ipotesi utilizzate. Il risultato non è completo finché non viene controllato: sostituzione, segno, ordine di grandezza, dominio e coerenza con il fenomeno. In applicazione tecnica si definiscono variabili e unità prima di calcolare, si mantiene una precisione compatibile con i dati e si interpreta il numero ottenuto. Il quaderno deve separare dati, modello, calcolo e commento finale.",
            "industrial": [f"Usare {topic} per modellare una grandezza meccanica, energetica, produttiva o di controllo."],
            "practical": ["Risolvi un esempio simbolico e poi uno numerico.", "Rappresenta il risultato e verifica almeno un caso limite."],
            "errors": ["Saltare le condizioni di esistenza o il dominio.", "Perdere il significato delle unità nel passaggio al modello.", "Accettare un risultato senza sostituzione o controllo grafico."],
            "summary": f"{topic}: definizione, rappresentazione, procedura, verifica e applicazione tecnica.",
            "self": [f"Qual è la definizione operativa di {topic}?", "Quale procedura useresti e perché?", "Come controlleresti il risultato in un caso tecnico?"],
        }
    if sid == "religion":
        return {
            "objectives": [f"Riconoscere le questioni etiche legate a {topic}.", "Confrontare responsabilità individuale e organizzativa.", "Argomentare una scelta considerando persone, ambiente e bene comune."],
            "intro": f"La lezione usa **{topic}** per riflettere sulla responsabilità nelle decisioni tecniche e professionali.",
            "explanation": f"Il tema {topic} viene analizzato distinguendo fatti, valori, soggetti coinvolti e conseguenze prevedibili. Una scelta tecnicamente possibile non è automaticamente giusta: occorre considerare dignità, sicurezza, equità, ambiente, trasparenza e responsabilità. Il metodo proposto è: descrivere il caso senza giudizi prematuri, individuare gli interessi in gioco, confrontare alternative, valutare impatti e motivare la decisione. Nel lavoro industriale l'etica si traduce in comportamenti verificabili: non occultare un rischio, non falsificare dati, rispettare persone e procedure, segnalare conflitti di interesse.",
            "industrial": ["Analizzare un dilemma su sicurezza, qualità, dati o impatto ambientale."],
            "practical": ["Scrivere una decisione motivata indicando soggetti, valori e conseguenze.", "Confrontare due alternative e dichiarare i criteri scelti."],
            "errors": ["Confondere legalità minima ed etica professionale.", "Ignorare gli effetti sulle persone meno visibili.", "Giudicare senza ricostruire i fatti."],
            "summary": f"{topic}: fatti, valori, responsabilità, alternative e conseguenze.",
            "self": ["Chi è coinvolto e chi sopporta il rischio?", "Quali valori entrano in conflitto?", "Come rendere trasparente la decisione?"],
        }
    if sid == "pe":
        return {
            "objectives": [f"Comprendere i principi di {topic}.", "Applicarli in modo progressivo e sicuro.", "Riconoscere limiti personali e segnali che richiedono un professionista sanitario."],
            "intro": f"La lezione affronta **{topic}** con attenzione a salute, ergonomia, progressione e prevenzione.",
            "explanation": f"Il lavoro su {topic} parte dalla qualità del movimento, non dalla prestazione assoluta. Si osservano postura, respirazione, controllo, fatica e recupero. Carico, durata e intensità vanno aumentati gradualmente e adattati alla persona. In ambiente di lavoro il principio centrale è ridurre esposizioni inutili, usare ausili, organizzare pause e mantenere una tecnica coerente. Dolore acuto, sintomi neurologici, trauma o peggioramento persistente richiedono valutazione sanitaria; l'app non formula diagnosi né prescrive riabilitazione.",
            "industrial": ["Applicare principi ergonomici a postazione, movimentazione e recupero durante il lavoro."],
            "practical": ["Osservare una sequenza motoria e annotare postura, controllo e compensi.", "Progettare una breve routine generale senza dolore e con progressione prudente."],
            "errors": ["Confondere fatica normale e dolore d'allarme.", "Aumentare il carico senza tecnica o recupero.", "Usare regole generali come prescrizione medica personale."],
            "summary": f"{topic}: tecnica, progressione, ergonomia, recupero e limiti di sicurezza.",
            "self": ["Qual è l'obiettivo del gesto?", "Come ridurre il rischio senza azzerare l'attività?", "Quali segnali impongono di fermarsi e chiedere una valutazione?"],
        }
    # discipline tecniche e scienze applicate
    labels = {
        "applied_science": "scienza e tecnologia applicata",
        "mechanics": "meccanica ed energia",
        "systems": "automazione",
        "technology": "processo e prodotto",
        "design": "disegno e organizzazione industriale",
    }
    area = labels.get(sid, "tecnica")
    return {
        "objectives": [f"Spiegare il principio di funzionamento di {topic}.", "Identificare grandezze, componenti, ingressi, uscite e vincoli.", "Applicare una procedura con controlli, sicurezza e interpretazione industriale."],
        "intro": f"La lezione affronta **{topic}** nell'area {area}, collegando principio, rappresentazione, calcolo, applicazione e manutenzione.",
        "explanation": f"Lo studio di {topic} parte dal sistema reale: funzione richiesta, confini, ingressi, uscite e condizioni operative. Si individuano quindi componenti e grandezze misurabili, con simboli e unità coerenti. Il modello serve a prevedere il comportamento, ma deve dichiarare semplificazioni e limiti. La procedura tecnica segue una sequenza controllabile: raccolta dati, scelta del modello o dello schema, calcolo o simulazione, verifica, interpretazione e documentazione. In industria il risultato va confrontato con tolleranze, prestazioni, sicurezza, manutenzione, qualità ed efficienza. Prima di agire su una macchina si applicano isolamento delle energie, DPI, istruzioni del costruttore e norme pertinenti. Una scheda finale dovrebbe riportare dati, ipotesi, passaggi, risultato, rischio residuo e controllo effettuato.",
        "industrial": [f"Riconoscere {topic} in una macchina, impianto, ciclo produttivo o documento tecnico.", "Valutare effetto su prestazione, qualità, sicurezza, energia e manutenzione."],
        "practical": ["Disegnare uno schema funzionale con ingressi, trasformazione e uscite.", "Preparare una procedura di verifica indicando strumento, unità, tolleranza e criterio di accettazione."],
        "errors": ["Usare una formula senza dichiarare unità e ipotesi.", "Confondere componente, funzione e grandezza misurata.", "Trascurare protezioni, energia residua o limiti del modello."],
        "summary": f"{topic}: funzione, principio, grandezze, modello, verifica, applicazione e sicurezza.",
        "self": [f"Qual è la funzione di {topic}?", "Quali dati e unità servono per verificarlo?", "Quale guasto o errore operativo è più probabile?"],
    }


subjects = {x["id"]: x for x in load("subjects.json")}
lessons = load("lessons.json")
lesson_by_id = {x["id"]: x for x in lessons}

for lesson in lessons:
    topic = topic_of(lesson)
    profile = lesson_profile(lesson["subjectId"], topic)
    lesson["objectives"] = profile["objectives"]
    lesson["introduction"] = profile["intro"]
    if lesson["status"] == "COMPLETE":
        lesson["explanation"] = profile["explanation"]
        lesson["industrialExamples"] = profile["industrial"]
        lesson["practicalExamples"] = profile["practical"]
        lesson["commonErrors"] = profile["errors"]
        lesson["summary"] = profile["summary"]
        lesson["selfCheck"] = profile["self"]
        formulas = formula_for(topic)
        lesson["formulas"] = formulas
        lesson["symbols"] = []
        lesson["numericExamples"] = ([{
            "title": "Esempio di impostazione",
            "data": f"Definire dati e unità per un caso su {topic}.",
            "solution": "Dichiarare ipotesi, applicare il modello, verificare unità e interpretare il risultato.",
        }] if formulas else [])
    else:
        lesson["explanation"] = (
            f"Scheda editoriale predisposta per {topic}. Contenuto da sviluppare e, quando necessario, "
            "da verificare con il programma della scuola e con fonti disciplinari aggiornate."
        )
        lesson["summary"] = f"Percorso previsto: definizione, contesto, metodo, applicazione e verifica di {topic}."
        lesson["commonErrors"] = profile["errors"]
        lesson["selfCheck"] = profile["self"]
        lesson["formulas"] = formula_for(topic)
        lesson["symbols"] = []
        lesson["numericExamples"] = []

save("lessons.json", lessons)

# Quiz: coerenti con materia e univoci, mantenendo ID e collegamenti.
quiz = load("quiz.json")
per_lesson_q = defaultdict(int)
for q in quiz:
    lesson = lesson_by_id[q["lessonId"]]
    sid = lesson["subjectId"]
    topic = topic_of(lesson)
    per_lesson_q[lesson["id"]] += 1
    n = per_lesson_q[lesson["id"]]
    tag = f"{lesson['year']}° anno, attività {n}"
    if sid == "italian":
        variants = [
            ("MULTIPLE_CHOICE", f"In {topic} ({tag}), quale passaggio rende l'analisi davvero argomentata?", ["Collegare la tesi a elementi del testo", "Elencare solo date", "Sostituire il testo con un'opinione", "Copiare una biografia"], "Collegare la tesi a elementi del testo", "Un'interpretazione è solida quando le affermazioni sono sostenute da elementi verificabili del testo."),
            ("TRUE_FALSE", f"Vero o falso — {topic} ({tag}): contesto, forma e contenuto devono essere letti insieme.", ["Vero", "Falso"], "Vero", "Separare completamente forma, contenuto e contesto impoverisce l'analisi."),
            ("COMPLETION", f"Completa — {topic} ({tag}): una tesi interpretativa deve essere sostenuta da ______ pertinenti.", ["prove testuali", "impressioni casuali", "unità di misura", "slogan"], "prove testuali", "Citazioni brevi, parole chiave e scelte formali sostengono l'interpretazione."),
            ("SEQUENCE", f"Ordina il metodo di analisi per {topic} ({tag}).", ["Inquadrare il contesto", "Leggere il testo", "Rilevare elementi formali", "Argomentare l'interpretazione"], "Inquadrare il contesto > Leggere il testo > Rilevare elementi formali > Argomentare l'interpretazione", "La sequenza passa dal quadro generale alle prove e infine alla tesi."),
        ]
    elif sid == "history":
        variants = [
            ("MULTIPLE_CHOICE", f"Per spiegare {topic} ({tag}), quale struttura è più corretta?", ["Cause, svolgimento, conseguenze e continuità", "Solo una data", "Solo la biografia di un personaggio", "Un giudizio senza fonti"], "Cause, svolgimento, conseguenze e continuità", "Un processo storico richiede relazioni causali, cronologia e conseguenze."),
            ("TRUE_FALSE", f"Vero o falso — {topic} ({tag}): una successione temporale dimostra automaticamente un rapporto di causa.", ["Vero", "Falso"], "Falso", "Il fatto che B venga dopo A non basta a dimostrare che A abbia causato B."),
            ("COMPLETION", f"Completa — {topic} ({tag}): una fonte va sempre letta considerando autore, data e ______.", ["contesto", "colore", "unità SI", "prezzo"], "contesto", "La provenienza e il contesto condizionano significato e attendibilità della fonte."),
            ("SEQUENCE", f"Ordina l'analisi storica di {topic} ({tag}).", ["Definire il periodo", "Individuare gli attori", "Confrontare cause e conseguenze", "Valutare cambiamenti e continuità"], "Definire il periodo > Individuare gli attori > Confrontare cause e conseguenze > Valutare cambiamenti e continuità", "La ricostruzione parte dal tempo e dagli attori e arriva all'interpretazione."),
        ]
    elif sid == "english":
        variants = [
            ("MULTIPLE_CHOICE", f"Which activity best practises {topic} ({tag}) in technical English?", ["Writing a clear workshop instruction", "Translating word by word without context", "Ignoring the verb form", "Removing all units"], "Writing a clear workshop instruction", "Technical English is learned through precise, contextualised communication."),
            ("TRUE_FALSE", f"True or false — {topic} ({tag}): word order and verb form can change the meaning of an instruction.", ["True", "False"], "True", "Grammar is part of safety and clarity in technical communication."),
            ("COMPLETION", f"Complete — {topic} ({tag}): a technical sentence should use precise verbs and consistent ______.", ["terminology", "rhymes", "slang", "ambiguity"], "terminology", "Stable terminology reduces misunderstanding."),
            ("SEQUENCE", f"Put the practice steps for {topic} ({tag}) in order.", ["Read the example", "Notice form and meaning", "Transform the sentence", "Use it in a technical context"], "Read the example > Notice form and meaning > Transform the sentence > Use it in a technical context", "The sequence moves from input to controlled and then applied production."),
        ]
    elif sid in {"math", "math_complements"}:
        variants = [
            ("MULTIPLE_CHOICE", f"Nel lavoro su {topic} ({tag}), quale controllo finale è più completo?", ["Dominio, passaggi, sostituzione e significato", "Solo il numero finale", "Solo il segno", "Solo la grafia"], "Dominio, passaggi, sostituzione e significato", "Un risultato matematico va verificato formalmente e interpretato nel problema."),
            ("TRUE_FALSE", f"Vero o falso — {topic} ({tag}): le condizioni di esistenza possono essere controllate solo dopo il calcolo.", ["Vero", "Falso"], "Falso", "Le condizioni vanno impostate prima e poi ricontrollate sul risultato."),
            ("COMPLETION", f"Completa — {topic} ({tag}): in un'applicazione tecnica, ogni valore deve conservare numero e ______.", ["unità coerente", "colore", "titolo", "firma"], "unità coerente", "La coerenza dimensionale impedisce molti errori di modello e conversione."),
            ("SEQUENCE", f"Ordina la procedura per un problema su {topic} ({tag}).", ["Definire dati e incognite", "Stabilire dominio e modello", "Svolgere i passaggi", "Controllare e interpretare"], "Definire dati e incognite > Stabilire dominio e modello > Svolgere i passaggi > Controllare e interpretare", "La procedura separa comprensione, modello, calcolo e verifica."),
        ]
    elif sid in {"religion", "pe"}:
        if sid == "religion":
            variants = [
                ("MULTIPLE_CHOICE", f"Nel caso {topic} ({tag}), quale decisione è professionalmente più responsabile?", ["Rendere visibili rischi e conseguenze", "Occultare i dati scomodi", "Valutare solo il profitto immediato", "Delegare senza controllare"], "Rendere visibili rischi e conseguenze", "La responsabilità richiede trasparenza, attenzione alle persone e motivazione delle scelte."),
                ("TRUE_FALSE", f"Vero o falso — {topic} ({tag}): ciò che è tecnicamente possibile è sempre eticamente corretto.", ["Vero", "Falso"], "Falso", "Fattibilità tecnica e giustificazione etica sono piani distinti."),
                ("COMPLETION", f"Completa — {topic} ({tag}): una scelta etica considera fatti, valori, alternative e ______.", ["conseguenze", "decorazioni", "slogan", "casualità"], "conseguenze", "Valutare conseguenze e soggetti coinvolti rende la decisione argomentabile."),
                ("SEQUENCE", f"Ordina l'analisi etica di {topic} ({tag}).", ["Ricostruire i fatti", "Individuare i soggetti", "Confrontare alternative", "Motivare la decisione"], "Ricostruire i fatti > Individuare i soggetti > Confrontare alternative > Motivare la decisione", "Prima si chiarisce il caso, poi si valutano alternative e criteri."),
            ]
        else:
            variants = [
                ("MULTIPLE_CHOICE", f"Per applicare {topic} ({tag}) in sicurezza, quale principio viene prima?", ["Qualità del movimento e progressione", "Aumento improvviso del carico", "Ignorare il dolore", "Confrontarsi solo con gli altri"], "Qualità del movimento e progressione", "Tecnica e progressione riducono il rischio più della ricerca immediata della prestazione."),
                ("TRUE_FALSE", f"Vero o falso — {topic} ({tag}): dolore acuto e sintomi neurologici vanno ignorati se l'attività è breve.", ["Vero", "Falso"], "Falso", "Questi segnali richiedono sospensione e valutazione appropriata."),
                ("COMPLETION", f"Completa — {topic} ({tag}): carico, durata e intensità devono aumentare in modo ______.", ["progressivo", "casuale", "massimo", "immediato"], "progressivo", "La progressione permette adattamento e controllo della risposta."),
                ("SEQUENCE", f"Ordina una pratica prudente per {topic} ({tag}).", ["Definire l'obiettivo", "Controllare tecnica e sintomi", "Applicare un carico adatto", "Valutare recupero e risposta"], "Definire l'obiettivo > Controllare tecnica e sintomi > Applicare un carico adatto > Valutare recupero e risposta", "La pratica sicura comprende preparazione, esecuzione e verifica."),
            ]
    else:
        variants = [
            ("MULTIPLE_CHOICE", f"Per analizzare {topic} ({tag}), quale scheda tecnica è più completa?", ["Funzione, dati, modello, verifica e sicurezza", "Solo il nome del componente", "Solo la formula", "Solo l'esperienza dell'operatore"], "Funzione, dati, modello, verifica e sicurezza", "La scheda deve rendere tracciabili funzione, ipotesi, calcolo, controlli e rischi."),
            ("TRUE_FALSE", f"Vero o falso — {topic} ({tag}): un modello può essere usato senza dichiararne ipotesi e limiti.", ["Vero", "Falso"], "Falso", "Un modello è affidabile solo nel campo per cui sono valide le sue ipotesi."),
            ("COMPLETION", f"Completa — {topic} ({tag}): prima del calcolo si definiscono sistema, dati, unità e ______.", ["vincoli", "colore", "marchio", "prezzo"], "vincoli", "I vincoli stabiliscono il campo di funzionamento e le condizioni di verifica."),
            ("SEQUENCE", f"Ordina la procedura tecnica per {topic} ({tag}).", ["Raccogliere i dati", "Scegliere modello o schema", "Calcolare o simulare", "Verificare, interpretare e documentare"], "Raccogliere i dati > Scegliere modello o schema > Calcolare o simulare > Verificare, interpretare e documentare", "La procedura evita calcoli prematuri e mantiene tracciabile la decisione."),
        ]
    kind, prompt, options, answer, explanation = variants[(int(q["id"].split("_")[-1]) - 1) % len(variants)]
    q.update(type=kind, prompt=prompt, options=options, correctAnswer=answer, explanation=explanation,
             steps=["Leggere la richiesta", "Richiamare il concetto", "Confrontare le alternative", "Motivare la risposta"],
             typicalError=("Rispondere per associazione di parole senza controllare il significato."),
             source="Seed editoriale originale MechLab Academy v1.1")
save("quiz.json", quiz)

# Esercizi coerenti e non duplicati.
exercises = load("exercises.json")
per_lesson_e = defaultdict(int)
for e in exercises:
    lesson = lesson_by_id[e["lessonId"]]
    sid = lesson["subjectId"]
    topic = topic_of(lesson)
    per_lesson_e[lesson["id"]] += 1
    n = per_lesson_e[lesson["id"]]
    e["title"] = f"{e['category'].replace('_',' ').title()} — {topic} — prova {n}"
    if sid == "italian":
        e["prompt"] = f"Su {topic}, scrivi un paragrafo argomentativo di 160–220 parole: tesi, due prove testuali o concettuali, collegamento al contesto e conclusione. Variante {n}."
        e["expected"] = "Testo coerente, lessico preciso, prove pertinenti e struttura riconoscibile."
        e["solution"] = "Traccia modello: contesto essenziale → tesi → prova 1 commentata → prova 2 commentata → collegamento → conclusione che riprende la tesi."
    elif sid == "history":
        e["prompt"] = f"Costruisci per {topic} una matrice con tre cause, quattro passaggi cronologici, tre conseguenze e un collegamento a lavoro, tecnologia o produzione. Variante {n}."
        e["expected"] = "Relazioni causali motivate e cronologia coerente."
        e["solution"] = "Separare cause strutturali e fattori scatenanti; ordinare gli eventi; distinguere conseguenze immediate e di lungo periodo; motivare il collegamento tecnico-economico."
    elif sid == "english":
        e["prompt"] = f"Use {topic} to write a {80+n*10}-word technical text about a machine, a process or a safety rule. Underline five target forms and add an Italian glossary."
        e["expected"] = "Correct target form, clear word order, precise technical vocabulary and consistent units."
        e["solution"] = "Model workflow: choose the communicative purpose, draft short sentences, check verbs and word order, verify terminology, then read the text aloud."
    elif sid in {"math", "math_complements"}:
        a = 4 + n
        b = 7 + lesson["year"]
        e["prompt"] = f"Imposta un problema su {topic} usando i parametri A={a} e B={b}. Definisci dominio, procedimento, risultato e almeno due controlli. Prova {n}."
        e["data"] = {"A": a, "B": b, "unitSystem": "SI quando applicabile"}
        e["expected"] = "Procedimento leggibile, dominio dichiarato, calcolo controllato e interpretazione."
        e["solution"] = "Soluzione metodologica: tradurre la richiesta in variabili, stabilire condizioni, applicare il metodo della lezione, sostituire il risultato e controllarlo graficamente o con un caso limite."
    elif sid == "religion":
        e["prompt"] = f"Analizza un caso professionale collegato a {topic}: fatti, soggetti, valori in conflitto, tre alternative, conseguenze e decisione motivata. Scenario {n}."
        e["expected"] = "Argomentazione trasparente che distingua fatti, valori e conseguenze."
        e["solution"] = "Una risposta valida ricostruisce il caso, rende visibili i soggetti meno tutelati, confronta alternative con criteri dichiarati e motiva la decisione."
    elif sid == "pe":
        e["prompt"] = f"Progetta una scheda prudente su {topic}: obiettivo, preparazione, progressione, criteri di arresto, recupero e adattamenti ergonomici. Scenario {n}."
        e["expected"] = "Sequenza progressiva e non diagnostica, con segnali di allarme espliciti."
        e["solution"] = "Definire prima obiettivo e limiti; privilegiare tecnica e progressione; distinguere fatica e dolore; rinviare a un professionista in presenza di segnali d'allarme."
    else:
        e["prompt"] = f"Caso tecnico {n} su {topic}: descrivi funzione, schema, dati necessari, unità, modello, procedura di verifica, rischio principale e criterio di accettazione."
        e["expected"] = "Scheda tecnica tracciabile con ipotesi, passaggi, controlli e sicurezza."
        e["solution"] = "Partire dalla funzione; delimitare il sistema; raccogliere dati; scegliere modello o schema; eseguire il calcolo/simulazione; confrontare con tolleranze; documentare esito e rischio residuo."
    e["steps"] = ["Comprensione", "Dati e ipotesi", "Metodo", "Svolgimento", "Controllo e commento"]
    e["source"] = "Seed editoriale originale MechLab Academy v1.1"
save("exercises.json", exercises)

# Flashcard: tre fuochi per lezione, tutti esplicitamente contestualizzati.
flashcards = load("flashcards.json")
per_lesson_f = defaultdict(int)
for f in flashcards:
    lesson = lesson_by_id[f["lessonId"]]
    sid = lesson["subjectId"]
    topic = topic_of(lesson)
    per_lesson_f[lesson["id"]] += 1
    n = per_lesson_f[lesson["id"]]
    focus = (n - 1) % 3
    if sid == "italian":
        qa = [
            (f"{topic} — quale griglia guida l'analisi?", "Contesto → testo → temi e stile → interpretazione sostenuta da prove."),
            (f"{topic} — quale errore evitare nell'analisi?", "Riassumere o elencare figure retoriche senza spiegare come sostengono il significato."),
            (f"{topic} — come costruire un paragrafo argomentativo?", "Tesi chiara → prova pertinente → commento → collegamento → conclusione."),
        ][focus]
    elif sid == "history":
        qa = [
            (f"{topic} — struttura minima di studio", "Cronologia, attori, cause, svolgimento, conseguenze, cambiamenti e continuità."),
            (f"{topic} — successione e causa sono equivalenti?", "No. La successione temporale non basta: serve un meccanismo causale sostenuto da fonti o dati."),
            (f"{topic} — collegamento tecnico-economico", "Chiedere come lavoro, energia, infrastrutture, strumenti e organizzazione produttiva cambiano il processo."),
        ][focus]
    elif sid == "english":
        qa = [
            (f"{topic} — study sequence", "Meaning and use → form → controlled examples → technical application → pronunciation check."),
            (f"{topic} — technical writing rule", "Use short, unambiguous sentences, precise verbs, stable terminology and consistent units."),
            (f"{topic} — common check", "Check word order, verb form, articles, prepositions and false friends."),
        ][focus]
    elif sid in {"math", "math_complements"}:
        qa = [
            (f"{topic} — procedura minima", "Definire oggetti e dominio → scegliere rappresentazione e metodo → svolgere → controllare → interpretare."),
            (f"{topic} — controlli finali", "Sostituzione, segno, ordine di grandezza, dominio, grafico e coerenza con il fenomeno."),
            (f"{topic} — applicazione tecnica", "Dichiarare variabili e unità, mantenere precisione coerente e commentare il risultato fisico."),
        ][focus]
    elif sid == "religion":
        qa = [
            (f"{topic} — metodo etico", "Fatti → soggetti → valori → alternative → conseguenze → decisione motivata."),
            (f"{topic} — responsabilità professionale", "Rendere visibili rischi, limiti, conflitti di interesse e impatti sulle persone."),
            (f"{topic} — fattibilità ed etica", "Una scelta tecnicamente possibile deve ancora essere valutata per sicurezza, equità, ambiente e bene comune."),
        ][focus]
    elif sid == "pe":
        qa = [
            (f"{topic} — priorità", "Qualità del movimento, progressione, recupero e assenza di segnali d'allarme."),
            (f"{topic} — segnali per fermarsi", "Dolore acuto, trauma, sintomi neurologici o peggioramento persistente richiedono valutazione."),
            (f"{topic} — ergonomia", "Ridurre esposizione, usare ausili, variare compiti e organizzare pause senza sostituire indicazioni sanitarie."),
        ][focus]
    else:
        qa = [
            (f"{topic} — struttura della scheda tecnica", "Funzione → confini → ingressi/uscite → grandezze → modello → verifica → sicurezza."),
            (f"{topic} — controllo del modello", "Dichiarare ipotesi, campo di validità, unità, tolleranze e criterio di accettazione."),
            (f"{topic} — applicazione industriale", "Valutare insieme prestazione, qualità, energia, manutenzione, sicurezza e rischio residuo."),
        ][focus]
    f["front"], f["back"] = qa
    formulas = formula_for(topic)
    f["formula"] = formulas[0] if formulas else ""
save("flashcards.json", flashcards)

# Mappe: titolo e nodi coerenti con materia; ID e coordinate restano stabili.
maps = load("maps.json")
per_topic_map = defaultdict(int)
for m in maps:
    lesson = lesson_by_id[m["lessonId"]]
    sid = lesson["subjectId"]
    topic = topic_of(lesson)
    per_topic_map[(sid, topic)] += 1
    variant = per_topic_map[(sid, topic)]
    variant_name = ["quadro generale", "metodo e applicazioni", "errori e collegamenti"][(variant - 1) % 3]
    m["title"] = f"{topic} — {variant_name} — {lesson['year']}° anno"
    if sid == "italian": labels = ["Contesto", "Testi e opere", "Temi", "Stile", "Interpretazione"]
    elif sid == "history": labels = ["Cronologia", "Attori", "Cause", "Conseguenze", "Tecnica e società"]
    elif sid == "english": labels = ["Meaning", "Form", "Examples", "Technical use", "Common errors"]
    elif sid in {"math", "math_complements"}: labels = ["Definizione", "Rappresentazione", "Procedura", "Controlli", "Applicazione tecnica"]
    elif sid == "religion": labels = ["Fatti", "Soggetti", "Valori", "Alternative", "Conseguenze"]
    elif sid == "pe": labels = ["Obiettivo", "Tecnica", "Progressione", "Ergonomia", "Segnali d'allarme"]
    else: labels = ["Funzione", "Componenti e dati", "Modello o schema", "Verifica", "Sicurezza e manutenzione"]
    for node, label in zip([n for n in m["nodes"] if n["id"] != "c"], labels):
        node["label"] = label
    m["nodes"][0]["label"] = topic
save("maps.json", maps)

print("Seed editoriale arricchito senza modificare ID o riferimenti.")
