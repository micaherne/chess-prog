package uk.co.micaherne;

import java.util.HashSet;
import java.util.Set;

import uk.co.micaherne.Position.NotationType;

public class Temp {

	/**
	 * @param args
	 * @throws FENException 
	 */
	public static void main(String[] args) throws FENException {
		String initialFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		Position initialPos = Position.fromFEN(initialFEN);
		Set<int[]> moves = initialPos.allValidMoves();
		Set<Position> positions = new HashSet<Position>();
		for(int[] move: moves) {
			Position pos = new Position(initialPos);
			//System.out.println(pos.moveNotation(move, NotationType.LONG_ALGEBRAIC));
			pos.move(move);
			positions.add(pos);
			//System.out.println(pos.getSideToMove());
		}
		Set<Position> positions2 = new HashSet<Position>();
		for(Position p : positions) {
			Set<int[]> moves2 = p.allValidMoves();
			for(int[] move: moves2) {
				Position pos = new Position(p);
				//System.out.println(pos.moveNotation(move, NotationType.LONG_ALGEBRAIC));
				pos.move(move);
				positions2.add(pos);
				//System.out.println(pos);
			}
		}
		Set<Position> positions3 = new HashSet<Position>();
		for(Position p : positions2) {
			Set<int[]> moves3 = p.allValidMoves();
			for(int[] move: moves3) {
				Position pos = new Position(p);
				//System.out.println(pos.moveNotation(move, NotationType.LONG_ALGEBRAIC));
				pos.move(move);
				positions3.add(pos);
				//System.out.println(pos);
			}
		}
		float maxPositionEval = 0;
		Position maxPosition = null;
		float currentEval = 0;
		
		Set<Position> positions4 = new HashSet<Position>();
		for(Position p : positions3) {
			Set<int[]> moves4 = p.allValidMoves();
			for(int[] move: moves4) {
				Position pos = new Position(p);
				//System.out.println(pos.moveNotation(move, NotationType.LONG_ALGEBRAIC));
				pos.move(move);
				
				//positions4.add(pos);
				//System.out.println(pos);
				if((currentEval = pos.evaluate()) > maxPositionEval) {
					maxPosition = pos;
					maxPositionEval = currentEval;
				}
			}
		}
		System.out.println(maxPosition);
		//System.out.println(positions4.size());
	}

}
