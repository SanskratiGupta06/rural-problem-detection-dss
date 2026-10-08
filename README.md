# Rural Problem Detection and Decision Support System

A bilingual (English / Hindi) decision support platform that collects village-level data,
classifies complaints, scores villages by priority and helps the Panchayat decide what to fix first.
Prototype built for the study area around Nipaniya Kalan (5 villages near VIT Bhopal).

## Project structure

```
rural-dss/
├── index.html              # redirects to frontend/ (used by GitHub Pages)
├── frontend/               # web app (HTML, CSS, JavaScript)
│   ├── index.html
│   ├── css/style.css
│   └── js/app.js           # UI, survey form, complaints board, village ranking, map, EN/HI toggle
└── backend/                # REST API (Java 17+, no external libraries)
    ├── run.sh / run.bat
    └── src/dss/
        ├── Main.java       # HTTP server and endpoints
        ├── Category.java   # 7 problem categories, weights, keywords, scheme suggestions
        ├── Classifier.java # complaint text -> category + severity (1-5)
        └── Scoring.java    # village priority score (0-100) and label
```

## How it works

1. **Survey + complaints** give a severity (0-100) per category for each village.
2. **Classifier** reads a complaint in Hindi or English and picks the category and severity.
3. **Scoring** blends survey severity (70%) with open complaints (30%) per category,
   then takes a weighted average (water 20%, roads/garbage/crops/health 15% each, lights/education 10% each).
4. Villages are ranked as Critical (70+), High (50+), Moderate (30+) or Low.

## Run the frontend

Open `frontend/index.html` in a browser. It needs no server; data is kept in the browser's localStorage.

Live demo (GitHub Pages): https://sanskratigupta06.github.io/rural-problem-detection-dss/

## Run the backend

Needs JDK 17 or newer (`javac -version` to check).

```
cd backend
./run.sh          # Windows: run.bat
```

The server starts on http://localhost:8080 (pass another port as an argument).

| Endpoint | Example |
|----------|---------|
| `GET /api/health` | `curl localhost:8080/api/health` |
| `GET /api/categories` | `curl localhost:8080/api/categories` |
| `GET /api/classify?text=...` | `curl -G localhost:8080/api/classify --data-urlencode "text=Potholes on the main road"` |
| `GET /api/priority?water=80&road=70&complaints=water:4` | `curl "localhost:8080/api/priority?water=80&road=70&complaints=water:4,health:3"` |

Example response of `/api/classify`:

```json
{"category":"road","categoryName":"Roads","severity":2,"priority":"Medium","suggestion":"Repair the road; apply under PMGSY"}
```

## Roadmap

Real database (PostgreSQL + PostGIS), login and roles, computer-vision detection of potholes and garbage,
Hindi speech input, real map tiles, field data collection and a second village visit.
