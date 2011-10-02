package uk.co.micaherne;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.co.micaherne.Position.Colour;
import uk.co.micaherne.Position.NotationType;

public class PositionTest {

	private static final String initialFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	private Position initialPos;

	@Before
	public void setUp() throws FENException {
		initialPos = Position.fromFEN(initialFEN);
	}
	
	@Test
	public void testFromFEN() throws FENException {
		assertEquals('r', initialPos.getPiece(8,1));
		assertEquals(' ', initialPos.getPiece(4, 5));
		assertEquals('P', initialPos.getPiece(2, 4));
		assertEquals('Q', initialPos.getPiece(1, 4));
		
		// Check castling is correct
		Position pos2 = Position.fromFEN("3k4/8/8/8/8/5b2/8/R2K4 w KQkq - 0 1");
		assertEquals("KQkq", pos2.getCastling());
		Position pos3 = Position.fromFEN("3k4/8/8/8/8/5b2/8/R2K4 w - - 0 1");
		assertEquals("-", pos3.getCastling());
	}
	
	@Test
	public void testMove() throws NotationException {
		String aboutToQueenFEN = "rn5r/4P1kp/5n2/pB3P1b/P7/4RN2/1P3PP1/4R1K1 w - - 3 31 ";
		Position pos = Position.fromFEN(aboutToQueenFEN);
		pos.move("e7e8q", NotationType.LONG_ALGEBRAIC);
		assertEquals('Q', pos.getPiece(8, 5));
	}
	
	@Test
	public void testIntMove() {
		initialPos.move(new int[] { 0, 6, 2, 5 });
		//System.out.println(initialPos);
	}
	
	@Test(expected=NotationException.class)
	public void testInvalidMove() throws NotationException {
		initialPos.move("d2-d4");
	}
	
	@Test
	public void testToFEN() {
		assertEquals(initialFEN, initialPos.toFEN());
	}
	
	@Test
	public void testEquals() throws FENException, NotationException {
		Position finalPos = Position.fromFEN("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2");
		initialPos.move("E2-E4");
		initialPos.move("C7-C5");
		initialPos.move("G1-F3");
		//System.out.println(initialPos.toFEN());
		//System.out.println(finalPos.toFEN());
		assertEquals(finalPos, initialPos);
	}

	@Test
	public void testCastlingRights() throws FENException, NotationException {
		Position finalPos = Position.fromFEN("r3k2r/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/R3K2R b KQkq - 1 2");
		assertEquals("KQkq", finalPos.getCastling());
		finalPos.move("A1-C1");
		assertEquals("Kkq", finalPos.getCastling());
		finalPos.move("H1-F1");
		assertEquals("kq", finalPos.getCastling());
		finalPos.move("H8-F8");
		assertEquals("q", finalPos.getCastling());
		finalPos.move("A8-C8");
		assertEquals("", finalPos.getCastling());
	}
	
	@Test
	public void testCastlingRights2() throws NotationException, FENException {
		Position finalPos = Position.fromFEN("r3k2r/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/R3K2R b KQkq - 1 2");
		assertEquals("KQkq", finalPos.getCastling());
		finalPos.move("E1-F1");
		assertEquals("kq", finalPos.getCastling());
		finalPos.move("E8-F8");
		assertEquals("", finalPos.getCastling());
	}
	
	@Test
	public void testPiecePositions() {
		Set<int[]> pos = initialPos.piecePositions('P');
		assertEquals(8, pos.size());
		for(int[] p : pos) {
			assertEquals(1, p[0]);
		}
	}
	
	@Test
	public void testAllValidMoves() {
		/*int[] krPawn = {1, 0};
		Set<int[]> validMoves = initialPos.validMoves(krPawn);
		for(int[] move : validMoves) {
			//System.out.println("Move: " + move[0] + ", " + move[1] + ", " + move[2] + ", " + move[3]);
		}
		
		int[] kn = { 0, 1 };
		validMoves = initialPos.validMoves(kn);
		for(int[] move : validMoves) {
			System.out.println("Move: " + move[0] + ", " + move[1] + ", " + move[2] + ", " + move[3]);
		}*/
		Set<int[]> validMoves = initialPos.allPseudoValidMoves();
		assertEquals(20, validMoves.size());
		for(int[] move : validMoves) {
			//System.out.println("Move: " + move[0] + ", " + move[1] + ", " + move[2] + ", " + move[3]);
			//System.out.println(initialPos.moveNotation(move, NotationType.LONG_ALGEBRAIC));
		}
		//assertTrue(validMoves.contains("A2-A3"));
	}
	
	@Test
	public void testAllValidMoves2() throws FENException {
		Position pos = Position.fromFEN("rnb1kbnr/ppp2Ppp/5q2/8/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 4");
		for(int[] move : pos.allPseudoValidMoves()) {
			if(pos.getPiece(move) == 'P' && move[0] > 3) {
				//System.out.println(move[0] + ", " + move[1] + ", " + move[2] + ", " + move[3]);
			}
		}
	}
	
	@Test
	public void testAllMoves() throws FENException {
		Position pos = Position.fromFEN("rnb1kbnr/ppp2Ppp/5q2/8/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 4");
		for(int[] move : pos.pseudoValidMoves(new int[] { 6, 5 })) {
			//System.out.println(move[0] + ", " + move[1] + ", " + move[2] + ", " + move[3]);
		}
	}
	
	@Test
	public void testNotation() throws FENException {
		Position finalPos = Position.fromFEN("r3k2r/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/R3K2R b KQkq - 1 2");
		assertEquals("0-0", finalPos.moveNotation(new int[] { 0, 4, 0, 6}, NotationType.LONG_ALGEBRAIC));
	}
	
	@Test
	public void testIsColour() {
		assertTrue(Position.isBlack('b'));
		assertFalse(Position.isBlack('B'));
		assertTrue(Position.isWhite('B'));
		assertFalse(Position.isWhite('b'));
		assertTrue(Position.isWhite('P'));
		
		assertEquals(Position.Colour.WHITE, Position.pieceColour('P'));
	}
	
	@Test
	public void testSwitchColour() {
		initialPos.move(new int[] { 0, 6, 2, 5 });
		assertEquals(Position.Colour.BLACK, initialPos.getSideToMove());
	}
	
	@Test
	public void testEp() {
		initialPos.move(new int[] {1, 4, 3, 4});
		assertEquals(2, initialPos.epSquare[0]);
		assertEquals(4, initialPos.epSquare[1]);
		assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", initialPos.toFEN());
		initialPos.move(new int[] {6, 4, 4, 4});
		assertEquals(5, initialPos.epSquare[0]);
		assertEquals(4, initialPos.epSquare[1]);
	}
	
	/*
	 * Check for being in check. Didn't notice that this was check:
	 * position startpos moves e2e4 d7d5 e4d5 e7e6 d5e6 d8f6 e6f7
	 * 
	 * Also: position startpos moves e2e4 b7b5 g1f3 f7f6 d2d4 b8a6 f1b5 c7c5 d4c5
	 *  - tried d6 putting king in check.
	 *  
	 *  position startpos moves e2e4 b8a6 d2d4 a8b8 g1f3 g7g6 f1c4 a6b4 c2c3 b4c6 e1g1 e7e6 e4e5 g8e7 c1g5 c6a5 c4d3 d7d6 d1a4 a5c6 b1d2 b7b5 d3b5 f7f6 g5f6 b8b5 a4b5 h8g8 f6e7 e8e7 b5c6 f8h6 f1e1 d6e5 e1e5 h6g7 e5e2 g7h6 a1e1 g8g7 e2e6 c8e6 c6e6 e7f8 d2e4 d8e7 e4c5 e7d8 f3e5 a7a6 e5d7 g7d7 c5d7 f8g7 e6e7 d8e7 e1e7 g7g8 d7c5 h6c1 b2b3 a6a5 e7c7 c1d2 c3c4 d2c3 d4d5 h7h6 f2f4 c3d4 g1f1 d4e3 g2g3 a5a4 f1e2 e3d4 e2d3 d4g7 d3e4 a4b3 a2b3 h6h5 d5d6 g7h6 d6d7 g8h7 d7d8q
	 *  - tried Kh8
	 */
	@Test
	public void testCheck() throws UCIException {
		IO io = new IO();
		io.doInput("position startpos moves e2e4 d7d5 e4d5 e7e6 d5e6 d8f6 e6f7");
		Position pos = io.getCurrentPosition();
		assertTrue(pos.inCheck(Colour.BLACK));
		io.doInput("position startpos moves e2e4 b8a6 g1f3 b7b6 d2d4 h7h6 f1b5 c7c6 b5d3 e7e6 e4e5 c8b7 b1c3 c6c5 c1e3 a6b4 d3b5 a7a6 b5e2 d8e7 e1g1 a8c8 a2a3 b4c6 d4d5 c6a7 d5d6 e7d8 e2d3 b6b5 d3e4 b7e4 c3e4 c8a8 e3c5 f7f5 e5f6 a7c8 f6f7 e8f7 f3e5 f7e8 d1h5");
		//assertTrue(pos.inCheck(Colour.BLACK));
	}
	
	@Test
	public void badMoveGen() throws UCIException {
		IO io = new IO();
		io.doInput("position startpos moves e2e4 b8a6 g1f3 e7e6 b2b3 h7h5 c1b2 g8h6 f1a6 b7a6 e1g1 a6a5 c2c4 a8b8 d2d4 d7d5 c4d5 h6g8 d5e6 c8e6 b1c3 e6g4 d1d3 b8b7 f3e5 d8d6 h2h3 g4c8 d3f3 d6f6 c3d5 f6f3 e5f3 a5a4 b3a4 b7b2 f3e5 b2c2 f1b1 g8f6 b1b8 f6d5 b8c8 e8e7 e4d5 h8h6 e5c6 e7f6 c8f8 c2c4 a4a5 c4a4 f8c8 a4c4 c8c7 a7a6 c7a7 g7g6 a7a6 f6g5 a1e1 g5f4 c6e5 c4d4 a6f6 f4g5 f6f7 d4f4 f7f4 g5f4 g2g3 f4f5 a5a6 g6g5 a6a7 h6a6 e5c6 f5g6 e1e8 g6h7 a7a8q a6a8 e8a8 h7g7 d5d6 h5h4 d6d7 g7h7 d7d8q h7g6 d8g8 g6f5 a8f8");
		Position pos = io.getCurrentPosition();
		assertTrue(pos.inCheck(Colour.BLACK));
	}
	
	@Test
	public void testCastleOutOfCheck() throws FENException {
		Position pos = Position.fromFEN("3k4/8/8/8/8/5b2/8/R3K2R w KQkq - 0 1 ");
		assertTrue(pos.getCastling().contains("K"));
		Set<int[]> moves = pos.pseudoValidMoves(new int[] { 0, 3 });
		for(int[] move : moves) {
			System.out.println("To: " + move[2] + ", " + move[3]);
			if(move[2] == 0 && move[3] == 2) {
				fail("Can't castle out of check (queenside)!");
			}
			if(move[2] == 0 && move[3] == 6) {
				fail("Can't castle out of check (kingside)!");
			}
		}
	}
}
