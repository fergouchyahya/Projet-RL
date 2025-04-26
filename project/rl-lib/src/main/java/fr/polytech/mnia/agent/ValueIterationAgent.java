package fr.polytech.mnia.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.polytech.mnia.Evironnement;
import de.prob.statespace.State;
import de.prob.statespace.Transition;

public class ValueIterationAgent implements Agent {

    private final double gamma;
    private final double theta;
    private final Map<State, Double> V = new HashMap<>();
    private final Set<State> visited = new HashSet<>();

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
            for (State s : visited) {
                if (env.isTerminal(s)) {
                    V.put(s, env.getReward(s));
                    continue;
                }
                double maxQ = Double.NEGATIVE_INFINITY;
                for (Transition t : env.getActions()) {
                    if (t.getSource().equals(s)) {
                        State next = t.getDestination();
                        double reward = env.getReward(next);
                        double q = reward + gamma * V.getOrDefault(next, 0.0);
                        if (q > maxQ) {
                            maxQ = q;
                        }
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
                break; // 🔥 Limiter le nombre d'itérations

            if (verbose) {
                System.out.println("Value Iteration Step: " + steps);
            }
        } while (!converged);

        // Pour analyse : remplir rewards et actions simulées
        for (State s : visited) {
            rewards.add(V.getOrDefault(s, 0.0));
            actionsChosen.add("(virtual action)");
        }
    }

    private void explore(State state, Evironnement env) {
        if (visited.contains(state))
            return;
        visited.add(state);
        for (Transition t : env.getActions()) {
            if (t.getSource().equals(state)) {
                explore(t.getDestination(), env);
            }
        }
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
