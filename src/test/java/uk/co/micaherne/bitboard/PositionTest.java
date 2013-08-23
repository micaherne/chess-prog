package uk.co.micaherne.bitboard;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PositionTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testResetPosition() {
		//fail("Not yet implemented");
	}

	@Test
	public void testFromFEN() {
		String startPos = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		Position pos = new Position();
		pos.fromFEN(startPos);
		assertEquals(0xFF00, pos.BBPawns & pos.BBWhitePieces);
	}

}
