package fr.polytech.mnia.tictactoe;

import fr.polytech.mnia.Evironnement;
import de.prob.statespace.State;
import de.prob.statespace.Transition;

import java.util.List;
import java.util.Random;

/**
 * TicTacToeEpisodeManager.java
 *
 * Classe utilitaire pour gérer un épisode complet de TicTacToe.
 * 
 * Caractéristiques :
 * - Le joueur 0 est contrôlé par l'agent
 * - Le joueur 1 joue de manière aléatoire
 * 
 * Utilisé pour simuler et observer le déroulement de parties de TicTacToe.
 * 
 * Exemple d'utilisation :
 * TicTacToeEpisodeManager manager = new TicTacToeEpisodeManager(env);
 * manager.playEpisode(true, true);
 */
public class TicTacToeEpisodeManager {

    private final Evironnement env;
    private final Random random = new Random();

    /**
     * Construit un gestionnaire d'épisodes de TicTacToe pour un environnement
     * donné.
     *
     * @param env environnement TicTacToe
     *
     *            Exemple :
     *            TicTacToeEpisodeManager manager = new
     *            TicTacToeEpisodeManager(env);
     */
    public TicTacToeEpisodeManager(Evironnement env) {
        this.env = env;
    }

    /**
     * Lance un épisode complet de TicTacToe.
     *
     * @param agentIsPlaying true si l'agent doit jouer (comme joueur 0), false pour
     *                       observer seulement
     * @param verbose        true pour affichage détaillé après chaque coup, false
     *                       sinon
     * @throws Exception en cas d'erreur pendant l'exécution
     *
     *                   Exemple :
     *                   manager.playEpisode(true, true);
     */
    public void playEpisode(boolean agentIsPlaying, boolean verbose) throws Exception {
        State state = env.getInitialState();

        String win0 = state.eval("win(0)").toString();
        String win1 = state.eval("win(1)").toString();

        while (win0.equals("FALSE") && win1.equals("FALSE") && !env.getActions().isEmpty()) {
            String turn = state.eval("turn").toString().trim();

            if (turn.equals("0")) {
                if (agentIsPlaying) {
                    List<Transition> actions = env.getActions();
                    Transition chosen = chooseBestAction(actions); // Par défaut : aléatoire
                    env.runAction(chosen);

                    if (verbose) {
                        System.out.println("Agent joue : " + chosen.getName() + " " + chosen.getParameterPredicate());
                        prettyPrintGrid();
                    }
                }
            } else {
                List<Transition> actions = env.getActions();
                Transition randomMove = actions.get(random.nextInt(actions.size()));
                env.runAction(randomMove);

                if (verbose) {
                    System.out.println("Joueur 1 joue (aléatoire) : " + randomMove.getName() + " "
                            + randomMove.getParameterPredicate());
                    prettyPrintGrid();
                }
            }

            // Mise à jour des états
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
     * Choisit la meilleure action à réaliser pour le joueur 0.
     * (Actuellement, le choix est fait aléatoirement parmi les actions
     * disponibles.)
     *
     * @param actions liste des actions possibles
     * @return action choisie
     *
     *         Exemple :
     *         Transition choix = manager.chooseBestAction(actionsDisponibles);
     */
    private Transition chooseBestAction(List<Transition> actions) {
        return actions.get(random.nextInt(actions.size()));
    }

    /**
     * Affiche la grille actuelle de TicTacToe dans la console.
     *
     * Exemple :
     * manager.prettyPrintGrid();
     */
    public void prettyPrintGrid() {
        String input = env.getState().eval("square").toString();
        String[][] board = { { " ", " ", " " }, { " ", " ", " " }, { " ", " ", " " } };

        input = input.replaceAll("[^0-9↦,]", "");
        String[] entries = input.split(",");

        for (String entry : entries) {
            if (entry.isEmpty())
                continue;
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
