package maintenance;

import java.util.List;

/**
 * Interface pour l'analyse de projets.
 * Chaque projet doit implémenter cette interface pour être analysé.
 */
public interface ProjectAnalyzer {
    /**
     * Analyse le projet et retourne la liste des tâches de maintenance identifiées
     */
    List<MaintenanceScheduler.Tache> analyzeProject(String projectPath);
    
    /**
     * Retourne le nom du projet
     */
    String getProjectName();
    
    /**
     * Retourne des informations sur le projet
     */
    ProjectInfo getProjectInfo();
}