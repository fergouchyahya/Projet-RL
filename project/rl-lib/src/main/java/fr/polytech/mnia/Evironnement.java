package fr.polytech.mnia;

import java.util.List;

import de.prob.statespace.State;
import de.prob.statespace.Transition;
import fr.polytech.mnia.reward.RewardFunction;

/**
 * Evironnement.java
 *
 * Représente l'interface entre un agent de renforcement et un modèle B chargé
 * via ProB.
 *
 * Il fournit :
 * - l'état courant et initial,
 * - la liste des actions disponibles,
 * - la récompense associée à un état,
 * - l'information de terminaison d'un épisode,
 * - un accès direct à l'animateur ProB pour l'affichage.
 *
 * Exemple d'utilisation :
 * Evironnement env = new Evironnement(runner, rewardFunction, "res");
 * List<Transition> actions = env.getActions();
 * double reward = env.getReward(state);
 */
public class Evironnement {

    // === Champs ===
    private State state; // État courant de l'environnement
    private final State initial; // État initial de l'environnement
    private final RewardFunction rewardFunction; // Fonction de récompense spécifique
    private final MyProb animator; // Accès direct à l'animateur ProB
    private final String rewardVariable; // Nom de la variable de récompense (ex: "res", "step", etc.)

    // === Constructeur ===

    /**
     * Initialise l'environnement à partir d'un Runner et d'une fonction de
     * récompense.
     *
     * @param runner         Runner chargé de l'initialisation de la machine B
     * @param rewardFunction Fonction de récompense spécifique à l'environnement
     * @param rewardVariable Nom de la variable à évaluer pour obtenir les
     *                       récompenses
     *
     *                       Exemple :
     *                       Evironnement env = new Evironnement(runner, new
     *                       SimpleRewardFunction(), "res");
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
     * @param t transition à appliquer
     *
     *          Exemple :
     *          env.runAction(transition);
     */
    public void runAction(Transition t) {
        state = t.getDestination().explore();
    }

    /**
     * Retourne la liste des actions disponibles depuis l'état courant.
     *
     * @return liste de transitions possibles
     *
     *         Exemple :
     *         List<Transition> actions = env.getActions();
     */
    public List<Transition> getActions() {
        return this.state.getOutTransitions();
    }

    /**
     * Retourne la liste des actions disponibles depuis un état donné.
     *
     * @param s état à explorer
     * @return liste de transitions possibles
     *
     *         Exemple :
     *         List<Transition> actions = env.getActions(state);
     */
    public List<Transition> getActions(State s) {
        return s.getOutTransitions();
    }

    /**
     * Retourne l'état courant de l'environnement.
     *
     * @return état courant
     *
     *         Exemple :
     *         State currentState = env.getState();
     */
    public State getState() {
        return this.state;
    }

    /**
     * Retourne l'état initial de l'environnement.
     *
     * @return état initial
     *
     *         Exemple :
     *         State initialState = env.getInitialState();
     */
    public State getInitialState() {
        return this.initial;
    }

    /**
     * Retourne la récompense associée à un état donné.
     *
     * @param s état à évaluer
     * @return valeur de la récompense
     *
     *         Exemple :
     *         double reward = env.getReward(state);
     */
    public double getReward(State s) {
        return rewardFunction.getReward(s);
    }

    /**
     * Indique si un état donné est terminal (fin d'épisode).
     *
     * @param s état à tester
     * @return true si l'état est terminal, false sinon
     *
     *         Exemple :
     *         boolean terminal = env.isTerminal(state);
     */
    public boolean isTerminal(State s) {
        return rewardFunction.isTerminal(s);
    }

    /**
     * Retourne l'animateur ProB (MyProb) associé à l'environnement.
     *
     * @return instance de MyProb
     *
     *         Exemple :
     *         MyProb animator = env.getAnimator();
     */
    public MyProb getAnimator() {
        return animator;
    }

    /**
     * Retourne le nom de la variable de récompense utilisée.
     *
     * @return nom de la variable
     *
     *         Exemple :
     *         String var = env.getRewardVariable();
     */
    public String getRewardVariable() {
        return rewardVariable;
    }

    /**
     * Retourne la fonction de récompense associée à l'environnement.
     *
     * @return fonction de récompense
     *
     *         Exemple :
     *         RewardFunction r = env.getRewardFunction();
     */
    public RewardFunction getRewardFunction() {
        return rewardFunction;
    }

    /**
     * Réinitialise l'environnement à l'état initial.
     *
     * Exemple :
     * env.reset();
     */
    public void reset() {
        State state = animator.getStateSpace().getRoot();
        Transition setup = state.findTransition(Transition.SETUP_CONSTANTS_NAME);
        if (setup != null) {
            state = setup.getDestination();
        }
        Transition initialise = state.findTransition(Transition.INITIALISE_MACHINE_NAME);
        if (initialise != null) {
            state = initialise.getDestination();
        }
        this.state = state.exploreIfNeeded();
    }
}
