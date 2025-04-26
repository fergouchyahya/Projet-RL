package fr.polytech.mnia.agent;

import fr.polytech.mnia.Evironnement;
import de.prob.statespace.State;
import de.prob.statespace.Transition;

import java.util.*;

/**
 * Implémentation de Policy Iteration pour MDP déterministe.
 * Agent totalement générique pour SimpleRL, YouTube et TicTacToe.
 */
public class PolicyIterationAgent implements Agent {

    private final double gamma;
    private final Map<State, Transition> policy = new HashMap<>();
    private final Map<State, Double> V = new HashMap<>();
    private final List<Double> rewards = new ArrayList<>();
    private final List<String> actionsChosen = new ArrayList<>();
    private final Random random = new Random();

    public PolicyIterationAgent(double gamma) {
        this.gamma = gamma;
    }

    @Override
    public void train(Evironnement env, int nbSteps, boolean verbose) throws Exception {
        State initialState = env.getInitialState();
        explore(initialState, env);

        // Initialisation de la politique aléatoire
        for (State s : V.keySet()) {
            List<Transition> actions = env.getActions(s);
            if (!actions.isEmpty()) {
                policy.put(s, actions.get(random.nextInt(actions.size())));
            }
        }

        int steps = 0;
        boolean stable;

        do {
            // Policy Evaluation
            for (State s : V.keySet()) {
                if (env.isTerminal(s)) {
                    V.put(s, env.getReward(s));
                    continue;
                }
                Transition t = policy.get(s);
                if (t != null && t.getSource().equals(s)) {
                    State next = t.getDestination().explore();
                    double reward = env.getReward(next);
                    V.put(s, reward + gamma * V.getOrDefault(next, 0.0));
                }
            }

            // Policy Improvement
            stable = true;
            for (State s : V.keySet()) {
                if (env.isTerminal(s))
                    continue;

                Transition oldAction = policy.get(s);
                Transition bestAction = bestAction(env, s);

                if (bestAction != null && !bestAction.equals(oldAction)) {
                    policy.put(s, bestAction);
                    stable = false;
                }
            }

            steps++;
            if (steps >= nbSteps)
                break;
        } while (!stable);

        for (State s : V.keySet()) {
            rewards.add(V.getOrDefault(s, 0.0));
            actionsChosen.add("(policy_step)");
        }
    }

    private void explore(State state, Evironnement env) {
        if (V.containsKey(state))
            return;
        V.put(state, 0.0);
        for (Transition t : env.getActions(state)) {
            explore(t.getDestination().explore(), env);
        }
    }

    private Transition bestAction(Evironnement env, State s) {
        List<Transition> actions = env.getActions(s);
        Transition best = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (Transition t : actions) {
            State next = t.getDestination().explore();
            double reward = env.getReward(next);
            double value = reward + gamma * V.getOrDefault(next, 0.0);
            if (value > bestValue) {
                best = t;
                bestValue = value;
            }
        }
        return best;
    }

    public List<Double> getRewards() {
        return rewards;
    }

    public List<String> getActionsChosen() {
        return actionsChosen;
    }
}
