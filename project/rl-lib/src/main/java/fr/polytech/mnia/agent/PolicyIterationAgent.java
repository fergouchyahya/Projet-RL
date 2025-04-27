package fr.polytech.mnia.agent;

import fr.polytech.mnia.Evironnement;
import de.prob.statespace.State;
import de.prob.statespace.Transition;

import java.util.*;

/**
 * PolicyIterationAgent.java
 *
 * Implémentation d'un agent utilisant l'algorithme de Policy Iteration pour un
 * MDP déterministe.
 * 
 * Cet agent est totalement générique et fonctionne avec des environnements
 * comme SimpleRL, YouTube ou TicTacToe.
 * Il explore l'espace des états pour estimer la meilleure politique, puis
 * exécute cette politique.
 * 
 * Exemple d'utilisation :
 * Agent agent = new PolicyIterationAgent(0.9);
 * agent.train(env, 1000, true);
 */
public class PolicyIterationAgent implements Agent {

    private final double gamma;
    private final Map<State, Transition> policy = new HashMap<>();
    private final Map<State, Double> V = new HashMap<>();
    private final List<Double> rewards = new ArrayList<>();
    private final List<String> actionsChosen = new ArrayList<>();
    private final Random random = new Random();

    /**
     * Construit un agent Policy Iteration avec un facteur d'actualisation donné.
     *
     * @param gamma facteur d'actualisation (0 ≤ gamma ≤ 1), contrôle l'importance
     *              des récompenses futures
     *
     *              Exemple :
     *              PolicyIterationAgent agent = new PolicyIterationAgent(0.95);
     */
    public PolicyIterationAgent(double gamma) {
        this.gamma = gamma;
    }

    /**
     * Entraîne l'agent dans un environnement par Policy Iteration pendant un nombre
     * maximal d'étapes.
     *
     * @param env     l'environnement d'entraînement (instance de Evironnement)
     * @param nbSteps nombre maximum d'itérations de Policy Iteration
     * @param verbose true pour affichage détaillé, false pour exécution silencieuse
     * @throws Exception en cas de problème durant les transitions ou l'exploration
     *
     *                   Exemple :
     *                   agent.train(env, 1000, false);
     */
    @Override
    public void train(Evironnement env, int nbSteps, boolean verbose) throws Exception {
        State initialState = env.getInitialState();
        explore(initialState, env);

        // Initialisation aléatoire de la politique
        for (State s : V.keySet()) {
            List<Transition> actions = env.getActions(s);
            if (!actions.isEmpty()) {
                policy.put(s, actions.get(random.nextInt(actions.size())));
            }
        }

        int steps = 0;
        boolean stable;

        do {
            // Policy Evaluation : met à jour V(s) pour chaque état
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

            // Policy Improvement : améliore la politique en choisissant la meilleure action
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

        // Simulation de la politique finale
        env.reset();
        State state = env.getInitialState();

        while (!env.isTerminal(state)) {
            Transition action = policy.get(state);
            if (action == null)
                break;

            actionsChosen.add(action.getParameterPredicate());

            env.runAction(action);
            State nextState = env.getState();
            double reward = env.getReward(nextState);
            rewards.add(reward);

            state = nextState;
        }
    }

    /**
     * Explore récursivement tous les états accessibles à partir d'un état donné.
     *
     * @param state l'état de départ
     * @param env   l'environnement d'exploration
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
     * Sélectionne la meilleure action depuis un état donné en maximisant la valeur
     * attendue.
     *
     * @param env l'environnement
     * @param s   l'état courant
     * @return la meilleure transition/action
     *
     *         Exemple :
     *         Transition meilleurAction = agent.bestAction(env, state);
     */
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

    /**
     * Retourne la liste des récompenses obtenues lors de la simulation de la
     * politique.
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
     * Retourne la liste des actions choisies lors de la simulation de la politique.
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
