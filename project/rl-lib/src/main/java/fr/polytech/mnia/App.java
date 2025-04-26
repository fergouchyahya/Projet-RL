package fr.polytech.mnia;

import fr.polytech.mnia.agent.*;
import fr.polytech.mnia.reward.*;
import fr.polytech.mnia.analysis.AgentAnalyzer;
import fr.polytech.mnia.graph.GraphGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Classe principale du projet.
 * Permet à l'utilisateur de :
 * - Choisir l'environnement (SimpleRL, YouTube, TicTacToe)
 * - Choisir les agents de renforcement à entraîner
 * - Définir les paramètres d'entraînement (nombre d'étapes, affichage)
 * - Lancer l'entraînement, l'analyse, et la génération automatique des graphes
 */
public class App {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        // === 1. Choix de l'environnement ===
        System.out.println("=== Sélectionnez l'environnement ===");
        System.out.println("[1] SimpleRL");
        System.out.println("[2] YouTube");
        System.out.println("[3] TicTacToe");

        int envChoice = scanner.nextInt();
        scanner.nextLine(); // vider le buffer après nextInt

        Runner runner;
        Evironnement env;
        String rewardVariable;
        RewardFunction rewardFunction;

        // Initialisation en fonction du choix utilisateur
        if (envChoice == 1) {
            runner = new SimpleRunner();
            rewardFunction = new SimpleRewardFunction();
            rewardVariable = "res"; // Variable de récompense dans SimpleRL
        } else if (envChoice == 2) {
            runner = new YouTubeRunner();
            rewardFunction = new YouTubeRewardFunction();
            rewardVariable = "step"; // Pas de duration directement accessible
        } else {
            runner = new TicTacToeRunner();
            rewardFunction = new TicTacToeRewardFunction();
            rewardVariable = "some_variable"; // À adapter selon TicTacToe.mch
        }

        // Création de l'environnement
        env = new Evironnement(runner, rewardFunction, rewardVariable);

        // === 2. Choix des agents à entraîner ===
        System.out.println("\n=== Sélectionnez les agents (ex: 1 2 3) ===");
        System.out.println("[1] Epsilon Greedy");
        System.out.println("[2] UCB");
        System.out.println("[3] Bandit Gradient");
        System.out.println("[4] Value Iteration");
        System.out.println("[5] Policy Iteration");
        System.out.println("[6] Q-Learning");

        String line = scanner.nextLine();
        String[] choices = line.split("\\s+");
        List<Agent> agents = new ArrayList<>();

        // Configuration de chaque agent sélectionné
        for (String choice : choices) {
            switch (choice) {
                case "1":
                    System.out.print("Entrez epsilon pour Epsilon Greedy : ");
                    double epsilon = Double.parseDouble(scanner.nextLine().replace(",", "."));
                    agents.add(new EpsilonGreedyAgent(epsilon));
                    break;
                case "2":
                    agents.add(new UCBAgent());
                    break;
                case "3":
                    System.out.print("Entrez alpha pour Bandit Gradient : ");
                    double alpha = Double.parseDouble(scanner.nextLine().replace(",", "."));
                    agents.add(new BanditGradientAgent(alpha));
                    break;
                case "4":
                    System.out.print("Entrez gamma pour Value Iteration : ");
                    double gammaV = Double.parseDouble(scanner.nextLine().replace(",", "."));
                    System.out.print("Entrez theta pour Value Iteration : ");
                    double thetaV = Double.parseDouble(scanner.nextLine().replace(",", "."));
                    agents.add(new ValueIterationAgent(gammaV, thetaV));
                    break;
                case "5":
                    System.out.print("Entrez gamma pour Policy Iteration : ");
                    double gammaP = Double.parseDouble(scanner.nextLine().replace(",", "."));
                    agents.add(new PolicyIterationAgent(gammaP));
                    break;
                case "6":
                    System.out.print("Entrez alpha pour Q-Learning : ");
                    double alphaQ = Double.parseDouble(scanner.nextLine().replace(",", "."));
                    System.out.print("Entrez gamma pour Q-Learning : ");
                    double gammaQ = Double.parseDouble(scanner.nextLine().replace(",", "."));
                    System.out.print("Entrez epsilon pour Q-Learning : ");
                    double epsilonQ = Double.parseDouble(scanner.nextLine().replace(",", "."));
                    agents.add(new QLearningAgent(alphaQ, gammaQ, epsilonQ));
                    break;
                default:
                    System.out.println("Choix d'agent non reconnu : " + choice);
            }
        }

        // === 3. Paramètres d'entraînement ===
        System.out.print("\nEntrez le nombre d'étapes d'entraînement : ");
        int nbSteps = scanner.nextInt();
        scanner.nextLine(); // vider le buffer

        System.out.println("\n=== Mode d'affichage ===");
        System.out.println("[1] Affichage détaillé (chaque action)");
        System.out.println("[2] Mode silencieux (résultats uniquement)");

        int affichageMode = scanner.nextInt();
        scanner.nextLine();
        boolean verbose = (affichageMode == 1);

        // === 4. Entraînement et analyse des agents ===
        int agentNumber = 1;
        for (Agent agent : agents) {
            System.out.println("\n=== Entraînement de l'agent " + agentNumber + " ===\n");
            agent.train(env, nbSteps, verbose);

            System.out.println("\n=== Analyse de l'agent " + agentNumber + " ===\n");
            AgentAnalyzer.analyze(agent);

            agentNumber++;
        }

        scanner.close();
        System.exit(0);
    }
}
