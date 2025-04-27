package fr.polytech.mnia.reward;

import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.statespace.State;

/**
 * TicTacToeRewardFunction.java
 *
 * Fonction de récompense spécifique pour l'environnement TicTacToe.
 * 
 * Caractéristiques :
 * - +1 si le joueur 0 gagne
 * - -1 si le joueur 1 gagne
 * - 0 si match nul
 * - -0.25 si la partie est encore en cours
 *
 * Exemple d'utilisation :
 * RewardFunction rewardFunction = new TicTacToeRewardFunction();
 * double r = rewardFunction.getReward(state);
 * boolean terminal = rewardFunction.isTerminal(state);
 */
public class TicTacToeRewardFunction implements RewardFunction {

    /**
     * Calcule la récompense associée à l'état donné.
     *
     * @param state état du jeu à évaluer
     * @return la récompense :
     *         - 1.0 si 0 gagne
     *         - -1.0 si 1 gagne
     *         - 0.0 en cas de match nul
     *         - -0.25 si la partie est encore en cours
     *
     *         Exemple :
     *         double reward = rewardFunction.getReward(state);
     */
    @Override
    public double getReward(State state) {
        String win1 = state.eval("win(1)").toString();
        String win0 = state.eval("win(0)").toString();
        int numSquares = getCardinality(state);

        if (win0.equals("TRUE")) {
            return 1.0; // 0 gagne
        } else if (win1.equals("TRUE")) {
            return -1.0; // 1 gagne
        } else if (numSquares == 9) {
            return 0.0; // Match nul
        } else {
            return -0.25; // Partie en cours
        }
    }

    /**
     * Vérifie si l'état correspond à un état terminal (fin de partie).
     *
     * @param state état à vérifier
     * @return true si victoire d'un joueur ou match nul, false sinon
     *
     *         Exemple :
     *         boolean isTerminal = rewardFunction.isTerminal(state);
     */
    @Override
    public boolean isTerminal(State state) {
        String win1 = state.eval("win(1)").toString();
        String win0 = state.eval("win(0)").toString();
        int numSquares = getCardinality(state);

        return win0.equals("TRUE") || win1.equals("TRUE") || numSquares == 9;
    }

    /**
     * Retourne le nombre de cases jouées dans l'état donné (cardinal de
     * dom(square)).
     *
     * @param state état du jeu
     * @return nombre de cases occupées
     *
     *         Exemple :
     *         int nbCases = rewardFunction.getCardinality(state);
     */
    private int getCardinality(State state) {
        try {
            AbstractEvalResult result = state.eval("card(dom(square))");
            String value = result.toString().trim();
            return Integer.parseInt(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse card(dom(square)) result", e);
        }
    }
}
