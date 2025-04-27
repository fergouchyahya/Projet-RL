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

/**
 * EpsilonGreedyAgent.java
 *
 * Implémentation d'un agent d'apprentissage par renforcement utilisant la
 * stratégie Epsilon-Greedy.
 * 
 * L'agent choisit une action au hasard avec probabilité ε (exploration),
 * ou sélectionne la meilleure action connue avec probabilité 1-ε
 * (exploitation).
 * 
 * Exemple d'utilisation :
 * Agent agent = new EpsilonGreedyAgent(0.1);
 * agent.train(env, 1000, true);
 */
public class EpsilonGreedyAgent implements Agent {

    private final double epsilon;
    private final Map<String, Double> estimates = new HashMap<>();
    private final Map<String, Integer> counts = new HashMap<>();
    private final Random random = new Random();
    private final List<Double> rewards = new ArrayList<>();
    private final List<String> actionsChosen = new ArrayList<>();

    /**
     * Construit un agent Epsilon-Greedy avec une probabilité ε d'exploration.
     *
     * @param epsilon valeur de ε entre 0 et 1 (ex: 0.1 pour 10% d'exploration)
     *
     *                Exemple :
     *                EpsilonGreedyAgent agent = new EpsilonGreedyAgent(0.1);
     */
    public EpsilonGreedyAgent(double epsilon) {
        this.epsilon = epsilon;
    }

    /**
     * Entraîne l'agent dans un environnement donné pendant un nombre fixé d'étapes.
     *
     * @param env     l'environnement d'apprentissage (instance de Evironnement)
     * @param nbSteps le nombre d'étapes d'entraînement
     * @param verbose true pour affichage détaillé, false pour exécution silencieuse
     * @throws Exception en cas d'erreur pendant une transition
     *
     *                   Exemple :
     *                   agent.train(env, 500, true);
     */
    @Override
    public void train(Evironnement env, int nbSteps, boolean verbose) throws Exception {
        for (int episode = 0; episode < nbSteps; episode++) {
            State state = env.getInitialState();
            List<Transition> actions = env.getActions();

            Transition chosen;
            if (random.nextDouble() < epsilon) {
                // Exploration
                chosen = actions.get(random.nextInt(actions.size()));
            } else {
                // Exploitation
                chosen = bestAction(actions);
            }

            if (env.getRewardFunction() instanceof YouTubeRewardFunction) {
                ((YouTubeRewardFunction) env.getRewardFunction()).updateChosenVideo(chosen);
            }

            env.runAction(chosen);
            state = env.getState();

            double reward = env.getReward(state);
            rewards.add(reward);
            actionsChosen.add(chosen.getParameterPredicate());

            if (verbose) {
                System.out.println("Evaluation : " + state.eval(env.getRewardVariable()));
                System.out.println("State ID : " + state.getId());
                env.getAnimator().printState(state);
                env.getAnimator().printActions(env.getActions());
                System.out.println();
            }

            String key = chosen.getName() + chosen.getParameterPredicate();
            counts.put(key, counts.getOrDefault(key, 0) + 1);
            double oldEstimate = estimates.getOrDefault(key, 0.0);
            double newEstimate = oldEstimate + (1.0 / counts.get(key)) * (reward - oldEstimate);
            estimates.put(key, newEstimate);
        }
    }

    /**
     * Sélectionne la meilleure action selon les estimations de récompense
     * accumulées.
     *
     * @param actions liste des actions possibles
     * @return l'action avec la meilleure valeur estimée
     *
     *         Exemple :
     *         Transition meilleureAction = agent.bestAction(actionsDisponibles);
     */
    private Transition bestAction(List<Transition> actions) {
        Transition best = actions.get(0);
        double bestValue = estimates.getOrDefault(best.getName() + best.getParameterPredicate(), 0.0);
        for (Transition t : actions) {
            double value = estimates.getOrDefault(t.getName() + t.getParameterPredicate(), 0.0);
            if (value > bestValue) {
                best = t;
                bestValue = value;
            }
        }
        return best;
    }

    /**
     * Retourne la liste des récompenses obtenues au cours de l'entraînement.
     *
     * @return liste de récompenses (Double)
     *
     *         Exemple :
     *         List<Double> recompenses = agent.getRewards();
     */
    @Override
    public List<Double> getRewards() {
        return rewards;
    }

    /**
     * Retourne la liste des actions choisies par l'agent au cours de
     * l'entraînement.
     *
     * @return liste d'actions choisies (String)
     *
     *         Exemple :
     *         List<String> actions = agent.getActionsChosen();
     */
    @Override
    public List<String> getActionsChosen() {
        return actionsChosen;
    }
}
