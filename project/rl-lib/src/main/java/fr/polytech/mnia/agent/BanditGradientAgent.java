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
 * Implémentation d'un agent Epsilon Greedy.
 */
public class BanditGradientAgent implements Agent {

    private final double alpha;
    private final Map<String, Double> preferences = new HashMap<>();
    private final Random random = new Random();

    private final List<Double> rewards = new ArrayList<>();
    private final List<String> actionsChosen = new ArrayList<>();

    public BanditGradientAgent(double alpha) {
        this.alpha = alpha;
    }

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

    private double softmaxProbability(Transition action, List<Transition> actions) {
        double numerator = Math.exp(preferences.getOrDefault(action.getName() + action.getParameterPredicate(), 0.0));
        double denominator = actions.stream()
                .mapToDouble(a -> Math.exp(preferences.getOrDefault(a.getName() + a.getParameterPredicate(), 0.0)))
                .sum();
        return numerator / denominator;
    }

    private double baseline() {
        return 0.0;
    }

    public List<Double> getRewards() {
        return rewards;
    }

    public List<String> getActionsChosen() {
        return actionsChosen;
    }
}
