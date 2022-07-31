/*
    Copyright © 2021, 2022 Stephen R Chadfield.

    This file is part of Shogi Explorer.

    Shogi Explorer is free software: you can redistribute it and/or modify it under the terms of the 
    GNU General Public License as published by the Free Software Foundation, either version 3 
    of the License, or (at your option) any later version.

    Shogi Explorer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
    without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
    See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with Shogi Explorer. 
    If not, see <https://www.gnu.org/licenses/>.
 */

package com.chadfield.shogiexplorer.objects;

import com.chadfield.shogiexplorer.main.AnalysisManager;
import com.chadfield.shogiexplorer.main.EngineManager;
import com.chadfield.shogiexplorer.main.SFENParser;
import com.chadfield.shogiexplorer.objects.Board.Turn;
import com.chadfield.shogiexplorer.utils.NotationUtils;
import com.chadfield.shogiexplorer.utils.ParserUtils;
import com.ibm.icu.text.Transliterator;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultIntervalXYDataset;

public class GameAnalyser {

    private Process process;
    private OutputStream stdin;
    private BufferedReader bufferedReader;
    private String lastScore = "";
    private String opinion = "";
    private int analysisTimePerMove;
    private static final int ANALYSIS_MISTAKE_THRESHOLD = 250;
    private static final int ANALYSIS_BLUNDER_THRESHOLD = 500;
    private static final int ANALYSIS_IGNORE_THRESHOLD = 2000;
    double[] x1Start;
    double[] x1;
    double[] x1End;
    double[] y1Start;
    double[] y1;
    double[] y1End;
    double[][] data1;
    double[] x2Start;
    double[] x2;
    double[] x2End;
    double[] y2Start;
    double[] y2;
    double[] y2End;
    double[][] data2;
    XYPlot plot;
    int range;
    String scoreStr;
    List<Integer> scoreList;
    JRadioButtonMenuItem graphView1;
    JRadioButtonMenuItem graphView2;
    JRadioButtonMenuItem graphView3;
    JButton haltAnalysisButton;
    JMenuItem stopAnalysisMenuItem;
    JMenuItem analyseGameMenuItem;
    JButton analyseGameToolbarButton;
    JMenuItem analysePositionMenuItem;
    JButton analysePositionToolbarButton;
    JMenuItem resumeAnalysisMenuItem;
    JButton resumeAnalysisToolbarButton;
    Transliterator trans = Transliterator.getInstance("Halfwidth-Fullwidth");
    Turn turn;
    int multiPV;
    int rowNum;
    boolean bestMove;
    boolean interrupted;
    private List<List<Position>> positionAnalysisList;

    public void analyse(Game game, Engine engine, JList<String> moveList, JTable analysisTable, AnalysisParameter analysisParam, AtomicBoolean analysing, XYPlot plot, boolean saveAnalysis, boolean resume) throws IOException {
        analysing.set(true);
        this.plot = plot;
        DefaultIntervalXYDataset plotDataset = (DefaultIntervalXYDataset) plot.getDataset();
        this.analysisTimePerMove = analysisParam.getAnalysisTimePerMove();
        this.graphView1 = analysisParam.getGraphView1();
        this.graphView2 = analysisParam.getGraphView2();
        this.graphView3 = analysisParam.getGraphView3();
        this.haltAnalysisButton = analysisParam.getHaltAnalysisButton();
        this.stopAnalysisMenuItem = analysisParam.getStopAnalysisMenuItem();
        this.analyseGameMenuItem = analysisParam.getAnalyseGameMenuItem();
        this.analyseGameToolbarButton = analysisParam.getAnalyseGameToolbarButton();
        this.analysePositionMenuItem = analysisParam.getAnalysePositionMenuItem();
        this.analysePositionToolbarButton = analysisParam.getAnalysePositionToolbarButton();
        this.resumeAnalysisMenuItem = analysisParam.getResumeAnalysisMenuItem();
        this.resumeAnalysisToolbarButton = analysisParam.getResumeAnalysisToolbarButton();
        this.x1Start = analysisParam.getX1Start();
        this.x1 = analysisParam.getX1();
        this.x1End = analysisParam.getX1End();
        this.y1Start = analysisParam.getY1Start();
        this.y1 = analysisParam.getY1();
        this.y1End = analysisParam.getY1End();
        this.x2Start = analysisParam.getX2Start();
        this.x2 = analysisParam.getX2();
        this.x2End = analysisParam.getX2End();
        this.y2Start = analysisParam.getY2Start();
        this.y2 = analysisParam.getY2();
        this.y2End = analysisParam.getY2End();
        initializeEngine(engine);
        initiateUSIProtocol();
        setOptions(engine);
        getReady();
        String engineMove = null;
        String japaneseMove = null;
        String sfen = null;
        String lastSFEN = null;
        int count = 1;
        int resumeCount = 1;
        if (resume) {
            resumeCount = analysisTable.getRowCount();
        }
        Coordinate lastDestination = null;
        Coordinate previousMoveDestination = null;
        if (!resume) {
            game.setAnalysisPositionList(new ArrayList<>());
        }
        scoreList = new ArrayList<>();
        game.isHandicap();

        if (game.isHandicap()) {
            turn = Turn.GOTE;
        } else {
            turn = Turn.SENTE;
        }

        interrupted = false;

        for (Position position : game.getPositionList()) {

            if (engineMove != null) {
                if (!resume || count > resumeCount) {
                    updateMoveList(moveList, count);
                    analysePosition(game, lastSFEN, engineMove, japaneseMove, analysisTable, plotDataset, count, turn, previousMoveDestination);
                }
                if (interrupted) {
                    break;
                }
                previousMoveDestination = lastDestination;
                count++;
                turn = ParserUtils.switchTurn(turn);
            }

            lastSFEN = sfen;
            sfen = position.getGameSFEN();
            engineMove = position.getNotation().getEngineMove();

            if (turn == Turn.SENTE) {
                japaneseMove = trans.transliterate(" ☗" + position.getNotation().getJapanese());
            } else {
                japaneseMove = trans.transliterate(" ☖" + position.getNotation().getJapanese());
            }

            lastDestination = position.getDestination();
        }

        if (!interrupted) {
            updateMoveList(moveList, count);
            analysePosition(game, lastSFEN, engineMove, japaneseMove, analysisTable, plotDataset, count, turn, previousMoveDestination);
            count++;
        }

        if (analysisTable.getRowCount() > 0) {
            analysisTable.setRowSelectionInterval(count - 2, count - 2);
            analysisTable.scrollRectToVisible(new Rectangle(analysisTable.getCellRect(count - 2, 0, true)));
        }

        quitEngine();
        haltAnalysisButton.setEnabled(false);
        stopAnalysisMenuItem.setEnabled(false);
        analyseGameMenuItem.setEnabled(true);
        analyseGameToolbarButton.setEnabled(true);
        analysePositionMenuItem.setEnabled(true);
        analysePositionToolbarButton.setEnabled(true);
        resumeAnalysisMenuItem.setEnabled(count < game.getPositionList().size() - 1);
        resumeAnalysisToolbarButton.setEnabled(resumeAnalysisMenuItem.isEnabled());
        analysisParam.setX1Start(x1Start);
        analysisParam.setX1(x1);
        analysisParam.setX1End(x1End);
        analysisParam.setY1Start(y1Start);
        analysisParam.setY1(y1);
        analysisParam.setY1End(y1End);
        analysisParam.setX2Start(x2Start);
        analysisParam.setX2(x2);
        analysisParam.setX2End(x2End);
        analysisParam.setY2Start(y2Start);
        analysisParam.setY2(y2);
        analysisParam.setY2End(y2End);

        if (saveAnalysis) {
            Analysis analysis = new Analysis();
            analysis.setAnalysisPositionList(game.getAnalysisPositionList());
            List<Object[]> tableRows = new ArrayList<>();
            DefaultTableModel analysisTableModel = (DefaultTableModel) analysisTable.getModel();
            for (int i = 0; i < analysisTableModel.getRowCount(); i++) {
                tableRows.add(new Object[]{
                    analysisTableModel.getValueAt(i, 0),
                    analysisTableModel.getValueAt(i, 1),
                    analysisTableModel.getValueAt(i, 2),
                    analysisTableModel.getValueAt(i, 3),
                    analysisTableModel.getValueAt(i, 4)
                });
            }
            analysis.setTableRows(tableRows);
            analysis.setScoreList(scoreList);
            AnalysisManager.save(analysis, analysisParam.getKifFile());
        }
        analysing.set(false);
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

    public void analysePosition(Engine engine, AnalysisParameter analysisParam, AtomicBoolean analysing, Position position, JTable positionAnalysisTable) throws IOException {
        analysing.set(true);
        bestMove = false;
        positionAnalysisList = new ArrayList<>();
        multiPV = 0;
        rowNum = 0;
        DefaultTableModel tableModel = (DefaultTableModel) positionAnalysisTable.getModel();
        Turn positionTurn = getTurn(position);
        String sfen = position.getGameSFEN();
        initializeEngine(engine);
        initiateUSIProtocol();
        setOptions(engine);
        // Infinite analysis does not play well with opening books.
        stdin.write(("setoption USI_OwnBook value false\n").getBytes());
        getReady();
        stdin.write(("position sfen " + position.getGameSFEN() + "\n").getBytes());
        stdin.write(("go infinite\n").getBytes());
        stdin.flush();
        String line;
        while (!bestMove && (line = bufferedReader.readLine()) != null) {
            if (Thread.interrupted()) {
                stdin.write(("stop\n").getBytes());
                stdin.flush();
            }
            updateTableModel(line, tableModel, positionTurn, sfen);
        }
        stdin.flush();
        analysisParam.setPositionAnalysisList(positionAnalysisList);
        analysing.set(false);
    }

    private Turn getTurn(Position position) {
        if (position.getGameSFEN().split(" ")[1].contentEquals("b")) {
            return Turn.SENTE;
        } else {
            return Turn.GOTE;
        }
    }

    private void updateTableModel(String line, DefaultTableModel tableModel, Turn positionTurn, String sfen) {
        if (!line.contains("bestmove")) {
            ArrayList<Position> pvPositionList = getPVPositionList(sfen, line, null);
            Object[] tableInsert = getTableInsert(line, positionTurn, pvPositionList, tableModel);
            positionAnalysisList.add(rowNum, pvPositionList);
            tableModel.insertRow(rowNum, tableInsert);
        } else {
            bestMove = true;
        }
    }

    private Object[] getTableInsert(String line, Turn positionTurn, ArrayList<Position> pvPositionList, DefaultTableModel tableModel) {
        turn = positionTurn;
        boolean lower = false;
        boolean upper = false;
        boolean foundPV = false;
        String depth = null;
        String seldepth = null;
        String nodes = null;
        int score;
        String[] splitLine = line.split(" ");
        for (int i = 0; i < splitLine.length; i++) {
            if (!foundPV) {
                switch (splitLine[i]) {
                    case "multipv" -> {
                        int thisMultiPv = Integer.parseInt(splitLine[i + 1]);
                        if (thisMultiPv < multiPV) {
                            positionAnalysisList.add(0, null);
                            tableModel.insertRow(0, new Object[]{});
                        }
                        multiPV = thisMultiPv;
                        rowNum = multiPV - 1;
                    }
                    case "depth" -> {
                        depth = splitLine[i + 1];
                    }
                    case "seldepth" -> {
                        seldepth = splitLine[i + 1]; 
                    }
                    case "nodes" -> {
                        nodes = splitLine[i + 1];
                    }
                    case "lowerbound" -> {
                        lower = true;
                    }
                    case "upperbound" -> {
                        upper = true;
                    }
                    case "cp" -> {
                        score = getScore(turn, splitLine[i + 1]);
                        scoreStr = Integer.toString(score);
                    }
                    case "mate" -> {
                        int mateNum = Integer.parseInt(splitLine[i + 1]);
                        if (mateNum > 0) {
                            score = 31111;
                        } else {
                            score = -31111;
                        }
                        if (turn == Turn.GOTE) {
                            score = -score;
                        }
                        if (score > 0) {
                            scoreStr = "+Mate:" + Math.abs(mateNum);
                        } else {
                            scoreStr = "-Mate:" + Math.abs(mateNum);
                        }
                    }
                    case "pv" -> {
                        foundPV = true;
                    }
                    default -> {
                        // Unwanted element.
                    }
                }
            }
        }

        StringBuilder pvBuilder = new StringBuilder("");

        for (Position position : pvPositionList) {
            pvBuilder.append(position.getNotation().getJapanese());
            pvBuilder.append("\u3000");
        }

        String pvStr = pvBuilder.toString().trim();
        String lowUp = getLowUpString(lower, upper);

        return new Object[]{depth + "/" + seldepth, nodes, scoreStr, lowUp, pvStr};
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

    private void analysePosition(Game game, String sfen, String engineMove, String japaneseMove, JTable analysisTable, DefaultIntervalXYDataset plotDataset, int moveNum, Turn turn, Coordinate previousMoveDestination) throws IOException {
        stdin.write(("position sfen " + sfen + " " + engineMove + "\n").getBytes());
        stdin.write(("go movetime " + analysisTimePerMove * 1000 + "\n").getBytes());
        stdin.flush();
        String line;
        List<String> lineList = new ArrayList<>();
        while (!interrupted && (line = bufferedReader.readLine()) != null) {
            if (Thread.interrupted()) {
                stdin.write("stop\n".getBytes());
                stdin.flush();
                interrupted = true;
            }
            if (line.contains("bestmove")) {
                String bestLine = getBestLine(line, lineList);
                ArrayList<Position> pvPositionList = getPVPositionList(sfen, bestLine, previousMoveDestination);
                updateTableModel(analysisTable, getTableInsert(bestLine, moveNum, turn, japaneseMove, pvPositionList, plotDataset));
                game.getAnalysisPositionList().add(pvPositionList);
                return;
            }
            lineList.add(line);
        }
    }

    private ArrayList<Position> getPVPositionList(String sfen, String bestLine, Coordinate previousMoveDestination) {
        ArrayList<Position> result = new ArrayList<>();
        String currentSfen = sfen;
        Coordinate thisPreviousMoveDestination = previousMoveDestination;
        for (String move : getBestLineMoveList(bestLine)) {
            Position position = getPosition(currentSfen, move, thisPreviousMoveDestination);
            result.add(position);
            currentSfen = position.getGameSFEN();
            thisPreviousMoveDestination = position.getDestination();
        }
        return result;
    }

    private Position getPosition(String sfen, String move, Coordinate lastDestination) {
        Board board = SFENParser.parse(sfen);
        Notation notation = executeMove(board, move, lastDestination);
        return new Position(SFENParser.getSFEN(board), board.getSource(), board.getDestination(), notation);
    }

    private Notation executeMove(Board board, String move, Coordinate lastDestination) {
        Coordinate thisDestination;
        Coordinate thisSource;
        Koma.Type sourceKomaType;
        boolean isDrop = isDrop(move);
        String disambiguation;

        thisDestination = getDestinationCoordinate(move);
        if (thisDestination == null) {
            Notation notation = new Notation();
            notation.setJapanese(move);
            return notation;
        }
        if (isDrop) {
            thisSource = null;
            Koma sourceKoma = ParserUtils.getDropKoma(move.substring(0, 1), board.getNextTurn());
            sourceKomaType = sourceKoma.getType();
            disambiguation = NotationUtils.getDropNotation(board, thisDestination, sourceKomaType);
            executeDropMove(board, thisDestination, sourceKoma);
        } else {
            thisSource = getSourceCoordinate(move);
            Koma sourceKoma = getKoma(board, thisSource);
            sourceKomaType = sourceKoma.getType();
            disambiguation = NotationUtils.getDisambiguation(board, thisSource, thisDestination, sourceKomaType);
            executeRegularMove(board, thisSource, thisDestination, sourceKoma, move);
        }

        board.setNextTurn(ParserUtils.switchTurn(board.getNextTurn()));
        board.setSource(thisSource);
        board.setDestination(thisDestination);
        board.setMoveCount(board.getMoveCount() + 1);

        Notation notation = new Notation();
        notation.setJapanese(trans.transliterate(getNotation(board, lastDestination, sourceKomaType, isDrop, move, disambiguation)));

        return notation;
    }

    private static String getNotation(Board board, Coordinate lastDestination, Koma.Type sourceKomaType, boolean isDrop, String move, String disambiguation) {
        StringBuilder result = new StringBuilder("");
        if (board.getNextTurn() == Board.Turn.GOTE) {
            result.append("☗");
        } else {
            result.append("☖");
        }

        Coordinate destination = board.getDestination();
        if (lastDestination != null && destination.sameValue(lastDestination)) {
            result.append(NotationUtils.SAME);
        } else {
            result.append(NotationUtils.getJapaneseCoordinate(destination));
        }

        result.append(NotationUtils.getKomaKanji(sourceKomaType));
        result.append(disambiguation);
        if (!isDrop && isPromoted(move)) {
            result.append(NotationUtils.PROMOTED);
        }
        return result.toString();
    }

    private static void removePieceInHand(Koma.Type komaType, Board board) {
        if (board.getInHandKomaMap().get(komaType) == 1) {
            board.getInHandKomaMap().remove(komaType);
        } else {
            board.getInHandKomaMap().put(komaType, board.getInHandKomaMap().get(komaType) - 1);
        }
    }

    private static void executeDropMove(Board board, Coordinate thisDestination, Koma sourceKoma) {
        try {
            putKoma(board, thisDestination, sourceKoma);
            removePieceInHand(sourceKoma.getType(), board);
        } catch (Exception ex) {
            Logger.getLogger(GameAnalyser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void executeRegularMove(Board board, Coordinate thisSource, Coordinate thisDestination, Koma sourceKoma, String move) {
        Koma destinationKoma = getKoma(board, thisDestination);
        if (destinationKoma != null) {
            ParserUtils.addPieceToInHand(destinationKoma, board);
        }
        putKoma(board, thisDestination, promCheck(sourceKoma, move));
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
        return move.charAt(move.length() - 1) == '+';
    }

    public static void putKoma(Board board, Coordinate coords, Koma koma) {
        board.getMasu()[9 - coords.getX()][coords.getY() - 1] = koma;
    }

    public static Koma getKoma(Board board, Coordinate coords) {
        return board.getMasu()[9 - coords.getX()][coords.getY() - 1];
    }

    private boolean isDrop(String move) {
        return move.contains("*");
    }

    private Coordinate getDestinationCoordinate(String move) {
        Coordinate result = new Coordinate();
        if (move.contains("*")) {
            int starIndex = move.indexOf("*");
            result.setX(move.charAt(starIndex + 1) - 49 + 1);
            result.setY(move.charAt(starIndex + 2) - 97 + 1);
        } else {
            result.setX(move.charAt(2) - 49 + 1);
            result.setY(move.charAt(3) - 97 + 1);
        }
        if (result.getX() > 9 || result.getX() < 1 || result.getY() > 9 || result.getY() < 1) {
            return null;
        }
        return result;
    }

    private Coordinate getSourceCoordinate(String move) {
        Coordinate result = new Coordinate();
        result.setX(move.charAt(0) - 49 + 1);
        result.setY(move.charAt(1) - 97 + 1);
        if (result.getX() > 9 || result.getX() < 1 || result.getY() > 9 || result.getY() < 1) {
            return null;
        }

        return result;
    }

    private String getBestLine(String line, List<String> lineList) {
        String bestMoveStr = line.split(" ")[1];
        int lineListSize = lineList.size();
        for (int j = lineListSize - 1; j >= 0; j--) {
            if (lineList.get(j).contains("pv " + bestMoveStr)) {
                return lineList.get(j);
            }
        }
        return lineList.get(lineListSize - 1);
    }

    private Object[] getTableInsert(String lastLine, int moveNum, Turn turn, String japaneseMove, ArrayList<Position> pvPositionList, DefaultIntervalXYDataset plotDataset) {
        boolean lower = false;
        boolean upper = false;
        boolean foundPV = false;
        Integer score = null;
        String[] splitLine = lastLine.split(" ");
        for (int i = 0; i < splitLine.length; i++) {
            if (!foundPV) {
                switch (splitLine[i]) {
                    case "lowerbound" -> {
                        lower = true;
                    }
                    case "upperbound" -> {
                        upper = true;
                    }
                    case "cp" -> {
                        score = getScore(turn, splitLine[i + 1]);
                        processScore(score, moveNum, turn, plotDataset);
                    }
                    case "mate" -> {
                        int mateNum = Integer.parseInt(splitLine[i + 1]);
                        if (mateNum > 0) {
                            score = 31111;
                        } else {
                            score = -31111;
                        }
                        if (turn == Turn.GOTE) {
                            score = -score;
                        }
                        processScore(score, moveNum, turn, plotDataset);
                        if (score > 0) {
                            scoreStr = "+Mate:" + Math.abs(mateNum);
                        } else {
                            scoreStr = "-Mate:" + Math.abs(mateNum);
                        }
                    }
                    case "pv" -> {
                        foundPV = true;
                    }
                    default -> {
                        // Unwanted element.
                    }
                }
            }
        }

        scoreList.add(score);

        StringBuilder pvBuilder = new StringBuilder("");

        for (Position position : pvPositionList) {
            pvBuilder.append(position.getNotation().getJapanese());
            pvBuilder.append("\u3000");
        }

        String pvStr = pvBuilder.toString().trim();

        String lowUp = getLowUpString(lower, upper);

        return new Object[]{moveNum + japaneseMove, "", scoreStr, lowUp, pvStr};
    }

    private void processScore(int score, int moveNum, Turn turn, DefaultIntervalXYDataset plotDataset) {
        int testRange = Math.abs(score);
        int newRange;
        JRadioButtonMenuItem thisItem;
        if (testRange > 2000) {
            newRange = 3000;
            thisItem = graphView3;
        } else if (testRange > 1000) {
            newRange = 2000;
            thisItem = graphView2;
        } else {
            newRange = 1000;
            thisItem = graphView1;
        }
        if (newRange > range) {
            range = newRange;
            thisItem.setSelected(true);
            plot.getRangeAxis().setRange(-range, range);
        }

        scoreStr = Integer.toString(score);
        opinion = compareScore(lastScore, scoreStr);
        lastScore = String.copyValueOf(scoreStr.toCharArray());

        if (moveNum < 50) {
            plot.getDomainAxis().setRange(0, 50);
        } else if (moveNum == 50) {
            plot.getDomainAxis().setAutoRange(true);
        }
        if (turn == Turn.SENTE) {
            x1Start = arrayAppend(x1Start, moveNum - 1.0);
            x1 = arrayAppend(x1, moveNum - 0.5);
            x1End = arrayAppend(x1End, moveNum);
            y1Start = arrayAppend(y1Start, 0);
            y1 = arrayAppend(y1, score);
            y1End = arrayAppend(y1End, 0);
            data1 = new double[][]{x1, x1Start, x1End, y1, y1Start, y1End};
            plotDataset.addSeries("S", data1);
        } else {
            x2Start = arrayAppend(x2Start, moveNum - 1.0);
            x2 = arrayAppend(x2, moveNum - 0.5);
            x2End = arrayAppend(x2End, moveNum);
            y2Start = arrayAppend(y2Start, 0);
            y2 = arrayAppend(y2, score);
            y2End = arrayAppend(y2End, 0);
            data2 = new double[][]{x2, x2Start, x2End, y2, y2Start, y2End};
            plotDataset.addSeries("G", data2);
        }
    }

    private double[] arrayAppend(double[] array, double value) {
        double[] result = Arrays.copyOf(array, array.length + 1);
        result[result.length - 1] = value;
        return result;
    }

    private List<String> getBestLineMoveList(String line) {
        List<String> result = new ArrayList<>();
        boolean foundPV = false;
        String[] splitLine = line.split(" ");
        for (int i = 0; i < splitLine.length; i++) {
            if (!foundPV && splitLine[i].contentEquals("pv")) {
                for (int j = i + 1; j < splitLine.length; j++) {
                    if (!splitLine[j].contains("%")) {
                        result.add(splitLine[j]);
                    }
                }
                foundPV = true;
            }
        }
        return result;
    }

    private String compareScore(String lastScore, String score) {
        int lastScoreVal;
        int scoreVal;

        if (lastScore.isEmpty() || score.isEmpty()) {
            return "";
        }

        lastScoreVal = getLastScoreVal();

        scoreVal = getScoreVal(score);

        if ((scoreVal >= ANALYSIS_IGNORE_THRESHOLD && lastScoreVal >= ANALYSIS_IGNORE_THRESHOLD) || (scoreVal <= -ANALYSIS_IGNORE_THRESHOLD && lastScoreVal <= -ANALYSIS_IGNORE_THRESHOLD)) {
            return "";
        }

        return assessScores(turn, lastScoreVal, scoreVal);
    }

    private String assessScores(Turn turn, int lastScoreVal, int scoreVal) {
        // We are finding the opinion for the PREVIOUS move.
        if (turn == Turn.SENTE) {
            if (scoreVal - lastScoreVal > ANALYSIS_BLUNDER_THRESHOLD) {
                return "??";
            }
            if (scoreVal - lastScoreVal > ANALYSIS_MISTAKE_THRESHOLD) {
                return "?";
            }
        } else {
            if (scoreVal - lastScoreVal < -ANALYSIS_BLUNDER_THRESHOLD) {
                return "??";
            }
            if (scoreVal - lastScoreVal < -ANALYSIS_MISTAKE_THRESHOLD) {
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

    private int getScore(Turn turn, String value) {
        if (turn == Turn.SENTE) {
            return Integer.parseInt(value);
        } else {
            return Integer.parseInt(value) * -1;
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
                    analysisTableModel.setValueAt(opinion, analysisTableModel.getRowCount() - 1, 1);
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
