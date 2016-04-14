package pacman.controllers.ruoyu_chen;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.*;

import static pacman.game.Constants.MOVE.*;

/**
 * Created by ruoyu on 4/11/16.
 */
public class KNN_Controller extends Controller<MOVE> {

    public static StarterGhosts ghosts = new StarterGhosts();

    @Override
    public MOVE getMove(Game game, long timeDue){
        Random rnd = new Random();
        String filePath = "/Users/ruoyu/Documents/FAI-Ruoyu_Chen/PacMan/data/AI_TrainingData/trainData1.txt";
        MOVE knn_move = knn(game, filePath);
        MOVE[] allPossibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
        for (MOVE m : allPossibleMoves) {
            if (m == knn_move) {
                return knn_move;
            }
        }
        int highIndex = rnd.nextInt(allPossibleMoves.length);
        return allPossibleMoves[highIndex];
    }

    public MOVE knn (Game game, String filePath){
        // read training data
        ReadTrainingData rtd = new ReadTrainingData();
        ArrayList<dataPoint> dpArr = rtd.read(filePath);

        // get current game data point information
        int pacManLocation = game.getPacmanCurrentNodeIndex();
        int ghostLocation = game.getGhostCurrentNodeIndex(Constants.GHOST.PINKY);
        int score = game.getScore();
        int time = game.getTotalTime();
        double distance = game.getEuclideanDistance(pacManLocation, ghostLocation);
        int position = game.getPacmanCurrentNodeIndex();

        dataPoint dpCurr = new dataPoint(score, time, distance, position, MOVE.NEUTRAL); // default move for current node is Neutral

        // calculate the distance between current data point with datapoint in the dpArr. Store the distance as Key, and the Move as value. To a Tree Map.
        // Question? At this time we use Tree Map to store distance, there is a shortcome: we don't take duplicate point into account,
        // this is because Tree map doesn't take duplicate keys. This need to be impreved.
        TreeMap<Double, MOVE> tmap = new TreeMap<>();
        // loop through each point in dpArr
        Iterator<dataPoint> it = dpArr.iterator();
        while(it.hasNext()){
            dataPoint learnDp = it.next();
            double dis = calDpDistance(dpCurr, learnDp);
            tmap.put(dis, learnDp.getMove());
        }

        // set n = 5; get the nearest 5 data points, and get the most move
        TreeMap<Double, MOVE> firstFive= putFirstEntries(5, tmap);
        int down = 0;
        int up = 0;
        int right = 0;
        int left = 0;
        int neutral = 0;
        for (Double key : tmap.keySet()){
            MOVE move = tmap.get(key);

            switch (move) {
                case DOWN: down++;
                    break;
                case UP: up++;
                    break;
                case RIGHT: right++;
                    break;
                case LEFT: left++;
                    break;
                case NEUTRAL: neutral++;
                    break;
            }
        }
        // find the largest value
        int max = 0;
        if (max < down) {
            max = down;
        }
        if (max < up) {
            max = up;
        }
        if (max < right) {
            max = right;
        }
        if (max < left) {
            max = left;
        }
        if (max < neutral) {
            max = neutral;
        }

        // finally, find the move!
        if(max == down) {
            return DOWN;
        }
        if(max == up) {
            return UP;
        }
        if(max == right) {
            return RIGHT;
        }
        if(max == left) {
            return LEFT;
        } else {
            return NEUTRAL;
        }
    }

    // method to calculate distance between two data points
    public double calDpDistance (dataPoint dp1, dataPoint dp2){
        int scoreDis = Math.abs(dp1.getScore() - dp2.getScore());
        int timeDis = Math.abs(dp1.getTime() - dp2.getTime());
        double distanceDis = Math.abs(dp1.getDistance() - dp2.getDistance());
        int positionDis = Math.abs(dp1.getPosition() - dp2.getPosition());
        double DpDistance = Math.sqrt((scoreDis * scoreDis) + (timeDis * timeDis) + (distanceDis * distanceDis) + (positionDis * positionDis));
        return DpDistance;
    }

    // method to get the first N values in a TreeMap
    public TreeMap<Double,MOVE> putFirstEntries(int k, TreeMap<Double, MOVE> source) {
        int count = 0;
        TreeMap<Double,MOVE> target = new TreeMap<>();
        for (Map.Entry<Double, MOVE> entry:source.entrySet()) {
            if (count >= k) break;

            target.put(entry.getKey(), entry.getValue());
            count++;
        }
        return target;
    }
}