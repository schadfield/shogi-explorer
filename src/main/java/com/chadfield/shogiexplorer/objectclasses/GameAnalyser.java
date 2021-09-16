package com.chadfield.shogiexplorer.objectclasses;

import com.chadfield.shogiexplorer.main.EngineManager;
import com.chadfield.shogiexplorer.main.SFENParser;
import com.chadfield.shogiexplorer.objects.AnalysisParameter;
import com.chadfield.shogiexplorer.objects.Board;
import com.chadfield.shogiexplorer.objects.Coordinate;
import com.chadfield.shogiexplorer.objects.Engine;
import com.chadfield.shogiexplorer.objects.EngineOption;
import com.chadfield.shogiexplorer.objects.Game;
import com.chadfield.shogiexplorer.objects.Koma;
import com.chadfield.shogiexplorer.objects.Notation;
import com.chadfield.shogiexplorer.objects.Position;
import com.chadfield.shogiexplorer.utils.ParserUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class GameAnalyser {
    
    private Process process;
    private OutputStream stdin;
    private BufferedReader bufferedReader;
    private String lastScore = "";
    private String opinion = "";
    private int analysisTimePerMove;
    private int analysisMistakeThreshold;
    private int analysisBlunderThreshold;
    private int analysisIgnoreThreshold;

    public void analyse(Game game, Engine engine, JList<String> moveList, JTable analysisTable, AnalysisParameter analysisParam) throws IOException {
        this.analysisTimePerMove = analysisParam.getAnalysisTimePerMove();
        this.analysisMistakeThreshold = analysisParam.getAnalysisMistakeThreshold();
        this.analysisBlunderThreshold = analysisParam.getAnalysisBlunderThreshold();
        this.analysisIgnoreThreshold = analysisParam.getAnalysisIgnoreThreshold();
        initializeEngine(engine);
        initiateUSIProtocol();
        setOptions(engine);
        getReady();
        String engineMove = null;
        String sfen = null;
        String lastSFEN = null;
        int count = 0;
                
        for (Position position : game.getPositionList()) {
            if (engineMove != null) {
                count++;
                updateMoveList(moveList, count);
                analysePosition(game, lastSFEN, engineMove, analysisTable, count);
            } 
            lastSFEN = sfen;
            sfen = position.getGameSFEN();
            engineMove = position.getNotation().getEngineMove();
        }

        count++;
        updateMoveList(moveList, count);
        analysePosition(game, lastSFEN, engineMove, analysisTable, count);
        
        quitEngine();
    }

    private void updateMoveList(JList<String> moveList, final int index) {
        try {
            java.awt.EventQueue.invokeAndWait(()
                    -> moveList.setSelectedIndex(index));
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException ex) {
            Logger.getLogger(GameAnalyser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initializeEngine(Engine engine) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(engine.getPath());
            processBuilder.directory((new File(engine.getPath())).getParentFile());
            process = processBuilder.start();
        } catch (IOException ex) {
            Logger.getLogger(EngineManager.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        stdin = process.getOutputStream();

        bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    }

    private void initiateUSIProtocol() throws IOException {
        stdin.write("usi\n".getBytes());
        stdin.flush();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("usiok")) {
                return;
            }
        }
    }

    private void setOptions(Engine engine) throws IOException {
        for (EngineOption option : engine.getEngineOptionList()) {
            if (!option.getDef().contentEquals(option.getValue())) {
                stdin.write(("setoption " + option.getName() + " value " + option.getValue() + "\n").getBytes());
            }
        }
        stdin.write(("setoption USI_AnalyseMode value true\n").getBytes());
    }

    private void getReady() throws IOException {
        stdin.write("isready\n".getBytes());
        stdin.flush();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("readyok")) {
                stdin.write("usinewgame\n".getBytes());
                stdin.flush();
                return;
            }
        }
    }

    private void quitEngine() throws IOException {
        stdin.write("quit\n".getBytes());
        stdin.flush();
        process.destroy();
    }

    private void analysePosition(Game game, String sfen, String engineMove, JTable analysisTable, int moveNum) throws IOException {
        stdin.write(("position sfen " + sfen + " " + engineMove + "\n").getBytes());
        stdin.write(("go btime 0 wtime 0 byoyomi " + analysisTimePerMove*1000 + "\n").getBytes());
        stdin.flush();
        String line;
        List<String> lineList = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("bestmove")) {
                String bestLine = getBestLine(line, lineList);
                updateTableModel(analysisTable, getTableInsert(bestLine, moveNum, engineMove));
                game.getAnalysisPositionList().add(getBestLineList(sfen, bestLine));
                return;
            }
            lineList.add(line);
        }
    }
    
    private ArrayList<Position> getBestLineList(String sfen, String bestLine) {
        ArrayList<Position> result = new ArrayList<>();
        String currentSfen = sfen;
        for (String move : getBestLineMoveList(bestLine)) {
            Position position = getPosition(currentSfen, move);
            result.add(position);
            currentSfen  = position.getGameSFEN();
        }
        return result;
    }
    
    private Position getPosition(String sfen, String move) {
        Board board  = SFENParser.parse(sfen);
        executeMove(board, move);
        Notation notation = new Notation();
        return new Position(SFENParser.getSFEN(board), board.getSource(), board.getDestination(), notation);
    } 
    
    private Board executeMove(Board board, String move) {
        Coordinate thisDestination;
        Coordinate thisSource;
        
        thisDestination = getDestinationCoordinate(move);
        if (isDrop(move)) {
            thisSource = null;
            if (thisDestination != null) {
                executeDropMove(board, thisDestination, move);
            }
        } else {
            thisSource = getSourceCoordinate(move);
            if (thisSource != null && thisDestination != null) {
                executeRegularMove(board, thisSource, thisDestination, move);
            }
        }
        board.setNextTurn(ParserUtils.switchTurn(board.getNextTurn()));
        board.setSource(thisSource);
        board.setDestination(thisDestination);
        board.setMoveCount(board.getMoveCount()+1);
        return board;
    }
    
    private static void removePieceInHand(Koma.Type komaType, Board board) {
        if (board.getInHandKomaMap().get(komaType) == 1) {
            board.getInHandKomaMap().remove(komaType);
        } else {
            board.getInHandKomaMap().put(komaType, board.getInHandKomaMap().get(komaType) - 1);
        }
    }
        
    private static void executeDropMove(Board board, Coordinate thisDestination, String move) {
        Koma koma;
        try {
            koma = ParserUtils.getDropKoma(move.substring(0, 1), board.getNextTurn());
            if (koma == null) {
                return;
            }
            putKoma(board, thisDestination, koma);
            removePieceInHand(koma.getType(), board);
        } catch (Exception ex) {
            Logger.getLogger(GameAnalyser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    private void executeRegularMove(Board board, Coordinate thisSource, Coordinate thisDestination, String move) {
        if (getKoma(board, thisDestination) != null) {
            Koma thisKoma = getKoma(board, thisDestination);
            if (thisKoma != null) {
                ParserUtils.addPieceToInHand(getKoma(board, thisDestination), board);
            }
        }
        Koma thisKoma = getKoma(board, thisSource);
        if (thisKoma != null) {
            putKoma(board, thisDestination, promCheck(thisKoma, move));
        }
        putKoma(board, thisSource, null); 
    }
    
    private static Koma promCheck(Koma koma, String move) {
        if (!isPromoted(move)) {
            return koma;
        } else {
            return ParserUtils.promoteKoma(koma.getType());
        }
    }
    

    private static boolean isPromoted(String move) {
        return move.charAt(move.length()-1) == '+';
    }
    
    public static void putKoma(Board board, Coordinate coords, Koma koma) {
        if (coords != null) {
            board.getMasu()[9 - coords.getX()][coords.getY() - 1] = koma;
        }
    }
    
    public static Koma getKoma(Board board, Coordinate coords) {
        if (coords == null) {
            return null;
        }
        return board.getMasu()[9 - coords.getX()][coords.getY() - 1];
    }
    
    private boolean isDrop(String move) {
        return move.contains("*");
    }
    
    private Coordinate getDestinationCoordinate(String move) {
        Coordinate result = new Coordinate();
        if (move.contains("*")) {
            int starIndex = move.indexOf("*");
            result.setX(move.charAt(starIndex+1)-49+1);
            result.setY(move.charAt(starIndex+2)-97+1);   
        }  else {
            result.setX(move.charAt(2)-49+1);
            result.setY(move.charAt(3)-97+1);   
        }
        if (result.getX() > 9 || result.getX() < 1 || result.getY() > 9 || result.getY() < 1) {
            return null;
        }
        return result;
    }
    
    private Coordinate getSourceCoordinate(String move) {
        Coordinate result = new Coordinate();
        result.setX(move.charAt(0)-49+1);
        result.setY(move.charAt(1)-97+1);  
        if (result.getX() > 9 || result.getX() < 1 || result.getY() > 9 || result.getY() < 1) {
            return null;
        }

        return result;
    }
    
    private String getBestLine(String line, List<String> lineList) {
        String bestMove = line.split(" ")[1];
        for (int j = lineList.size() - 1; j >= 0; j--) {
            if (lineList.get(j).contains("pv " + bestMove)) {
                return lineList.get(j);
            }
        }
        return "";
    }

    private Object[] getTableInsert(String lastLine, int moveNum, String engineMove) {
        boolean lower = false;
        boolean upper = false;
        boolean foundPV = false;
        String score = "";
        String pvStr = "";
        String[] splitLine = lastLine.split(" ");
        for (int i = 0; i < splitLine.length; i++) {
            if (!foundPV) {
                switch (splitLine[i]) {
                    case "lowerbound":
                        lower = true;
                        break;
                    case "upperbound":
                        upper = true;
                        break;
                    case "cp":
                        score = getScore(moveNum, splitLine[i+1]);
                        opinion = compareScore(moveNum, lastScore,  score);
                        lastScore = String.copyValueOf(score.toCharArray());
                        break;
                    case "mate":
                        score = getMateScore(moveNum, splitLine[i+1]);
                        break;
                    case "pv":
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j = i+1; j < splitLine.length; j++ ) {
                            stringBuilder.append(splitLine[j]);
                            stringBuilder.append(" ");
                        }
                        pvStr = stringBuilder.toString().trim();
                        foundPV = true;
                        break;
                    default:
                }       
            }
        }

        String lowUp = getLowUpString(lower, upper);

        return new Object[]{moveNum + " " + engineMove, "", score, lowUp, pvStr};
    }
    
    private List<String> getBestLineMoveList(String line) {
        List<String> result = new ArrayList<>();
        boolean foundPV = false;
        String[] splitLine = line.split(" ");
        for (int i = 0; i < splitLine.length; i++) {
            if (!foundPV && splitLine[i].contentEquals("pv")) {
                for (int j = i+1; j < splitLine.length; j++ ) {
                    if (!splitLine[j].contains("%")) {
                        result.add(splitLine[j]);
                    }
                }
                foundPV = true;
            }
        }
        return result;
    }
    
    private String compareScore(int moveNum, String lastScore, String score) {
        int lastScoreVal;
        int scoreVal;
        
        if (lastScore.isEmpty() || score.isEmpty()) {
            return "";
        }
                
        lastScoreVal = getLastScoreVal();
                
        scoreVal = getScoreVal(score);
                
        if ((scoreVal >= analysisIgnoreThreshold && lastScoreVal >= analysisIgnoreThreshold) || (scoreVal <= -analysisIgnoreThreshold && lastScoreVal <= -analysisIgnoreThreshold)) {
            return "";
        }
        
        return assessScores(moveNum, lastScoreVal, scoreVal);
    }
    
    private String assessScores(int moveNum, int lastScoreVal, int scoreVal) {
        // We are finding the opinion for the PREVIOUS move.
        if (moveNum % 2 != 0) {
            // This move is for sente.
            if (scoreVal - lastScoreVal > analysisBlunderThreshold) {
                return "??";
            }
            if (scoreVal - lastScoreVal > analysisMistakeThreshold) {
                return "?";
            }
        } else {
            // This move is for gote.
            if (scoreVal - lastScoreVal < -analysisBlunderThreshold) {
                return "??";
            }
            if (scoreVal - lastScoreVal < -analysisMistakeThreshold) {
                return "?";
            }
        }
        return "";
    }
    
    private int getScoreVal(String score) {
        if (score.contains("+Mate")) {
            return -31111;
        } else if (score.contains("-Mate")) {
            return 31111;
        } else {
            return Integer.parseInt(score);
        }
    }
    
    private int getLastScoreVal() {
        if (lastScore.contains("+Mate")) {
            return 31111;
        } else if (lastScore.contains("-Mate")) {
            return -31111;
        } else {
            return Integer.parseInt(lastScore);
        }
    }
    
    private String getMateScore(int moveNum, String value) {
        int scoreVal;
        String score;
        if (moveNum % 2 != 0) {
            scoreVal = Integer.parseInt(value);
        } else {
            scoreVal = Integer.parseInt(value) * -1;
        }
        if (scoreVal > 0) {
            score = "+Mate:";
        } else {
            score = "-Mate:";
        }
        score += Math.abs(scoreVal);
        return score;
    }

    private String getScore(int moveNum, String value) {
        if (moveNum % 2 != 0) {
            return value;
        } else {
            return Integer.toString(Integer.parseInt(value) * -1);
        }
    }

    private String getLowUpString(boolean lower, boolean upper) {
        if (lower) {
            return "--";
        }
        if (upper) {
            return "++";
        }
        return "";
    }

    private void updateTableModel(JTable analysisTable, Object[] newRow) {
        DefaultTableModel analysisTableModel = (DefaultTableModel) analysisTable.getModel();
        try {
            java.awt.EventQueue.invokeAndWait(()
                    -> {
                if (!opinion.isEmpty()) {
                    analysisTableModel.setValueAt(opinion, analysisTableModel.getRowCount()-1, 1);
                    opinion = "";
                }
                analysisTableModel.addRow(newRow);
                analysisTable.scrollRectToVisible(analysisTable.getCellRect(analysisTableModel.getRowCount() - 1, 0, true));
            });
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException ex) {
            Logger.getLogger(GameAnalyser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
