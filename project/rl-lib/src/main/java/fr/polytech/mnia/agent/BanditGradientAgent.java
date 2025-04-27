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
 * BanditGradientAgent.java
 *
 * Implémentation d'un agent de renforcement basé sur l'algorithme des Bandits
 * Gradients.
 *
 * L'agent ajuste dynamiquement ses préférences pour chaque action en fonction
 * des récompenses obtenues,
 * selon une politique de sélection basée sur la fonction Softmax.
 * 
 * Exemple d'utilisation :
 * Agent agent = new BanditGradientAgent(0.1);
 * agent.train(env, 1000, true);
 */
public class BanditGradientAgent implements Agent {

    private final double alpha;
    private final Map<String, Double> preferences = new HashMap<>();
    private final Random random = new Random();

    private final List<Double> rewards = new ArrayList<>();
    private final List<String> actionsChosen = new ArrayList<>();

    /**
     * Construit un agent Bandit Gradient avec un taux d'apprentissage donné.
     *
     * @param alpha le taux d'apprentissage utilisé pour ajuster les préférences
     *              (ex: 0.1)
     *
     *              Exemple :
     *              BanditGradientAgent agent = new BanditGradientAgent(0.05);
     */
    public BanditGradientAgent(double alpha) {
        this.alpha = alpha;
    }

    /**
     * Entraîne l'agent dans un environnement sur un nombre donné d'étapes.
     *
     * @param env     l'environnement (instance de Evironnement) où l'agent agit
     * @param nbSteps nombre d'épisodes (étapes) d'entraînement
     * @param verbose true pour affichage détaillé des états et actions, false pour
     *                mode silencieux
     * @throws Exception en cas de problème lors d'une transition ou évaluation
     *
     *                   Exemple :
     *                   agent.train(env, 500, true);
     */
    @Override
    public void train(Evironnement env, int nbSteps, boolean verbose) throws Exception {
        for (int episode = 0; episode < nbSteps; episode++) {
            State state = env.getInitialState();
            List<Transition> actions = env.getActions();

            Transition chosen = softmaxSample(actions);

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
                System.out.println("State ID :" + state.getId());
                env.getAnimator().printState(state);
                env.getAnimator().printActions(env.getActions());
            }

            for (Transition t : actions) {
                String key = t.getName() + t.getParameterPredicate();
                double prob = softmaxProbability(t, actions);
                double update = alpha * (reward - baseline()) * ((t.equals(chosen) ? 1 : 0) - prob);
                preferences.put(key, preferences.getOrDefault(key, 0.0) + update);
            }
        }
    }

    /**
     * Sélectionne une action parmi les actions disponibles selon une distribution
     * Softmax.
     *
     * @param actions liste des actions possibles
     * @return une action choisie aléatoirement en fonction de la probabilité
     *         Softmax
     *
     *         Exemple :
     *         Transition action = agent.softmaxSample(actionsDisponibles);
     */
    private Transition softmaxSample(List<Transition> actions) {
        double total = actions.stream()
                .mapToDouble(a -> Math.exp(preferences.getOrDefault(a.getName() + a.getParameterPredicate(), 0.0)))
                .sum();
        double r = random.nextDouble() * total;
        double sum = 0.0;
        for (Transition a : actions) {
            sum += Math.exp(preferences.getOrDefault(a.getName() + a.getParameterPredicate(), 0.0));
            if (r <= sum) {
                return a;
            }
        }
        return actions.get(actions.size() - 1);
    }

    /**
     * Calcule la probabilité Softmax associée à une action parmi toutes les actions
     * disponibles.
     *
     * @param action  l'action pour laquelle on veut la probabilité
     * @param actions liste des actions possibles
     * @return la probabilité Softmax de l'action
     *
     *         Exemple :
     *         double proba = agent.softmaxProbability(monAction,
     *         actionsDisponibles);
     */
    private double softmaxProbability(Transition action, List<Transition> actions) {
        double numerator = Math.exp(preferences.getOrDefault(action.getName() + action.getParameterPredicate(), 0.0));
        double denominator = actions.stream()
                .mapToDouble(a -> Math.exp(preferences.getOrDefault(a.getName() + a.getParameterPredicate(), 0.0)))
                .sum();
        return numerator / denominator;
    }

    /**
     * Retourne la valeur de la baseline (récompense moyenne utilisée dans
     * l'algorithme Bandit Gradient).
     *
     * @return 0.0 (baseline constante dans cette implémentation)
     *
     *         Exemple :
     *         double base = agent.baseline();
     */
    private double baseline() {
        return 0.0;
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
