package com.chadfield.shogiexplorer.main;

import com.chadfield.shogiexplorer.objects.Engine;
import com.chadfield.shogiexplorer.objects.EngineOption;
import com.chadfield.shogiexplorer.objects.Game;
import com.chadfield.shogiexplorer.objects.Position;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
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

    Process process;
    OutputStream stdin;
    InputStream stdout;
    BufferedReader bufferedReader;
    String lastScore = "";
    String opinion = "";

    public void analyse(Game game, Engine engine, JList<String> moveList, JTable analysisTable) throws IOException {
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
                analysePosition(lastSFEN, engineMove, analysisTable, count);
            }
            lastSFEN = sfen;
            sfen = position.getGameSFEN();
            engineMove = position.getEngineMove();
        }

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
        stdout = process.getInputStream();

        bufferedReader = new BufferedReader(new InputStreamReader(stdout));
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

    private void analysePosition(String sfen, String engineMove, JTable analysisTable, int moveNum) throws IOException {
        stdin.write(("position sfen " + sfen + " " + engineMove + "\n").getBytes());
        stdin.write("go btime 0 wtime 0 byoyomi 3000\n".getBytes());
        stdin.flush();
        String line;
        String lastLine = "";
        while ((line = bufferedReader.readLine()) != null) {
            //System.out.println(line);
            if (line.contains("bestmove")) {
                updateTableModel(analysisTable, getTableInsert(lastLine, moveNum, engineMove));
                return;
            }
            lastLine = line;
        }
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
    
    private String compareScore(int moveNum, String lastScore, String score) {
        int lastScoreVal;
        int scoreVal;
        
        if (lastScore.isEmpty() || score.isEmpty()) {
            return "";
        }
                
        lastScoreVal = getLastScoreVal();
                
        scoreVal = getScoreVal(score);
                
        if ((scoreVal > 1999 && lastScoreVal > 1999) || (scoreVal < -1999 && lastScoreVal < -1999)) {
            return "";
        }
        
        return assessScores(moveNum, lastScoreVal, scoreVal);
    }
    
    private String assessScores(int moveNum, int lastScoreVal, int scoreVal) {
        // We are finding the opinion for the PREVIOUS move.
        if (moveNum % 2 != 0) {
            // This move is for sente.
            if (scoreVal - lastScoreVal > 500) {
                return "??";
            }
            if (scoreVal - lastScoreVal > 250) {
                return "?";
            }
        } else {
            // This move is for gote.
            if (scoreVal - lastScoreVal < -500) {
                return "??";
            }
            if (scoreVal - lastScoreVal < -250) {
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
