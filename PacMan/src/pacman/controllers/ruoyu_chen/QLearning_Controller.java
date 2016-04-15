package pacman.controllers.ruoyu_chen;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by ruoyu on 4/11/16.
 */
public class QLearning_Controller extends Controller<MOVE> {

    public static StarterGhosts ghosts = new StarterGhosts();

    // Parameter of Q learning
    public static final double learningRate = 0.8;
    public static final double discount = 0.5;

    @Override
    public MOVE getMove(Game game, long timeDue) {
        State curNode = new State(game);

        // get all possible moves of current state
        MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
        Random rnd = new Random();

        double highQ = Integer.MIN_VALUE;
        MOVE highMove = MOVE.NEUTRAL;

        for (MOVE m : possibleMoves){
            Game gameCopy = game.copy();
            Game gameAtM = gameCopy;
            gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
            State tempNode = new State(gameAtM);

            updateNode(curNode, tempNode, m);
        }

        for (Link l : curNode.links){
            System.out.println("Trying to move: " + l.move.toString() + " with Q value " + l.Q);
            if (highQ < l.Q){
                highQ = l.Q;
                highMove = l.move;
            }
        }

        System.out.println("High Q: " + highQ + "Go: " + highMove);

        return highMove;
    }

    // method of updating state node
    public static void updateNode(State currNode, State nextNode, MOVE move) {
        Link link = new Link(currNode, nextNode, move);

        int reward = (nextNode.game.wasPillEaten() ? 12 : 0) +
                (nextNode.game.wasPowerPillEaten() ? 3 : 0) +
                20 * (nextNode.game.getNumGhostsEaten() - currNode.game.getNumGhostsEaten()) -
                (currNode.game.getPacmanLastMoveMade() == move.opposite() ? 6 : 5) -
                (nextNode.game.wasPacManEaten() ? 350 : 0) +
                (nextNode.game.getNumberOfActivePills() == 0 ? 50 : 0);

        if(currNode.links.isEmpty() || !(currNode.links.contains(link))){
            link.Q = reward;
            currNode.links.add(link);
            System.out.println("New link added.");
        } else {
            System.out.println("Link updated.");
            for (Link l : currNode.links){
                if (link.equals(l)){
                    double tempQ = 0;
                    for (Link ll : link.toState.links) {
                        tempQ = ll.Q > tempQ ? ll.Q : tempQ;
                    }

                    link.Q = l.Q + learningRate * (reward + discount * tempQ - l.Q);
                    currNode.links.remove(l);
                    currNode.links.add(link);
                    break;
                }
            }
        }
    }
}

class State{
    public Game game;
    // distance to nearest ghost
    int disToNearestG;
    // distance to nearest pill
    int disToNearestP;
    // distance to nearest power pill
    int disToNearestPB;
    // boolean whether the ghosts are eatable
    boolean eatable;

    // construct a set to store all links which start at this state
    public Set<Link> links = new HashSet<Link>();

    public State(Game game) {
        this.game = game;

        int current = game.getPacmanCurrentNodeIndex();
        int[] pills = game.getActivePillsIndices();
        int[] powerPills = game.getActivePowerPillsIndices();

        // find the distance to the nearest active pill
        this.disToNearestP = Integer.MAX_VALUE;
        for(int i=0; i<pills.length; i++){
            int tempDistance = game.getManhattanDistance(current, pills[i]);
            this.disToNearestP = tempDistance < this.disToNearestP ? tempDistance : this.disToNearestP;
        }
        // find the distance to the nearest power pill
        this.disToNearestPB = Integer.MAX_VALUE;
        for (int i=0; i<powerPills.length; i++){
            int tempDis = game.getManhattanDistance(current, powerPills[i]);
            this.disToNearestPB = tempDis < this.disToNearestPB ? tempDis : this.disToNearestPB;
        }
        // Find the distance to the nearest ghost
        Constants.GHOST[] ghosts = Constants.GHOST.values();
        this.disToNearestG = Integer.MAX_VALUE;
        for (Constants.GHOST g : ghosts){
            int tempDistance = game.getManhattanDistance(current, game.getGhostCurrentNodeIndex(g));
            if (tempDistance < this.disToNearestG){
                this.disToNearestG = tempDistance;
                this.eatable = game.getGhostLairTime(g) > 0;
            }
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        if (disToNearestG != state.disToNearestG) return false;
        if (disToNearestP != state.disToNearestP) return false;
        if (disToNearestPB != state.disToNearestPB) return false;
        if (eatable != state.eatable) return false;

        return true;
    }
}

class Link{
    public double Q;
    public MOVE move;
    public State fromState, toState;

    public Link(State fromState, State toState, MOVE move) {
        this.move = move;
        this.toState = toState;
        this.fromState = fromState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link link = (Link) o;

        if (fromState != null ? !fromState.equals(link.fromState) : link.fromState != null) return false;
        return toState != null ? toState.equals(link.toState) : link.toState == null;
    }

}


