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
	
	@Test
	public void testLevel1() throws IOException, FENException {
		testLoadEpd();
		String line;
		int count = 0;
		while((line = reader.readLine()) != null) {
			count++;
			String[] bits = line.split(";");
			Position pos = Position.fromFEN(bits[0]);
			int perft = Perft.perft(pos, 1);
			assertEquals("Testing line " + count, bits[1].trim(), "D1 " + perft);
		}
	}
	
	@Test
	public void testLevel2() throws FENException, IOException {
		testLoadEpd();
		String line;
		int count = 0;
		while((line = reader.readLine()) != null) {
			count++;
			String[] bits = line.split(";");
			Position pos = Position.fromFEN(bits[0]);
			int perft = Perft.perft(pos, 2);
			assertEquals("Testing line " + count, bits[2].trim(), "D2 " + perft);
		}
	}
	
	

}
