package uk.co.micaherne.eighteight.perft;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.micaherne.FENException;
import uk.co.micaherne.eighteight.Position;

public class PerftDebugger {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		PerftDebugger m = new PerftDebugger();
		m.run();
	}

	public void run() throws IOException {
		String perftline = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1 ;D1 48 ;D2 2039 ;D3 97862 ;D4 4085603 ;D5 193690690";
		//String perftline = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1pB1P3/2N2Q1p/PPPB1PPP/R3K2R b KQkq - 1 1 ;D1 41 ;D2 2082 ;D3 84835 ;D4 4085603 ;D5 193690690";
		 //String perftline = "r3k2r/p2pqpb1/bnP1pnp1/2p1N3/1pB1P3/2N2Q1p/PPPB1PPP/R3K2R b KQkq - 1 2 ;D1 39 ;D2 2048 ;D3 78898";
		String[] parts = perftline.split(";");
		Position pos;
		try {
			pos = Position.fromFEN(perftline);
		} catch (FENException e) {
			e.printStackTrace();
			return;
		}
		List<Integer> correct = new ArrayList<Integer>();
		correct.add(0);
		for (int i = 1; i < parts.length; i++) {
			correct.add(Integer.parseInt(parts[i].substring(3).trim()));
		}
		for (int depth = 1; depth <= 3; depth++) {
			int perft = Perft.perft(pos, depth);
			if (perft != correct.get(depth).intValue()) {
				findMissingMoves(pos, depth);
			} else {
				System.out.println("Depth " + depth + " OK. ");
			}
		}
	}

	public Map<String, Integer> getDivideFromInput(Position pos, int depth)
			throws IOException {
		System.out.println("Please get the following divide:");
		System.out.println(pos.toFEN(true));
		System.out.println("Depth: " + depth);

		HashMap<String, Integer> result = new HashMap<String, Integer>();
		String line = null;
		InputStreamReader in = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(in);
		while (!"go".equals(line = br.readLine())) {
			String[] parts = line.split(" ");
			result.put(parts[0], Integer.parseInt(parts[1].trim()));
		}
		return result;
	}

	public void findMissingMoves(Position pos, int depth) throws IOException {
		Map<String, Integer> correctDivide = getDivideFromInput(pos, depth);
		System.out.println("Dividing...");
		List<String> divide = Perft.divideIntoList(pos, depth);
		Map<String, Integer> wrongDivide = new HashMap<String, Integer>();
		for (String line : divide) {
			line = line.trim();
			if ("".equals(line) || "go".equals(line))
				break;
			String[] parts = line.split(" +");
			try {
				wrongDivide.put(parts[0].trim(),
						Integer.parseInt(parts[1].trim()));
			} catch (Exception e) {
				break;
			}
		}
		for (String move : correctDivide.keySet()) {
			if (wrongDivide.get(move) == null) {
				System.out.println(move + " was not generated.");
			} else if (wrongDivide.get(move) - correctDivide.get(move) == 0) {
				System.out.println(move + " OK");
			} else {
				// System.out.println(move + ". Correct: " +
				// correctDivide.get(move) + ", Wrong: " +
				// wrongDivide.get(move));
				System.out.println("Wrong number of moves for " + move);
				if (depth > 0) {
					Position newPos = new Position(pos);
					pos.move(move);
					findMissingMoves(newPos, depth - 1);
				}
			}
		}
	}

}
