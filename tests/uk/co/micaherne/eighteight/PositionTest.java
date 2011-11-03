package uk.co.micaherne.eighteight;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.EncryptedPrivateKeyInfo;

import org.junit.Before;
import org.junit.Test;

import uk.co.micaherne.FENException;
import uk.co.micaherne.eighteight.perft.Perft;

public class PositionTest{

	private static final String perftPos2 = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -";
	private static final String perftPos3 = "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -";
	private static final String perftPos4 = "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1";
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testPieceValues() {
		assertEquals(Position.ROOK, Position.MOVES_LINEAR);
		assertEquals(Position.BISHOP, Position.MOVES_DIAGONAL);
		assertEquals(Position.KNIGHT, Position.MOVES_KNIGHT_WISE | Position.SINGLE_JUMP);
		assertEquals(Position.QUEEN, Position.MOVES_DIAGONAL | Position.MOVES_LINEAR);
		assertEquals(Position.KING, Position.MOVES_DIAGONAL | Position.MOVES_LINEAR | Position.SINGLE_JUMP);
	}

	@Test
	public void testToString() {
		Position pos = new Position();
		pos.initialPosition();
	}

	@Test
	public void testSquareNo() {
		int e4 = Position.squareNo(3, 4);
		assertEquals(0x34, e4);
		int c6 = Position.squareNo(5, 2);
		assertEquals(0x52, c6);
	}
	
	@Test
	public void testPieceToString() {
		char x = Position.pieceToChar((byte)2);
		assertEquals('r', x);
	}
	
	@Test
	public void move() throws FENException {
		Position pos = new Position();
		pos.initialPosition();
/*		pos.move(0x14, 0x34);
		assertFalse(pos.whiteToMove);
		assertEquals(Position.EP_SQUARE, pos.board[0x24] & Position.EP_SQUARE );
		assertEquals(Position.WHITE | Position.PAWN, pos.board[0x34]);
		pos.initialPosition();*/
		pos.move("e2e4");
		assertFalse(pos.whiteToMove);
		assertEquals(Position.EP_SQUARE, pos.board[0x24] & Position.EP_SQUARE );
		assertEquals(Position.WHITE | Position.PAWN, pos.board[0x34]);
		pos.move(0x64, 0x44);
		assertEquals(Position.EP_SQUARE, pos.board[0x54] & Position.EP_SQUARE );

		// test weird situation where a1-c1 created a phantom rook at d1
		Position pos2 = Position.fromFEN("4k3/8/8/8/8/8/8/R3K3 w Q - 0 1");
		//pos2.move("a1c1");
		pos2.move(new int[] { 0x00, 0x02 });
		assertEquals(0, pos2.board[0x03]);
		
		Position pos3 = Position.fromFEN("r3k2r/p2pqpb1/bn2pnp1/2pPN3/1pB1P3/2N2Q1p/PPPB1PPP/R3K2R b KQkq c6 1 1");
		pos3.move("d5c6");
		assertEquals(Position.EMPTY, pos3.board[0x42]);
	}
	
	@Test
	public void testFromFEN() throws FENException{
		Position pos = Position.fromFEN(perftPos2);
		assertEquals(Position.CAN_CASTLE, pos.board[0x00] & Position.CAN_CASTLE);
		assertEquals(0, pos.board[0x01] & Position.CAN_CASTLE);
		assertTrue(pos.whiteToMove);
		
		String fenPart = "c6";
		int a = Position.squareNo(Integer.parseInt("" + fenPart.charAt(1)) - 1,
				fenPart.charAt(0) - 'a');
		assertEquals(0x52, a);
		Position pos2 = Position.fromFEN("r3k2r/p2pqpb1/bn2pnp1/2pP4/1pN1P3/2N2Q1p/PPPBBPPP/R3K2R b KQkq c6 1 1");
		assertEquals(Position.EP_SQUARE, pos2.board[0x52] & Position.EP_SQUARE);
	}


	@Test
	public void testToFEN() throws FENException {
		Position pos = Position.fromFEN(perftPos2);
		assertEquals(perftPos2, pos.toFEN(false));
		pos = Position.fromFEN(perftPos4);
		assertEquals(perftPos4, pos.toFEN(true));
	}
	
	@Test
	public void testAttacks() throws FENException {
		Position pos = Position.fromFEN(perftPos2);
		assertTrue(pos.attacks(0x43, 0x54)); // white pawn attacks black pawn
		assertFalse(pos.attacks(0x42, 0x53)); // empty square attacks nothing
		assertTrue(pos.attacks(0x14, 0x50)); // white bishop attacks black bishop
		assertTrue(pos.attacks(0x44, 0x65)); // white knight attacks black pawn
		assertFalse(pos.attacks(0x44, 0x54)); // white knight attacks black pawn
		assertFalse(pos.attacks(0x25, 0x65)); // white queen attacks black pawn
		assertTrue(pos.attacks(0x25, 0x55)); // white knight attacks black pawn
		
		Position pos2 = Position.fromFEN("r3k2r/8/8/8/8/8/8/3RK2R b KQkq - 0 1");
		//assertTrue(pos2.attacks(0x03, 0x73)); why is this ok?
		
		Position pos3 = Position.fromFEN("8/k7/8/1B6/8/8/8/K5Bb w - - 0 1");
		//System.out.println(pos3);
		assertTrue(pos3.attacks(0x06, 0x60));
		
		}
	
	@Test
	public void testAttacked() throws FENException {
		Position pos2 = Position.fromFEN(perftPos2);
		//assertFalse(pos2.attacked(0x05, 0));
		
		Position pos6 = Position.fromFEN("3rk2r/8/8/8/8/8/8/R3K2R w KQk - 0 1");
		assertTrue(pos6.attacks(0x74, 0x75));
		assertTrue(pos6.attacks(0x73, 0x03));
		assertTrue(pos6.attacked(0x03, Position.BLACK));
	}
	@Test
	public void testDirection() {
		assertEquals(0x44, 0x34 + Position.DIRECTION_NORTH);
		assertEquals(0x24, 0x34 + Position.DIRECTION_SOUTH);
		assertEquals(0x35, 0x34 + Position.DIRECTION_EAST);
		assertEquals(0x33, 0x34 + Position.DIRECTION_WEST);
		assertEquals(0x45, 0x34 + Position.DIRECTION_NORTHEAST);
		assertEquals(0x25, 0x34 + Position.DIRECTION_SOUTHEAST);
		assertEquals(0x23, 0x34 + Position.DIRECTION_SOUTHWEST);
		assertEquals(0x43, 0x34 + Position.DIRECTION_NORTHWEST);
		
		assertEquals(0x55, 0x34 + Position.DIRECTION_NORTHNORTHEAST);
		assertEquals(0x46, 0x34 + Position.DIRECTION_EASTNORTHEAST);
		assertEquals(0x26, 0x34 + Position.DIRECTION_EASTSOUTHEAST);
		assertEquals(0x15, 0x34 + Position.DIRECTION_SOUTHSOUTHEAST);
		
		assertEquals(0x53, 0x34 + Position.DIRECTION_NORTHNORTHWEST);
		assertEquals(0x42, 0x34 + Position.DIRECTION_WESTNORTHWEST);
		assertEquals(0x22, 0x34 + Position.DIRECTION_WESTSOUTHWEST);
		assertEquals(0x13, 0x34 + Position.DIRECTION_SOUTHSOUTHWEST);
		
		assertEquals(Position.DIRECTION_NORTHNORTHEAST, Position.direction(0x23, 0x65));
		assertEquals(Position.DIRECTION_EAST, Position.direction(0x23, 0x24));
		
		// Sorting of helper arrays
		int currentDir = Integer.MIN_VALUE;
		for(int dir : Position.DIRECTIONS_DIAGONAL) {
			assertTrue(dir > currentDir);
			currentDir = dir;
		}
		currentDir = Integer.MIN_VALUE;
		for(int dir : Position.DIRECTIONS_LINEAR) {
			assertTrue(dir > currentDir);
			currentDir = dir;
		}
		currentDir = Integer.MIN_VALUE;
		for(int dir : Position.DIRECTIONS_KNIGHT) {
			assertTrue(dir > currentDir);
			currentDir = dir;
		}
		
		assertEquals(Position.DIRECTION_EAST, Position.direction(0x74, 0x75));
		assertEquals(Position.DIRECTION_SOUTH, Position.direction(0x73, 0x03));
		assertEquals(Position.DIRECTION_NORTHWEST, Position.direction(0x06, 0x60));
	}
	
	@Test
	public void testMisc() {
		assertTrue(Arrays.binarySearch(Position.DIRECTIONS_KNIGHT, Position.DIRECTION_EASTNORTHEAST) > 0);
		int pieceMoved = Position.KING | Position.BLACK | Position.CAN_CASTLE;
		pieceMoved &= ~Position.CAN_CASTLE;
		assertEquals(Position.KING | Position.BLACK, pieceMoved);
	}
	
	@Test
	public void testMoveGen() throws FENException {
		// position 22 from perft file comes up with 0-0-0!
		Position pos = Position.fromFEN("4k2r/8/8/8/8/8/8/4K3 b k - 0 1");
		assertEquals(0, pos.board[0x70]);
		Set<int[]> moves = pos.pseudoValidMoves(0x74);
		assertEquals(6, moves.size());
		
		Position pos2 = Position.fromFEN("r3k3/8/8/8/8/8/8/4K3 b q - 0 1"); // position 23
		moves = pos2.pseudoValidMoves();
		assertEquals(16, moves.size());

		Position pos3 = Position.fromFEN("8/Pk6/8/8/8/8/6Kp/8 b - - 0 1"); // position 123
		moves = pos3.pseudoValidMoves(0x17);
		assertEquals(4, moves.size());
		
		Position pos4 = Position.fromFEN("n1n5/1Pk5/8/8/8/8/5Kp1/5N1N b - - 0 1"); // position 124
		moves = pos4.pseudoValidMoves(0x16);
		assertEquals(12, moves.size());
		
		// level 2 issues
		Position pos5 = Position.fromFEN("r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1");
		moves = pos5.validMoves();
		assertEquals(26, moves.size());
		int count = 0;
		for (int[] move : moves) {
			String move1 = Position.moveToNotation(move);
			Position pos5temp = new Position(pos5);
			pos5temp.move(move1);
			Set<int[]> moves2 = pos5temp.validMoves();
			//System.out.println(pos5temp);
			for(int[] move2 : moves2) {
				count++;
				//System.out.println(count + ". " + move1 + ", " + Position.moveToNotation(move2));
			}
		}
		/*Position pos6 = Position.fromFEN("r3k2r/8/8/8/8/8/8/R3K2R w KQk - 0 1");
		moves = pos6.validMoves();
		assertTrue(pos6.attacked(0x03, Position.BLACK));
		for(int[] move : moves) {
			if(move[0] == 0x04 && move[1] == 0x02) {
				fail("Castling through check");
			}
		}*/
		
		Position pos6 = Position.fromFEN("8/8/8/8/8/8/6k1/4K2R b K - 0 1 ");
		assertEquals(32, Perft.perft(pos6, 2));
		assertEquals(134, Perft.perft(pos6, 3));
		assertEquals(2073, Perft.perft(pos6, 4));
	}
	
	@Test
	public void testOppositeColour() {
		assertEquals(Position.WHITE, Position.oppositeColour(Position.BLACK));
		assertEquals(Position.BLACK, Position.oppositeColour(Position.WHITE));
	}
	
	@Test
	public void testMoveToNotation() {
		assertEquals("e2e4", Position.moveToNotation(new int[] { 0x14, 0x34 }));
		assertEquals("f6g6", Position.moveToNotation(new int[] { 0x55, 0x56 }));
	}
	
	@Test
	public void testMoveFromNotation() {
		Position pos = new Position();
		pos.initialPosition();
		int[] move1 = pos.moveFromNotation("e2e4");
		assertEquals(0x14, move1[0]);
		assertEquals(0x34, move1[1]);
	}
	
	@Test
	public void testIsCheck() throws FENException {
		Position pos = Position.fromFEN("8/2p5/1K1p4/1P5r/1R3p1k/8/4P1P1/8 b - - 0 1 ");
		assertTrue(pos.attacks(0x62, 0x51));
		assertTrue(pos.isCheck(Position.WHITE));
		
		Position pos2 = Position.fromFEN("8/k7/8/1B6/8/8/8/K5Bb w - - 0 1");
		//System.out.println(pos2);
		assertTrue(pos2.attacked(0x60, Position.WHITE));
		//assertTrue(pos2.isCheck(Position.BLACK));
	}
		
	@Test
	public void testCopy() throws FENException, CloneNotSupportedException {
		Position pos = Position.fromFEN("8/2p5/1K1p4/1P5r/1R3p1k/8/4P1P1/8 b - - 0 1 ");
		Position pos2 = new Position(pos);
		pos2.move(new int[] { 0x14, 0x34 });
		assertEquals(Position.WHITE | Position.PAWN, pos.board[0x14]);
		assertTrue(pos2.whiteToMove);
		assertFalse(pos.whiteToMove);
	}
	
	@Test
	public void testEvaluate() throws FENException {
		Position pos = Position.fromFEN("4k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQk - 0 1");
		assertEquals(510, pos.evaluate());
		pos.whiteToMove = false;
		assertEquals(-510, pos.evaluate());
		Position pos2 = new Position();
		pos2.initialPosition();
		assertEquals(0, pos2.evaluate());
	}
	
	@Test
	public void testBestMove() throws FENException, CloneNotSupportedException {
		Position pos = Position.fromFEN(perftPos2);
		assertEquals(perftPos2, pos.toFEN(false));
		AlphaBetaSearch search = new AlphaBetaSearch();
		int[] a = search.bestMove(pos, 1);
		assertEquals(48, search.nodesSearched);
		System.out.println(Position.moveToNotation(a));
	}
	

}
