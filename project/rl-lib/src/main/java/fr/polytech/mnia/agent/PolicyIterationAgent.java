package fr.polytech.mnia.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import fr.polytech.mnia.Evironnement;
import de.prob.statespace.State;
import de.prob.statespace.Transition;

public class PolicyIterationAgent implements Agent {

    private final double gamma;
    private final Map<State, Transition> policy = new HashMap<>();
    private final Map<State, Double> V = new HashMap<>();
    private final Set<State> visited = new HashSet<>();
    private final Random random = new Random();

    private final List<Double> rewards = new ArrayList<>();
    private final List<String> actionsChosen = new ArrayList<>();

    public PolicyIterationAgent(double gamma) {
        this.gamma = gamma;
    }

    @Override
    public void train(Evironnement env, int nbSteps, boolean verbose) throws Exception {
        State initialState = env.getInitialState();
        explore(initialState, env);

        // Initialiser une politique aléatoire
        for (State s : visited) {
            List<Transition> actions = env.getActions();
            if (!actions.isEmpty()) {
                policy.put(s, actions.get(random.nextInt(actions.size())));
            }
        }

        int steps = 0;
        boolean stable;
        do {
            // Policy Evaluation
            for (State s : visited) {
                if (env.isTerminal(s)) {
                    V.put(s, env.getReward(s));
                } else {
                    Transition t = policy.get(s);
                    if (t != null && t.getSource().equals(s)) {
                        State next = t.getDestination();
                        double reward = env.getReward(next);
                        V.put(s, reward + gamma * V.getOrDefault(next, 0.0));
                    }
                }
            }

            // Policy Improvement
            stable = true;
            for (State s : visited) {
                if (env.isTerminal(s)) continue;

                Transition oldAction = policy.get(s);
                Transition bestAction = bestAction(env, s);

                if (bestAction != null && !bestAction.equals(oldAction)) {
                    policy.put(s, bestAction);
                    stable = false;
                }
            }

            steps++;
            if (steps >= nbSteps) break; // 🔥 On arrête après nbSteps itérations max

            if (verbose) {
                System.out.println("Policy Iteration Step: " + steps);
            }
        } while (!stable);

        // Pour analyse : on remplit les récompenses choisies
        for (State s : visited) {
            if (policy.containsKey(s)) {
                Transition t = policy.get(s);
                actionsChosen.add(t.getParameterPredicate());
                rewards.add(V.getOrDefault(s, 0.0));
            }
        }
    }

    private void explore(State state, Evironnement env) {
        if (visited.contains(state)) return;
        visited.add(state);
        for (Transition t : env.getActions()) {
            if (t.getSource().equals(state)) {
                explore(t.getDestination(), env);
            }
        }
    }

    private Transition bestAction(Evironnement env, State s) {
        List<Transition> actions = env.getActions();
        Transition best = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (Transition t : actions) {
            if (t.getSource().equals(s)) {
                State next = t.getDestination();
                double reward = env.getReward(next);
                double value = reward + gamma * V.getOrDefault(next, 0.0);
                if (value > bestValue) {
                    best = t;
                    bestValue = value;
                }
            }
        }
        return best;
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
