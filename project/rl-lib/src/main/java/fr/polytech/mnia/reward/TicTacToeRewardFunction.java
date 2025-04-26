package fr.polytech.mnia.reward;

import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.statespace.State;

/**
 * Fonction de récompense pour l'environnement TicTacToe.
 */
public class TicTacToeRewardFunction implements RewardFunction {

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

    @Override
    public boolean isTerminal(State state) {
        String win1 = state.eval("win(1)").toString();
        String win0 = state.eval("win(0)").toString();
        int numSquares = getCardinality(state);

        return win0.equals("TRUE") || win1.equals("TRUE") || numSquares == 9;
    }

    /**
     * Retourne le nombre de cases jouées (cardinal de dom(square)).
     * 
     * @param state L'état à évaluer
     * @return Nombre de cases utilisées
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
