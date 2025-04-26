package fr.polytech.mnia.analysis;

import fr.polytech.mnia.agent.Agent;
import fr.polytech.mnia.graph.GraphGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe utilitaire pour analyser les performances d'un agent :
 * - Récompense cumulée
 * - Récompense moyenne
 * - Distribution des actions choisies
 * - Evolution de la récompense au cours du temps
 * - Génération automatique de graphes
 */
public class AgentAnalyzer {

    /**
     * Analyse les résultats d'un agent après entraînement.
     *
     * @param agent L'agent à analyser
     */
    public static void analyze(Agent agent) {
        List<Double> rewards = agent.getRewards();
        List<String> actions = agent.getActionsChosen();

        if (rewards.isEmpty()) {
            System.out.println("\n[Analyse] Aucun reward enregistré. Pas d'analyse possible.");
            return;
        }

        // === 1. Calcul des statistiques de base ===
        double totalReward = rewards.stream().mapToDouble(Double::doubleValue).sum();
        double averageReward = totalReward / rewards.size();

        System.out.println("\n==============================================");
        System.out.println("=== Analyse de l'agent : " + agent.getClass().getSimpleName() + " ===");
        System.out.println("==============================================\n");

        System.out.println("Nombre total d'étapes réalisées : " + rewards.size());
        System.out.printf("Récompense cumulée : %.2f\n", totalReward);
        System.out.printf("Récompense moyenne : %.4f\n", averageReward);

        // === 2. Analyse de la distribution des actions ===
        Map<String, Integer> actionCounts = new HashMap<>();
        for (String action : actions) {
            actionCounts.put(action, actionCounts.getOrDefault(action, 0) + 1);
        }

        System.out.println("\n--- Distribution des actions choisies ---");
        System.out.printf("%-30s %-10s %-10s\n", "Action", "Nombre", "Pourcentage");
        System.out.println("------------------------------------------------------------");
        for (Map.Entry<String, Integer> entry : actionCounts.entrySet()) {
            double pourcentage = 100.0 * entry.getValue() / actions.size();
            System.out.printf("%-30s %-10d %.2f%%\n", entry.getKey(), entry.getValue(), pourcentage);
        }

        // === 3. Evolution de la récompense cumulée/moyenne ===
        System.out.println("\n--- Evolution de la moyenne des récompenses ---");

        int stepsToShow = 10; // Nombre de points d'échantillonnage
        int stepSize = Math.max(1, rewards.size() / stepsToShow);

        double runningTotal = 0.0;
        for (int i = 0; i < rewards.size(); i += stepSize) {
            runningTotal += rewards.get(i);
            double moyenne = runningTotal / (i + 1);
            System.out.printf("Step %-5d | Moyenne cumulative reward: %.4f\n", i, moyenne);
        }

        // === 4. Génération automatique des graphes ===
        try {
            String agentName = agent.getClass().getSimpleName();
            GraphGenerator.createLineChartRewards(rewards, agentName);
            GraphGenerator.createHistogramActions(actions, agentName);
        } catch (Exception e) {
            System.out.println("[Graph] Erreur lors de la génération des graphes : " + e.getMessage());
        }
    }
}
