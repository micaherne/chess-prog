package uk.co.micaherne.bitboard;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.co.micaherne.FENException;
import uk.co.micaherne.NotationException;
import uk.co.micaherne.bitboard.BitboardPosition;
import uk.co.micaherne.bitboard.BitboardPosition.NotationType;

public class BitboardPositionTest {
	
	private static final String initialFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

	@Before
	public void setUp() {
		
	}
	
	@Test
	public void test() {
		long t = 0x0101010101010101L;
	}
	
	@Test
	public void bitIndexToSquareName() {
		assertEquals("a1", BitboardPosition.bitNumberToSquareName(0));
		assertEquals("e5", BitboardPosition.bitNumberToSquareName(36));
		assertEquals("a6", BitboardPosition.bitNumberToSquareName(40));
	}
	
	@Test
	public void squareNameToBitIndex() {
		assertEquals(0, BitboardPosition.squareNameToBitNumber("a1"));
		assertEquals(36, BitboardPosition.squareNameToBitNumber("e5"));
		assertEquals(40, BitboardPosition.squareNameToBitNumber("a6"));
	}
	
	@Test
	public void testFromFen() throws FENException {
		BitboardPosition initPos = BitboardPosition.fromFEN(initialFEN);
		assertEquals('r', initPos.toChar(56));
		assertEquals('R', initPos.toChar(0));
		BitboardPosition testPos = BitboardPosition.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
		System.out.println(testPos);
	}
	
	@Test
	public void testMove() throws NotationException {
		BitboardPosition initPos = BitboardPosition.fromFEN(initialFEN);
		initPos.move(12, 28);
		assertEquals('P', initPos.toChar(28));
		assertEquals(' ', initPos.toChar(12));
		
		initPos = BitboardPosition.fromFEN(initialFEN);
		initPos.move("e2e4", NotationType.LONG_ALGEBRAIC);
		initPos.move("d7d5", NotationType.LONG_ALGEBRAIC);
		initPos.move("e4d5", NotationType.LONG_ALGEBRAIC);
		System.out.println(initPos);
		assertEquals('P', initPos.toChar(35));
		assertEquals(' ', initPos.toChar(12));
	}
	
	@Test
	public void testBitboardToString() throws FENException {
		BitboardPosition initPos = BitboardPosition.fromFEN(initialFEN);
		System.out.println(BitboardPosition.bitboardToString(initPos.pieceBitboards[6]));
	}
	
	@Test
	public void testWhitePieces() throws FENException {
		BitboardPosition initPos = BitboardPosition.fromFEN(initialFEN);
		System.out.println(BitboardPosition.bitboardToString(initPos.blackPieces()));
	}

}
