package pacman.controllers.ruoyu_chen;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.controllers.PacManNode;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.MOVE;
import pacman.game.Constants;
import pacman.game.Game;

/**
 * 
 * @Title Hwk 2, CS5100/4100 Artificial Intelligence, Hill CLimbing Controller
 * @Author Ruoyu.Chen
 * @Date Feb 22, 2016
 */

public class HillClimbing_Controller extends Controller<MOVE> {
	
	public static StarterGhosts ghosts = new StarterGhosts();

	@Override
	public MOVE getMove(Game game, long timeDue) {
		// TODO Auto-generated method stub
		Random rnd = new Random();
		MOVE[] allMoves = MOVE.values();
		
		// initiate highScore and highMove and set them as ArrayList
		ArrayList<Integer> highScore = new ArrayList<Integer>();
		highScore.add(-1);
		ArrayList<MOVE> highMove = new ArrayList<MOVE>();
		
		for (MOVE m : allMoves){
			Game gameCopy = game.copy();
			Game gameAtM = gameCopy;
			gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
			
			// judge whether there is a wall, and deal with it.
			if (gameAtM.getPacmanCurrentNodeIndex() == game.getPacmanCurrentNodeIndex()) {
				System.out.println("Trying Move: " + m + ", Obstacle!!!");
				continue;
			} else {
				int tempHighScore = this.hcb(new PacManNode(gameAtM, 0));
				
				if (highScore.get(0) < tempHighScore) {
					highScore.clear();
					highScore.add(tempHighScore);
					highMove.clear();
					highMove.add(m);
				} else if (highScore.get(0) == tempHighScore) {
					highScore.add(tempHighScore);
					highMove.add(m);
				}
				
				System.out.println("Trying Move: " + m + ", Score: " + tempHighScore);
			}
		}
		// get highest score move and return it
		int highIndex = rnd.nextInt(highScore.size());
		
		System.out.println("High Score: " + highScore.get(highIndex));
		
		return highMove.get(highIndex);
	}

	/**
	 * 
	 * @param state
	 * @param maxdepth
	 * @return
	 */
	private int hcb(PacManNode rootGameState) {
		// TODO Auto-generated method stub
		MOVE[] allMoves = Constants.MOVE.values();
		int highScore = -1;
		
		for(MOVE m : allMoves){
			Game gameCopy = rootGameState.gameState.copy();
			gameCopy.advanceGame(m, ghosts.getMove(gameCopy, 0));
			if(gameCopy.getPacmanCurrentNodeIndex() == rootGameState.gameState.getPacmanCurrentNodeIndex()){
				continue;
			} else {
				PacManNode node = new PacManNode(gameCopy, rootGameState.depth+1);
				int score = rootGameState.gameState.getScore();
				if (highScore < score){
					highScore = score;
				}
			}
		}
		
		return highScore;
	}
	
}