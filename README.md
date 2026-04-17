# TP7 – Manipulation de MongoDB avec une API Java

> **Université Sultan Moulay Slimane – ENSA Khouribga**  
> Module : Base de Données NoSQL  
> Encadrante : **Pr. Nassima SOUSSI**

---

## Objectif

Développer une application Java en **mode terminal** qui se connecte à une base de données **MongoDB** et réalise :

- La gestion des bases de données et des collections
- La manipulation avancée du dataset **`books.json`** (questions du TP1)

---

## Prérequis

| Outil | Version recommandée |
|-------|-------------------|
| Java JDK | 17+ |
| Maven | 3.8+ |
| MongoDB | 6.x (local sur `localhost:27017`) |

---

## Structure du projet

```
TP7_MongoDB/
├── src/
│   └── main/
│       └── java/
│           └── org/example/
│               └── Main.java          # Application principale (terminal)
├── pom.xml                            # Dépendances Maven
└── README.md
```

---

## Installation & Lancement

### 1. Cloner le projet
```bash
git clone https://github.com/<votre-username>/TP7-MongoDB-Java.git
cd TP7-MongoDB-Java
```

### 2. Importer le dataset
```bash
mongoimport --db test --collection tp1 --file books.json --jsonArray
```

### 3. Compiler et exécuter
```bash
mvn package -q
java -jar target/tp7-mongodb-1.0-SNAPSHOT.jar
```

---

## Fonctionnalités

### Gestion des bases de données

| Option | Description |
|--------|-------------|
| 1 | Afficher toutes les bases de données |
| 2 | Créer une base de données |
| 3 | Supprimer une base de données |
| 4 | Afficher les collections d'une base |
| 5 | Ajouter un document à une collection |

### Requêtes TP1 – `books.json`

| Option | Requête | MQL équivalent |
|--------|---------|----------------|
| 6 | Q1 – N premiers documents | `db.tp1.find().limit(N)` |
| 7 | Q2 – Livres par catégorie | `db.tp1.find({ categories: "..." })` |
| 8 | Q3 – Auteur + pages > 300, triés ISBN | `db.tp1.find({ authors: "...", pageCount: { $gt: 300 } }).sort(...)` |
| 9 | Q4 – Mot-clé titre + année | `db.tp1.find({ title: /.../, "publishedDate.date": /^YYYY/ })` |
| 10 | Q5 – Marquer livres comme empruntés | `db.tp1.updateMany({ title: /MongoDB/ }, { $set: { emprunte: true } })` |
| 11 | Q6 – Pages = 0 ou authors vide | `db.tp1.find({ $or: [...] })` |
| 12 | Q7 – Auteurs d'une catégorie | `db.tp1.find(...).sort({ pageCount: -1 })` |
| 13 | Q8 – Ajouter auteurs (push) | `db.tp1.updateMany(..., { $push: { authors: { $each: [...] } } })` |
| 14 | Q9 – Catégories exclues + années 2009/2011 | `db.tp1.find({ categories: { $nin: [...] }, ... })` |
| 15 | Q10 – Moyenne pages / catégorie | `db.tp1.aggregate([ $unwind, $group($avg), $sort ])` |
| 16 | Q11 – Sujets par auteur → collection | `db.tp1.aggregate([ ..., $out: "auteurs_categories" ])` |

---

## Technologies utilisées

- **Java 17**
- **MongoDB Java Driver** `4.11.1` (sync)
- **Maven** (gestion des dépendances + build)
- Couleurs ANSI pour le terminal

---

## Aperçu du terminal

```
╔══════════════════════════════════════════════════════╗
║       MongoDB Manager - TP7  (Terminal Mode)         ║
║   ENSA Khouribga  |  Pr. Nassima SOUSSI              ║
╚══════════════════════════════════════════════════════╝

  URI        : mongodb://localhost:27017
  Base       : test
  Collection : tp1

══════════ MENU PRINCIPAL ══════════
  ── Gestion des bases ──
   1  Afficher toutes les bases de données
   2  Créer une base de données
   ...
```

---

## Configuration

Modifiez les constantes en haut de `Main.java` si nécessaire :

```java
private static final String URI        = "mongodb://localhost:27017";
private static final String DB_NAME    = "test";
private static final String COLLECTION = "tp1";
```
