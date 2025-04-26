package fr.polytech.mnia.reward;

import de.prob.statespace.State;

/**
 * Interface générique pour définir une fonction de récompense
 * pour un environnement donné.
 */
public interface RewardFunction {
    /**
     * Évalue et retourne la récompense associée à un état donné.
     * 
     * @param state État à évaluer
     * @return Récompense sous forme d'un double
     */
    double getReward(State state);

    /**
     * Indique si un état est terminal (fin d'épisode).
     * 
     * @param state État à tester
     * @return true si terminal, false sinon
     */
    boolean isTerminal(State state);
}
