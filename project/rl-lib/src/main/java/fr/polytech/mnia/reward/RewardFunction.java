package fr.polytech.mnia.reward;

import de.prob.statespace.State;

/**
 * RewardFunction.java
 *
 * Interface générique pour définir une fonction de récompense dans un
 * environnement donné.
 * 
 * Cette interface permet de :
 * - calculer la récompense d'un état
 * - vérifier si un état est terminal (fin d'un épisode)
 *
 * Exemple d'utilisation :
 * public class MyRewardFunction implements RewardFunction {
 * public double getReward(State state) { ... }
 * public boolean isTerminal(State state) { ... }
 * }
 */
public interface RewardFunction {

    /**
     * Évalue et retourne la récompense associée à un état donné.
     *
     * @param state l'état pour lequel la récompense doit être évaluée
     * @return récompense associée à l'état, sous forme d'un double
     *
     *         Exemple :
     *         double reward = rewardFunction.getReward(currentState);
     */
    double getReward(State state);

    /**
     * Indique si l'état donné est un état terminal (c'est-à-dire la fin d'un
     * épisode).
     *
     * @param state l'état à vérifier
     * @return true si l'état est terminal, false sinon
     *
     *         Exemple :
     *         if (rewardFunction.isTerminal(currentState)) { ... }
     */
    boolean isTerminal(State state);
}
