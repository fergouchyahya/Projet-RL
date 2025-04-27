package fr.polytech.mnia.agent;

import fr.polytech.mnia.Evironnement;
import fr.polytech.mnia.tictactoe.TicTacToeEpisodeManager;
import de.prob.statespace.State;
import de.prob.statespace.Transition;

import java.util.*;

/**
 * QLearningTicTacToeAgent.java
 *
 * Implémentation d'un agent utilisant l'algorithme de Q-Learning spécialisé pour le jeu du Tic-Tac-Toe.
 * 
 * Caractéristiques :
 * - L'agent apprend en jouant le rôle du joueur 0.
 * - Le joueur 1 joue de manière aléatoire.
 * - L'entraînement se fait par nombre d'épisodes complets (matchs).
 * 
 * Exemple d'utilisation :
 *     Agent agent = new QLearningTicTacToeAgent(0.5, 0.9, 0.1);
 *     agent.train(env, 1000, true);
 */
public class QLearningTicTacToeAgent implements Agent {

    private final double alpha;
    private final double gamma;
    private final double epsilon;
    private final Map<State, Map<Transition, Double>> Q = new HashMap<>();
    private final Random random = new Random();
    private final List<Double> rewards = new ArrayList<>();
    private final List<String> actionsChosen = new ArrayList<>();

    /**
     * Construit un agent Q-Learning spécialisé pour Tic-Tac-Toe.
     *
     * @param alpha taux d'apprentissage (0 ≤ alpha ≤ 1)
     * @param gamma facteur d'actualisation des récompenses futures (0 ≤ gamma ≤ 1)
     * @param epsilon probabilité d'exploration aléatoire (0 ≤ epsilon ≤ 1)
     *
     * Exemple :
     *     QLearningTicTacToeAgent agent = new QLearningTicTacToeAgent(0.5, 0.9, 0.1);
     */
    public QLearningTicTacToeAgent(double alpha, double gamma, double epsilon) {
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
    }

    /**
     * Entraîne l'agent dans l'environnement Tic-Tac-Toe pendant un certain nombre d'épisodes.
     *
     * @param env environnement du jeu
     * @param nbEpisodes nombre d'épisodes d'entraînement
     * @param verbose true pour afficher les étapes du jeu, false sinon
     * @throws Exception en cas d'erreur pendant l'exécution
     *
     * Exemple :
     *     agent.train(env, 1000, true);
     */
    @Override
    public void train(Evironnement env, int nbEpisodes, boolean verbose) throws Exception {
        TicTacToeEpisodeManager manager = new TicTacToeEpisodeManager(env);

        for (int episode = 0; episode < nbEpisodes; episode++) {
            env.reset();
            State state = env.getInitialState();

            if (verbose) {
                System.out.println("\n=== Début de l'épisode " + (episode + 1) + " ===");
                manager.prettyPrintGrid();
            }

            while (!env.isTerminal(state)) {
                String turn = state.eval("turn").toString().trim();

                List<Transition> actions = env.getActions(state);
                if (actions.isEmpty())
                    break;

                if (turn.equals("0")) {
                    // Tour de l'agent (joueur 0)
                    Transition chosen = chooseAction(state, actions);

                    env.runAction(chosen);
                    State nextState = env.getState();

                    double reward = env.getReward(nextState);
                    updateQValue(state, chosen, reward, nextState);

                    rewards.add(reward);
                    actionsChosen.add(chosen.getParameterPredicate());

                    if (verbose) {
                        System.out.println("\n[Agent 0 joue] : " + chosen.getName() + " " + chosen.getParameterPredicate());
                        manager.prettyPrintGrid();
                    }

                    state = nextState;
                } else {
                    // Tour du joueur 1 (adversaire aléatoire)
                    Transition randomMove = actions.get(random.nextInt(actions.size()));
                    env.runAction(randomMove);
                    state = env.getState();

                    if (verbose) {
                        System.out.println("\n[Joueur 1 (aléatoire) joue] : " + randomMove.getName() + " " + randomMove.getParameterPredicate());
                        manager.prettyPrintGrid();
                    }
                }
            }

            if (verbose) {
                System.out.println("\n=== Fin de l'épisode " + (episode + 1) + " ===");
                manager.prettyPrintGrid();
            }
        }
    }

    /**
     * Met à jour la valeur Q pour une paire (état, action).
     *
     * @param state état courant
     * @param action action réalisée
     * @param reward récompense obtenue après l'action
     * @param nextState état suivant après l'action
     *
     * Exemple :
     *     agent.updateQValue(etat, action, 1.0, etatSuivant);
     */
    private void updateQValue(State state, Transition action, double reward, State nextState) {
        double oldQ = Q.getOrDefault(state, new HashMap<>()).getOrDefault(action, 0.0);
        double nextMaxQ = maxQ(nextState);
        double newQ = oldQ + alpha * (reward + gamma * nextMaxQ - oldQ);
        Q.computeIfAbsent(state, k -> new HashMap<>()).put(action, newQ);
    }

    /**
     * Choisit une action selon une stratégie ε-Greedy.
     *
     * @param state état courant
     * @param actions liste des actions disponibles
     * @return action choisie
     *
     * Exemple :
     *     Transition actionChoisie = agent.chooseAction(etat, actionsDisponibles);
     */
    private Transition chooseAction(State state, List<Transition> actions) {
        if (random.nextDouble() < epsilon) {
            return actions.get(random.nextInt(actions.size()));
        }
        return bestAction(state, actions);
    }

    /**
     * Sélectionne la meilleure action basée sur les valeurs Q apprises.
     *
     * @param state état courant
     * @param actions liste des actions disponibles
     * @return meilleure action estimée
     *
     * Exemple :
     *     Transition meilleureAction = agent.bestAction(etat, actionsDisponibles);
     */
    private Transition bestAction(State state, List<Transition> actions) {
        Transition best = actions.get(0);
        double bestValue = Q.getOrDefault(state, new HashMap<>()).getOrDefault(best, 0.0);
        for (Transition t : actions) {
            double value = Q.getOrDefault(state, new HashMap<>()).getOrDefault(t, 0.0);
            if (value > bestValue) {
                best = t;
                bestValue = value;
            }
        }
        return best;
    }

    /**
     * Renvoie la meilleure valeur Q pour un état donné.
     *
     * @param state état courant
     * @return meilleure valeur Q estimée
     *
     * Exemple :
     *     double maxQValue = agent.maxQ(etat);
     */
    private double maxQ(State state) {
        return Q.getOrDefault(state, new HashMap<>())
                .values().stream()
                .mapToDouble(v -> v)
                .max().orElse(0.0);
    }

    /**
     * Retourne la liste des récompenses obtenues pendant l'entraînement.
     *
     * @return liste des récompenses (Double)
     *
     * Exemple :
     *     List<Double> recompenses = agent.getRewards();
     */
    @Override
    public List<Double> getRewards() {
        return rewards;
    }

    /**
     * Retourne la liste des actions choisies par l'agent pendant l'entraînement.
     *
     * @return liste des actions choisies (String)
     *
     * Exemple :
     *     List<String> actions = agent.getActionsChosen();
     */
    @Override
    public List<String> getActionsChosen() {
        return actionsChosen;
    }

    /**
     * Calcule et retourne le chemin optimal à partir d'un état donné en suivant la politique Q-apprise.
     *
     * @param startState état de départ
     * @param env environnement associé
     * @return chemin optimal sous forme de liste d'actions
     *
     * Exemple :
     *     List<String> cheminOptimal = agent.getOptimalPath(startState, env);
     */
    public List<String> getOptimalPath(State startState, Evironnement env) throws Exception {
        List<String> optimalPath = new ArrayList<>();
        State state = startState;

        while (!env.isTerminal(state)) {
            List<Transition> actions = env.getActions(state);
            if (actions.isEmpty())
                break;

            Transition bestAction = bestAction(state, actions);
            optimalPath.add(bestAction.getParameterPredicate());

            state = bestAction.getDestination().explore();
        }

        return optimalPath;
    }
}
