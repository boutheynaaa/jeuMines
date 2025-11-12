package maintenance;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe principale pour l'algorithme de maintenance non déterministe.
 * Implémente les 7 phases décrites dans le document algorithmique.
 * 
 * @author Benslama Asma, Hammami Boutheyna
 * @version 1.0
 */
public class MaintenanceScheduler {
    
    private static final Random random = new Random();
    private static final double POIDS_RANG = 0.30;
    private static final double POIDS_PRIORITE = 0.25;
    private static final double POIDS_IMPACT = 0.20;
    private static final double POIDS_TEMPS = 0.15;
    private static final double POIDS_ROI = 0.10;
    
    // PHASE 1 : IDENTIFICATION DES TÂCHES
    public static List<Tache> phase1_IdentificationTaches(String projetPath) {
        System.out.println("\n=== PHASE 1 : IDENTIFICATION DES TÂCHES ===");
        
        List<Tache> taches = new ArrayList<>();
        
        // Analyse statique simulée
        taches.add(creerTache("T001", "Ajouter tests unitaires", TypeTache.PREVENTIVE, 
            8, RangImportance.IMPORTANT, Priorite.P1_HAUTE));
        
        taches.add(creerTache("T002", "Refactoring find_empty_cells()", TypeTache.PERFECTIVE,
            4, RangImportance.IMPORTANT, Priorite.P2_NORMALE));
        
        taches.add(creerTache("T003", "Corriger bug validation mines", TypeTache.CORRECTIVE,
            2, RangImportance.CRITIQUE, Priorite.P0_URGENT));
        
        taches.add(creerTache("T004", "Documenter API publique", TypeTache.PERFECTIVE,
            6, RangImportance.MODERE, Priorite.P2_NORMALE));
        
        taches.add(creerTache("T005", "Optimiser algorithme de recherche", TypeTache.PERFECTIVE,
            12, RangImportance.IMPORTANT, Priorite.P1_HAUTE));
        
        System.out.println("✓ " + taches.size() + " tâches identifiées");
        return taches;
    }
    
    // PHASE 2 : ÉVALUATION MULTICRITÈRE
    public static void phase2_EvaluationMulticritere(List<Tache> taches) {
        System.out.println("\n=== PHASE 2 : ÉVALUATION MULTICRITÈRE ===");
        
        for (Tache t : taches) {
            // Critère TEMPS
            t.categorieTemps = CategorieTemps.determiner(t.dureeHeures);
            
            // Critère IMPACT
            t.impact = evaluerImpact(t);
            
            System.out.println("✓ " + t.id + " évalué: " + t.categorieTemps + 
                             ", Impact=" + String.format("%.1f", t.impact.calculerScore()));
        }
    }
    
    // PHASE 3 : CINQ QUESTIONS CLÉS
    public static void phase3_CinqQuestions(List<Tache> taches) {
        System.out.println("\n=== PHASE 3 : ANALYSE PAR 5 QUESTIONS ===");
        
        for (Tache t : taches) {
            // QUAND ? - Planification temporelle
            definirDateDebut(t);
            
            // COMBIEN ? - Estimation ressources
            t.coutTotal = calculerCout(t);
            
            // POURQUOI ? - ROI
            t.benefices = estimerBenefices(t);
            t.roi = calculerROI(t.benefices, t.coutTotal);
            
            // COMMENT ? - Techniques
            definirTechniques(t);
            
            // QUI ? - Ressources
            affecterRessources(t);
            
            System.out.println("✓ " + t.id + ": Début=" + t.dateDebut + 
                             ", ROI=" + String.format("%.1f%%", t.roi));
        }
    }
    
    // PHASE 4 : CALCUL DES SCORES
    public static void phase4_CalculScores(List<Tache> taches) {
        System.out.println("\n=== PHASE 4 : CALCUL DES SCORES ===");
        
        for (Tache t : taches) {
            // C1: Score Rang
            double c1 = t.rang.score;
            
            // C2: Multiplicateur Priorité
            double c2 = t.priorite.multiplicateur * 100;
            
            // C3: Score Impact
            double c3 = t.impact.calculerScore();
            
            // C4: Score Temps (inversé)
            double c4 = 100.0 / t.categorieTemps.coefficient;
            
            // C5: ROI (plafonné à 100)
            double c5 = Math.min(t.roi, 100);
            
            // Score de base
            double scoreBase = c1 * POIDS_RANG + 
                              c2 * POIDS_PRIORITE + 
                              c3 * POIDS_IMPACT + 
                              c4 * POIDS_TEMPS + 
                              c5 * POIDS_ROI;
            
            // Facteur aléatoire [0.9, 1.1]
            double facteurAleatoire = 0.9 + random.nextDouble() * 0.2;
            
            t.scoreFinal = scoreBase * facteurAleatoire;
            
            System.out.println(String.format("✓ %s: Score=%.2f (C1=%.1f, C2=%.1f, C3=%.1f, C4=%.1f, C5=%.1f)",
                t.id, t.scoreFinal, c1, c2, c3, c4, c5));
        }
    }
    
    // PHASE 5 : ORDONNANCEMENT
    public static List<Tache> phase5_Ordonnancement(List<Tache> taches) {
        System.out.println("\n=== PHASE 5 : ORDONNANCEMENT ===");
        
        // Tri par score décroissant
        List<Tache> planning = taches.stream()
            .sorted((t1, t2) -> Double.compare(t2.scoreFinal, t1.scoreFinal))
            .collect(Collectors.toList());
        
        // Ajustement des dates en tenant compte des dépendances
        LocalDate currentDate = LocalDate.now();
        for (Tache t : planning) {
            if (t.dateDebut.isBefore(currentDate)) {
                t.dateDebut = currentDate;
            }
            t.dateFin = t.dateDebut.plusDays((long) Math.ceil(t.dureeHeures / 8.0));
            currentDate = t.dateFin.plusDays(1);
            
            System.out.println(String.format("✓ Position %d: %s (Score: %.2f)",
                planning.indexOf(t) + 1, t.id, t.scoreFinal));
        }
        
        return planning;
    }
    
    // PHASE 6 : SUIVI ET CONTRÔLE
    public static void phase6_SuiviControle(List<Tache> planning) {
        System.out.println("\n=== PHASE 6 : SUIVI ET CONTRÔLE ===");
        
        for (Tache t : planning) {
            // Simulation de l'exécution
            t.statut = StatutTache.TERMINEE;
            
            // Métriques de suivi
            double amelioration = random.nextDouble() * 100;
            
            if (amelioration < 70) {
                System.out.println("⚠ " + t.id + " nécessite une révision");
            } else {
                System.out.println("✓ " + t.id + " validée (amélioration: " + 
                                 String.format("%.1f%%", amelioration) + ")");
            }
        }
    }
    
    // PHASE 7 : GÉNÉRATION RAPPORT
    public static RapportMaintenance phase7_GenerationRapport(List<Tache> planning) {
        System.out.println("\n=== PHASE 7 : GÉNÉRATION DU RAPPORT ===");
        
        RapportMaintenance rapport = new RapportMaintenance();
        rapport.projet = "Jeu de Mines";
        rapport.dateAnalyse = LocalDate.now();
        rapport.analystes.addAll(Arrays.asList("Benslama Asma", "Hammami Boutheyna"));
        rapport.version = "1.0";
        rapport.nbTachesIdentifiees = planning.size();
        rapport.detteInitiale = "1j 5h";
        rapport.detteResiduelle = "0j 3h";
        
        rapport.effortTotal = planning.stream()
            .mapToDouble(t -> t.dureeHeures)
            .sum();
        
        rapport.coutTotal = planning.stream()
            .mapToDouble(t -> t.coutTotal)
            .sum();
        
        rapport.roiGlobal = planning.stream()
            .mapToDouble(t -> t.roi)
            .average()
            .orElse(0);
        
        if (!planning.isEmpty()) {
            rapport.dureeProjet = java.time.temporal.ChronoUnit.DAYS.between(
                planning.get(0).dateDebut,
                planning.get(planning.size() - 1).dateFin
            );
        }
        
        rapport.planningDetaille = new ArrayList<>(planning);
        
        // Analyse des risques
        rapport.risques.add("Complexité cyclomatique élevée dans certains modules");
        rapport.risques.add("Absence de tests unitaires sur composants critiques");
        
        // Recommandations
        rapport.recommandations.add("Implémenter TDD pour nouveaux développements");
        rapport.recommandations.add("Révision de code systématique avant merge");
        rapport.recommandations.add("Monitoring continu de la couverture de tests");
        
        System.out.println("✓ Rapport généré avec succès");
        
        return rapport;
    }
    
    // ==================== FONCTIONS AUXILIAIRES ====================
    
    private static Tache creerTache(String id, String desc, TypeTache type, 
                                   double heures, RangImportance rang, Priorite priorite) {
        Tache t = new Tache(id, desc, type);
        t.dureeHeures = heures;
        t.rang = rang;
        t.priorite = priorite;
        return t;
    }
    
    private static Impact evaluerImpact(Tache t) {
        double tech = 0, fonc = 0, org = 0;
        
        switch (t.type) {
            case CORRECTIVE:
                tech = 30; fonc = 35; org = 15;
                break;
            case PREVENTIVE:
                tech = 35; fonc = 25; org = 10;
                break;
            case PERFECTIVE:
                tech = 40; fonc = 20; org = 10;
                break;
            case ADAPTIVE:
                tech = 25; fonc = 30; org = 15;
                break;
        }
        
        if (t.rang == RangImportance.CRITIQUE) {
            tech += 10; fonc += 10;
        }
        
        return new Impact(tech, fonc, org);
    }
    
    private static void definirDateDebut(Tache t) {
        LocalDate now = LocalDate.now();
        switch (t.priorite) {
            case P0_URGENT:
                t.dateDebut = now;
                break;
            case P1_HAUTE:
                t.dateDebut = now.plusDays(2);
                break;
            case P2_NORMALE:
                t.dateDebut = now.plusWeeks(1);
                break;
            case P3_BASSE:
                t.dateDebut = now.plusWeeks(2);
                break;
            case P4_DIFFERABLE:
                t.dateDebut = now.plusMonths(1);
                break;
        }
    }
    
    private static double calculerCout(Tache t) {
        double tauxHoraire = 50.0; // €/heure
        return t.dureeHeures * tauxHoraire;
    }
    
    private static double estimerBenefices(Tache t) {
        double base = 1000.0;
        
        switch (t.type) {
            case CORRECTIVE:
                base = 1500.0;
                break;
            case PREVENTIVE:
                base = 2000.0;
                break;
            case PERFECTIVE:
                base = 1200.0;
                break;
            case ADAPTIVE:
                base = 1800.0;
                break;
        }
        
        if (t.rang == RangImportance.CRITIQUE) base *= 2.0;
        if (t.rang == RangImportance.IMPORTANT) base *= 1.5;
        
        return base;
    }
    
    private static double calculerROI(double benefices, double cout) {
        if (cout == 0) return 100.0;
        return ((benefices - cout) / cout) * 100;
    }
    
    private static void definirTechniques(Tache t) {
        switch (t.type) {
            case CORRECTIVE:
                t.techniquesAppliquees.add("Debugging");
                t.techniquesAppliquees.add("Tests de régression");
                break;
            case PREVENTIVE:
                t.techniquesAppliquees.add("TDD");
                t.techniquesAppliquees.add("JUnit 5");
                t.techniquesAppliquees.add("JaCoCo");
                break;
            case PERFECTIVE:
                t.techniquesAppliquees.add("Extract Method");
                t.techniquesAppliquees.add("Simplify Conditionals");
                t.techniquesAppliquees.add("SonarQube");
                break;
            case ADAPTIVE:
                t.techniquesAppliquees.add("Analyse d'impact");
                t.techniquesAppliquees.add("Migration progressive");
                break;
        }
    }
    
    private static void affecterRessources(Tache t) {
        int nbDevs = (int) Math.ceil(t.dureeHeures / 16.0); // Max 2 jours par dev
        for (int i = 1; i <= nbDevs; i++) {
            t.ressourcesAffectees.add("Développeur_" + i);
        }
    }
    
    // ==================== API PUBLIQUE ====================
    
    /**
     * Exécute l'algorithme complet de maintenance et retourne le rapport.
     * 
     * @param projetPath Chemin du projet à analyser
     * @param nomProjet Nom du projet
     * @return Rapport de maintenance complet
     */
    public static RapportMaintenance executerAlgorithme(String projetPath, String nomProjet) {
        List<Tache> taches = phase1_IdentificationTaches(projetPath);
        phase2_EvaluationMulticritere(taches);
        phase3_CinqQuestions(taches);
        phase4_CalculScores(taches);
        List<Tache> planning = phase5_Ordonnancement(taches);
        phase6_SuiviControle(planning);
        RapportMaintenance rapport = phase7_GenerationRapport(planning);
        rapport.projet = nomProjet;
        return rapport;
    }
    
    /**
     * Ajoute une tâche personnalisée au système.
     * 
     * @param id Identifiant unique
     * @param description Description de la tâche
     * @param type Type de maintenance
     * @param heures Durée estimée en heures
     * @param rang Importance de la tâche
     * @param priorite Niveau de priorité
     * @return La tâche créée
     */
    public static Tache creerTachePersonnalisee(String id, String description, TypeTache type,
                                                 double heures, RangImportance rang, Priorite priorite) {
        return creerTache(id, description, type, heures, rang, priorite);
    }
    
    // ==================== CLASSES INTERNES ====================
    
    /** Représente une tâche de maintenance */
    public static class Tache {
        public String id;
        public String description;
        public TypeTache type;
        public CategorieTemps categorieTemps;
        public double dureeHeures;
        public RangImportance rang;
        public Priorite priorite;
        public Impact impact;
        public double coutTotal;
        public double benefices;
        public double roi;
        public LocalDate dateDebut;
        public LocalDate dateFin;
        public List<String> dependances;
        public List<String> ressourcesAffectees;
        public List<String> techniquesAppliquees;
        public Map<String, Double> objectifsMetriques;
        public double scoreFinal;
        public StatutTache statut;
        
        public Tache(String id, String description, TypeTache type) {
            this.id = id;
            this.description = description;
            this.type = type;
            this.dependances = new ArrayList<>();
            this.ressourcesAffectees = new ArrayList<>();
            this.techniquesAppliquees = new ArrayList<>();
            this.objectifsMetriques = new HashMap<>();
            this.statut = StatutTache.EN_ATTENTE;
        }
    }
    
    public enum TypeTache {
        CORRECTIVE, PREVENTIVE, PERFECTIVE, ADAPTIVE
    }
    
    public enum CategorieTemps {
        COURT(1.0), MOYEN(1.5), LONG(2.0), TRES_LONG(3.0);
        
        final double coefficient;
        CategorieTemps(double coeff) { this.coefficient = coeff; }
        
        static CategorieTemps determiner(double heures) {
            if (heures <= 2) return COURT;
            if (heures <= 8) return MOYEN;
            if (heures <= 24) return LONG;
            return TRES_LONG;
        }
    }
    
    public enum RangImportance {
        CRITIQUE(100), IMPORTANT(75), MODERE(50), MINEUR(25), COSMETIQUE(10);
        
        final int score;
        RangImportance(int score) { this.score = score; }
    }
    
    public enum Priorite {
        P0_URGENT(4.0), P1_HAUTE(3.0), P2_NORMALE(2.0), P3_BASSE(1.0), P4_DIFFERABLE(0.5);
        
        final double multiplicateur;
        Priorite(double mult) { this.multiplicateur = mult; }
    }
    
    public static class Impact {
        public double technique;
        public double fonctionnel;
        public double organisationnel;
        
        public Impact(double tech, double fonc, double org) {
            this.technique = Math.min(tech, 40);
            this.fonctionnel = Math.min(fonc, 40);
            this.organisationnel = Math.min(org, 20);
        }
        
        public double calculerScore() {
            return 0.4 * technique + 0.4 * fonctionnel + 0.2 * organisationnel;
        }
    }
    
    public enum StatutTache {
        EN_ATTENTE, EN_COURS, TERMINEE, BLOQUEE, REJETEE
    }
    
    public static class RapportMaintenance {
        public String projet;
        public LocalDate dateAnalyse;
        public List<String> analystes;
        public String version;
        public int nbTachesIdentifiees;
        public String detteInitiale;
        public String detteResiduelle;
        public double effortTotal;
        public double coutTotal;
        public double roiGlobal;
        public long dureeProjet;
        public List<Tache> planningDetaille;
        public List<String> risques;
        public List<String> recommandations;
        
        public RapportMaintenance() {
            this.analystes = new ArrayList<>();
            this.planningDetaille = new ArrayList<>();
            this.risques = new ArrayList<>();
            this.recommandations = new ArrayList<>();
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════════════════\n");
            sb.append("        RAPPORT DE MAINTENANCE - ").append(projet).append("\n");
            sb.append("═══════════════════════════════════════════════════════\n\n");
            sb.append("Date d'analyse : ").append(dateAnalyse).append("\n");
            sb.append("Version : ").append(version).append("\n");
            sb.append("Analystes : ").append(String.join(", ", analystes)).append("\n\n");
            
            sb.append("--- SYNTHÈSE EXÉCUTIVE ---\n");
            sb.append("Tâches identifiées : ").append(nbTachesIdentifiees).append("\n");
            sb.append("Dette technique initiale : ").append(detteInitiale).append("\n");
            sb.append("Dette technique résiduelle : ").append(detteResiduelle).append("\n");
            sb.append("Effort total estimé : ").append(String.format("%.1f heures", effortTotal)).append("\n");
            sb.append("Coût total : ").append(String.format("%.2f €", coutTotal)).append("\n");
            sb.append("ROI global : ").append(String.format("%.1f%%", roiGlobal)).append("\n");
            sb.append("Durée projet : ").append(dureeProjet).append(" jours\n\n");
            
            sb.append("--- PLANNING DÉTAILLÉ ---\n");
            for (int i = 0; i < planningDetaille.size(); i++) {
                Tache t = planningDetaille.get(i);
                sb.append(String.format("\n%d. %s [Score: %.2f]\n", i+1, t.description, t.scoreFinal));
                sb.append("   ID: ").append(t.id).append("\n");
                sb.append("   Priorité: ").append(t.priorite).append(" | Rang: ").append(t.rang).append("\n");
                sb.append("   Dates: ").append(t.dateDebut).append(" → ").append(t.dateFin).append("\n");
                sb.append("   Durée: ").append(String.format("%.1fh", t.dureeHeures)).append("\n");
                sb.append("   ROI: ").append(String.format("%.1f%%", t.roi)).append("\n");
                if (!t.ressourcesAffectees.isEmpty()) {
                    sb.append("   Ressources: ").append(String.join(", ", t.ressourcesAffectees)).append("\n");
                }
            }
            
            if (!risques.isEmpty()) {
                sb.append("\n\n--- ANALYSE DES RISQUES ---\n");
                risques.forEach(r -> sb.append("⚠ ").append(r).append("\n"));
            }
            
            if (!recommandations.isEmpty()) {
                sb.append("\n--- RECOMMANDATIONS ---\n");
                recommandations.forEach(r -> sb.append("➤ ").append(r).append("\n"));
            }
            
            sb.append("\n═══════════════════════════════════════════════════════\n");
            return sb.toString();
        }
    }
    
    // ==================== MAIN (DEMO) ====================
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   ALGORITHME DE MAINTENANCE NON DÉTERMINISTE         ║");
        System.out.println("║            Application: Jeu de Mines                  ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        
        try {
            RapportMaintenance rapport = executerAlgorithme("projet/jeu-mines", "Jeu de Mines");
            System.out.println("\n" + rapport);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution: " + e.getMessage());
            e.printStackTrace();
        }
    }
}