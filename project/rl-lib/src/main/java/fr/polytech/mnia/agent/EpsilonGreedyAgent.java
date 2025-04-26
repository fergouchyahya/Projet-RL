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
 * Implémentation d'un agent Epsilon-Greedy pour l'apprentissage par
 * renforcement.
 */
public class EpsilonGreedyAgent implements Agent {

    private final double epsilon;
    private final Map<String, Double> estimates = new HashMap<>();
    private final Map<String, Integer> counts = new HashMap<>();
    private final Random random = new Random();
    private final List<Double> rewards = new ArrayList<>();
    private final List<String> actionsChosen = new ArrayList<>();

    public EpsilonGreedyAgent(double epsilon) {
        this.epsilon = epsilon;
    }

    @Override
    public void train(Evironnement env, int nbSteps, boolean verbose) throws Exception {
        for (int episode = 0; episode < nbSteps; episode++) {
            State state = env.getInitialState();
            List<Transition> actions = env.getActions();

            Transition chosen;
            if (random.nextDouble() < epsilon) {
                chosen = actions.get(random.nextInt(actions.size()));
            } else {
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

    public List<Double> getRewards() {
        return rewards;
    }

    public List<String> getActionsChosen() {
        return actionsChosen;
    }
}
