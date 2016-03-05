package pacman.controllers.ruoyu_chen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.PriorityQueue;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.controllers.PacManNode;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.MOVE;
import pacman.game.internal.Maze;
import pacman.game.internal.Node;
import pacman.game.Game;

public class AStar_Controller extends Controller<MOVE>{
	
	private N[] graph;

	public static StarterGhosts ghosts = new StarterGhosts();
	
	// Define static MOVE[] arrayList CurrentMoves to store all Moves by last calculate in AStar
	public static ArrayList<MOVE> CurrentMoves = new ArrayList<MOVE>();
	// Initialize last move to Neutral to use in A star
	public static MOVE lastMove = MOVE.NEUTRAL;
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		// check whether CurrentMoves is null, that is whether pacman has get the nearest goal, if not, then return next move to get the goal
		if (CurrentMoves.isEmpty() == false){
			// return the first element in CurrentMoves
			lastMove = CurrentMoves.remove(0);
			return lastMove;
		}
		else{
			Maze currentMaze = game.getCurrentMaze();
			createGraph(currentMaze.graph);
			// define target index, target is the nearest pill to the current node of pacman
			// current node index
			int currentIndex = game.getPacmanCurrentNodeIndex();
			// Firstly get all pills index
			int[] allPillIndices = game.getPillIndices();
			// Randomly select one pill as target.
			int rnd = new Random().nextInt(allPillIndices.length);
			int targetIndex = allPillIndices[rnd];
			// loop through each pill, and get the distance from current node to the pill, get the nearest pill as target
//			int targetIndex = 1000;
//			for(int i : allPillIndices){
//				int dis = game.getShortestPathDistance(currentIndex, i);
//				if(dis < targetIndex){
//					targetIndex = dis;
//				}
//			}
			
			// Use A star algorithm to get a complete path (a series of moves) from current node to the target
			CurrentMoves = astar(currentIndex, targetIndex, lastMove, game);
			MOVE currentMove = CurrentMoves.remove(0);
			return currentMove;			
		}
	}

	public ArrayList<MOVE> astar(int currentIndex, int targetIndex,
			MOVE lastMove2, Game game) {
		// TODO Auto-generated method stub
		N start=graph[currentIndex];
		N target=graph[targetIndex];
		
        PriorityQueue<N> open = new PriorityQueue<N>();
        ArrayList<N> closed = new ArrayList<N>();

        start.g = 0;
        start.h = game.getShortestPathDistance(start.index, target.index);

        start.reached=lastMove2;
        
        open.add(start);

        while(!open.isEmpty())
        {
            N currentNode = open.poll();
            closed.add(currentNode);
            
            if (currentNode.isEqual(target))
                break;

            for(E next : currentNode.adj)
            {
            	if(next.move!=currentNode.reached.opposite())
            	{
	                double currentDistance = next.cost;
	
	                if (!open.contains(next.node) && !closed.contains(next.node))
	                {
	                    next.node.g = currentDistance + currentNode.g;
	                    next.node.h = game.getShortestPathDistance(next.node.index, target.index);
	                    next.node.parent = currentNode;
	                    
	                    next.node.reached=next.move;
	
	                    open.add(next.node);
	                }
	                else if (currentDistance + currentNode.g < next.node.g)
	                {
	                    next.node.g = currentDistance + currentNode.g;
	                    next.node.parent = currentNode;
	                    
	                    next.node.reached=next.move;
	
	                    if (open.contains(next.node))
	                        open.remove(next.node);
	
	                    if (closed.contains(next.node))
	                        closed.remove(next.node);
	
	                    open.add(next.node);
	                }
	            }
            }
        }

        return extractPath(target, game);
	}

	private ArrayList<MOVE> extractPath(N target, Game game)
	{
    	ArrayList<Integer> route = new ArrayList<Integer>();
        N current = target;
        route.add(current.index);

        while (current.parent != null)
        {
            route.add(current.parent.index);
            current = current.parent;
        }
        
        Collections.reverse(route);

        ArrayList<MOVE> moveArray = new ArrayList<MOVE>();
        // loop through route and get each move
        for(int i=0; i<route.size()-1; i++){
        	MOVE thisMove = game.getMoveToMakeToReachDirectNeighbour(route.get(i), route.get(i+1));
        	moveArray.add(thisMove);
        }
        return moveArray;
    }
	
	// this function will transfer the current maze into a graph(N[])
	public void createGraph(Node[] nodes)
	{
		graph=new N[nodes.length];
		
		//create graph
		for(int i=0;i<nodes.length;i++)
			graph[i]=new N(nodes[i].nodeIndex);
		
		//add neighbours
		for(int i=0;i<nodes.length;i++)
		{	
			EnumMap<MOVE,Integer> neighbours=nodes[i].neighbourhood;
			MOVE[] moves=MOVE.values();
			
			for(int j=0;j<moves.length;j++)
				if(neighbours.containsKey(moves[j]))
					graph[i].adj.add(new E(graph[neighbours.get(moves[j])],moves[j],1));	
		}
	}

}

// Remaining classes define data structures that used in A star algorithm
class N implements Comparable<N>
{
    public N parent;
    public double g, h;
    public boolean visited = false;
    public ArrayList<E> adj;
    public int index;
    public MOVE reached=null;

    public N(int index)
    {
        adj = new ArrayList<E>();
        this.index=index;
    }

    public N(double g, double h)
    {
        this.g = g;
        this.h = h;
    }

    public boolean isEqual(N another)
    {
        return index == another.index;
    }

    public String toString()
    {
        return ""+index;
    }

	public int compareTo(N another)
	{
      if ((g + h) < (another.g + another.h))
    	  return -1;
      else  if ((g + h) > (another.g + another.h))
    	  return 1;
		
		return 0;
	}
}

class E
{
	public N node;
	public MOVE move;
	public double cost;
	
	public E(N node,MOVE move,double cost)
	{
		this.node=node;
		this.move=move;
		this.cost=cost;
	}
}
