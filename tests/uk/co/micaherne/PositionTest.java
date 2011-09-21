package uk.co.micaherne;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

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
		System.out.println(initialPos.toString());
	}

}
