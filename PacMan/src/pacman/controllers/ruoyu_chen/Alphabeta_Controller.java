package pacman.controllers.ruoyu_chen;

import java.util.ArrayList;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.controllers.PacManNode;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * @Title Hwk 3, CS5100/4100 Artificial Intelligence, Alpha-beta
 * @Author Ruoyu.Chen
 * @Date Mar. 22, 2016
 */

public class Alphabeta_Controller extends Controller<MOVE> {
	
	public static StarterGhosts ghosts = new StarterGhosts();

	@Override
	public MOVE getMove(Game game, long timeDue) {
		// TODO Auto-generated method stub
		Random rnd = new Random();
		MOVE[] allMoves = MOVE.values();
		
//		initiate highScore and highMove and set them as ArrayList
		ArrayList<Integer> highScore = new ArrayList<Integer>();
		highScore.add(Integer.MIN_VALUE);
		ArrayList<MOVE> highMove = new ArrayList<MOVE>();
		
		for (MOVE m : allMoves){
			Game gameCopy = game.copy();
			Game gameAtM = gameCopy;
			gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
			
//			judge whether there is a wall, and deal with it
			if(gameAtM.getPacmanCurrentNodeIndex() == game.getPacmanCurrentNodeIndex()){
				System.out.println("Trying Move: " + m + ", Obstacle!!!");
				continue;
			} else{
				int tempHighScore = this.alphabeta(new PacManNode(gameAtM, 0), 7, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
				if(highScore.get(0) < tempHighScore){
					highScore.clear();
					highScore.add(tempHighScore);
					highMove.clear();
					highMove.add(m);
				} else if(highScore.get(0) == tempHighScore){
					highScore.add(tempHighScore);
					highMove.add(m);
				}
				
				System.out.println("Trying Move: " + m + ", Score: "
						+ tempHighScore);
			}
		}
		// get highest score move and return it.
		int highIndex = rnd.nextInt(highScore.size());
		System.out.println("----> Moving "+ highMove.get(highIndex) + " High Score: " + highScore.get(highIndex));
//		System.out.println("----> Moving "+ highMove.get(0) + " High Score: " + highScore.get(0));
		
		return highMove.get(highIndex);
//		return highMove.get(0);
	}

	public int alphabeta(PacManNode pacManNode, int maxdepth, int alpha, int beta, boolean pacMan) {
		MOVE[] allMoves = MOVE.values();
		// TODO Auto-generated method stub
		if(maxdepth == 0 || pacManNode.depth >= maxdepth){
			return heuristic(pacManNode);
		}
		if(pacMan){
			// pacMan round
			int v = Integer.MIN_VALUE;
			for(MOVE m : allMoves){
				Game gameCopy = pacManNode.gameState.copy();
				gameCopy.advanceGame(m, ghosts.getMove(gameCopy, 0));
				if (gameCopy.getPacmanCurrentNodeIndex() == pacManNode.gameState
						.getPacmanCurrentNodeIndex()) {
					continue;
				} else {
					int score = alphabeta(new PacManNode(gameCopy, pacManNode.depth + 1), maxdepth, alpha, beta, false);
					v = Math.max(v, score);
					alpha = Math.max(alpha, v);
					if(beta <= alpha){
						break; // beta cut off
					}
				}
			}
			return v;
		} else {
			// ghosts round
			int v = Integer.MAX_VALUE;
			for(MOVE m : allMoves){
				Game gameCopy = pacManNode.gameState.copy();
				gameCopy.advanceGame(m, ghosts.getMove(gameCopy, 0));
				if (gameCopy.getPacmanCurrentNodeIndex() == pacManNode.gameState
						.getPacmanCurrentNodeIndex()) {
					continue;
				} else {
					int score = alphabeta(new PacManNode(gameCopy, pacManNode.depth + 1), maxdepth, alpha, beta, true);
					v = Math.min(v, score);
					beta = Math.min(beta, v);
					if(beta <= alpha){
						break; // alpha cut off
					}
				}
			}
			return v;
		}
	}

	private int heuristic(PacManNode pacManNode) {
		// TODO Auto-generated method stub
		// The score will be the actual score by the getScore functon plus the total distance between pacMan and all ghosts.
		GHOST[] allGhosts = GHOST.values();
		int distance = 0;
		for(GHOST g : allGhosts){
			distance += pacManNode.gameState.getShortestPathDistance(pacManNode.gameState.getPacmanCurrentNodeIndex(),
																	pacManNode.gameState.getGhostCurrentNodeIndex(g));
		}
//		int score = pacManNode.gameState.getScore() + distance;
//		int score = pacManNode.gameState.getScore();
		int score = pacManNode.gameState.getScore() + distance / 10;
		return score;
	}
}
