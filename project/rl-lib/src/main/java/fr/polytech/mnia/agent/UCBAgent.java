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
 * Implémentation d'un agent Upper Confidence Bound (UCB).
 */
public class UCBAgent implements Agent {

    private final Map<String, Double> estimates = new HashMap<>();
    private final Map<String, Integer> counts = new HashMap<>();
    private final Random random = new Random();
    private final List<Double> rewards = new ArrayList<>();
    private final List<String> actionsChosen = new ArrayList<>();

    @Override
    public void train(Evironnement env, int nbSteps, boolean verbose) throws Exception {
        int totalActions = 0;
        for (int episode = 0; episode < nbSteps; episode++) {
            State state = env.getInitialState();
            List<Transition> actions = env.getActions();

            Transition chosen = ucbSelect(actions, totalActions);

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

            totalActions++;
        }
    }

    private Transition ucbSelect(List<Transition> actions, int total) {
        Transition best = actions.get(0);
        double bestValue = ucbValue(best, total);

        for (Transition t : actions) {
            double value = ucbValue(t, total);
            if (value > bestValue) {
                best = t;
                bestValue = value;
            }
        }
        return best;
    }

    private double ucbValue(Transition action, int total) {
        String key = action.getName() + action.getParameterPredicate();
        double mean = estimates.getOrDefault(key, 0.0);
        int n = counts.getOrDefault(key, 0) + 1;
        return mean + Math.sqrt(2 * Math.log(Math.max(1, total)) / n);
    }

    public List<Double> getRewards() {
        return rewards;
    }

    public List<String> getActionsChosen() {
        return actionsChosen;
    }
}
