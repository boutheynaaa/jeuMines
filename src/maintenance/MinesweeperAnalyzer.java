package maintenance;

import java.util.ArrayList;
import java.util.List;
import static maintenance.MaintenanceScheduler.*;

/**
 * Analyseur spécifique pour le projet Jeu de Mines
 * Basé sur les résultats de l'analyse SonarQube et JArchitect
 */
public class MinesweeperAnalyzer implements ProjectAnalyzer {
    
    @Override
    public List<Tache> analyzeProject(String projectPath) {
        List<Tache> taches = new ArrayList<>();
        
        // TÂCHE 1: Ajouter tests unitaires (Couverture 0%)
        Tache t1 = new Tache("T001", "Ajouter tests unitaires pour Board et Mines", 
                             TypeTache.PREVENTIVE);
        t1.dureeHeures = 8.0;
        t1.rang = RangImportance.CRITIQUE;
        t1.priorite = Priorite.P1_HAUTE;
        t1.impact = new Impact(40, 30, 20);
        taches.add(t1);
        
        // TÂCHE 2: Refactoring find_empty_cells()
        Tache t2 = new Tache("T002", "Refactoring find_empty_cells() - Réduire complexité de 27 à 15", 
                             TypeTache.PERFECTIVE);
        t2.dureeHeures = 4.0;
        t2.rang = RangImportance.IMPORTANT;
        t2.priorite = Priorite.P2_NORMALE;
        t2.impact = new Impact(35, 15, 10);
        taches.add(t2);
        
        // TÂCHE 3: Corriger bug SonarQube
        Tache t3 = new Tache("T003", "Corriger bug de fiabilité (SonarQube)", 
                             TypeTache.CORRECTIVE);
        t3.dureeHeures = 2.0;
        t3.rang = RangImportance.IMPORTANT;
        t3.priorite = Priorite.P1_HAUTE;
        t3.impact = new Impact(30, 35, 10);
        taches.add(t3);
        
        // TÂCHE 4: Documentation
        Tache t4 = new Tache("T004", "Ajouter documentation Javadoc", 
                             TypeTache.PERFECTIVE);
        t4.dureeHeures = 6.0;
        t4.rang = RangImportance.MODERE;
        t4.priorite = Priorite.P2_NORMALE;
        t4.impact = new Impact(10, 10, 30);
        taches.add(t4);
        
        // TÂCHE 5: Refactoring méthodes longues
        Tache t5 = new Tache("T005", "Découper méthodes longues", 
                             TypeTache.PERFECTIVE);
        t5.dureeHeures = 5.0;
        t5.rang = RangImportance.MODERE;
        t5.priorite = Priorite.P3_BASSE;
        t5.impact = new Impact(25, 10, 5);
        taches.add(t5);
        
        // TÂCHE 6: Code smells
        Tache t6 = new Tache("T006", "Traiter 56 code smells", 
                             TypeTache.PERFECTIVE);
        t6.dureeHeures = 10.0;
        t6.rang = RangImportance.MODERE;
        t6.priorite = Priorite.P3_BASSE;
        t6.impact = new Impact(30, 5, 10);
        taches.add(t6);
        
        // TÂCHE 7: CI/CD
        Tache t7 = new Tache("T007", "Configurer pipeline CI/CD avec JaCoCo", 
                             TypeTache.ADAPTIVE);
        t7.dureeHeures = 4.0;
        t7.rang = RangImportance.MODERE;
        t7.priorite = Priorite.P2_NORMALE;
        t7.impact = new Impact(20, 15, 25);
        taches.add(t7);
        
        return taches;
    }
    
    @Override
    public String getProjectName() {
        return "Jeu de Mines";
    }
    
    @Override
    public ProjectInfo getProjectInfo() {
        ProjectInfo info = new ProjectInfo("Jeu de Mines", "mines");
        info.setVersion("1.0-SNAPSHOT");
        info.setLinesOfCode(299);
        info.setTechnicalDebt("1j 5h");
        info.setTestCoverage(0.0);
        info.setNumberOfClasses(4);
        return info;
    }
}