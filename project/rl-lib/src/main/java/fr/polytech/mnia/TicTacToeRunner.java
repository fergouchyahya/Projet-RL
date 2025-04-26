package fr.polytech.mnia;

/**
 * Runner pour la machine B TicTacToe.
 * Permet d'animer une partie en exécutant des coups aléatoires,
 * tout en affichant l'état de la grille.
 */
public class TicTacToeRunner extends Runner {

    /**
     * Constructeur : charge et initialise la machine B tictac.mch.
     */
    public TicTacToeRunner() {
        super("/TicTacToe/tictac.mch");
        this.initialise();
    }

    /**
     * Exécute une séquence de coups aléatoires
     * jusqu'à ce qu'un joueur gagne ou que la grille soit pleine.
     * Affiche la grille et les états de victoire après chaque coup.
     */
    public void execSequence() throws Exception {
        String win1 = state.eval("win(1)").toString();
        String win0 = state.eval("win(0)").toString();

        while (win1.equals("FALSE") && win0.equals("FALSE") && !state.getOutTransitions().isEmpty()) {
            state = state.anyOperation(null).explore();
            win1 = state.eval("win(1)").toString();
            win0 = state.eval("win(0)").toString();
            this.prettyPrintTicTacToe();

            System.out.println("\nRésultats:");
            System.out.println("win(1) = " + win1);
            System.out.println("win(0) = " + win0 + "\n");
        }
    }

    /**
     * Affiche la grille de jeu au format humainement lisible.
     */
    private void prettyPrintTicTacToe() {
        String input = state.eval("square").toString();

        // Grille 3x3 remplie avec espaces par défaut
        String[][] board = { { " ", " ", " " }, { " ", " ", " " }, { " ", " ", " " } };

        // Extraction des valeurs : garde uniquement chiffres et séparateurs
        input = input.replaceAll("[^0-9↦,]", "");
        String[] entries = input.split(",");

        for (String entry : entries) {
            String[] parts = entry.split("↦");
            int row = Integer.parseInt(parts[0]) - 1; // Convertir en index (0-2)
            int col = Integer.parseInt(parts[1]) - 1;
            String value = parts[2]; // Valeur (0 ou 1)
            board[row][col] = value;
        }

        // Affichage de la grille
        for (int i = 0; i < 3; i++) {
            System.out.println(" " + board[i][0] + " | " + board[i][1] + " | " + board[i][2]);
            if (i < 2)
                System.out.println("---+---+---");
        }
    }
}
