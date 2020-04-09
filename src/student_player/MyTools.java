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

	public static final String[] blindAlleyCard = {"1", "2", "2_flip", "3", "3_flip", "4", "4_flip", "11", "11_flip", "12", "12_flip", "13", "14", "14_flip", "15"};
	public static final List<String> blindAlleyCardList = Arrays.asList(blindAlleyCard);
	public static final String[] normalCard = {"0", "5", "5_flip", "6", "6_flip", "7", "7_flip", "8", "9", "9_flip", "10"};
	public static final List<String> normalCardList = Arrays.asList(normalCard);
	public static final String[] toolCard = {"bonus", "destroy", "malus", "map"};
	public static final List<String> toolCardList = Arrays.asList(toolCard);

	public static int getNuggetPositionIndex(SaboteurBoardState boardState) {
		for (int i = 0; i < 3; i++) {
			SaboteurTile hiddenCard = boardState.getHiddenBoard()[SaboteurBoardState.hiddenPos[i][0]][SaboteurBoardState.hiddenPos[i][1]];
			if (hiddenCard.getIdx() == "nugget") {
				return i;
			}
		}
		return -1;	// error
	}

	public static boolean isBlindAlleyCard(SaboteurBoardState boardState, int index) {
		for (int i = 0; i < blindAlleyCard.length; i++) {
			if(boardState.getCurrentPlayerCards().get(index).getName() == blindAlleyCard[i]) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSpecifiedCardInHand(ArrayList<SaboteurCard> cardInHand, String name, int index) {
		if(cardInHand.get(index).getName() == name) {
			return true;
		}
		else
			return false;
	}

	// Assume we are not experiencing Malus, we choose the next best move and return its move index
	public static int chooseNextBestMove(int nuggetIndex, ArrayList<SaboteurMove> legalMoves, SaboteurBoardState boardState) {
		int minDistance = Integer.MAX_VALUE;
		int index = 0;
		if (nuggetIndex == -1) {
			for (int i = 0; i < legalMoves.size(); i++) {
				if (legalMoves.get(i).getCardPlayed().getName() == "map") {
					return i;
				}
			}
		}
		else {
			int [] destinationPos = {SaboteurBoardState.hiddenPos[nuggetIndex][0], SaboteurBoardState.hiddenPos[nuggetIndex][1]};
			for (int i = 0; i < legalMoves.size(); i++) {
				if (normalCardList.contains(legalMoves.get(i).getCardPlayed().getName())) {
					int [] legalMovePos = {legalMoves.get(i).getPosPlayed()[0], legalMoves.get(i).getPosPlayed()[1]};
					int distance = Math.abs(destinationPos[0] - legalMovePos[0]) + Math.abs(destinationPos[1] - legalMovePos[1]);
					if (distance < minDistance) {
						minDistance = distance;
						index = i;
					}
				}
			}
			return index;
		}
		return -1;
	}

}