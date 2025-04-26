package fr.polytech.mnia;

import java.util.List;

import de.prob.statespace.State;
import de.prob.statespace.Transition;
import fr.polytech.mnia.reward.RewardFunction;

/**
 * Evironnement représente l'interface entre un agent de renforcement
 * et un modèle ProB chargé (machine B spécifiée).
 * 
 * Il fournit :
 * - l'état courant et initial,
 * - les actions possibles,
 * - la récompense associée à un état,
 * - l'information de terminaison,
 * - l'accès à l'animateur ProB pour l'affichage.
 */
public class Evironnement {

    // === Champs ===
    private State state; // État courant de l'environnement
    private final State initial; // État initial de l'environnement
    private final RewardFunction rewardFunction; // Fonction de récompense spécifique
    private final MyProb animator; // Accès direct à l'animateur ProB
    private final String rewardVariable; // Variable à utiliser pour évaluer la récompense

    // === Constructeur ===

    /**
     * Initialise l'environnement à partir d'un Runner et d'une fonction de
     * récompense.
     * 
     * @param runner         Le runner chargé du modèle
     * @param rewardFunction La fonction de récompense spécifique
     * @param rewardVariable Le nom de la variable de récompense (ex: "res", "step",
     *                       etc.)
     */
    public Evironnement(Runner runner, RewardFunction rewardFunction, String rewardVariable) {
        this.state = runner.getState();
        this.initial = runner.getInitialState();
        this.rewardFunction = rewardFunction;
        this.animator = runner.animator;
        this.rewardVariable = rewardVariable;
    }

    // === Méthodes principales ===

    /**
     * Applique une action (transition) sur l'environnement et met à jour l'état
     * courant.
     * 
     * @param t Transition à appliquer
     */
    public void runAction(Transition t) {
        state = t.getDestination().explore();
    }

    /**
     * Retourne la liste des actions disponibles depuis l'état courant.
     */
    public List<Transition> getActions() {
        return this.state.getOutTransitions();
    }

    /**
     * Retourne la liste des actions disponibles depuis un état donné.
     * 
     * @param s L'état à explorer
     */
    public List<Transition> getActions(State s) {
        return s.getOutTransitions();
    }

    /**
     * Retourne l'état courant.
     */
    public State getState() {
        return this.state;
    }

    /**
     * Retourne l'état initial.
     */
    public State getInitialState() {
        return this.initial;
    }

    /**
     * Retourne la récompense associée à un état donné.
     * 
     * @param s État à évaluer
     */
    public double getReward(State s) {
        return rewardFunction.getReward(s);
    }

    /**
     * Indique si un état est terminal (fin d'épisode).
     * 
     * @param s État à tester
     */
    public boolean isTerminal(State s) {
        return rewardFunction.isTerminal(s);
    }

    /**
     * Retourne l'animateur MyProb associé.
     */
    public MyProb getAnimator() {
        return animator;
    }

    /**
     * Retourne le nom de la variable utilisée pour évaluer la récompense.
     */
    public String getRewardVariable() {
        return rewardVariable;
    }

    /**
     * Retourne la fonction de récompense utilisée.
     */
    public RewardFunction getRewardFunction() {
        return rewardFunction;
    }
}
// === Fin de la classe Evironnement ===