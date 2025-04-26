package fr.polytech.mnia;

import fr.polytech.mnia.agent.*;
import fr.polytech.mnia.reward.*;
import fr.polytech.mnia.analysis.AgentAnalyzer;
import fr.polytech.mnia.graph.GraphGenerator;
import fr.polytech.mnia.tictactoe.TicTacToeEpisodeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Classe principale pour lancer les expériences RL
 * sur SimpleRL, YouTube et TicTacToe.
 */
public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        // === 1. Sélectionner l'environnement ===
        System.out.println("=== Choisissez l'environnement ===");
        System.out.println("[1] SimpleRL");
        System.out.println("[2] YouTube");
        System.out.println("[3] TicTacToe");

        int envChoice = scanner.nextInt();
        scanner.nextLine();

        Runner runner;
        Evironnement env;
        String rewardVariable;
        RewardFunction rewardFunction;

        if (envChoice == 1) {
            runner = new SimpleRunner();
            rewardFunction = new SimpleRewardFunction();
            rewardVariable = "res";
        } else if (envChoice == 2) {
            runner = new YouTubeRunner();
            rewardFunction = new YouTubeRewardFunction();
            rewardVariable = "step";
        } else {
            runner = new TicTacToeRunner();
            rewardFunction = new TicTacToeRewardFunction();
            rewardVariable = "square"; // Pas utilisé directement, placeholder
        }

        env = new Evironnement(runner, rewardFunction, rewardVariable);

        // === 2. Choisir les agents ===
        System.out.println("\n=== Choisissez les agents (ex: 1 4 6) ===");
        System.out.println("[1] Epsilon Greedy");
        System.out.println("[2] UCB");
        System.out.println("[3] Bandit Gradient");
        System.out.println("[4] Value Iteration");
        System.out.println("[5] Policy Iteration");
        System.out.println("[6] Q-Learning");

        String line = scanner.nextLine();
        String[] choices = line.split("\\s+");
        List<Agent> agents = new ArrayList<>();

        for (String choice : choices) {
            switch (choice) {
                case "1":
                    System.out.print("Epsilon pour Epsilon Greedy : ");
                    double epsilon = Double.parseDouble(scanner.nextLine().replace(",", "."));
                    agents.add(new EpsilonGreedyAgent(epsilon));
                    break;
                case "2":
                    agents.add(new UCBAgent());
                    break;
                case "3":
                    System.out.print("Alpha pour Bandit Gradient : ");
                    double alpha = Double.parseDouble(scanner.nextLine().replace(",", "."));
                    agents.add(new BanditGradientAgent(alpha));
                    break;
                case "4":
                    System.out.print("Gamma pour Value Iteration : ");
                    double gammaV = Double.parseDouble(scanner.nextLine().replace(",", "."));
                    System.out.print("Theta pour Value Iteration : ");
                    double thetaV = Double.parseDouble(scanner.nextLine().replace(",", "."));
                    agents.add(new ValueIterationAgent(gammaV, thetaV));
                    break;
                case "5":
                    System.out.print("Gamma pour Policy Iteration : ");
                    double gammaP = Double.parseDouble(scanner.nextLine().replace(",", "."));
                    agents.add(new PolicyIterationAgent(gammaP));
                    break;
                case "6":
                    System.out.print("Alpha pour Q-Learning : ");
                    double alphaQ = Double.parseDouble(scanner.nextLine().replace(",", "."));
                    System.out.print("Gamma pour Q-Learning : ");
                    double gammaQ = Double.parseDouble(scanner.nextLine().replace(",", "."));
                    System.out.print("Epsilon pour Q-Learning : ");
                    double epsilonQ = Double.parseDouble(scanner.nextLine().replace(",", "."));
                    agents.add(new QLearningAgent(alphaQ, gammaQ, epsilonQ));
                    break;
                default:
                    System.out.println("Choix non reconnu : " + choice);
            }
        }

        // === 3. Paramètres de l'entraînement ===
        System.out.print("\nEntrez le nombre d'étapes d'entraînement : ");
        int nbSteps = scanner.nextInt();
        scanner.nextLine();

        System.out.println("\n=== Mode d'affichage ===");
        System.out.println("[1] Affichage détaillé");
        System.out.println("[2] Mode silencieux");

        int affichageMode = scanner.nextInt();
        scanner.nextLine();
        boolean verbose = (affichageMode == 1);

        // Nettoyage du dossier de graphes
        GraphGenerator.clearGraphsFolder();

        // === 4. Entraîner et analyser chaque agent ===
        int agentNumber = 1;
        for (Agent agent : agents) {
            System.out.println("\n=== Entraînement de l'agent " + agentNumber + " ===");

            if (envChoice == 3 && agent instanceof QLearningAgent) {
                // Pour TicTacToe et Q-Learning : entraînement par épisodes
                TicTacToeEpisodeManager manager = new TicTacToeEpisodeManager(env);
                for (int i = 0; i < nbSteps; i++) {
                    manager.playEpisode(true, verbose);
                }
            } else {
                agent.train(env, nbSteps, verbose);
            }

            System.out.println("\n=== Analyse de l'agent " + agentNumber + " ===\n");
            AgentAnalyzer.analyze(agent);

            agentNumber++;
        }

        scanner.close();
        System.exit(0);
    }
}
