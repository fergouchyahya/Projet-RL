package fr.polytech.mnia.reward;

import de.prob.statespace.Transition;
import de.prob.statespace.State;

/**
 * YouTubeRewardFunction.java
 *
 * Fonction de récompense spécifique pour l'environnement YouTube.
 * 
 * Caractéristiques :
 * - La récompense dépend de la vidéo choisie.
 * - L'environnement n'a pas d'états terminaux (simule un flux infini de
 * vidéos).
 * 
 * Exemple d'utilisation :
 * RewardFunction rewardFunction = new YouTubeRewardFunction();
 * rewardFunction.updateChosenVideo(transition);
 * double r = rewardFunction.getReward(state);
 * boolean terminal = rewardFunction.isTerminal(state);
 */
public class YouTubeRewardFunction implements RewardFunction {

    private String lastChosenVideo = null;

    /**
     * Met à jour la dernière vidéo choisie à partir d'une transition effectuée.
     *
     * @param t transition effectuée (normalement "choose")
     *
     *          Exemple :
     *          rewardFunction.updateChosenVideo(transition);
     */
    public void updateChosenVideo(Transition t) {
        if (t.getName().equals("choose")) {
            String params = t.getParameterPredicate();
            if (params != null && params.contains("vv =")) {
                lastChosenVideo = params.split("=")[1].trim();
            }
        }
    }

    /**
     * Calcule la récompense associée à la dernière vidéo choisie.
     *
     * @param state état courant (utilisé pour la compatibilité, mais seul
     *              lastChosenVideo est utilisé)
     * @return récompense entre 0 et 1 selon l'intérêt de la vidéo
     *
     *         Exemple :
     *         double reward = rewardFunction.getReward(state);
     */
    @Override
    public double getReward(State state) {
        if (lastChosenVideo == null) {
            return 0.0;
        }

        switch (lastChosenVideo) {
            case "Gaming":
                return 1.0;
            case "Musique_populaire":
                return 0.8;
            case "Tutoriel_Python":
                return 0.6;
            case "Vlog_de_voyage":
                return 0.2;
            default:
                return 0.0;
        }
    }

    /**
     * Indique si l'état est terminal.
     *
     * @param state état courant
     * @return toujours false car YouTube modélise un flux infini
     *
     *         Exemple :
     *         boolean terminal = rewardFunction.isTerminal(state);
     */
    @Override
    public boolean isTerminal(State state) {
        return false;
    }
}
