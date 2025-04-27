package fr.polytech.mnia.reward;

import de.prob.statespace.State;

/**
 * SimpleRewardFunction.java
 *
 * Fonction de récompense spécifique pour l'environnement SimpleRL.
 * 
 * Caractéristiques :
 * - Retourne 1.0 si l'utilisateur fait un bon choix (OK), sinon 0.0.
 * - Chaque action aboutit directement à un état terminal.
 *
 * Exemple d'utilisation :
 * RewardFunction rewardFunction = new SimpleRewardFunction();
 * double r = rewardFunction.getReward(state);
 * boolean terminal = rewardFunction.isTerminal(state);
 */
public class SimpleRewardFunction implements RewardFunction {

    /**
     * Calcule la récompense associée à l'état donné.
     *
     * @param state l'état à évaluer
     * @return 1.0 si l'attribut "res" vaut "OK", sinon 0.0
     *
     *         Exemple :
     *         double reward = rewardFunction.getReward(state);
     */
    @Override
    public double getReward(State state) {
        String res = state.eval("res").toString();
        if (res.equals("OK")) {
            return 1.0; // Bon choix : récompense 1
        } else {
            return 0.0; // Mauvais choix : récompense 0
        }
    }

    /**
     * Détermine si l'état est un état terminal.
     *
     * @param state l'état à vérifier
     * @return toujours true dans SimpleRL (chaque action aboutit à un état final)
     *
     *         Exemple :
     *         boolean isTerminal = rewardFunction.isTerminal(state);
     */
    @Override
    public boolean isTerminal(State state) {
        return true;
    }
}
