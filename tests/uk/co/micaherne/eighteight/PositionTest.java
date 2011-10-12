package uk.co.micaherne.eighteight;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.co.micaherne.FENException;

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
	}
	
	@Test
	public void testPieceToString() {
		char x = Position.pieceToChar((byte)2);
		assertEquals('r', x);
	}
	
	@Test
	public void move() {
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

	}
	
	@Test
	public void testFromFEN() throws FENException{
		Position pos = Position.fromFEN(perftPos2);
		assertEquals(Position.CAN_CASTLE, pos.board[0x00] & Position.CAN_CASTLE);
		assertEquals(0, pos.board[0x01] & Position.CAN_CASTLE);
		assertTrue(pos.whiteToMove);
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
		}
	
	@Test
	public void testAttacked() throws FENException {
		Position pos2 = Position.fromFEN(perftPos2);
		assertFalse(pos2.attacked(0x05, 0));
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
	}
	
	@Test
	public void testMisc() {
		assertTrue(Arrays.binarySearch(Position.DIRECTIONS_KNIGHT, Position.DIRECTION_EASTNORTHEAST) > 0);
		int pieceMoved = Position.KING | Position.BLACK | Position.CAN_CASTLE;
		pieceMoved &= ~Position.CAN_CASTLE;
		assertEquals(Position.KING | Position.BLACK, pieceMoved);
	}
	
	//@Test
	public void testMoveGen() throws FENException {
		Position pos = new Position();
		pos.initialPosition();
		assertTrue(pos.whiteToMove);
		System.out.println(pos);
		Set<int[]> moves = pos.validMoves();
		assertEquals(20, moves.size());
		pos = Position.fromFEN(perftPos2);
		moves = pos.validMoves();
		assertEquals(48, moves.size());
		pos = Position.fromFEN(perftPos3);
		moves = pos.validMoves();
		int count = 0;
		for(int[] move : moves) {
			pos.move(move);
			if(!pos.isCheck(Position.WHITE)) {
				count++;
			} else {
				//System.out.println("Rejecting: " + Position.moveToNotation(move));
			}
			pos.undoMove();
		}
		assertEquals(14, count);
		pos = Position.fromFEN(perftPos4);
		moves = pos.validMoves();
		count = 0;
		for(int[] move : moves) {
			pos.move(move);
			if(!pos.isCheck(Position.WHITE)) {
				count++;
			} else {
				//System.out.println("Rejecting: " + Position.moveToNotation(move));
			}
			pos.undoMove();
		}
		assertEquals(6, count);
		
	}
	
	@Test
	public void testOppositeColour() {
		assertEquals(Position.WHITE, Position.oppositeColour(Position.BLACK));
		assertEquals(Position.BLACK, Position.oppositeColour(Position.WHITE));
	}
	
	@Test
	public void testMoveToNotation() {
		assertEquals("e2e4", Position.moveToNotation(new int[] { 0x14, 0x34 }));
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
	}
	
	@Test
	public void testClone() throws FENException, CloneNotSupportedException {
		Position pos = Position.fromFEN("8/2p5/1K1p4/1P5r/1R3p1k/8/4P1P1/8 b - - 0 1 ");
		Position pos2 = pos.clone();
		pos2.move(new int[] { 0x14, 0x34 });
	}
	
	@Test
	public void testBestMove() throws FENException, CloneNotSupportedException {
		Position pos = Position.fromFEN(perftPos2);
		assertEquals(perftPos2, pos.toFEN(false));
		System.out.println(Position.moveToNotation(pos.bestMove(3)));
	}
	
	@Test
	public void testPerft() {
		Position pos = new Position();
		pos.initialPosition();
		assertEquals(20, perft(pos, 1));
		assertEquals(400, perft(pos, 2));
		assertEquals(8902, perft(pos, 3));
		assertEquals(197281, perft(pos, 4));
		assertEquals(4865609, perft(pos, 5));
		assertEquals(119060324, perft(pos, 6));
	}
	
	public int perft(Position pos, int depth) {
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

}
