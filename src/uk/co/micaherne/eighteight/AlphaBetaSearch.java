package uk.co.micaherne.eighteight;

import java.util.Set;

public class AlphaBetaSearch {

	public int nodesSearched = 0;
	
	public int[] bestMove(Position position, int depth) {
		nodesSearched = 0;
		position.bestMove = new int[] { -1, -1 };
		// negaMax(4);
		alphaBeta(position, depth, -200000, 200000);
		System.out.println("Best move: " + Position.moveToNotation(position.bestMove));
		return position.bestMove;
	}

	public int alphaBeta(Position position, int depth, int alpha, int beta)
			 {
		if (depth == 0) {
			nodesSearched++;
			return position.evaluate();
		}
		int localalpha = alpha;
		int max = Integer.MIN_VALUE;
		Set<int[]> moves = position.pseudoValidMoves();
		for (int[] m : moves) {
			Position resultingPosition = new Position(position);
			resultingPosition.move(m);
			if (resultingPosition.whiteToMove) {
				if (resultingPosition.isCheck(Position.BLACK)) {
					continue;
				}
			} else {
				if (resultingPosition.isCheck(Position.WHITE)) {
					continue;
				}
			}
			int score = - alphaBeta(resultingPosition, depth - 1, -beta,
					-localalpha);
			if (score > max) {
				max = score;
				position.bestMove = m;
			}
			if (max >= beta) {
				break;
			}
			if (max > localalpha) {
				localalpha = max;
			}
		}
		return max;
	}

}
