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
		System.out.println("Our AI is " + playerNb);
		ArrayList<SaboteurMove> legalMoves = boardState.getAllLegalMoves();
		ArrayList<SaboteurCard> currentCard = boardState.getCurrentPlayerCards();
		int nuggetIndex = MyTools.getNuggetPositionIndex(boardState);
/*		for (int i = 0; i < currentCard.size(); i ++) {
			System.out.println(MyTools.isBlindAlleyCard(boardState, i));
			System.out.println(currentCard.get(i).getName());
		}
		for(int i = 0; i < MyTools.blindAlleyCardList.size(); i++) {
			System.out.println(MyTools.blindAlleyCardList.get(i));
		}
		System.out.println("nuggtet Index is:" + nuggetIndex); */
		int moveIndex;
		if (boardState.getNbMalus(playerNb) == 0) {	// if we can use Tile card
			moveIndex = MyTools.chooseNextBestMove(nuggetIndex, legalMoves, boardState);
//			System.out.println("move Index is:" + moveIndex);
			if (MyTools.getNuggetPositionIndex(boardState) == -1 && moveIndex == -2) {	// if we do not know the destination but we have Map card in hand
				myMove = new SaboteurMove(new SaboteurMap(), SaboteurBoardState.hiddenPos[MyTools.nexthiddenTileIndex(boardState)][0], 
						SaboteurBoardState.hiddenPos[MyTools.nexthiddenTileIndex(boardState)][1], playerNb);
			}
			else if (MyTools.getNuggetPositionIndex(boardState) == -1 && moveIndex == -1) {	// if we neither know the destination nor have map card
				for (int i = 0; i < currentCard.size(); i++) {
					if (MyTools.isBlindAlleyCard(boardState, i)) {	// if we have blind alley card in our hand, drop it
						myMove = new SaboteurMove(new SaboteurDrop(), i, 0, playerNb);
						break;
					}
					else if (currentCard.get(i).getName().equals("Malus")) {	// if we have Malus card in our hand, use it
						myMove = new SaboteurMove(new SaboteurMalus(), 0, 0, playerNb);
						break;
					}
					else	 {	// if our hand does not have the above two, we choose a random move.
						if (i == (currentCard.size() - 1)) {
							moveIndex = MyTools.chooseNextBestMove(1, legalMoves, boardState);
							myMove = legalMoves.get(moveIndex);
							break;
						}
						continue;
					}
				}
			}
			else if (MyTools.getNuggetPositionIndex(boardState) != -1 && moveIndex == -1) {	// if we know the destination but we do not have normal card
				for (int i = 0; i < currentCard.size(); i++) {
					if (MyTools.isBlindAlleyCard(boardState, i) || currentCard.get(i).getName().equals("Map")) {	// if the card is blind alley or map card, discard it
						myMove = new SaboteurMove(new SaboteurDrop(), i, 0, playerNb);
						break;
					}
					else if (currentCard.get(i).getName().equals("Malus")) {	// if we have Malus card in our hand, use it
						myMove = new SaboteurMove(new SaboteurMalus(), 0, 0, playerNb);
						break;
					}
					else	 {	// if our hand does not have the above two, we choose a random move.
						if (i == (currentCard.size() - 1)) {
							myMove = boardState.getRandomMove();
							break;
						}
						continue;
					}
				}
			}
			else
				myMove = legalMoves.get(moveIndex);
		}
		else {	// if we cannnot use Tile card
			if (MyTools.hasCardInHand(currentCard, "Bonus")) {	// if we have bonus card, use it
				myMove = new SaboteurMove(new SaboteurBonus(), 0, 0, playerNb);
			}
			else if (MyTools.getNuggetPositionIndex(boardState) == -1 && MyTools.hasCardInHand(boardState.getCurrentPlayerCards(), "Map")) {	
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
						if (i == (currentCard.size() - 1)) {		// the only card remaining that we can play is destroy, play it randomly
							myMove = boardState.getRandomMove();
							break;
						}
						continue;
					}
				}
			}
		}
		if (boardState.isLegal(myMove))	// check if our move is a legal move, if it is, return it
			return myMove;
		else		// if it is not a legal move, play randomly (to ensure no error occurs)
			return boardState.getRandomMove();
	}

	/*	public Move chooseMove(SaboteurBoardState boardState) {
		// You probably will make separate functions in MyTools.
		// For example, maybe you'll need to load some pre-processed best opening
		// strategies...
		// MyTools.getSomething();
		ArrayList<SaboteurMove> legalMoves = boardState.getAllLegalMoves();
		int player = boardState.getTurnPlayer();
		boolean isMaximizing = true;
		if (player == 1)
			isMaximizing = true;
		else
			isMaximizing = false;
		// Get the best node containing its score and index via minimax
		int[] node = minimax (boardState, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, player, isMaximizing);		
		Move myMove = legalMoves.get(node[1]);
		// Return your move to be processed by the server.
		return myMove;
	}


	// Proceed a minimax search in the board with alpha beta prunning
	// It will return a score and its index number in order to get the best move.
	public int[] minimax(SaboteurBoardState boardState, int depth, int alpha, int beta, int player, boolean isMaximizing) {
		int[] node = new int[2];
		int score = 0;
		int bestScore = 0;
		int index = 0;
		SaboteurBoardState newState = boardState;
		// If game is over, check who wins the game or tie the game
		if (boardState.gameOver() == true) {
			if (boardState.getWinner() == player) {
				score = 1000 - depth;
			}
			else if (boardState.getWinner() != player && boardState.getWinner() < 2){
				score = -1000 - depth;
			}
			else
				score = 0;
		}
		// If depth is equal to 0, call evaluation
		else if (depth == 0) {
			score = evaluation(newState);
		}
		// If game is neither over nor depth is not equal to 0, recursively call minimax to get the desired score and index
		else {
			if (isMaximizing == true) {
				bestScore = Integer.MIN_VALUE;
				ArrayList<SaboteurMove> legalMoves = newState.getAllLegalMoves();
				for (int i = 0; i < legalMoves.size(); i++) {
					newState.processMove(legalMoves.get(i));
					score = minimax(newState, depth - 1, alpha, beta, player, false)[0];
					bestScore = Math.max(score, bestScore);
					if (alpha < beta) {
						alpha = Math.max(alpha, score);
						index = i;
					}
					else if (beta <= alpha)
							break;
				}
				node[0] = bestScore;
				node[1] = index;
				return node;
			}
			else {
				bestScore = Integer.MAX_VALUE;
				ArrayList<SaboteurMove> legalMoves = newState.getAllLegalMoves();
				for (int i = 0; i < legalMoves.size(); i++) {
					newState.processMove(legalMoves.get(i));
					score = minimax(newState, depth - 1, alpha, beta, player, true)[0];
					bestScore = Math.min(score, bestScore);
					if (alpha < beta) {
						alpha = Math.min(alpha, score);
						index = i;
					}
					else if (beta <= alpha)
							break;
				}
				node[0] = bestScore;
				node[1] = index;
				return node;
			}
		}
		node[0] = score;
		node[1] = index;
		return node;
	}

	public int evaluation(SaboteurBoardState boardState) {
		int score = 0;
		return score;
	} */
}