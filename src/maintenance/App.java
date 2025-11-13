package maintenance;

import static maintenance.MaintenanceScheduler.*;

/**
 * Application principale pour l'analyse de maintenance du Jeu de Mines
 */
public class App {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   ANALYSE DE MAINTENANCE - JEU DE MINES              ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
        
        try {
            // 1. Créer l'analyseur
            ProjectAnalyzer analyzer = new MinesweeperAnalyzer();
            ProjectInfo projectInfo = analyzer.getProjectInfo();
            
            // 2. Afficher infos projet
            afficherInfosProjet(projectInfo);
            
            // 3. Exécuter l'algorithme
            RapportMaintenance rapport = MaintenanceScheduler.executerAlgorithmeAvecAnalyseur(
                analyzer, projectInfo
            );
            
            // 4. Afficher rapport
            System.out.println(rapport);
            
            // 5. Recommandations
            afficherRecommandations();
            
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void afficherInfosProjet(ProjectInfo info) {
        System.out.println("📊 INFORMATIONS DU PROJET");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Nom              : " + info.getName());
        System.out.println("Lignes de code   : " + info.getLinesOfCode());
        System.out.println("Dette technique  : " + info.getTechnicalDebt());
        System.out.println("Couverture tests : " + info.getTestCoverage() + "%");
        System.out.println("Classes          : " + info.getNumberOfClasses());
        System.out.println();
    }
    
    private static void afficherRecommandations() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║   RECOMMANDATIONS PRIORITAIRES                        ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
        
        System.out.println("🎯 SEMAINE 1:");
        System.out.println("   → Ajouter tests unitaires (80% couverture)");
        System.out.println("   → Corriger bug de fiabilité");
        System.out.println();
        
        System.out.println("🔧 SEMAINE 2:");
        System.out.println("   → Refactoring find_empty_cells()");
        System.out.println("   → Ajouter documentation Javadoc");
        System.out.println();
        
        System.out.println("📈 MÉTRIQUES CIBLES:");
        System.out.println("   • Couverture     : 0% → 80%");
        System.out.println("   • Complexité     : 27 → 15");
        System.out.println("   • Dette technique: 1j 5h → < 4h");
    }
}