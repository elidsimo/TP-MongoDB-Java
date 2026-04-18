package org.example;

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import java.util.*;
import java.util.regex.Pattern;

/**
 * TP7 - Manipulation de MongoDB avec une API Java (version terminal)
 * Université Sultan Moulay Slimane - ENSA Khouribga
 * Pr. Nassima SOUSSI
 */
public class Main {

    private static final String URI        = "mongodb://localhost:27017";
    private static final String DB_NAME    = "test";
    private static final String COLLECTION = "tp1";

    private static final String RESET  = "\u001B[0m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE   = "\u001B[34m";
    private static final String RED    = "\u001B[31m";
    private static final String CYAN   = "\u001B[36m";
    private static final String BOLD   = "\u001B[1m";
    private static final String DIM    = "\u001B[2m";

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            running = handleChoice(choice);
        }
        System.out.println(DIM + "\nAu revoir !\n" + RESET);
    }

    private static void printMenu() {
        System.out.println(YELLOW + BOLD + "\n══════════ MENU PRINCIPAL ══════════" + RESET);
        System.out.println(BOLD + "  ── Gestion des bases ──" + RESET);
        System.out.println("   1  Afficher toutes les bases de données");
        System.out.println("   2  Créer une base de données");
        System.out.println("   3  Supprimer une base de données");
        System.out.println("   4  Afficher les collections d'une base");
        System.out.println("   5  Ajouter un document à la collection");
        System.out.println(BOLD + "  ── Requêtes TP1 (books.json) ──" + RESET);
        System.out.println("   6  Q1  - N premiers documents");
        System.out.println("   7  Q2  - Livres par catégorie");
        System.out.println("   8  Q3  - Auteur + pages > 300, triés par ISBN");
        System.out.println("   9  Q4  - Mot-clé dans titre + année publiée");
        System.out.println("  10  Q5  - Marquer livres 'MongoDB' comme empruntés");
        System.out.println("  11  Q6  - Pages = 0 ou authors vide");
        System.out.println("  12  Q7  - Auteurs d'une catégorie (triés pages desc)");
        System.out.println("  13  Q8  - Ajouter auteurs aux livres [mot-clé]");
        System.out.println("  14  Q9  - Catégories exclues + années 2009/2011");
        System.out.println("  15  Q10 - Moyenne des pages par catégorie");
        System.out.println("  16  Q11 - Sujets par auteur → collection auteurs_categories");
        System.out.println(BOLD + "   0  Quitter" + RESET);
        System.out.print(CYAN + "\n> Votre choix : " + RESET);
    }

    private static boolean handleChoice(String choice) {
        switch (choice) {
            case "1"  -> runQ1_ListDatabases();
            case "2"  -> runQ2_CreateDatabase();
            case "3"  -> runQ3_DropDatabase();
            case "4"  -> runQ4_ListCollections();
            case "5"  -> runQ5_AddDocument();
            case "6"  -> runTP1_Q1();
            case "7"  -> runTP1_Q2();
            case "8"  -> runTP1_Q3();
            case "9"  -> runTP1_Q4();
            case "10" -> runTP1_Q5();
            case "11" -> runTP1_Q6();
            case "12" -> runTP1_Q7();
            case "13" -> runTP1_Q8();
            case "14" -> runTP1_Q9();
            case "15" -> runTP1_Q10();
            case "16" -> runTP1_Q11();
            case "0"  -> { return false; }
            default   -> System.out.println(RED + "  Choix invalide." + RESET);
        }
        return true;
    }

    private static void header(String title) {
        System.out.println(YELLOW + "\n─── " + title + " ───" + RESET);
    }
    private static void ok(String msg)   { System.out.println(GREEN  + "  ✔  " + msg + RESET); }
    private static void item(String msg) { System.out.println("     • " + msg); }
    private static void info(String msg) { System.out.println(BLUE   + "  ℹ  " + msg + RESET); }
    private static void err(String msg)  { System.out.println(RED    + "  ✖  " + msg + RESET); }

    private static String prompt(String label) {
        System.out.print(CYAN + "  " + label + " : " + RESET);
        return scanner.nextLine().trim();
    }

    private static int promptInt(String label, int defaultVal) {
        String v = prompt(label + " [défaut=" + defaultVal + "]");
        try { return v.isEmpty() ? defaultVal : Integer.parseInt(v); }
        catch (NumberFormatException e) { return defaultVal; }
    }

    /** 1 - Afficher toutes les bases de données */
    private static void runQ1_ListDatabases() {
        header("Bases de données existantes");
        try (MongoClient c = MongoClients.create(URI)) {
            c.listDatabaseNames().forEach(Main::item);
        } catch (Exception ex) { err(ex.getMessage()); }
    }

    /** 2 - Créer une base de données */
    private static void runQ2_CreateDatabase() {
        String nom = prompt("Nom de la base à créer");
        if (nom.isEmpty()) { err("Nom vide, opération annulée."); return; }
        header("Créer la base : " + nom);
        try (MongoClient c = MongoClients.create(URI)) {
            c.getDatabase(nom).createCollection("testCollection");
            ok("Base '" + nom + "' créée avec la collection 'testCollection'.");
        } catch (Exception ex) { err(ex.getMessage()); }
    }

    /** 3 - Supprimer une base de données */
    private static void runQ3_DropDatabase() {
        String nom = prompt("Nom de la base à supprimer");
        if (nom.isEmpty()) { err("Nom vide, opération annulée."); return; }
        header("Supprimer la base : " + nom);
        try (MongoClient c = MongoClients.create(URI)) {
            c.getDatabase(nom).drop();
            ok("Base '" + nom + "' supprimée.");
        } catch (Exception ex) { err(ex.getMessage()); }
    }

    /** 4 - Afficher les collections d'une base */
    private static void runQ4_ListCollections() {
        String nom = prompt("Nom de la base [défaut=" + DB_NAME + "]");
        if (nom.isEmpty()) nom = DB_NAME;
        header("Collections dans : " + nom);
        try (MongoClient c = MongoClients.create(URI)) {
            c.getDatabase(nom).listCollectionNames().forEach(Main::item);
        } catch (Exception ex) { err(ex.getMessage()); }
    }

    /** 5 - Ajouter un document */
    private static void runQ5_AddDocument() {
        header("Ajouter un document dans " + DB_NAME + "." + COLLECTION);
        String titre  = prompt("Titre");
        String auteur = prompt("Auteur");
        int    annee  = promptInt("Année", 2024);
        try (MongoClient c = MongoClients.create(URI)) {
            MongoCollection<Document> col = c.getDatabase(DB_NAME).getCollection(COLLECTION);
            col.insertOne(new Document("title", titre)
                    .append("authors", Arrays.asList(auteur))
                    .append("year", annee));
            ok("Document ajouté : '" + titre + "' par " + auteur + " (" + annee + ")");
        } catch (Exception ex) { err(ex.getMessage()); }
    }

  
    /**
     * Q1 – Afficher les N premiers documents
     * MQL : db.tp1.find().limit(N)
     */
    private static void runTP1_Q1() {
        int limite = promptInt("Limite (nombre de documents)", 200);
        header("TP1 Q1 - " + limite + " premiers documents");
        try (MongoClient c = MongoClients.create(URI)) {
            MongoCollection<Document> col = c.getDatabase(DB_NAME).getCollection(COLLECTION);
            info("Total dans la collection : " + col.countDocuments());
            col.find().limit(limite).forEach(d -> item(d.getString("title")));
        } catch (Exception ex) { err(ex.getMessage()); }
    }

    /**
     * Q2 – Livres d'une catégorie donnée
     * MQL : db.tp1.find({ categories: "Internet" }, { title:1, isbn:1, pageCount:1, _id:0 })
     */
    private static void runTP1_Q2() {
        String cat = prompt("Catégorie [défaut=Internet]");
        if (cat.isEmpty()) cat = "Internet";
        header("TP1 Q2 - Livres de la catégorie : " + cat);
        try (MongoClient c = MongoClients.create(URI)) {
            MongoCollection<Document> col = c.getDatabase(DB_NAME).getCollection(COLLECTION);
            Bson filtre = eq("categories", cat);
            Bson proj   = Projections.fields(
                    Projections.include("title", "isbn", "pageCount"),
                    Projections.excludeId());
            info("Nombre trouvé : " + col.countDocuments(filtre));
            col.find(filtre).projection(proj).forEach(d ->
                    item(d.getString("title")
                            + "  |  ISBN: " + d.getString("isbn")
                            + "  |  Pages: " + d.getInteger("pageCount", 0)));
        } catch (Exception ex) { err(ex.getMessage()); }
    }

    /**
     * Q3 – Livres d'un auteur précis avec pageCount > 300, triés par ISBN asc
     * MQL : db.tp1.find({ authors: "David A. Black", pageCount: { $gt: 300 } }).sort({ isbn: 1 })
     */
    private static void runTP1_Q3() {
        String auteur = prompt("Auteur [défaut=David A. Black]");
        if (auteur.isEmpty()) auteur = "David A. Black";
        header("TP1 Q3 - Livres de [" + auteur + "] | pages > 300 | triés ISBN");
        try (MongoClient c = MongoClients.create(URI)) {
            MongoCollection<Document> col = c.getDatabase(DB_NAME).getCollection(COLLECTION);
            Bson filtre = and(eq("authors", auteur), gt("pageCount", 300));
            col.find(filtre).sort(Sorts.ascending("isbn")).forEach(d ->
                    item(d.getString("title")
                            + "  |  ISBN: " + d.getString("isbn")
                            + "  |  Pages: " + d.getInteger("pageCount", 0)));
        } catch (Exception ex) { err(ex.getMessage()); }
    }

    /**
     * Q4 – Livres dont le titre contient un mot-clé ET publiés une année donnée
     * MQL : db.tp1.find({ title: /Action/, "publishedDate.date": /^2011/ })
     */
    private static void runTP1_Q4() {
        String motCle = prompt("Mot-clé dans le titre [défaut=Action]");
        if (motCle.isEmpty()) motCle = "Action";
        String annee = prompt("Année publiée [défaut=2011]");
        if (annee.isEmpty()) annee = "2011";
        header("TP1 Q4 - Titre contient [" + motCle + "] + publié en " + annee);
        try (MongoClient c = MongoClients.create(URI)) {
            MongoCollection<Document> col = c.getDatabase(DB_NAME).getCollection(COLLECTION);
            Bson filtre = and(
                    regex("title", Pattern.compile(motCle, Pattern.CASE_INSENSITIVE)),
                    regex("publishedDate.date", Pattern.compile("^" + annee))
            );
            info("Nombre trouvé : " + col.countDocuments(filtre));
            col.find(filtre).forEach(d -> {
                Document pub  = d.get("publishedDate", Document.class);
                String   date = pub != null ? pub.getString("date") : "?";
                item(d.getString("title") + "  |  Publié le : " + date);
            });
        } catch (Exception ex) { err(ex.getMessage()); }
    }

    /**
     * Q5 – Ajouter champ emprunte:true aux livres dont le titre contient "MongoDB"
     * MQL : db.tp1.updateMany({ title: /MongoDB/ }, { $set: { emprunte: true } })
     */
    private static void runTP1_Q5() {
        header("TP1 Q5 - Marquer livres [MongoDB] comme empruntés");
        try (MongoClient c = MongoClients.create(URI)) {
            MongoCollection<Document> col = c.getDatabase(DB_NAME).getCollection(COLLECTION);
            Bson filtre = regex("title", Pattern.compile("MongoDB", Pattern.CASE_INSENSITIVE));
            var result  = col.updateMany(filtre, set("emprunte", true));
            ok("Matched: " + result.getMatchedCount()
                    + "  |  Modifiés: " + result.getModifiedCount());
        } catch (Exception ex) { err(ex.getMessage()); }
    }

    /**
     * Q6 – Afficher livres avec pageCount = 0 OU authors vide
     * MQL : db.tp1.find({ $or: [ { pageCount: 0 }, { authors: { $size: 0 } } ] },
     *                   { isbn:1, title:1, _id:0 })
     */
    private static void runTP1_Q6() {
        header("TP1 Q6 - Livres avec pageCount=0 ou authors vide");
        try (MongoClient c = MongoClients.create(URI)) {
            MongoCollection<Document> col = c.getDatabase(DB_NAME).getCollection(COLLECTION);
            Bson filtre = or(
                    eq("pageCount", 0),
                    Filters.size("authors", 0)
            );
            Bson proj = Projections.fields(
                    Projections.include("title", "isbn"),
                    Projections.excludeId());
            info("Nombre trouvé : " + col.countDocuments(filtre));
            col.find(filtre).projection(proj).forEach(d ->
                    item(d.getString("title")
                            + "  |  ISBN: " + d.getString("isbn")));
        } catch (Exception ex) { err(ex.getMessage()); }
    }

    /**
     * Q7 – Auteurs publiant dans une catégorie, triés par pageCount desc
     * MQL : db.tp1.find({ categories: "Microsoft" }, { authors:1, pageCount:1, _id:0 })
     *             .sort({ pageCount: -1 })
     */
    private static void runTP1_Q7() {
        String cat = prompt("Catégorie [défaut=Microsoft]");
        if (cat.isEmpty()) cat = "Microsoft";
        header("TP1 Q7 - Auteurs dans la catégorie : " + cat);
        try (MongoClient c = MongoClients.create(URI)) {
            MongoCollection<Document> col = c.getDatabase(DB_NAME).getCollection(COLLECTION);
            Bson filtre = eq("categories", cat);
            Bson proj   = Projections.fields(
                    Projections.include("authors", "pageCount"),
                    Projections.excludeId());
            col.find(filtre).sort(Sorts.descending("pageCount")).projection(proj).forEach(d -> {
                List<?> authors = d.getList("authors", Object.class);
                item("Pages: " + d.getInteger("pageCount", 0)
                        + "  |  Auteurs: " + (authors != null ? authors : "[]"));
            });
        } catch (Exception ex) { err(ex.getMessage()); }
    }

    /**
     * Q8 – Ajouter deux auteurs aux livres dont le titre contient le mot-clé
     * MQL : db.tp1.updateMany({ title: /Database/ },
     *                         { $push: { authors: { $each: ["Pr. Nassima SOUSSI","Ton Nom"] } } })
     */
    private static void runTP1_Q8() {
        String motCle = prompt("Mot-clé dans le titre [défaut=Database]");
        if (motCle.isEmpty()) motCle = "Database";
        header("TP1 Q8 - Ajouter auteurs aux livres contenant [" + motCle + "]");
        try (MongoClient c = MongoClients.create(URI)) {
            MongoCollection<Document> col = c.getDatabase(DB_NAME).getCollection(COLLECTION);
            Bson filtre = regex("title", Pattern.compile(motCle, Pattern.CASE_INSENSITIVE));
            Bson update = Updates.pushEach("authors",
                    Arrays.asList("Pr. Nassima SOUSSI", "Ton Nom"));
            var result = col.updateMany(filtre, update);
            ok("Matched: " + result.getMatchedCount()
                    + "  |  Modifiés: " + result.getModifiedCount());
        } catch (Exception ex) { err(ex.getMessage()); }
    }

    /**
     * Q9 – Livres dont les catégories NE sont PAS dans la liste exclue
     *      ET publiés en 2009 ou 2011
     * MQL : db.tp1.find({
     *   categories: { $nin: ["Business","Microsoft","Microsoft.NET","In Action"] },
     *   "publishedDate.date": /^(2009|2011)/
     * }, { title:1, "publishedDate.date":1, categories:1, _id:0 })
     */
    private static void runTP1_Q9() {
        header("TP1 Q9 - Catégories exclues + publié en 2009 ou 2011");
        try (MongoClient c = MongoClients.create(URI)) {
            MongoCollection<Document> col = c.getDatabase(DB_NAME).getCollection(COLLECTION);
            List<String> exclues = Arrays.asList(
                    "Business", "Microsoft", "Microsoft.NET", "In Action");
            Bson filtre = and(
                    nin("categories", exclues),
                    regex("publishedDate.date", Pattern.compile("^(2009|2011)"))
            );
            Bson proj = Projections.fields(
                    Projections.include("title", "publishedDate", "categories"),
                    Projections.excludeId());
            info("Nombre trouvé : " + col.countDocuments(filtre));
            col.find(filtre).projection(proj).forEach(d -> {
                Document pub  = d.get("publishedDate", Document.class);
                String   date = pub != null ? pub.getString("date") : "?";
                List<?>  cats = d.getList("categories", Object.class);
                item(d.getString("title")
                        + "  |  " + date
                        + "  |  " + cats);
            });
        } catch (Exception ex) { err(ex.getMessage()); }
    }

    /**
     * Q10 – Moyenne des pages par catégorie (agrégation), triée décroissante
     * MQL : db.tp1.aggregate([
     *   { $unwind: "$categories" },
     *   { $group: { _id: "$categories", moyenne: { $avg: "$pageCount" } } },
     *   { $sort: { moyenne: -1 } }
     * ])
     */
    private static void runTP1_Q10() {
        header("TP1 Q10 - Moyenne des pages par catégorie");
        try (MongoClient c = MongoClients.create(URI)) {
            MongoCollection<Document> col = c.getDatabase(DB_NAME).getCollection(COLLECTION);
            List<Bson> pipeline = Arrays.asList(
                    Aggregates.unwind("$categories"),
                    Aggregates.group("$categories",
                            Accumulators.avg("moyenne", "$pageCount")),
                    Aggregates.sort(Sorts.descending("moyenne"))
            );
            col.aggregate(pipeline).forEach(d -> {
                Double moy = d.getDouble("moyenne");
                item(String.format("%-30s ->  %.1f pages en moyenne",
                        d.getString("_id"), moy != null ? moy : 0.0));
            });
        } catch (Exception ex) { err(ex.getMessage()); }
    }

    /**
     * Q11 – Sujets par auteur, résultat sauvegardé dans auteurs_categories
     * MQL : db.tp1.aggregate([
     *   { $unwind: "$authors" },
     *   { $unwind: "$categories" },
     *   { $group: { _id: "$authors", categories: { $addToSet: "$categories" } } },
     *   { $out: "auteurs_categories" }
     * ])
     */
    private static void runTP1_Q11() {
        header("TP1 Q11 - Sujets par auteur  →  collection auteurs_categories");
        try (MongoClient c = MongoClients.create(URI)) {
            MongoCollection<Document> col = c.getDatabase(DB_NAME).getCollection(COLLECTION);
            List<Bson> pipeline = Arrays.asList(
                    Aggregates.unwind("$authors"),
                    Aggregates.unwind("$categories"),
                    Aggregates.group("$authors",
                            Accumulators.addToSet("categories", "$categories")),
                    Aggregates.out("auteurs_categories")
            );
            col.aggregate(pipeline).toCollection();
            ok("Collection 'auteurs_categories' créée / mise à jour.");
            info("── Vérification : 5 premiers enregistrements ──");
            MongoCollection<Document> outCol =
                    c.getDatabase(DB_NAME).getCollection("auteurs_categories");
            outCol.find().limit(5).forEach(d -> {
                List<?> cats = d.getList("categories", Object.class);
                item(d.getString("_id") + "  →  " + cats);
            });
        } catch (Exception ex) { err(ex.getMessage()); }
    }
}