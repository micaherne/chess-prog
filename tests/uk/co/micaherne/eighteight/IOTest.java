package uk.co.micaherne.eighteight;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.co.micaherne.UCIException;

public class IOTest {

	private IO io;

	@Before
	public void setUp() throws Exception {
		this.io = new IO();
	}

	@Test
	public void testIO() throws UCIException, CloneNotSupportedException {
		io.doInput("position startpos moves e2e4 h7h5 d2d4");
		//System.out.println(io.currentPosition);
		int[] bestMove = new AlphaBetaSearch().bestMove(io.currentPosition, 5);
		assertNotNull(bestMove);
		//System.out.println(Position.moveToNotation(bestMove));
	}

	@Test
	public void testStartInput() {
		//fail("Not yet implemented");
	}

	@Test
	public void testDoInput() throws UCIException {
		//fail("Not yet implemented");
		io.doInput("position startpos moves d2d4 e7e6 e2e4 c7c5 g1f3");
		assertFalse(io.currentPosition.whiteToMove);
		io.doInput("position startpos moves e2e4");
		assertFalse(io.currentPosition.whiteToMove);
		Set<int[]> mvs = io.currentPosition.pseudoValidMoves();
		for(int[] move : mvs) {
			System.out.println(Integer.toHexString(move[0]) + " to " + Integer.toHexString(move[1]));
		}
	}

	@Test
	public void testGetCurrentPosition() {
		//fail("Not yet implemented");
	}
	
	@Test
	public void testMoveGen() throws UCIException {
		io.doInput("position startpos moves e2e4 h7h6 d2d4 e7e6 b1c3 f8b4 g1f3 c7c5 f1b5 d8a5 d1e2 g8e7 c1d2 f7f5 e4f5");
		for(int[] move : io.currentPosition.pseudoValidMoves()) {
			if("h8h6".equals(Position.moveToNotation(move))) {
				fail("Invalid move found");
			}
		}
	}

}
