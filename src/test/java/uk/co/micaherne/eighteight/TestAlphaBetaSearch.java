package uk.co.micaherne.eighteight;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.co.micaherne.FENException;

public class TestAlphaBetaSearch {

	@Test
	public void testBestMove() throws FENException {
		// Test for checkmates
		Position pos2 = Position.fromFEN("r1bqkbnr/ppp3pp/2np4/4p3/2B1P3/5Q2/PPPP1PPP/RNB1K1NR w KQkq - 0 1");
		AlphaBetaSearch search = new AlphaBetaSearch();
		int[] move = search.bestMove(pos2, 2);
		assertEquals("f3f7", Position.moveToNotation(move));
	}

	@Test
	public void testAlphaBeta() {
		//fail("Not yet implemented");
	}

}
