package fr.polytech.mnia;

import fr.polytech.mnia.agent.*;
import fr.polytech.mnia.reward.*;
import fr.polytech.mnia.analysis.AgentAnalyzer;
import fr.polytech.mnia.graph.GraphGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * App.java
 *
 * Programme principal pour tester et analyser des agents d'apprentissage par
 * renforcement
 * sur différents environnements (SimpleRL, YouTube, TicTacToe).
 * 
 * Fonctionnalités :
 * - Sélectionner un environnement
 * - Choisir un ou plusieurs agents
 * - Définir les paramètres d'entraînement
 * - Analyser les résultats et générer des graphes
 *
 * Exemple d'utilisation :
 * java App
 */
public class App {

    /**
     * Point d'entrée principal de l'application.
     *
     * @param args arguments en ligne de commande (non utilisés ici)
     * @throws Exception en cas d'erreur durant l'exécution
     */
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n==============================================");
        System.out.println("         Bienvenue dans l'application RL     ");
        System.out.println("==============================================\n");

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
            rewardVariable = "square";
        }

        env = new Evironnement(runner, rewardFunction, rewardVariable);

        // === 2. Choisir les agents disponibles selon l'environnement ===
        System.out.println("\n=== Choisissez les agents (ex: 1 3) ===");

        if (envChoice == 3) {
            System.out.println("[4] Value Iteration");
            System.out.println("[5] Policy Iteration");
            System.out.println("[6] Q-Learning");
        } else {
            System.out.println("[1] Epsilon Greedy");
            System.out.println("[2] UCB");
            System.out.println("[3] Bandit Gradient");
        }

        String line = scanner.nextLine();
        String[] choices = line.split("\\s+");
        List<Agent> agents = new ArrayList<>();

        // Ajout des agents sélectionnés
        for (String choice : choices) {
            switch (choice) {
                case "1":
                    if (envChoice != 3) {
                        System.out.print("Epsilon pour Epsilon Greedy : ");
                        double epsilon = Double.parseDouble(scanner.nextLine().replace(",", "."));
                        agents.add(new EpsilonGreedyAgent(epsilon));
                    }
                    break;
                case "2":
                    if (envChoice != 3) {
                        agents.add(new UCBAgent());
                    }
                    break;
                case "3":
                    if (envChoice != 3) {
                        System.out.print("Alpha pour Bandit Gradient : ");
                        double alpha = Double.parseDouble(scanner.nextLine().replace(",", "."));
                        agents.add(new BanditGradientAgent(alpha));
                    }
                    break;
                case "4":
                    if (envChoice == 3) {
                        System.out.print("Gamma pour Value Iteration : ");
                        double gammaV = Double.parseDouble(scanner.nextLine().replace(",", "."));
                        System.out.print("Theta pour Value Iteration : ");
                        double thetaV = Double.parseDouble(scanner.nextLine().replace(",", "."));
                        agents.add(new ValueIterationAgent(gammaV, thetaV));
                    }
                    break;
                case "5":
                    if (envChoice == 3) {
                        System.out.print("Gamma pour Policy Iteration : ");
                        double gammaP = Double.parseDouble(scanner.nextLine().replace(",", "."));
                        agents.add(new PolicyIterationAgent(gammaP));
                    }
                    break;
                case "6":
                    if (envChoice == 3) {
                        System.out.print("Alpha pour Q-Learning : ");
                        double alphaQ = Double.parseDouble(scanner.nextLine().replace(",", "."));
                        System.out.print("Gamma pour Q-Learning : ");
                        double gammaQ = Double.parseDouble(scanner.nextLine().replace(",", "."));
                        System.out.print("Epsilon pour Q-Learning : ");
                        double epsilonQ = Double.parseDouble(scanner.nextLine().replace(",", "."));
                        agents.add(new QLearningTicTacToeAgent(alphaQ, gammaQ, epsilonQ));
                    }
                    break;
                default:
                    System.out.println("Choix non reconnu : " + choice);
            }
        }

        // === 3. Mode d'affichage pendant l'entraînement ===
        System.out.println("\n=== Mode d'affichage pendant l'entraînement ===");
        System.out.println("[1] Affichage détaillé");
        System.out.println("[2] Mode silencieux");

        int affichageMode = scanner.nextInt();
        scanner.nextLine();
        boolean verbose = (affichageMode == 1);

        // === 4. Paramètres d'entraînement ===
        System.out.println("\n=== Paramètres de l'entraînement ===");

        int nbSteps = 0;
        if (envChoice == 3) {
            System.out.print("Entrez le nombre d'itérations max pour TicTacToe : ");
            nbSteps = scanner.nextInt();
            scanner.nextLine();
        } else {
            System.out.print("Entrez le nombre d'étapes d'entraînement : ");
            nbSteps = scanner.nextInt();
            scanner.nextLine();
        }

        // Nettoyage du dossier de graphes
        GraphGenerator.clearGraphsFolder();

        // === 5. Entraîner et analyser chaque agent ===
        int agentNumber = 1;
        for (Agent agent : agents) {
            System.out.println("\n==============================================");
            System.out.println(
                    "=== Entraînement de l'agent " + agentNumber + " : " + agent.getClass().getSimpleName() + " ===");
            System.out.println("==============================================\n");

            agent.train(env, nbSteps, verbose);

            System.out.println("\n=== Analyse de l'agent " + agentNumber + " ===\n");
            AgentAnalyzer.analyze(agent);

            if (verbose && agent instanceof QLearningTicTacToeAgent) {
                System.out.println("\n--- Chemin optimal appris par l'agent ---");
                List<String> optimalPath = ((QLearningTicTacToeAgent) agent).getOptimalPath(env.getInitialState(), env);
                for (String move : optimalPath) {
                    System.out.println("- " + move);
                }
            }

            agentNumber++;
        }

        System.out.println("\n=== Fin de l'expérience ===");
        scanner.close();
        System.exit(0);
    }
}
