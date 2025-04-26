package fr.polytech.mnia.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.polytech.mnia.Evironnement;
import fr.polytech.mnia.reward.YouTubeRewardFunction;
import de.prob.statespace.State;
import de.prob.statespace.Transition;

public class QLearningAgent implements Agent {

    private final double alpha;
    private final double gamma;
    private final double epsilon;
    private final Map<State, Map<Transition, Double>> Q = new HashMap<>();
    private final Random random = new Random();

    private final List<Double> rewards = new ArrayList<>();
    private final List<String> actionsChosen = new ArrayList<>();

    public QLearningAgent(double alpha, double gamma, double epsilon) {
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
    }

    @Override
    public void train(Evironnement env, int nbSteps, boolean verbose) throws Exception {
        for (int episode = 0; episode < nbSteps; episode++) {
            State state = env.getInitialState();

            while (!env.isTerminal(state)) {
                List<Transition> actions = env.getActions(state);

                if (actions.isEmpty())
                    break; // Sécurité si aucun mouvement possible

                Transition action = chooseAction(state, actions);

                if (env.getRewardFunction() instanceof YouTubeRewardFunction) {
                    ((YouTubeRewardFunction) env.getRewardFunction()).updateChosenVideo(action);
                }

                State nextState = action.getDestination();
                double reward = env.getReward(nextState);

                double oldQ = Q.getOrDefault(state, new HashMap<>())
                        .getOrDefault(action, 0.0);
                double nextMaxQ = maxQ(nextState);
                double newQ = oldQ + alpha * (reward + gamma * nextMaxQ - oldQ);

                Q.computeIfAbsent(state, k -> new HashMap<>()).put(action, newQ);

                // 🔥 Stocker pour l'analyse
                rewards.add(reward);
                actionsChosen.add(action.getParameterPredicate());

                // 🔥 Afficher si verbose
                if (verbose) {
                    System.out.println("Action : " + action.getParameterPredicate());
                    System.out.println("État ID : " + nextState.getId());
                    System.out.println("Reward : " + reward);
                    System.out.println("------");
                }

                state = nextState;
            }
        }
    }

    private Transition chooseAction(State s, List<Transition> actions) {
        if (random.nextDouble() < epsilon) {
            return actions.get(random.nextInt(actions.size()));
        }
        return bestAction(s, actions);
    }

    private Transition bestAction(State s, List<Transition> actions) {
        Transition best = actions.get(0);
        double bestValue = Q.getOrDefault(s, new HashMap<>()).getOrDefault(best, 0.0);
        for (Transition t : actions) {
            double value = Q.getOrDefault(s, new HashMap<>()).getOrDefault(t, 0.0);
            if (value > bestValue) {
                best = t;
                bestValue = value;
            }
        }
        return best;
    }

    private double maxQ(State s) {
        return Q.getOrDefault(s, new HashMap<>()).values()
                .stream().mapToDouble(v -> v).max().orElse(0.0);
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
