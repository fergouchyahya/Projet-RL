package fr.polytech.mnia.tictactoe;

import fr.polytech.mnia.Evironnement;
import de.prob.statespace.State;
import de.prob.statespace.Transition;

import java.util.List;
import java.util.Random;

/**
 * Classe utilitaire pour gérer un épisode complet de TicTacToe.
 * - Le joueur 0 est contrôlé par l'agent
 * - Le joueur 1 joue aléatoirement
 */
public class TicTacToeEpisodeManager {

    private final Evironnement env;
    private final Random random = new Random();

    public TicTacToeEpisodeManager(Evironnement env) {
        this.env = env;
    }

    /**
     * Lance un épisode complet.
     * 
     * @param agentIsPlaying Si true ➔ l'agent joue comme joueur 0, sinon il observe
     *                       seulement
     * @param verbose        Si true ➔ affichage détaillé
     * @throws Exception
     */
    public void playEpisode(boolean agentIsPlaying, boolean verbose) throws Exception {
        State state = env.getInitialState();

        String win0 = state.eval("win(0)").toString();
        String win1 = state.eval("win(1)").toString();

        while (win0.equals("FALSE") && win1.equals("FALSE") && !env.getActions().isEmpty()) {
            String turn = state.eval("turn").toString().trim();

            if (turn.equals("0")) {
                if (agentIsPlaying) {
                    // C'est à l'agent de jouer
                    List<Transition> actions = env.getActions();
                    Transition chosen = chooseBestAction(actions); // à implémenter selon agent
                    env.runAction(chosen);

                    if (verbose) {
                        System.out.println("Agent joue : " + chosen.getName() + " " + chosen.getParameterPredicate());
                        prettyPrintGrid();
                    }
                }
            } else {
                // C'est au joueur 1 de jouer (aléatoire)
                List<Transition> actions = env.getActions();
                Transition randomMove = actions.get(random.nextInt(actions.size()));
                env.runAction(randomMove);

                if (verbose) {
                    System.out.println("Joueur 1 joue (aléatoire) : " + randomMove.getName() + " "
                            + randomMove.getParameterPredicate());
                    prettyPrintGrid();
                }
            }

            // Mise à jour
            state = env.getState();
            win0 = state.eval("win(0)").toString();
            win1 = state.eval("win(1)").toString();
        }

        if (verbose) {
            System.out.println("\nFin de la partie : ");
            System.out.println("win(0) = " + win0);
            System.out.println("win(1) = " + win1);
            prettyPrintGrid();
        }
    }

    /**
     * Méthode de choix de la meilleure action pour l'agent.
     * Pour l'instant : au hasard (à améliorer pour Q-learning ou Policy).
     */
    private Transition chooseBestAction(List<Transition> actions) {
        return actions.get(random.nextInt(actions.size()));
    }

    /**
     * Affiche la grille actuelle de TicTacToe.
     */
    public void prettyPrintGrid() {
        String input = env.getState().eval("square").toString();
        String[][] board = { { " ", " ", " " }, { " ", " ", " " }, { " ", " ", " " } };

        input = input.replaceAll("[^0-9↦,]", "");
        String[] entries = input.split(",");

        for (String entry : entries) {
            String[] parts = entry.split("↦");
            int row = Integer.parseInt(parts[0]) - 1;
            int col = Integer.parseInt(parts[1]) - 1;
            String value = parts[2];
            board[row][col] = value;
        }

        for (int i = 0; i < 3; i++) {
            System.out.println(" " + board[i][0] + " | " + board[i][1] + " | " + board[i][2]);
            if (i < 2)
                System.out.println("---+---+---");
        }
    }
}
