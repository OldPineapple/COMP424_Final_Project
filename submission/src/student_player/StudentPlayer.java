package student_player;

import boardgame.Move;

import Saboteur.SaboteurPlayer;
import Saboteur.cardClasses.SaboteurBonus;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurDrop;
import Saboteur.cardClasses.SaboteurMalus;
import Saboteur.cardClasses.SaboteurMap;

import java.util.ArrayList;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;

/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {

	/**
	 * You must modify this constructor to return your student number. This is
	 * important, because this is what the code that runs the competition uses to
	 * associate you with your agent. The constructor should do nothing else.
	 */
	public StudentPlayer() {
		super("260778557");
	}

	/**
	 * This is the primary method that you need to implement. The ``boardState``
	 * object contains the current state of the game, which your agent must use to
	 * make decisions.
	 */
	public Move chooseMove(SaboteurBoardState boardState) {
		SaboteurMove myMove = null;
		int playerNb = boardState.getTurnPlayer();
		ArrayList<int[]> originTargets = new ArrayList<>();
		originTargets.add(new int[]{SaboteurBoardState.originPos,SaboteurBoardState.originPos}); 
		ArrayList<SaboteurMove> legalMoves = boardState.getAllLegalMoves();
		ArrayList<SaboteurCard> currentCard = boardState.getCurrentPlayerCards();
		int nuggetIndex = MyTools.getNuggetPositionIndex(boardState);
		int moveIndex;
		if (boardState.getNbMalus(playerNb) == 0) {	// if we can use Tile card
			moveIndex = MyTools.chooseNextBestMove(nuggetIndex, legalMoves, boardState);
			if (nuggetIndex == -1 && moveIndex == -2) {	// if we do not know the destination but we have Map card in hand
				myMove = new SaboteurMove(new SaboteurMap(), SaboteurBoardState.hiddenPos[MyTools.nexthiddenTileIndex(boardState)][0], 
						SaboteurBoardState.hiddenPos[MyTools.nexthiddenTileIndex(boardState)][1], playerNb);
			}
			else if (nuggetIndex == -1 && moveIndex == -1) {	// if we neither know the destination nor have map card
				for (int i = 0; i < currentCard.size(); i++) {
					if (MyTools.isBlindAlleyCard(boardState, i)) {	// if we have blind alley card in our hand, drop it
						myMove = new SaboteurMove(new SaboteurDrop(), i, 0, playerNb);
						break;
					}
					else if (currentCard.get(i).getName().equals("Malus")) {	// if we have Malus card in our hand, use it
						myMove = new SaboteurMove(new SaboteurMalus(), 0, 0, playerNb);
						break;
					}
					else	 {	// if our hand does not have the above two, assume the destination is the middle hidden tile
						if (i == (currentCard.size() - 1)) {
							moveIndex = MyTools.chooseNextBestMove(1, legalMoves, boardState);
							myMove = legalMoves.get(moveIndex);
							break;
						}
						continue;
					}
				}
			}
			else if (nuggetIndex != -1 && moveIndex == -1) {	// if we know the destination but we do not have normal card
				for (int i = 0; i < currentCard.size(); i++) {
					if (MyTools.isBlindAlleyCard(boardState, i) || currentCard.get(i).getName().equals("Map")) {	// if the card is blind alley or map card, discard it
						myMove = new SaboteurMove(new SaboteurDrop(), i, 0, playerNb);
						break;
					}
					else if (currentCard.get(i).getName().equals("Malus")) {	// if we have Malus card in our hand, use it
						myMove = new SaboteurMove(new SaboteurMalus(), 0, 0, playerNb);
						break;
					}
					else	 {	// if our hand does not have the above two, we drop the first card in hand
						if (i == (currentCard.size() - 1)) {
							myMove = new SaboteurMove(new SaboteurDrop(), 0, 0, playerNb);
							break;
						}
						continue;
					}
				}
			}
			else {	// if we know the nugget position and we have normal card
				int[] targetPos = {SaboteurBoardState.hiddenPos[nuggetIndex][0], SaboteurBoardState.hiddenPos[nuggetIndex][1]};
				if (MyTools.cardPath(boardState, originTargets, targetPos, true) && MyTools.pathToHidden(boardState, nuggetIndex) == false) {
					// if from origin to destination, card is connected but one-map is not connected, which means there is a blind alley card played by opponent
					// in the middle of the path, resulting game is not over
					if (MyTools.hasCardInHand(currentCard, "Destroy")) {	// if we have destroy card, use it to delete the blind alley tile
						myMove = MyTools.DestroyBlindAlley(boardState, nuggetIndex);
					}
					else {	// if we do not have destroy card in hand, we drop/use card to get destroy card
						for (int i = 0; i < currentCard.size(); i++) {
							if (MyTools.isBlindAlleyCard(boardState, i) || currentCard.get(i).getName().equals("Map")) {	
								// if we have blind alley card, drop it, if we have Map card but we know the destination, drop it as well
								myMove = new SaboteurMove(new SaboteurDrop(), i, 0, playerNb);
								break;
							}
							else if (currentCard.get(i).getName().equals("Malus")) {	// if we have Malus card in our hand, use it
								myMove = new SaboteurMove(new SaboteurMalus(), 0, 0, playerNb);
								break;
							}
							else {
								if (i == (currentCard.size() - 1)) {		// drop the first card
									myMove = new SaboteurMove(new SaboteurDrop(), 0, 0, playerNb);
									break;
								}
								continue;
							}
						}
					}
				}
				else		// it ideally needs to check the step that we can win the game, combo with destroy, but failed to implement it
					myMove = legalMoves.get(moveIndex);
			}

		}
		else {	// if we cannnot use Tile card
			if (MyTools.hasCardInHand(currentCard, "Bonus")) {	// if we have bonus card, use it
				myMove = new SaboteurMove(new SaboteurBonus(), 0, 0, playerNb);
			}
			else if (nuggetIndex == -1 && MyTools.hasCardInHand(boardState.getCurrentPlayerCards(), "Map")) {	
				// if we do not know the destination but we have Map card in hand
				myMove = new SaboteurMove(new SaboteurMap(), SaboteurBoardState.hiddenPos[MyTools.nexthiddenTileIndex(boardState)][0], 
						SaboteurBoardState.hiddenPos[MyTools.nexthiddenTileIndex(boardState)][1], playerNb);
			}
			else {
				for (int i = 0; i < currentCard.size(); i++) {
					if (MyTools.isBlindAlleyCard(boardState, i) || currentCard.get(i).getName().equals("Map")) {	
						// if we have blind alley card, drop it, if we have Map card but we know the destination, drop it as well
						myMove = new SaboteurMove(new SaboteurDrop(), i, 0, playerNb);
						break;
					}
					else if (currentCard.get(i).getName().equals("Malus")) {	// if we have Malus card in our hand, use it
						myMove = new SaboteurMove(new SaboteurMalus(), 0, 0, playerNb);
						break;
					}
					else {
						if (i == (currentCard.size() - 1)) {		// drop the first card
							myMove = new SaboteurMove(new SaboteurDrop(), 0, 0, playerNb);
							break;
						}
						continue;
					}
				}
			}
		}
		System.out.println("260778557 player acting as player number: " + boardState.getTurnPlayer());
		if (boardState.isLegal(myMove))	// check if our move is a legal move, if it is, return it
			return myMove;
		else		// if it is not a legal move, play randomly (to ensure no error occurs)
			return boardState.getRandomMove();
	}
}