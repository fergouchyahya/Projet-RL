package fr.polytech.mnia.agent;

import fr.polytech.mnia.Evironnement;
import java.util.List;

/**
 * Agent.java
 *
 * Cette interface définit le comportement de base pour tout agent de
 * renforcement.
 * 
 * Chaque agent doit être capable de :
 * - s'entraîner dans un environnement donné
 * - récupérer la liste des récompenses accumulées pendant l'entraînement
 * - récupérer la liste des actions choisies pendant l'entraînement
 * 
 * Exemple d'utilisation :
 * public class MonAgent implements Agent {
 * // Implémentations des méthodes ici
 * }
 */
public interface Agent {

    /**
     * Entraîne l'agent sur un environnement donné.
     *
     * @param env     l'environnement dans lequel l'agent évolue (par exemple, un
     *                jeu ou un problème d'optimisation)
     * @param nbSteps le nombre d'étapes (actions) pendant lesquelles l'agent doit
     *                s'entraîner
     * @param verbose si true, affiche les détails de l'entraînement à chaque étape
     *                ; si false, mode silencieux
     * @throws Exception en cas d'erreur lors de l'exécution (par exemple si une
     *                   action est invalide)
     *
     *                   Exemple d'utilisation :
     *                   agent.train(monEnvironnement, 1000, true);
     */
    void train(Evironnement env, int nbSteps, boolean verbose) throws Exception;

    /**
     * Retourne la liste des récompenses obtenues par l'agent pendant
     * l'entraînement.
     *
     * @return une liste de récompenses (chaque élément est un Double représentant
     *         la récompense d'une étape)
     *
     *         Exemple d'utilisation :
     *         List<Double> recompenses = agent.getRewards();
     */
    List<Double> getRewards();

    /**
     * Retourne la liste des actions choisies par l'agent pendant l'entraînement.
     *
     * @return une liste de chaînes de caractères représentant les actions choisies
     *
     *         Exemple d'utilisation :
     *         List<String> actions = agent.getActionsChosen();
     */
    List<String> getActionsChosen();
}
