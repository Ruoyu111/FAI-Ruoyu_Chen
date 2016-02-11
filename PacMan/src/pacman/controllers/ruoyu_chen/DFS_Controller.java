package pacman.controllers.ruoyu_chen;

import java.util.ArrayList;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.controllers.PacManNode;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * @Title Hwk 1, CS5100/4100 Artificial Intelligence, Depth-First Search
 * @Author Ruoyu.Chen
 * @Date Feb 9, 2016
 */

public class DFS_Controller extends Controller<MOVE> {

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

		for (MOVE m : allMoves) {
			Game gameCopy = game.copy();
			Game gameAtM = gameCopy;
			gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));

			// judge whether there is a wall, and deal with it.
			if (gameAtM.getPacmanCurrentNodeIndex() == game
					.getPacmanCurrentNodeIndex()) {
				System.out.println("Trying Move: " + m + ", Obstacle!!!");
				continue;
			} else {
				int tempHighScore = this.dfs(new PacManNode(gameAtM, 0), 7);

				if (highScore.get(0) < tempHighScore) {
					highScore.clear();
					highScore.add(tempHighScore);
					highMove.clear();
					highMove.add(m);
				} else if (highScore.get(0) == tempHighScore) {
					highScore.add(tempHighScore);
					highMove.add(m);
				}

				System.out.println("Trying Move: " + m + ", Score: "
						+ tempHighScore);
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
	public int dfs(PacManNode state, int maxdepth) {
		// TODO Auto-generated method stub
		MOVE[] allMoves = MOVE.values();
		int highScore = -1;

		for (MOVE m : allMoves) {
			Game gameCopy = state.gameState.copy();
			gameCopy.advanceGame(m, ghosts.getMove(gameCopy, 0));

			if (state.depth >= maxdepth) {
				int score = state.gameState.getScore();
				if (highScore < score) {
					highScore = score;
				}
			} else {
				if (gameCopy.getPacmanCurrentNodeIndex() == state.gameState
						.getPacmanCurrentNodeIndex()) {
					continue;
				} else {
					highScore = dfs(new PacManNode(gameCopy, state.depth + 1),
							7);
				}
			}
		}
		return highScore;
	}
}
