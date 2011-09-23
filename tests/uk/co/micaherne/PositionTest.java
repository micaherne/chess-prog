package uk.co.micaherne;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

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
	}
	
	@Test
	public void testMove() throws NotationException {
		initialPos.move("D2-D4");
		initialPos.move("G8-F6");
		initialPos.move("C2-C4");
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
		System.out.println(initialPos.toFEN());
		System.out.println(finalPos.toFEN());
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
		Set<int[]> validMoves = initialPos.allValidMoves();
		assertEquals(20, validMoves.size());
		for(int[] move : validMoves) {
			System.out.println("Move: " + move[0] + ", " + move[1] + ", " + move[2] + ", " + move[3]);
			System.out.println(initialPos.moveNotation(move, NotationType.LONG_ALGEBRAIC));
		}
		//assertTrue(validMoves.contains("A2-A3"));
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
}
