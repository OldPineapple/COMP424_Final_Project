package student_player;

import boardgame.Move;

import Saboteur.SaboteurPlayer;

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
		// If game is over, check who wins the game or tie the game
//		if (boardState.gameOver() == true) {
//			if (boardState.getWinner() == player) {
//				score = 1000 - depth;
//			}
//			else if (boardState.getWinner() != player && boardState.getWinner() < 2){
//				score = -1000 - depth;
//			}
//			else
//				score = 0;
		// If game is over or depth is equal to 0, call evaluation
		if (boardState.gameOver() == true || depth == 0) {
			score = evaluation();
			node[0] = score;
			node[1] = index;
			return node;
		}
		// if game is not over or depth is not equal to 0, recursively call minimax
		else {
			if (isMaximizing == true) {
				bestScore = Integer.MIN_VALUE;
				ArrayList<SaboteurMove> legalMoves = boardState.getAllLegalMoves();
				for (int i = 0; i < legalMoves.size(); i++) {
					boardState.processMove(legalMoves.get(i));
					score = minimax(boardState, depth - 1, alpha, beta, player, false)[0];
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
				ArrayList<SaboteurMove> legalMoves = boardState.getAllLegalMoves();
				for (int i = 0; i < legalMoves.size(); i++) {
					boardState.processMove(legalMoves.get(i));
					score = minimax(boardState, depth - 1, alpha, beta, player, true)[0];
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
	}
	
	public int evaluation() {
		return 1;
	}
}