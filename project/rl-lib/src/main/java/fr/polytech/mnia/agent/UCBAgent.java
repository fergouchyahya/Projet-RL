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
 * UCBAgent.java
 *
 * Implémentation d'un agent utilisant l'algorithme UCB (Upper Confidence Bound)
 * pour l'apprentissage par renforcement.
 * 
 * L'agent choisit ses actions en équilibrant exploitation et exploration
 * en fonction de l'estimation de la récompense et du niveau d'incertitude.
 * 
 * Exemple d'utilisation :
 * Agent agent = new UCBAgent();
 * agent.train(env, 1000, true);
 */
public class UCBAgent implements Agent {

    private final Map<String, Double> estimates = new HashMap<>();
    private final Map<String, Integer> counts = new HashMap<>();
    private final List<Double> rewards = new ArrayList<>();
    private final List<String> actionsChosen = new ArrayList<>();

    /**
     * Entraîne l'agent dans un environnement donné sur un nombre fixé d'étapes.
     *
     * @param env     l'environnement d'apprentissage
     * @param nbSteps nombre d'actions (étapes) à réaliser
     * @param verbose true pour affichage détaillé, false pour exécution silencieuse
     * @throws Exception en cas de problème pendant l'entraînement
     *
     *                   Exemple :
     *                   agent.train(env, 1000, false);
     */
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

    /**
     * Sélectionne une action parmi celles disponibles en utilisant la formule UCB.
     *
     * @param actions liste des actions possibles
     * @param total   nombre total d'actions effectuées jusqu'ici
     * @return action choisie selon la stratégie UCB
     *
     *         Exemple :
     *         Transition meilleureAction = agent.ucbSelect(actionsDisponibles,
     *         totalActions);
     */
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

    /**
     * Calcule la valeur UCB d'une action.
     *
     * @param action action à évaluer
     * @param total  nombre total d'actions effectuées
     * @return valeur UCB calculée
     *
     *         Exemple :
     *         double valeurUCB = agent.ucbValue(action, totalActions);
     */
    private double ucbValue(Transition action, int total) {
        String key = action.getName() + action.getParameterPredicate();
        double mean = estimates.getOrDefault(key, 0.0);
        int n = counts.getOrDefault(key, 0) + 1;
        return mean + Math.sqrt(2 * Math.log(Math.max(1, total)) / n);
    }

    /**
     * Retourne la liste des récompenses obtenues pendant l'entraînement.
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
     * Retourne la liste des actions choisies pendant l'entraînement.
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
