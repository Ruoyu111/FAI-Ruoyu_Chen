package pacman.controllers.ruoyu_chen;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.controllers.PacManNode;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class BFS_Controller extends Controller<MOVE> {
	
	public static StarterGhosts ghosts = new StarterGhosts();
	@Override
	public MOVE getMove(Game game, long timeDue) {
		// TODO Auto-generated method stub
		Random rnd = new Random();
		MOVE[] allMoves = MOVE.values();
		
//		int highScore = -1;
//		MOVE highMove = null;
		
		ArrayList<Integer> highScore = new ArrayList<Integer>();
//		set initial value of highScore
		highScore.add(-1);
		ArrayList<MOVE> highMove = new ArrayList<MOVE>();
		
		for (MOVE m : allMoves){
//			System.out.println("Trying Move: " + m);
			Game gameCopy = game.copy();
			Game gameAtM = gameCopy;
			gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
			
//			Judge whether there is wall, that is to say, whether PacMan remains in the same pot. And try to avoid it.
			if(gameAtM.getPacmanCurrentNodeIndex() == game.getPacmanCurrentNodeIndex()){
				System.out.println("Trying Move: " + m + ", Obstacle!!!");
				continue;
			} else {
				int tempHighScore = this.bfs(new PacManNode(gameAtM, 0), 7);
				
				if(highScore.get(0) < tempHighScore){
					highScore.clear();
					highScore.add(tempHighScore);
					highMove.clear();
					highMove.add(m);
				} else if(highScore.get(0) == tempHighScore) {
					highScore.add(tempHighScore);
					highMove.add(m);
				}
				
				System.out.println("Trying Move: " + m + ", Score: " + tempHighScore);				
			}
		}
		// Get a random move from highMove if there are multiple equal score moves. In order to avoid 'back and forth' effect.
		int highIndex = rnd.nextInt(highScore.size());
		
		System.out.println("High Score: " + highScore.get(highIndex) + ", High Move: " + highMove.get(highIndex));
		
		return highMove.get(highIndex);			
	}
	public int bfs(PacManNode rootGameState, int maxdepth) {
		// TODO Auto-generated method stub
		MOVE[] allMoves = Constants.MOVE.values();
		int highScore = -1;
		
		Queue<PacManNode> queue = new LinkedList<PacManNode>();
		queue.add(rootGameState);
		
		while(!queue.isEmpty()){
			PacManNode pmnode = queue.remove();
			
			if(pmnode.depth >= maxdepth){
				int score = pmnode.gameState.getScore();
				if (highScore < score){
					highScore = score;
				}
			}
			else{
				for(MOVE m : allMoves){
					Game gameCopy = pmnode.gameState.copy();
					gameCopy.advanceGame(m, ghosts.getMove(gameCopy, 0));
					if(gameCopy.getPacmanCurrentNodeIndex() == pmnode.gameState.getPacmanCurrentNodeIndex()){
						continue;
					} else {
						PacManNode node = new PacManNode(gameCopy, pmnode.depth+1);
						queue.add(node);						
					}
				}
			}
		}
		
		return highScore;
	}
	
}