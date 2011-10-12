package uk.co.micaherne.eighteight;

import static org.junit.Assert.*;

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
		System.out.println(io.currentPosition);
		int[] bestMove = io.currentPosition.bestMove(5);
		assertNotNull(bestMove);
		System.out.println(Position.moveToNotation(bestMove));
	}

	@Test
	public void testStartInput() {
		//fail("Not yet implemented");
	}

	@Test
	public void testDoInput() throws UCIException {
		//fail("Not yet implemented");
		io.doInput("position startpos moves e2e4 e7e5");
		//System.out.println(io.currentPosition);
	}

	@Test
	public void testGetCurrentPosition() {
		//fail("Not yet implemented");
	}

}
