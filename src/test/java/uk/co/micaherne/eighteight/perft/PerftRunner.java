package uk.co.micaherne.eighteight.perft;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import org.junit.Test;

import uk.co.micaherne.FENException;
import uk.co.micaherne.eighteight.Position;

public class PerftRunner {

	private BufferedReader reader;

	@Test
	public void testLoadEpd() {
		reader = new BufferedReader(new InputStreamReader(
			    getClass().getClassLoader().getResourceAsStream(
			        "uk/co/micaherne/eighteight/perft/perftsuite.epd")));
		assertNotNull(reader);
	}
	
	@Test
	public void testReadFile() throws IOException {
		testLoadEpd();
		String line;
		int lineCount = 0;
		String hundredthLine = null;
		while((line = reader.readLine()) != null) {
			lineCount++;
			if(lineCount == 100) hundredthLine = line;
		}
		String[] bits = hundredthLine.split(" ");
		assertEquals("k7/8/7p/8/8/6P1/8/K7", bits[0]);
	}
	
	//@Test
	public void testLevel3() throws FENException, IOException {
		testLevel(3);
	}
	
    /* Currently failing on position 3 after about 4000000 nodes:
            *e5c4 : us: 77751, them: 77752
            *c7c5: us: 1758, them: 1759
            *e2c4 : us: 84834, them: 84835 */
	//@Test
	public void testLevel4() throws FENException, IOException {
		testLevel(4);
	}
	
	@Test
	public void testLevel5() throws FENException, IOException {
		testLevel(5);
	}
	
	public void testLevel(int depth) throws FENException, IOException {
		testLoadEpd();
		String line;
		int count = 0;
		while((line = reader.readLine()) != null) {
			count++;
			if(line.startsWith("#")) continue;
			System.out.println("Perft " + depth + ": " + count);
			String[] bits = line.split(";");
			Position pos = Position.fromFEN(bits[0]);
			int perft = Perft.perft(pos, depth);
			assertEquals("Testing line " + count, bits[depth].trim(), "D" + depth + " " + perft);
		}
	}
	
	@Test
	public void testCount() throws NumberFormatException, IOException {
		testLoadEpd();
		String line;
		int count = 0;
		while((line = reader.readLine()) != null) {
			String[] bits = line.split(";");
			int i = Integer.parseInt(bits[5].trim().substring(3));
			count += i;
		}
		System.out.println(count);
	}
	
	//@Test
	public void testPos2() throws FENException {
		Position pos = Position.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
		assertEquals(48, Perft.perft(pos, 1));
		assertEquals(2039, Perft.perft(pos, 2));
		assertEquals(97862, Perft.perft(pos, 3));
		assertEquals(4085603, Perft.perft(pos, 4));
		
	}
	
	
	@Test
	public void testDivide() throws FENException {
		// Orig: Position pos = Position.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
		// e5c4 Position pos = Position.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/3P4/1pN1P3/2N2Q1p/PPPBBPPP/R3K2R b KQkq - 1 1 ");
		Position pos = Position.fromFEN("r3k2r/p2pqpb1/bn2pnp1/2pP4/1pN1P3/2N2Q1p/PPPBBPPP/R3K2R b KQkq c6 1 1");
		 
		//System.out.println(Perft.divide(pos, 2));
	}


}
