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

package com.chadfield.shogiexplorer.main;

import com.chadfield.shogiexplorer.objects.Board;
import com.chadfield.shogiexplorer.objects.Coordinate;
import com.chadfield.shogiexplorer.objects.Koma;
import java.util.HashMap;
import java.util.Map;

public class PositionEditor {

    private PositionEditor() {
        throw new IllegalStateException("Utility class");
    }

    public static void processLeft(Board board) {
        int x = board.getEdit().getX();
        int y = board.getEdit().getY();
        if (x < 9) {
            board.setEdit(new Coordinate(++x, y));
        } else {
            if (y > 1) {
                board.setEdit(new Coordinate(1, --y));
            } else {
                board.setEdit(new Coordinate(1, 9));
            }
        }
    }

    public static void processRight(Board board) {
        int x = board.getEdit().getX();
        int y = board.getEdit().getY();
        if (x > 1) {
            board.setEdit(new Coordinate(--x, y));
        } else {
            if (y < 9) {
                board.setEdit(new Coordinate(9, ++y));
            } else {
                board.setEdit(new Coordinate(9, 1));
            }
        }
    }

    public static void processUp(Board board) {
        if (board.getEdit() != null) {
            int x = board.getEdit().getX();
            int y = board.getEdit().getY();
            if (y > 1) {
                board.setEdit(new Coordinate(x, --y));
            }
        }
    }

    public static void processDown(Board board) {
        if (board.getEdit() != null) {
            int x = board.getEdit().getX();
            int y = board.getEdit().getY();
            if (y < 9) {
                board.setEdit(new Coordinate(x, ++y));
            }
        }
    }

    public static boolean processKey(char thisChar, Board board, boolean modified, int komadaiCount) {
        Koma.Type komaType = getKomaType(thisChar, modified && (komadaiCount == -1));
        if (komaType != null) {
            if (komadaiCount != -1) {
                updateInHand(board, komaType, komadaiCount);
            } else {
                KifParser.putKoma(board, board.getEdit(), new Koma(komaType));
            }
            return true;
        }
        return false;
    }

    private static void updateInHand(Board board, Koma.Type komaType, int komadaiCount) {
        if (komadaiCount == 0) {
            board.getInHandKomaMap().remove(komaType);
        } else {
            board.getInHandKomaMap().put(komaType, komadaiCount);
        }
    }

    private static Koma.Type getKomaType(char thisChar, boolean modified) {

        Map<Character, Koma.Type> komaTypeMap = new HashMap<>();

        komaTypeMap.put('p', Koma.Type.SFU);
        komaTypeMap.put('l', Koma.Type.SKY);
        komaTypeMap.put('n', Koma.Type.SKE);
        komaTypeMap.put('s', Koma.Type.SGI);
        komaTypeMap.put('g', Koma.Type.SKI);
        komaTypeMap.put('b', Koma.Type.SKA);
        komaTypeMap.put('r', Koma.Type.SHI);
        komaTypeMap.put('k', Koma.Type.SGY);
        komaTypeMap.put('P', Koma.Type.GFU);
        komaTypeMap.put('L', Koma.Type.GKY);
        komaTypeMap.put('N', Koma.Type.GKE);
        komaTypeMap.put('S', Koma.Type.GGI);
        komaTypeMap.put('G', Koma.Type.GKI);
        komaTypeMap.put('B', Koma.Type.GKA);
        komaTypeMap.put('R', Koma.Type.GHI);
        komaTypeMap.put('K', Koma.Type.GOU);

        Map<Character, Koma.Type> komaTypeMapModified = new HashMap<>();

        komaTypeMapModified.put('p', Koma.Type.STO);
        komaTypeMapModified.put('l', Koma.Type.SNY);
        komaTypeMapModified.put('n', Koma.Type.SNK);
        komaTypeMapModified.put('s', Koma.Type.SNG);
        komaTypeMapModified.put('g', Koma.Type.SKI);
        komaTypeMapModified.put('b', Koma.Type.SUM);
        komaTypeMapModified.put('r', Koma.Type.SRY);
        komaTypeMapModified.put('k', Koma.Type.SOU);
        komaTypeMapModified.put('P', Koma.Type.GTO);
        komaTypeMapModified.put('L', Koma.Type.GNY);
        komaTypeMapModified.put('N', Koma.Type.GNK);
        komaTypeMapModified.put('S', Koma.Type.GNG);
        komaTypeMapModified.put('G', Koma.Type.GKI);
        komaTypeMapModified.put('B', Koma.Type.GUM);
        komaTypeMapModified.put('R', Koma.Type.GRY);
        komaTypeMapModified.put('K', Koma.Type.GGY);

        if (modified) {
            return komaTypeMapModified.get(thisChar);
        } else {
            return komaTypeMap.get(thisChar);
        }
    }

}
