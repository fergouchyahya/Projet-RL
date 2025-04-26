package fr.polytech.mnia.agent;

import fr.polytech.mnia.Evironnement;
import java.util.List;

/**
 * Interface Agent pour tout agent de renforcement.
 * 
 * Chaque agent doit pouvoir :
 * - s'entraîner sur un environnement donné
 * - fournir la liste de ses récompenses
 * - fournir la liste de ses actions choisies
 */
public interface Agent {
    /**
     * Entraîne l'agent sur un environnement donné.
     * @param env l'environnement
     * @param nbSteps le nombre d'étapes d'entraînement
     * @param verbose true pour affichage détaillé, false pour mode silencieux
     */
    void train(Evironnement env, int nbSteps, boolean verbose) throws Exception;

    /**
     * Retourne la liste des récompenses obtenues.
     */
    List<Double> getRewards();

    /**
     * Retourne la liste des actions choisies.
     */
    List<String> getActionsChosen();
}
