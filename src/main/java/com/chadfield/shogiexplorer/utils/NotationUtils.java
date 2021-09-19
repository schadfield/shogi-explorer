package com.chadfield.shogiexplorer.utils;

import com.chadfield.shogiexplorer.main.KifParser;
import com.chadfield.shogiexplorer.objects.Board;
import com.chadfield.shogiexplorer.objects.Coordinate;
import com.chadfield.shogiexplorer.objects.Koma;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class NotationUtils {

    public static final String DROPPED = "打";
    public static final String DOWNWARD = "引";
    public static final String HORIZONTALLY = "寄";
    public static final String UPWARD = "上";
    public static final String FROM_RIGHT = "右";
    public static final String FROM_LEFT = "左";
    public static final String VERTICAL = "直";
    public static final String UPWARD_UD = "行";

    private static boolean onBoard(Coordinate coordinate) {
        return coordinate.getX() > 0 && coordinate.getX() < 10 && coordinate.getY() > 0 && coordinate.getY() < 10;
    }

    private static Coordinate getSourceOnVector(Board board, Coordinate destination, int deltaX, int deltaY, Koma.Type komaType) {
        Coordinate testCoordinate = new Coordinate(destination.getX() + deltaX, destination.getY() + deltaY);
        while (true) {
            if (!onBoard(testCoordinate)) {
                return null;
            }
            Koma koma = board.getMasu()[9 - testCoordinate.getX()][testCoordinate.getY() - 1];
            if (koma != null) {
                if (koma.getType() == komaType) {
                    return testCoordinate;
                } else {
                    return null;
                }
            }
            testCoordinate.setX(testCoordinate.getX() + deltaX);
            testCoordinate.setY(testCoordinate.getY() + deltaY);
        }
    }

    private static int numWithSameY(Coordinate coordinate, List<Coordinate> coordinateList) {
        int count = 0;
        for (Coordinate thisCoordinate : coordinateList) {
            if (haveSameY(coordinate, thisCoordinate)) {
                count++;
            }
        }
        return count;
    }

    static boolean onRight(Coordinate firstCoordinate, Coordinate secondCoordinate, boolean isSente) {
        if (isSente) {
            return firstCoordinate.getX() < secondCoordinate.getX();
        } else {
            return firstCoordinate.getX() > secondCoordinate.getX();
        }
    }

    static boolean isAbove(Coordinate firstCoordinate, Coordinate secondCoordinate, boolean isSente) {
        if (isSente) {
            return firstCoordinate.getY() < secondCoordinate.getY();
        } else {
            return firstCoordinate.getY() > secondCoordinate.getY();
        }
    }

    private static String disXHI(Board board, Coordinate sourceCoordinate, Coordinate destinationCoordinate, Koma.Type komaType) {
        boolean isSente = komaType == Koma.Type.SHI;
        List<Coordinate> sourceList = getPossibleSources(board, destinationCoordinate, komaType);
        if (sourceList.size() > 1) {
            // There is ambiguity.
            if (isAbove(sourceCoordinate, destinationCoordinate, isSente)) {
                // The source is above the destination.
                return DOWNWARD;
            } else if (isBelow(sourceCoordinate, destinationCoordinate, isSente)) {
                // The source is below the destination.
                return UPWARD;
            } else {
                if (numWithSameY(sourceCoordinate, sourceList) > 1) {
                    if (onLeft(sourceCoordinate, destinationCoordinate, isSente)) {
                        // The source is left of the destination.
                        return FROM_LEFT;
                    } else {
                        return FROM_RIGHT;
                    }
                } else {
                    return HORIZONTALLY;
                }
            }
        }
        return "";
    }

    private static String disXGI(Board board, Coordinate sourceCoordinate, Coordinate destinationCoordinate, Koma.Type komaType) {
        boolean isSente = komaType == Koma.Type.SGI;
        List<Coordinate> sourceList = getPossibleSources(board, destinationCoordinate, komaType);
        if (sourceList.size() > 1) {
            // There is ambiguity.
            if (isAbove(sourceCoordinate, destinationCoordinate, isSente)) {
                // The source is one of two locations above the destination.
                if (numWithSameY(sourceCoordinate, sourceList) == 1) {
                    // Simple down case.
                    return DOWNWARD;
                } else if (onLeft(sourceCoordinate, destinationCoordinate, isSente)) {
                    // Above-left.
                    if (numWithSameX(sourceCoordinate, sourceList) == 1) {
                        return FROM_LEFT;
                    } else {
                        return FROM_LEFT + DOWNWARD;
                    }
                } else {
                    // Above-right.
                    if (numWithSameX(sourceCoordinate, sourceList) == 1) {
                        return FROM_RIGHT;
                    } else {
                        return FROM_RIGHT + DOWNWARD;
                    }
                }
            } else {
                // The source is one of three locations below the destination.
                return bottomThree(sourceCoordinate, destinationCoordinate, sourceList, isSente);
            }
        }
        return "";
    }

    private static String disXKXI(Board board, Coordinate sourceCoordinate, Coordinate destinationCoordinate, Koma.Type komaType) {
        boolean isSente = komaType == Koma.Type.SKI || komaType == Koma.Type.STO || komaType == Koma.Type.SNK || komaType == Koma.Type.SNY || komaType == Koma.Type.SNG;
        List<Coordinate> sourceList = getPossibleSources(board, destinationCoordinate, komaType);
        if (sourceList.size() > 1) {
            // There is ambiguity.
            if (isAbove(sourceCoordinate, destinationCoordinate, isSente)) {
                // The source is in the single location above the destination.
                return DOWNWARD;
            } else if (Objects.equals(sourceCoordinate.getY(), destinationCoordinate.getY())) {
                // The source is next to the destination.
                if (numWithSameY(sourceCoordinate, sourceList) == 1) {
                    // Simple horizontal case.
                    return HORIZONTALLY;
                } else if (onLeft(sourceCoordinate, destinationCoordinate, isSente)) {
                    // Above-left.
                    if (numWithSameX(sourceCoordinate, sourceList) == 1) {
                        return FROM_LEFT;
                    } else {
                        return FROM_LEFT + HORIZONTALLY;
                    }
                } else {
                    // Above-right.
                    if (numWithSameX(sourceCoordinate, sourceList) == 1) {
                        return FROM_RIGHT;
                    } else {
                        return FROM_RIGHT + HORIZONTALLY;
                    }
                }
            } else {
                // The source is one of three locations below the destination.
                return bottomThree(sourceCoordinate, destinationCoordinate, sourceList, isSente);
            }
        }
        return "";
    }

    private static boolean haveSameX(Coordinate first, Coordinate second) {
        return Objects.equals(first.getX(), second.getX());
    }

    private static String disXKE(Board board, Coordinate sourceCoordinate, Coordinate destinationCoordinate, Koma.Type komaType) {
        boolean isSente = komaType == Koma.Type.SKE;
        List<Coordinate> sourceList = getPossibleSources(board, destinationCoordinate, komaType);
        if (sourceList.size() > 1) {
            if (onLeft(sourceCoordinate, destinationCoordinate, isSente)) {
                return FROM_LEFT;
            } else {
                return FROM_RIGHT;
            }
        }
        return "";
    }


    static boolean isBelow(Coordinate firstCoordinate, Coordinate secondCoordinate, boolean isSente) {
        if (isSente) {
            return firstCoordinate.getY() > secondCoordinate.getY();
        } else {
            return firstCoordinate.getY() < secondCoordinate.getY();
        }
    }

    private static String disXUMRY(Board board, Coordinate sourceCoordinate, Coordinate destinationCoordinate, Koma.Type komaType) {
        boolean isSente = komaType == Koma.Type.SUM || komaType == Koma.Type.SRY;
        List<Coordinate> sourceList = getPossibleSources(board, destinationCoordinate, komaType);
        if (sourceList.size() > 1) {
            // There is ambiguity.
            Coordinate otherCoordinate = getOtherCoordinate(sourceCoordinate, sourceList);
            if (isBelow(sourceCoordinate, destinationCoordinate, isSente)) {
                // The source is below the destination.
                if (isBelow(otherCoordinate, destinationCoordinate, isSente)) {
                    // The other piece is also below the destination.
                    if (onLeft(sourceCoordinate, otherCoordinate, isSente)) {
                        // The source is to left of the other.
                        return FROM_LEFT;
                    } else {
                        return FROM_RIGHT;
                    }
                } else {
                    // Simple upwards.
                    return UPWARD;
                }
            } else if (isAbove(sourceCoordinate, destinationCoordinate, isSente)) {
                // The source is above the destination.
                if (isAbove(otherCoordinate, destinationCoordinate, isSente)) {
                    // The other piece is also above the destination.
                    if (onLeft(sourceCoordinate, otherCoordinate, isSente)) {
                        // The source is to left of the other.
                        return FROM_LEFT;
                    } else {
                        return FROM_RIGHT;
                    }
                } else {
                    // Simple downwards.
                    return DOWNWARD;
                }
            } else {
                // The source is level with the destination.
                if (numWithSameY(sourceCoordinate, sourceList) > 1) {
                    // Both possible pieces are level with the destination.
                    if (onLeft(sourceCoordinate, otherCoordinate, isSente)) {
                        // The source is to left of the other.
                        return FROM_LEFT;
                    } else {
                        return FROM_RIGHT;
                    }
                } else {
                    // Simple horizontal.
                    return HORIZONTALLY;
                }
            }
        }
        return "";
    }

    private static Coordinate getOtherCoordinate(Coordinate sourceCoordinate, List<Coordinate> sourceList) {
        if (!sourceCoordinate.sameValue(sourceList.get(0))) {
            return sourceList.get(0);
        } else {
            return sourceList.get(1);
        }
    }

    private static String disXKA(Board board, Coordinate sourceCoordinate, Coordinate destinationCoordinate, Koma.Type komaType) {
        boolean isSente = komaType == Koma.Type.SKA;
        List<Coordinate> sourceList = getPossibleSources(board, destinationCoordinate, komaType);
        if (sourceList.size() > 1) {
            // There is ambiguity.
            Coordinate otherCoordinate = getOtherCoordinate(sourceCoordinate, sourceList);
            if (isBelow(sourceCoordinate, destinationCoordinate, isSente)) {
                // The source is below the destination.
                if (isBelow(otherCoordinate, destinationCoordinate, isSente)) {
                    // The other piece is also below the destination.
                    if (onLeft(sourceCoordinate, otherCoordinate, isSente)) {
                        return FROM_LEFT;
                    } else {
                        return FROM_RIGHT;
                    }
                } else {
                    // Simple upward.
                    return UPWARD;
                }
            } else // The source is above the destination.
            if (isAbove(otherCoordinate, destinationCoordinate, isSente)) {
                // The other piece is also above the destination.
                if (onLeft(sourceCoordinate, otherCoordinate, isSente)) {
                    return FROM_LEFT;
                } else {
                    return FROM_RIGHT;
                }
            } else {
                // Simple downward.
                return DOWNWARD;
            }
        }
        return "";
    }

    private static List<Coordinate> getSourcesOnDiagonalVectors(Board board, Coordinate destination, Koma.Type komaType) {
        List<Coordinate> result = new ArrayList<>();
        Coordinate resultCoordinate;
        resultCoordinate = getSourceOnVector(board, destination, -1, -1, komaType);
        if (resultCoordinate != null) {
            result.add(resultCoordinate);
        }
        resultCoordinate = getSourceOnVector(board, destination, 1, -1, komaType);
        if (resultCoordinate != null) {
            result.add(resultCoordinate);
        }
        resultCoordinate = getSourceOnVector(board, destination, -1, 1, komaType);
        if (resultCoordinate != null) {
            result.add(resultCoordinate);
        }
        resultCoordinate = getSourceOnVector(board, destination, 1, 1, komaType);
        if (resultCoordinate != null) {
            result.add(resultCoordinate);
        }
        return result;
    }

    public static String getDisambiguation(Board board, Coordinate sourceCoordinate, Coordinate destinationCoordinate, Koma.Type komaType) {
        switch (komaType) {
            case SKE:
            case GKE:
                return disXKE(board, sourceCoordinate, destinationCoordinate, komaType);
            case SGI:
            case GGI:
                return disXGI(board, sourceCoordinate, destinationCoordinate, komaType);
            case SKI:
            case STO:
            case SNK:
            case SNY:
            case SNG:
            case GKI:
            case GTO:
            case GNK:
            case GNY:
            case GNG:
                return disXKXI(board, sourceCoordinate, destinationCoordinate, komaType);
            case SKA:
            case GKA:
                return disXKA(board, sourceCoordinate, destinationCoordinate, komaType);
            case SHI:
            case GHI:
                return disXHI(board, sourceCoordinate, destinationCoordinate, komaType);
            case SUM:
            case SRY:
            case GUM:
            case GRY:
                return disXUMRY(board, sourceCoordinate, destinationCoordinate, komaType);
            default:
                return "";
        }
    }

    public static List<Coordinate> getPossibleSources(Board board, Coordinate destination, Koma.Type komaType) {
        List<Coordinate> result = new ArrayList<>();
        Coordinate resultCoordinate;
        switch (komaType) {
            case SKE:
                return getSourcesForKoma(board, destination, new int[]{1, -1}, new int[]{2, 2}, komaType);
            case GKE:
                return getSourcesForKoma(board, destination, new int[]{1, -1}, new int[]{-2, -2}, komaType);
            case SGI:
                return getSourcesForKoma(board, destination, new int[]{1, 0, -1, 1, -1}, new int[]{1, 1, 1, -1, -1}, komaType);
            case GGI:
                return getSourcesForKoma(board, destination, new int[]{1, 0, -1, 1, -1}, new int[]{-1, -1, -1, 1, 1}, komaType);
            case SKI:
            case STO:
            case SNK:
            case SNY:
            case SNG:
                return getSourcesForKoma(board, destination, new int[]{1, 0, -1, 1, -1, 0}, new int[]{1, 1, 1, 0, 0, -1}, komaType);
            case GKI:
            case GTO:
            case GNK:
            case GNY:
            case GNG:
                return getSourcesForKoma(board, destination, new int[]{1, 0, -1, 1, -1, 0}, new int[]{-1, -1, -1, 0, 0, 1}, komaType);
            case SKY:
                resultCoordinate = getSourceOnVector(board, destination, 0, 1, komaType);
                if (resultCoordinate != null) {
                    result.add(resultCoordinate);
                }
                return result;
            case GKY:
                resultCoordinate = getSourceOnVector(board, destination, 0, -1, komaType);
                if (resultCoordinate != null) {
                    result.add(resultCoordinate);
                }
                return result;
            case SKA:
            case GKA:
                return getSourcesOnDiagonalVectors(board, destination, komaType);
            case SHI:
            case GHI:
                return getSourcesOnHorizontalVectors(board, destination, komaType);
            case SUM:
            case GUM:
                result = getSourcesForKoma(board, destination, new int[]{-1, 1, 0, 0}, new int[]{0, 0, -1, 1}, komaType);
                for (Coordinate thisCoordinate : getSourcesOnDiagonalVectors(board, destination, komaType)) {
                    result.add(thisCoordinate);
                }
                return result;
            case SRY:
            case GRY:
                result = getSourcesForKoma(board, destination, new int[]{-1, 1, 1, -1}, new int[]{-1, -1, 1, 1}, komaType);
                for (Coordinate thisCoordinate : getSourcesOnHorizontalVectors(board, destination, komaType)) {
                    result.add(thisCoordinate);
                }
                return result;
            default:
        }
        return result;
    }

    public static Coordinate getSourceCoordinate(String move) {
        Coordinate result = new Coordinate();
        result.setX(Integer.parseInt(move.substring(move.indexOf("(") + 1, move.indexOf("(") + 2)));
        result.setY(Integer.parseInt(move.substring(move.indexOf("(") + 1, move.indexOf(")"))) - result.getX() * 10);
        return result;
    }

    private static List<Coordinate> getSourcesOnHorizontalVectors(Board board, Coordinate destination, Koma.Type komaType) {
        List<Coordinate> result = new ArrayList<>();
        Coordinate resultCoordinate;
        resultCoordinate = getSourceOnVector(board, destination, -1, 0, komaType);
        if (resultCoordinate != null) {
            result.add(resultCoordinate);
        }
        resultCoordinate = getSourceOnVector(board, destination, 1, 0, komaType);
        if (resultCoordinate != null) {
            result.add(resultCoordinate);
        }
        resultCoordinate = getSourceOnVector(board, destination, 0, -1, komaType);
        if (resultCoordinate != null) {
            result.add(resultCoordinate);
        }
        resultCoordinate = getSourceOnVector(board, destination, 0, 1, komaType);
        if (resultCoordinate != null) {
            result.add(resultCoordinate);
        }
        return result;
    }

    private static String bottomThree(Coordinate sourceCoordinate, Coordinate destinationCoordinate, List<Coordinate> sourceList, boolean isSente) {
        if (numWithSameY(sourceCoordinate, sourceList) == 1) {
            // Simple up case.
            return UPWARD;
        } else if (onLeft(sourceCoordinate, destinationCoordinate, isSente)) {
            // Below-left.
            if (numWithSameX(sourceCoordinate, sourceList) == 1) {
                return FROM_LEFT;
            } else {
                return FROM_LEFT + UPWARD;
            }
        } else if (onRight(sourceCoordinate, destinationCoordinate, isSente)) {
            // Below-right.
            if (numWithSameX(sourceCoordinate, sourceList) == 1) {
                return FROM_RIGHT;
            } else {
                return FROM_RIGHT + UPWARD;
            }
        } else {
            // Directly below destination.
            return VERTICAL;
        }
    }

    private static boolean haveSameY(Coordinate first, Coordinate second) {
        return Objects.equals(first.getY(), second.getY());
    }

    static boolean onLeft(Coordinate firstCoordinate, Coordinate secondCoordinate, boolean isSente) {
        if (isSente) {
            return firstCoordinate.getX() > secondCoordinate.getX();
        } else {
            return firstCoordinate.getX() < secondCoordinate.getX();
        }
    }

    private static int numWithSameX(Coordinate coordinate, List<Coordinate> coordinateList) {
        int count = 0;
        for (Coordinate thisCoordinate : coordinateList) {
            if (haveSameX(coordinate, thisCoordinate)) {
                count++;
            }
        }
        return count;
    }

    public static List<Coordinate> getSourcesForKoma(Board board, Coordinate destination, int[] offsetX, int[] offsetY, Koma.Type komaType) {
        List<Coordinate> result = new ArrayList<>();
        for (int k = 0; k < offsetX.length; k++) {
            Coordinate testCoordinate = new Coordinate(destination.getX() + offsetX[k], destination.getY() + offsetY[k]);
            if (NotationUtils.onBoard(testCoordinate)) {
                Koma koma = KifParser.getKoma(board, testCoordinate);
                if (koma != null && koma.getType().equals(komaType)) {
                    result.add(testCoordinate);
                }
            }
        }
        return result;
    }

    public static String getDropNotation(Board board, Coordinate thisDestination, Koma.Type komaType) {
        if (!NotationUtils.getPossibleSources(board, thisDestination, komaType).isEmpty()) {
            return DROPPED;
        }
        return "";
    }
    
    private NotationUtils() {
        throw new IllegalStateException("Utility class");
    }
    
}
