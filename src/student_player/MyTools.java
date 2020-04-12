package student_player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurTile;
import Saboteur.cardClasses.SaboteurCard;

public class MyTools {
	public static double getSomething() {
		return Math.random();
	}

	public static final String[] blindAlleyCard = {"Tile:1", "Tile:2", "Tile:3", "Tile:4", "Tile:11", "Tile:12", "Tile:13", "Tile:14", "Tile:15"};
	public static final List<String> blindAlleyCardList = Arrays.asList(blindAlleyCard);
	public static final String[] normalCard = {"Tile:0", "Tile:5", "Tile:6", "Tile:7", "Tile:8", "Tile:9", "Tile:10"};
	public static final List<String> normalCardList = Arrays.asList(normalCard);
	public static final String[] toolCard = {"Bonus", "Destroy", "Malus", "Map"};
	public static final List<String> toolCardList = Arrays.asList(toolCard);

	public static int getNuggetPositionIndex(SaboteurBoardState boardState) {
		for (int i = 0; i < 3; i++) {
			SaboteurTile hiddenCard = boardState.getHiddenBoard()[SaboteurBoardState.hiddenPos[i][0]][SaboteurBoardState.hiddenPos[i][1]];
			if (hiddenCard.getIdx().equals("nugget")) {
				return i;
			}
		}
		return -1;	// error
	}
	
	public static int nexthiddenTileIndex(SaboteurBoardState boardState) {
		for (int i = 0; i < 3; i++) {
			SaboteurTile hiddenCard = boardState.getHiddenBoard()[SaboteurBoardState.hiddenPos[i][0]][SaboteurBoardState.hiddenPos[i][1]];
			if (hiddenCard.getIdx().equals("8")) {
				return i;
			}
		}
		return -1;	// error
	}

	public static boolean isBlindAlleyCard(SaboteurBoardState boardState, int index) {
		if (blindAlleyCardList.contains(boardState.getCurrentPlayerCards().get(index).getName()))
			return true;
		return false;
	}

	public static boolean hasCardInHand(ArrayList<SaboteurCard> cardInHand, String name) {
		for (int i = 0; i < cardInHand.size(); i++) {
			if(cardInHand.get(i).getName().equals(name)) {
				return true;
			}
		}
			return false;
	}

	// Assume we are not experiencing Malus, we choose the next best move and return its move index
	public static int chooseNextBestMove(int nuggetIndex, ArrayList<SaboteurMove> legalMoves, SaboteurBoardState boardState) {
		int minDistance = Integer.MAX_VALUE - 1;
		int index = -1;
		if (nuggetIndex == -1) {		// if the destination is unknown, check if we can use map, if not, return -1
			if (hasCardInHand(boardState.getCurrentPlayerCards(), "Map"))
				return -2;
			else
				return -1;
		}
		else {	// if the destination is known, choose the move that can shorten the distance, if not, return -1
			int [] destinationPos = {SaboteurBoardState.hiddenPos[nuggetIndex][0], SaboteurBoardState.hiddenPos[nuggetIndex][1]};
			for (int i = 0; i < legalMoves.size(); i++) {
				if (normalCardList.contains(legalMoves.get(i).getCardPlayed().getName())) {
					int []legalMovePos = {legalMoves.get(i).getPosPlayed()[0], legalMoves.get(i).getPosPlayed()[1]};
					int distance = Math.abs(destinationPos[0] - legalMovePos[0]) + Math.abs(destinationPos[1] - legalMovePos[1]);
					if (distance < minDistance) {
						minDistance = distance;
						index = i;
					}
				}
			}
//			System.out.println("distance is:" + minDistance);
			return index;
		}
	}

}