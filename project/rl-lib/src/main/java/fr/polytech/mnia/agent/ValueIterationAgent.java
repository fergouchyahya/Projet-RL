package fr.polytech.mnia.agent;

import fr.polytech.mnia.Evironnement;
import de.prob.statespace.State;
import de.prob.statespace.Transition;

import java.util.*;

/**
 * Implémentation de Value Iteration pour MDP déterministe.
 * Agent totalement générique pour SimpleRL, YouTube et TicTacToe.
 */
public class ValueIterationAgent implements Agent {

    private final double gamma; // Taux d'actualisation
    private final double theta; // Seuil de convergence
    private final Map<State, Double> V = new HashMap<>();
    private final List<Double> rewards = new ArrayList<>();
    private final List<String> actionsChosen = new ArrayList<>();

    public ValueIterationAgent(double gamma, double theta) {
        this.gamma = gamma;
        this.theta = theta;
    }

    @Override
    public void train(Evironnement env, int nbSteps, boolean verbose) throws Exception {
        State initialState = env.getInitialState();
        explore(initialState, env);

        int steps = 0;
        boolean converged;

        do {
            converged = true;
            for (State s : V.keySet()) {
                if (env.isTerminal(s)) {
                    V.put(s, env.getReward(s));
                    continue;
                }
                double maxQ = Double.NEGATIVE_INFINITY;
                for (Transition t : env.getActions(s)) {
                    State next = t.getDestination().explore();
                    double reward = env.getReward(next);
                    double q = reward + gamma * V.getOrDefault(next, 0.0);
                    if (q > maxQ) {
                        maxQ = q;
                    }
                }
                double delta = Math.abs(maxQ - V.getOrDefault(s, 0.0));
                if (delta > theta) {
                    converged = false;
                }
                V.put(s, maxQ);
            }
            steps++;
            if (steps >= nbSteps)
                break;
        } while (!converged);

        // Enregistrement de récompenses pour analyse
        for (State s : V.keySet()) {
            rewards.add(V.getOrDefault(s, 0.0));
            actionsChosen.add("(policy_step)"); // Placeholder
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

    public List<Double> getRewards() {
        return rewards;
    }

    public List<String> getActionsChosen() {
        return actionsChosen;
    }
}
