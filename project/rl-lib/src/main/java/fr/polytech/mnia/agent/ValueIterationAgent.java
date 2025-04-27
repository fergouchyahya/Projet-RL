package fr.polytech.mnia.agent;

import fr.polytech.mnia.Evironnement;
import de.prob.statespace.State;
import de.prob.statespace.Transition;

import java.util.*;

/**
 * ValueIterationAgent.java
 *
 * Implémentation d'un agent utilisant l'algorithme de Value Iteration pour un
 * MDP déterministe.
 *
 * Cet agent est totalement générique et fonctionne avec SimpleRL, YouTube et
 * TicTacToe.
 * Il calcule la valeur optimale des états avant de simuler une trajectoire
 * optimale.
 * 
 * Exemple d'utilisation :
 * Agent agent = new ValueIterationAgent(0.9, 0.01);
 * agent.train(env, 1000, true);
 */
public class ValueIterationAgent implements Agent {

    private final double gamma;
    private final double theta;
    private final Map<State, Double> V = new HashMap<>();
    private final List<Double> rewards = new ArrayList<>();
    private final List<String> actionsChosen = new ArrayList<>();

    /**
     * Construit un agent Value Iteration avec les paramètres spécifiés.
     *
     * @param gamma facteur d'actualisation (entre 0 et 1)
     * @param theta seuil de convergence (petit nombre positif)
     *
     *              Exemple :
     *              ValueIterationAgent agent = new ValueIterationAgent(0.95, 0.01);
     */
    public ValueIterationAgent(double gamma, double theta) {
        this.gamma = gamma;
        this.theta = theta;
    }

    /**
     * Entraîne l'agent par Value Iteration sur un nombre limité d'étapes.
     *
     * @param env     environnement de travail
     * @param nbSteps nombre maximum d'itérations avant arrêt
     * @param verbose true pour affichage des étapes, false sinon
     * @throws Exception en cas d'erreur pendant l'exécution
     *
     *                   Exemple :
     *                   agent.train(env, 500, true);
     */
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

        // Après Value Iteration : simulation de la politique optimale
        env.reset();
        State state = env.getInitialState();

        while (!env.isTerminal(state)) {
            List<Transition> actions = env.getActions(state);
            if (actions.isEmpty())
                break;

            Transition bestAction = null;
            double bestValue = Double.NEGATIVE_INFINITY;
            for (Transition t : actions) {
                State next = t.getDestination().explore();
                double reward = env.getReward(next);
                double q = reward + gamma * V.getOrDefault(next, 0.0);
                if (q > bestValue) {
                    bestValue = q;
                    bestAction = t;
                }
            }

            if (bestAction == null)
                break;

            actionsChosen.add(bestAction.getParameterPredicate());

            env.runAction(bestAction);
            State nextState = env.getState();
            double reward = env.getReward(nextState);
            rewards.add(reward);

            state = nextState;
        }
    }

    /**
     * Explore récursivement tous les états accessibles à partir de l'état donné.
     *
     * @param state état de départ
     * @param env   environnement utilisé pour récupérer les transitions
     *
     *              Exemple :
     *              agent.explore(initialState, env);
     */
    private void explore(State state, Evironnement env) {
        if (V.containsKey(state))
            return;
        V.put(state, 0.0);
        for (Transition t : env.getActions(state)) {
            explore(t.getDestination().explore(), env);
        }
    }

    /**
     * Retourne la liste des récompenses obtenues pendant la simulation.
     *
     * @return liste des récompenses (Double)
     *
     *         Exemple :
     *         List<Double> recompenses = agent.getRewards();
     */
    @Override
    public List<Double> getRewards() {
        return rewards;
    }

    /**
     * Retourne la liste des actions choisies pendant la simulation.
     *
     * @return liste des actions (String)
     *
     *         Exemple :
     *         List<String> actions = agent.getActionsChosen();
     */
    @Override
    public List<String> getActionsChosen() {
        return actionsChosen;
    }
}
