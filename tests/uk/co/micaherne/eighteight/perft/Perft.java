package uk.co.micaherne.eighteight.perft;

import java.util.HashSet;
import java.util.Set;

import uk.co.micaherne.eighteight.Position;

public class Perft {
	
	public static int perft(Position pos, int depth) {
		if(depth == 0)  return 1;
		int nodes = 0;
		int millionNodes = 0;
		Set<int[]> moves = new HashSet<int[]>();
		for(int[] move : pos.validMoves()) {
			Position pos2 = new Position(pos);
			pos2.move(move);
			// trim out checks
			if(pos2.whiteToMove) {
				if(pos2.isCheck(Position.BLACK)) continue;
			} else {
				if(pos2.isCheck(Position.WHITE)) continue;
			}
			nodes += perft(pos2, depth - 1);
			if(nodes - (millionNodes * 1000000) > 1000000) {
				System.out.println(millionNodes * 1000000 + " nodes evaluated");
				millionNodes = nodes / 1000000;
			}
		}
		return nodes;
	}
	
	public static String divide(Position pos, int depth) {
		StringBuilder result = new StringBuilder();
		for(int[] move : pos.validMoves()) {
			Position pos2 = new Position(pos);
			pos2.move(move);
			String moveStr = Position.moveToNotation(move);
			result.append(String.format("%1$-6s", moveStr));
			result.append(perft(pos2, depth - 1));
			result.append("\n");
		}
		return result.toString();
	}

}
