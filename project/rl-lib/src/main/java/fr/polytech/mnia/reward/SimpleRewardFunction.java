package fr.polytech.mnia.reward;

import de.prob.statespace.State;

/**
 * Fonction de récompense pour l'environnement SimpleRL.
 */
public class SimpleRewardFunction implements RewardFunction {

    @Override
    public double getReward(State state) {
        String res = state.eval("res").toString();
        if (res.equals("OK")) {
            return 1.0; // Bon choix : récompense 1
        } else {
            return 0.0; // Mauvais choix : récompense 0
        }
    }

    @Override
    public boolean isTerminal(State state) {
        // Dans SimpleRL, chaque action mène directement à un résultat : un état
        // terminal
        return true;
    }
}
