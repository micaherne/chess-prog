package uk.co.micaherne.eighteight.perft;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.micaherne.eighteight.Position;

public class Perft {

	public static int perft(Position pos, int depth) {
		if (depth == 0)
			return 1;
		int nodes = 0;
		int millionNodes = 0;
		Set<int[]> moves = new HashSet<int[]>();
		for (int[] move : pos.validMoves()) {
			Position pos2 = new Position(pos);
			pos2.move(move);
			// trim out checks
			if (pos2.whiteToMove) {
				if (pos2.isCheck(Position.BLACK))
					continue;
			} else {
				if (pos2.isCheck(Position.WHITE))
					continue;
			}
			nodes += perft(pos2, depth - 1);
			if (nodes - (millionNodes * 1000000) > 1000000) {
				millionNodes = nodes / 1000000;
				System.out.println(millionNodes * 1000000 + " nodes evaluated");
			}
		}
		return nodes;
	}

	public static List<String> divideIntoList(Position pos, int depth) {
		List<String> result = new ArrayList<String>();
		for (int[] move : pos.validMoves()) {
			Position pos2 = new Position(pos);
			pos2.move(move);
			String moveStr = Position.moveToNotation(move);
			result.add(String.format("%1$-6s", moveStr)
					+ perft(pos2, depth - 1));
		}
		Collections.sort(result);
		return result;
	}

	public static String divide(Position pos, int depth) {
		StringBuilder result = new StringBuilder();
		for (String line : divideIntoList(pos, depth)) {
			result.append(line);
			result.append("\n");
		}
		return result.toString();
	}



}
