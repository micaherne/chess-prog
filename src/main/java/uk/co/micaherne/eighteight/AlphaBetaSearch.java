package uk.co.micaherne.eighteight;

import java.util.Set;

public class AlphaBetaSearch {
	
	public static TranspositionTable transpositionTable = new TranspositionTable();

	public int nodesSearched = 0;
	
	public int[] bestMove(Position position, int depth) {
		nodesSearched = 0;
		position.bestMove = new int[] { -1, -1 };
		// negaMax(4);
		alphaBeta(position, depth, -200000, 200000);
		return position.bestMove;
	}

	public int alphaBeta(Position position, int depth, int alpha, int beta)
			 {
		if (depth == 0) {
			nodesSearched++;
			return position.evaluate();
		}
		if(transpositionTable.containsKey(position)) {
			PositionEvaluation previousEvaluation = transpositionTable.get(position);
			if(previousEvaluation.depth >= depth) {
				position.bestMove = previousEvaluation.bestMove;
				return previousEvaluation.value;
			}
		}
		int localalpha = alpha;
		int max = Integer.MIN_VALUE;
		Set<int[]> moves = position.pseudoValidMoves();
		int validMoveCount = 0;
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
			validMoveCount++;
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
		if(validMoveCount == 0) {
			// either mate or stalemate
			if(position.whiteToMove && position.isCheck(Position.WHITE)) {
				return - 20000;
			} else if(!position.whiteToMove && position.isCheck(Position.BLACK)) {
				return - 20000;
			} else {
				return 0;
			}
		}
		// Add to transposition table
		transpositionTable.put(position, new PositionEvaluation(max, depth, position.bestMove));
		return max;
	}

}
