package pacman.controllers.ruoyu_chen;

import pacman.game.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by ruoyu on 4/13/16.
 */
public class ReadTrainingData {
    public ArrayList<dataPoint> read(String filePath){
        Scanner s = null;
        try {
            s = new Scanner(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ArrayList<dataPoint> dataPointArr = new ArrayList<>();
        while (s.hasNextLine()){
            String line = s.nextLine();
            String[] details = line.split(" ");
            int score = Integer.parseInt(details[0]);
            int time = Integer.parseInt(details[1]);
            double distance = Double.parseDouble(details[2]);
            int position = Integer.parseInt(details[3]);
            String moveStr = details[4];
            Constants.MOVE move = Constants.MOVE.UP; // default value of move
            switch (moveStr) {
                case "UP" :
                    break;
                case "DOWN" : move = Constants.MOVE.DOWN;
                    break;
                case "LEFT" : move = Constants.MOVE.LEFT;
                    break;
                case "RIGHT" : move = Constants.MOVE.RIGHT;
                    break;
                case "NEUTRAL" : move = Constants.MOVE.NEUTRAL;
                    break;
            }
            dataPoint dp = new dataPoint(score, time, distance, position, move);
            dataPointArr.add(dp);
        }
        return dataPointArr;
    }
//    public static void main (String[] args) throws FileNotFoundException {
//        ReadTrainingData rtd = new ReadTrainingData();
//        ArrayList<dataPoint> arrTest = rtd.read("/Users/ruoyu/Documents/FAI-Ruoyu_Chen/PacMan/data/AI_TrainingData/trainData1.txt");
//        Iterator<dataPoint> it = arrTest.iterator();
//        while(it.hasNext()) {
//            it.next().print();
//        }
//    }
}

class dataPoint {
    int score;
    int time;
    double distance;
    int position;
    Constants.MOVE move;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Constants.MOVE getMove() {
        return move;
    }

    public void setMove(Constants.MOVE move) {
        this.move = move;
    }

    public dataPoint(int score, int time, double distance, int position, Constants.MOVE move) {
        this.score = score;
        this.time = time;
        this.distance = distance;
        this.position = position;
        this.move = move;
    }

    public void print(){
        System.out.println("Score: " + score + "; time: " + time + "; distance: " + distance + "; position: " + position + "; Move: " + move);
    }
}
