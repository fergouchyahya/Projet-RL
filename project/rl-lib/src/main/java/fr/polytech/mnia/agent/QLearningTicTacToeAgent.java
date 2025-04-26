package fr.polytech.mnia.agent;

import fr.polytech.mnia.Evironnement;
import fr.polytech.mnia.tictactoe.TicTacToeEpisodeManager;
import de.prob.statespace.State;
import de.prob.statespace.Transition;

import java.util.*;

/**
 * Q-Learning spécialisé pour TicTacToe :
 * - Entraînement par nombre d'épisodes (matchs complets)
 * - L'agent joue en tant que joueur 0
 * - Joueur 1 joue aléatoirement
 */
public class QLearningTicTacToeAgent implements Agent {

    private final double alpha;
    private final double gamma;
    private final double epsilon;
    private final Map<State, Map<Transition, Double>> Q = new HashMap<>();
    private final Random random = new Random();
    private final List<Double> rewards = new ArrayList<>();
    private final List<String> actionsChosen = new ArrayList<>();

    public QLearningTicTacToeAgent(double alpha, double gamma, double epsilon) {
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
    }

    @Override
    public void train(Evironnement env, int nbEpisodes, boolean verbose) throws Exception {
        TicTacToeEpisodeManager manager = new TicTacToeEpisodeManager(env);

        for (int episode = 0; episode < nbEpisodes; episode++) {
            env.reset(); 

            State state = env.getInitialState();

            while (!env.isTerminal(state)) {
                String turn = state.eval("turn").toString().trim();

                List<Transition> actions = env.getActions(state);
                if (actions.isEmpty())
                    break;

                Transition chosen;
                if (turn.equals("0")) {
                    // Tour de l'agent
                    chosen = chooseAction(state, actions);

                    env.runAction(chosen);
                    State nextState = env.getState();

                    double reward = env.getReward(nextState);
                    updateQValue(state, chosen, reward, nextState);

                    rewards.add(reward);
                    actionsChosen.add(chosen.getParameterPredicate());

                    if (verbose) {
                        System.out.println("Agent joue : " + chosen.getName() + " " + chosen.getParameterPredicate());
                        manager.prettyPrintGrid();
                    }

                    state = nextState;
                } else {
                    // Tour du joueur 1 (aléatoire)
                    Transition randomMove = actions.get(random.nextInt(actions.size()));
                    env.runAction(randomMove);
                    state = env.getState();
                }
            }
        }
    }

    private void updateQValue(State state, Transition action, double reward, State nextState) {
        double oldQ = Q.getOrDefault(state, new HashMap<>()).getOrDefault(action, 0.0);
        double nextMaxQ = maxQ(nextState);
        double newQ = oldQ + alpha * (reward + gamma * nextMaxQ - oldQ);
        Q.computeIfAbsent(state, k -> new HashMap<>()).put(action, newQ);
    }

    private Transition chooseAction(State state, List<Transition> actions) {
        if (random.nextDouble() < epsilon) {
            return actions.get(random.nextInt(actions.size()));
        }
        return bestAction(state, actions);
    }

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

    private double maxQ(State state) {
        return Q.getOrDefault(state, new HashMap<>())
                .values().stream()
                .mapToDouble(v -> v)
                .max().orElse(0.0);
    }

    @Override
    public List<Double> getRewards() {
        return rewards;
    }

    @Override
    public List<String> getActionsChosen() {
        return actionsChosen;
    }
}
