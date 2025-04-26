package fr.polytech.mnia;

/*
 * Cette classe illustre l'exécution de YouTube.mch
 */
public class YouTubeRunner extends Runner {
    /*
     * Le constructeur lance ProB sur la machine YouTube.mch
     * et initialise la machine
     */
    public YouTubeRunner() throws Exception {
        super("/Simple/YouTube.mch");
        this.initialise();
    }

    /*
     * La méthode execSequence donne un exemple d'interaction avec l'animateur étape
     * par étape. À chaque étape on affiche l'état et les transitions
     * déclenchables dans cet état.
     */
    public void execSequence() throws Exception {
        // Ici on commence par choisir Tutoriel_Python
        this.state = state.perform("choose", "vv = Tutoriel_Python").explore();
        System.out.println("Evaluation : " + this.state.eval("duration"));
        animator.printState(state);
        animator.printActions(state.getOutTransitions());

        // Ensuite on choisit Musique_populaire
        this.state = state.perform("choose", "vv = Musique_populaire").explore();
        System.out.println("Evaluation : " + this.state.eval("duration"));
        animator.printState(state);
        animator.printActions(state.getOutTransitions());

        // Ensuite on choisit Gaming
        this.state = state.perform("choose", "vv = Gaming").explore();
        System.out.println("Evaluation : " + this.state.eval("duration"));
        animator.printState(state);
        animator.printActions(state.getOutTransitions());

        // Ensuite on choisit Vlog_de_voyage
        this.state = state.perform("choose", "vv = Vlog_de_voyage").explore();
        System.out.println("Evaluation : " + this.state.eval("duration"));
        animator.printState(state);
        animator.printActions(state.getOutTransitions());

        // Ici on explore la transition numéro 1 (exemple)
        this.showTransition(state.getOutTransitions().get(1));
    }
}
