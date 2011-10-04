package uk.co.micaherne.bitboard;

import static org.junit.Assert.*;

import org.junit.Test;

public class BitboardUtilsTest {

	@Test
	public void testGenerateKingMoves() {
		BitboardUtils u = new BitboardUtils();
		u.generateKingAttacks();
		assertEquals(770, u.king_attacks[0]);
		assertEquals(0x1c141c0000L, u.king_attacks[27]);
	}

	@Test
	public void testRank() {
		assertEquals(0, BitboardUtils.rank(0));
		assertEquals(1, BitboardUtils.rank(8));
		assertEquals(7, BitboardUtils.rank(63));
	}

	@Test
	public void testFile() {
		assertEquals(0, BitboardUtils.file(0));
		assertEquals(5, BitboardUtils.file(5));
		assertEquals(5, BitboardUtils.file(13));
	}

}
