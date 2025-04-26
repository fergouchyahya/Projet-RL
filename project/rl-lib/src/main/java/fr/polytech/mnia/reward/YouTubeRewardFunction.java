package fr.polytech.mnia.reward;

import de.prob.statespace.Transition;
import de.prob.statespace.State;

/**
 * Fonction de récompense pour l'environnement YouTube.
 */
public class YouTubeRewardFunction implements RewardFunction {

    private String lastChosenVideo = null;

    /**
     * Met à jour la dernière vidéo choisie à partir d'une transition effectuée.
     * 
     * @param t Transition choisie
     */
    public void updateChosenVideo(Transition t) {
        if (t.getName().equals("choose")) {
            String params = t.getParameterPredicate();
            if (params != null && params.contains("vv =")) {
                lastChosenVideo = params.split("=")[1].trim();
            }
        }
    }

    @Override
    public double getReward(State state) {
        if (lastChosenVideo == null) {
            return 0.0;
        }

        // Récompenses selon la vidéo choisie
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

    @Override
    public boolean isTerminal(State state) {
        // L'environnement YouTube est infini par design : jamais terminal
        return false;
    }
}
