package student_player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurTile;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurDestroy;

public class MyTools {
  public static double getSomething() {
    return Math.random();
  }

  public static final String[] blindAlleyCard = {"Tile:1", "Tile:2", "Tile:3", "Tile:4", "Tile:5",
      "Tile:7", "Tile:11", "Tile:12", "Tile:13", "Tile:14", "Tile:15"};
  public static final List<String> blindAlleyCardList = Arrays.asList(blindAlleyCard);
  public static final String[] normalCard = {"Tile:0", "Tile:6", "Tile:8", "Tile:9", "Tile:10"};
  public static final List<String> normalCardList = Arrays.asList(normalCard);
  public static final String[] toolCard = {"Bonus", "Destroy", "Malus", "Map"};
  public static final List<String> toolCardList = Arrays.asList(toolCard);



  /**
   * This method returns the index of the nugget card
   * 
   * @param boardState
   * @return nugget card index
   */
  public static int getNuggetPositionIndex(SaboteurBoardState boardState) {
    for (int i = 0; i < 3; i++) {
      SaboteurTile hiddenCard = boardState
          .getHiddenBoard()[SaboteurBoardState.hiddenPos[i][0]][SaboteurBoardState.hiddenPos[i][1]];
      if (hiddenCard.getIdx().equals("nugget")) {
        return i;
      }
    }
    return -1; // error
  }


  /**
   * This method return the index of a hidden tile
   * 
   * @param boardState
   * @return hidden card index
   */
  public static int nexthiddenTileIndex(SaboteurBoardState boardState) {
    for (int i = 0; i < 3; i++) {
      SaboteurTile hiddenCard = boardState
          .getHiddenBoard()[SaboteurBoardState.hiddenPos[i][0]][SaboteurBoardState.hiddenPos[i][1]];
      if (hiddenCard.getIdx().equals("8")) {
        return i;
      }
    }
    return -1; // error
  }

  /**
   * This method calculates the straight line distance from current tile to goal using the
   * coordinates
   * 
   * @param move
   * @param goalTile
   * @return calculated distance
   */
  private static int calculateDistance(SaboteurMove move, int[] goalTile) {
    int[] curTile = {move.getPosPlayed()[0], move.getPosPlayed()[1]};
    int distance = Math.abs(curTile[0] - goalTile[0]) + Math.abs(curTile[1] - goalTile[1]);
    return distance;
  }

  /**
   * Check if the card is the blindAlley card
   * 
   * @param boardState
   * @param index
   * @return
   */
  public static boolean isBlindAlleyCard(SaboteurBoardState boardState, int index) {
    if (blindAlleyCardList.contains(boardState.getCurrentPlayerCards().get(index).getName()))
      return true;
    return false;
  }

  /**
   * Checks if there's a card with given name in hand
   * 
   * @param cardInHand
   * @param name
   * @return boolean
   */
  public static boolean hasCardInHand(ArrayList<SaboteurCard> cardInHand, String name) {
    for (int i = 0; i < cardInHand.size(); i++) {
      if (cardInHand.get(i).getName().equals(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Assume we are not experiencing Malus, we choose the next best move and return its move index
   * 
   * @param nuggetIndex
   * @param legalMoves
   * @param boardState
   * @return int
   */

  public static int chooseNextBestMove(int nuggetIndex, ArrayList<SaboteurMove> legalMoves,
      SaboteurBoardState boardState) {
    int minDistance = Integer.MAX_VALUE - 1;
    int index = -1;
    if (nuggetIndex == -1) { // if the destination is unknown, check if we can use map, if not,
                             // return -1
      if (hasCardInHand(boardState.getCurrentPlayerCards(), "Map"))
        return -2;
      else
        return -1;
    } else { // if the destination is known, choose the move that can shorten the distance, if not,
             // return -1
      int[] destinationPos = {SaboteurBoardState.hiddenPos[nuggetIndex][0],
          SaboteurBoardState.hiddenPos[nuggetIndex][1]};
      for (int i = 0; i < legalMoves.size(); i++) {
        if (normalCardList.contains(legalMoves.get(i).getCardPlayed().getName())) {
          int[] legalMovePos =
              {legalMoves.get(i).getPosPlayed()[0], legalMoves.get(i).getPosPlayed()[1]};
          int distance = Math.abs(destinationPos[0] - legalMovePos[0])
              + Math.abs(destinationPos[1] - legalMovePos[1]);
          if (distance < minDistance) {
            minDistance = distance;
            index = i;
          }
        }
      }
      // System.out.println("distance is:" + minDistance);
      return index;
    }
  }


  /**
   * This method will use the destroy card if there is a tile blocking the path to the nugget in a
   * given distance
   * 
   * @param boardState
   * @param nuggetIndex
   * @return newMove to destory the tile
   */
  public static SaboteurMove DestroyBlindAlley(SaboteurBoardState boardState, int nuggetIndex) {
    ArrayList<SaboteurMove> legalMoves = boardState.getAllLegalMoves();
    int newMove = 0;
    int dist = 500;
    int[] goalTile = {SaboteurBoardState.hiddenPos[nuggetIndex][0],
        SaboteurBoardState.hiddenPos[nuggetIndex][1]};

    if (nuggetIndex == -1) {
      nuggetIndex = 1;
    }

    for (int i = 0; i < legalMoves.size(); i++) {
      SaboteurMove move = legalMoves.get(i);
      if (move.getCardPlayed() instanceof SaboteurDestroy) {
        int[] targetMove = move.getPosPlayed();
        SaboteurTile tileToDestroy = boardState.getHiddenBoard()[targetMove[0]][targetMove[1]];

        if (blindAlleyCardList.contains(tileToDestroy.getIdx())) {
          int newDistance = calculateDistance(move, goalTile);

          if (newDistance < dist) {
            newMove = i;
            dist = newDistance;
          }
        }

      }
    }

    return legalMoves.get(newMove);
  }



  /*
   * 
   * ===============================================================================================
   * ====================== The following four methods are copied from SaboteurBoardState.java class
   * and modified by us What we need is to test whether there is a one-path connected from starting
   * point to destination
   * ===============================================================================================
   * ======================
   * 
   */
  private static boolean containsIntArray(ArrayList<int[]> a, int[] o) { // the .equals used in
                                                                         // Arraylist.contains is
                                                                         // not working between
                                                                         // arrays..
    if (o == null) {
      for (int i = 0; i < a.size(); i++) {
        if (a.get(i) == null)
          return true;
      }
    } else {
      for (int i = 0; i < a.size(); i++) {
        if (Arrays.equals(o, a.get(i)))
          return true;
      }
    }
    return false;
  }

  public static boolean cardPath(SaboteurBoardState boardState, ArrayList<int[]> originTargets,
      int[] targetPos, Boolean usingCard) {
    // the search algorithm, usingCard indicate weither we search a path of cards (true) or a path
    // of ones (aka tunnel)(false).
    ArrayList<int[]> queue = new ArrayList<>(); // will store the current neighboring tile. Composed
                                                // of position (int[]).
    ArrayList<int[]> visited = new ArrayList<int[]>(); // will store the visited tile with an Hash
                                                       // table where the key is the position the
                                                       // board.
    visited.add(targetPos);
    if (usingCard)
      addUnvisitedNeighborToQueue(boardState, targetPos, queue, visited,
          SaboteurBoardState.BOARD_SIZE, usingCard);
    else
      addUnvisitedNeighborToQueue(boardState, targetPos, queue, visited,
          SaboteurBoardState.BOARD_SIZE * 3, usingCard);
    while (queue.size() > 0) {
      int[] visitingPos = queue.remove(0);
      if (containsIntArray(originTargets, visitingPos)) {
        return true;
      }
      visited.add(visitingPos);
      if (usingCard)
        addUnvisitedNeighborToQueue(boardState, visitingPos, queue, visited,
            SaboteurBoardState.BOARD_SIZE, usingCard);
      else
        addUnvisitedNeighborToQueue(boardState, visitingPos, queue, visited,
            SaboteurBoardState.BOARD_SIZE * 3, usingCard);
    }
    return false;
  }

  private static void addUnvisitedNeighborToQueue(SaboteurBoardState boardState, int[] pos,
      ArrayList<int[]> queue, ArrayList<int[]> visited, int maxSize, boolean usingCard) {
    int[][] moves = {{0, -1}, {0, 1}, {1, 0}, {-1, 0}};
    int i = pos[0];
    int j = pos[1];
    for (int m = 0; m < 4; m++) {
      if (0 <= i + moves[m][0] && i + moves[m][0] < maxSize && 0 <= j + moves[m][1]
          && j + moves[m][1] < maxSize) { // if the hypothetical neighbor is still inside the board
        int[] neighborPos = new int[] {i + moves[m][0], j + moves[m][1]};
        if (!containsIntArray(visited, neighborPos)) {
          if (usingCard && boardState.getHiddenBoard()[neighborPos[0]][neighborPos[1]] != null)
            queue.add(neighborPos);
          else if (!usingCard
              && boardState.getHiddenIntBoard()[neighborPos[0]][neighborPos[1]] == 1)
            queue.add(neighborPos);
        }
      }
    }
  }

  public static boolean pathToHidden(SaboteurBoardState boardState, int nuggetIndex) {
    boardState.getHiddenIntBoard(); // update the hidden int board.
    boolean atLeastOnefound = false;
    ArrayList<int[]> originTargets = new ArrayList<>();
    originTargets.add(new int[] {SaboteurBoardState.originPos, SaboteurBoardState.originPos}); // the
                                                                                               // starting
                                                                                               // points
    int[] targetPos = {SaboteurBoardState.hiddenPos[nuggetIndex][0],
        SaboteurBoardState.hiddenPos[nuggetIndex][1]};
    if (cardPath(boardState, originTargets, targetPos, true)) { // checks that there is a cardPath
      // this.printBoard();
      // next: checks that there is a path of ones.
      ArrayList<int[]> originTargets2 = new ArrayList<>();
      // the starting points
      originTargets2.add(
          new int[] {SaboteurBoardState.originPos * 3 + 1, SaboteurBoardState.originPos * 3 + 1});
      originTargets2.add(
          new int[] {SaboteurBoardState.originPos * 3 + 1, SaboteurBoardState.originPos * 3 + 2});
      originTargets2
          .add(new int[] {SaboteurBoardState.originPos * 3 + 1, SaboteurBoardState.originPos * 3});
      originTargets2
          .add(new int[] {SaboteurBoardState.originPos * 3, SaboteurBoardState.originPos * 3 + 1});
      originTargets2.add(
          new int[] {SaboteurBoardState.originPos * 3 + 2, SaboteurBoardState.originPos * 3 + 1});
      // get the target position in 0-1 coordinate
      int[] targetPos2 = {targetPos[0] * 3 + 1, targetPos[1] * 3 + 1};
      if (cardPath(boardState, originTargets2, targetPos2, false)) {
        atLeastOnefound = true;
      }
    }
    return atLeastOnefound;
  }
}
