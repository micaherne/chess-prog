package uk.co.micaherne;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Position implements Cloneable {

	private char[][] pieces; // rank, file
	private Colour sideToMove = Colour.WHITE;
	private String castling = "KQkq";
	private int[] epSquare = null;
	private int halfmove = 0;
	private int fullmove = 1;

	// CONSTANTS
	// piece names
	private static final char[] WHITE_PIECES = new char[] { 'B', 'K', 'N', 'P',
			'Q', 'R' };
	private static final char[] BLACK_PIECES = new char[] { 'b', 'k', 'n', 'p',
			'q', 'r' };
	
	public static final String initialFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

	public static enum Colour {
		BLACK, WHITE
	}

	public static enum NotationType {
		COORDINATE, LONG_ALGEBRAIC
	}

	public Position() {
		this.pieces = new char[8][8];
	}
	
	public Position(Position other) {
		this.pieces = new char[8][8];
		for(int i = 0; i < other.pieces.length; i++) {
			System.arraycopy(other.pieces[i], 0, this.pieces[i], 0, other.pieces[i].length);
		}
		sideToMove = other.sideToMove;
		castling = other.castling;
		epSquare = other.epSquare;
		halfmove = other.halfmove;
		fullmove = other.fullmove;
	}

	public static Position fromFEN(String fen) throws FENException {
		Position result = new Position();
		String[] fenParts = fen.split(" ");
		String[] boardParts = fenParts[0].split("\\/");

		for (int i = 0; i < 8; i++) {
			String rank = null;
			try {
				rank = boardParts[7 - i];
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new FENException("Not enough ranks");
			}
			StringBuilder builder = new StringBuilder();
			for (String c : rank.split("")) {
				if (!c.matches("\\d")) {
					builder.append(c);
				} else {
					int cint = Integer.parseInt(c);
					while (cint > 0) {
						builder.append(" ");
						cint--;
					}
				}
			}
			if (builder.length() != 8) {
				throw new FENException("Not enough files");
			}
			for (int j = 0; j < 8; j++) {
				result.pieces[i][j] = builder.charAt(j);
			}
		}

		result.sideToMove = "w".equals(fenParts[1]) ? Colour.WHITE
				: Colour.BLACK;
		result.castling = fenParts[2];
		if (!"-".equals(fenParts[3])) {
			result.epSquare[1] = fenParts[3].charAt(0) - 'a';
			result.epSquare[0] = fenParts[3].charAt(1);
		}

		if (fenParts.length > 4) {
			try {
				result.halfmove = Integer.parseInt(fenParts[4]);
			} catch (NumberFormatException e) {
				throw new FENException("halfmove must be integer", e);
			}
		}

		if (fenParts.length > 5) {
			try {
				result.fullmove = Integer.parseInt(fenParts[5]);
			} catch (NumberFormatException e) {
				throw new FENException("fullmove must be integer", e);
			}
		}
		return result;
	}

	public String toFEN(boolean full) {
		StringBuilder result = new StringBuilder();
		for (int i = 7; i >= 0; i--) {
			int spaceCount = 0;
			for (int j = 0; j < 8; j++) {
				char p = pieces[i][j];
				if (p == ' ') {
					spaceCount++;
					continue;
				} else {
					if (spaceCount > 0) {
						result.append(spaceCount);
						spaceCount = 0;
					}
					result.append(p);
				}
			}
			if (spaceCount > 0) {
				result.append(spaceCount);
				spaceCount = 0;
			}
			if (i > 0) {
				result.append('/');
			}
		}

		result.append(' ');
		result.append(sideToMove == Colour.WHITE ? 'w' : 'b');
		result.append(' ');
		if (castling.length() > 0) {
			result.append(castling);
		} else {
			result.append("-");
		}
		result.append(' ');
		if (epSquare == null) {
			result.append('-');
		} else {
			result.append('a' + epSquare[1]);
			result.append(epSquare[0]);
		}
		if (full) {
			result.append(' ');
			result.append(halfmove);
			result.append(' ');
			result.append(fullmove);
		}

		return result.toString();
	}

	public String toFEN() {
		return toFEN(true);
	}

	public char getPiece(int rank, int file) {
		return this.pieces[rank - 1][file - 1];
	}

	public char getPiece(int[] pos) {
		return this.pieces[pos[0]][pos[1]];
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 7; i >= 0; i--) {
			for (int j = 0; j < 8; j++) {
				builder.append(this.pieces[i][j]);
			}
			if (i != 0)
				builder.append("\n");
		}
		return builder.toString();
	}

	public String moveNotation(int[] move, NotationType notationType) {
		StringBuilder result = new StringBuilder();
		char pieceMoved = pieces[move[0]][move[1]];
		if (pieceMoved == 'k' || pieceMoved == 'K') {
			if (Arrays.equals(move, new int[] { 0, 4, 0, 2 })
					|| Arrays.equals(move, new int[] { 7, 4, 7, 2 })) {
				return "0-0-0";
			} else if (Arrays.equals(move, new int[] { 0, 4, 0, 6 })
					|| Arrays.equals(move, new int[] { 7, 4, 7, 6 })) {
				return "0-0";
			}
		}
		if (NotationType.LONG_ALGEBRAIC.equals(notationType)) {
			if (pieceMoved != 'p' && pieceMoved != 'P') {
				String pieceMovedUpper = String.valueOf(pieceMoved)
						.toUpperCase();
				result.append(pieceMovedUpper);
			}
			result.append(Character.toChars(97 + move[1]));
			result.append(move[0] + 1);
			result.append('-');
			result.append(Character.toChars(97 + move[3]));
			result.append(move[2] + 1);
		}
		return result.toString();
	}

	public void move(int[] move) {
		if (sideToMove == Colour.WHITE) {
			sideToMove = Colour.BLACK;
		} else if (sideToMove == Colour.BLACK) {
			sideToMove = Colour.WHITE;
		}

		if (sideToMove == Colour.WHITE) {
			fullmove++;
		}

		char movedPiece = this.pieces[move[0]][move[1]];
		char takenPiece = this.pieces[move[2]][move[3]];

		// Castling - rook moves
		if (move[0] == 0 && move[1] == 0 && castling.contains("Q")) {
			castling = castling.replace("Q", "");
		}
		if (move[0] == 0 && move[1] == 7 && castling.contains("K")) {
			castling = castling.replace("K", "");
		}
		if (move[0] == 7 && move[1] == 0 && castling.contains("q")) {
			castling = castling.replace("q", "");
		}
		if (move[0] == 7 && move[1] == 7 && castling.contains("k")) {
			castling = castling.replace("k", "");
		}

		// Castling - king moves
		if (movedPiece == 'K') {
			castling = castling.replace("K", "").replace("Q", "");
		}
		if (movedPiece == 'k') {
			castling = castling.replace("k", "").replace("q", "");
		}

		if (movedPiece == 'p' || movedPiece == 'P' || takenPiece != ' ') {
			halfmove = 0;
		} else {
			halfmove++;
		}
		this.pieces[move[0]][move[1]] = ' ';
		this.pieces[move[2]][move[3]] = movedPiece;
		
		// Do castling
		if(movedPiece == 'K' && Math.abs(move[3] - move[1]) == 2) {
			if(move[3] == 6) {
				this.pieces[0][7] = ' ';
				this.pieces[0][5] = 'R';
			}
			if(move[3] == 2) {
				this.pieces[0][0] = ' ';
				this.pieces[0][3] = 'R';
			}
		} else if(movedPiece == 'k' && Math.abs(move[3] - move[1]) == 2) {
			if(move[3] == 6) {
				this.pieces[7][7] = ' ';
				this.pieces[7][5] = 'r';
			}
			if(move[3] == 2) {
				this.pieces[7][0] = ' ';
				this.pieces[7][3] = 'r';
			}
		}
	}

	public void move(String move) throws NotationException {
		move(move, Position.NotationType.COORDINATE);
	}

	public void move(String move, NotationType notationType)
			throws NotationException {
		if (notationType == NotationType.COORDINATE) {
			if (move.length() != 5) {
				throw new NotationException(
						"Co-ordinate notation must be in form XX-XX");
			}
			if (!move.toUpperCase().equals(move)) {
				throw new NotationException(
						"Co-ordinate notation must be in upper case");
			}
			String[] parts = move.split("-");
			if (parts.length != 2) {
				throw new NotationException(
						"Co-ordinate notation must have only 2 parts");
			}
			int[] from = coordPair(parts[0]);
			int[] to = coordPair(parts[1]);

			if (sideToMove == Colour.WHITE) {
				sideToMove = Colour.BLACK;
			} else if (sideToMove == Colour.BLACK) {
				sideToMove = Colour.WHITE;
			}

			if (sideToMove == Colour.WHITE) {
				fullmove++;
			}

			char movedPiece = this.pieces[from[0]][from[1]];
			char takenPiece = this.pieces[to[0]][to[1]];

			// Castling - rook moves
			if (from[0] == 0 && from[1] == 0 && castling.contains("Q")) {
				castling = castling.replace("Q", "");
			}
			if (from[0] == 0 && from[1] == 7 && castling.contains("K")) {
				castling = castling.replace("K", "");
			}
			if (from[0] == 7 && from[1] == 0 && castling.contains("q")) {
				castling = castling.replace("q", "");
			}
			if (from[0] == 7 && from[1] == 7 && castling.contains("k")) {
				castling = castling.replace("k", "");
			}

			// Castling - king moves
			if (movedPiece == 'K') {
				castling = castling.replace("K", "").replace("Q", "");
			}
			if (movedPiece == 'k') {
				castling = castling.replace("k", "").replace("q", "");
			}

			if (movedPiece == 'p' || movedPiece == 'P' || takenPiece != ' ') {
				halfmove = 0;
			} else {
				halfmove++;
			}
			this.pieces[to[0]][to[1]] = movedPiece;
			this.pieces[from[0]][from[1]] = ' ';
		} else if(NotationType.LONG_ALGEBRAIC.equals(notationType)) {
				if (move.length() < 4 || move.length() > 5) {
					throw new NotationException(
							"Long algebraic notation must be in form XXXX");
				}
				if (!move.toLowerCase().equals(move)) {
					throw new NotationException(
							"Long algebraic notation must be in lower case");
				}
				
				int[] m = stringToMove(move, NotationType.LONG_ALGEBRAIC);
				move(m);
		}
	}

	private int[] stringToMove(String moveString, NotationType notationType) {
		int[] result = new int[5];
		switch (notationType) {
		case LONG_ALGEBRAIC:
			// These look the wrong way round, but are correct!
			result[0] = moveString.charAt(1) - '1';
			result[1] = moveString.charAt(0) - 'a';
			result[2] = moveString.charAt(3) - '1';
			result[3] = moveString.charAt(2) - 'a';
			if(moveString.length() == 5) {
				result[4] = moveString.charAt(4);
			}
			break;

		default:
			break;
		}
		
		return result;
	}

	/**
	 * Find all pseudo-valid moves in the current position
	 * @return
	 */
	public Set<int[]> allPseudoValidMoves() {
		Set<int[]> result = new HashSet<int[]>();
		char[] pieceNames;
		if (sideToMove == Colour.WHITE) {
			pieceNames = WHITE_PIECES;
		} else if (sideToMove == Colour.BLACK) {
			pieceNames = BLACK_PIECES;
		} else {
			return null;
		}

		for (char pieceName : pieceNames) {
			Set<int[]> positions = piecePositions(pieceName);
			for (int[] pos : positions) {
				result.addAll(validMoves(pos));
			}
		}

		return result;
	}
	
	public boolean inCheck(Colour colour) {
		char ownKing;
		if(colour == Colour.WHITE) {
			ownKing = 'K';
		} else {
			ownKing = 'k';
		}
		Set<int[]> moves = allPseudoValidMoves();
		for(int[] move : moves) {
			char attackedPiece = getPiece(new int[] { move[2], move[3]} );
			if(attackedPiece == ' ') continue;
			if(attackedPiece == ownKing ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Find all valid moves for the piece at the given position
	 * 
	 * @param pos
	 *            array of rank, file
	 * @return Set of all valid moves as 4-long arrays
	 */
	public Set<int[]> validMoves(int[] pos) {
		HashSet<int[]> result = new HashSet<int[]>();

		char piece = pieces[pos[0]][pos[1]];
		if (piece == ' ') {
			return result;
		}

		if (piece == 'P') {
			validWhitePawnMoves(pos, result);
		} else if (piece == 'p') {
			validBlackPawnMoves(pos, result);
		} else if (piece == 'n' || piece == 'N') { // knight
			validKnightMoves(pos, result);
		} else if (piece == 'b' || piece == 'B') {
			validBishopMoves(pos, result);
		} else if (piece == 'r' || piece == 'R') {
			validRookMoves(pos, result);
		} else if (piece == 'q' || piece == 'Q') {
			validBishopMoves(pos, result);
			validRookMoves(pos, result);
		} else if (piece == 'k' || piece == 'K') {
			validKingMoves(pos, result);
		}
		
		return result;
	}

	private void validKingMoves(int[] pos, HashSet<int[]> result) {
		for (int x = -1; x <= 1; x++) {

			for (int y = -1; y <= 1; y++) {

				int[] possible = new int[] { pos[0] + x, pos[1] + y };
				if (validSquare(possible)) {
					if (getPiece(possible) == ' '
							|| pieceColour(getPiece(possible)) == oppositeColour(sideToMove)) {
						result.add(positionsToMove(pos, possible));
					}
				}

			}

		}

		// Castling
		if (getPiece(pos) == 'K' && Arrays.equals(pos, new int[] { 0, 4 })) {
			if (getPiece(1, 6) == ' ' && getPiece(1, 7) == ' '
					&& castling.contains("K")) {
				result.add(new int[] { 0, 4, 0, 6 });
			}
			if (getPiece(1, 4) == ' ' && getPiece(1, 3) == ' '
					&& getPiece(1, 2) == ' ' && castling.contains("Q")) {
				result.add(new int[] { 0, 4, 0, 2 });
			}
		}
		if (getPiece(pos) == 'k' && Arrays.equals(pos, new int[] { 7, 4 })) {
			if (getPiece(8, 6) == ' ' && getPiece(8, 7) == ' '
					&& castling.contains("k")) {
				result.add(new int[] { 7, 4, 7, 6 });
			}
			if (getPiece(8, 4) == ' ' && getPiece(8, 3) == ' '
					&& getPiece(8, 2) == ' ' && castling.contains("q")) {
				result.add(new int[] { 7, 4, 7, 2 });
			}
		}

	}

	private void validWhitePawnMoves(final int[] pos, HashSet<int[]> result) {
		int[] possible = new int[] { pos[0] + 1, pos[1] }; // forward one
		if (getPiece(possible) == ' ') {
			result.add(positionsToMove(pos, possible));
		}
		possible = new int[] { pos[0] + 2, pos[1] };
		int[] intermediateSquare = new int[] { pos[0] + 1, pos[1]};
		if (pos[0] == 1 && getPiece(possible) == ' ' && getPiece(intermediateSquare) == ' ') { // forward two
			result.add(positionsToMove(pos, possible));
		}
		possible = new int[] { pos[0] + 1, pos[1] - 1 };
		if (pos[1] > 0 && isBlack(getPiece(possible))) { // take to left
			result.add(positionsToMove(pos, possible));
		}
		if (Arrays.equals(possible, epSquare)) { // take to left e.p.
			result.add(positionsToMove(pos, possible));
		}
		possible = new int[] { pos[0] + 1, pos[1] + 1 }; // take to right
		if (pos[1] < 7 && isBlack(getPiece(possible))) {
			result.add(positionsToMove(pos, possible));
		}
		if (Arrays.equals(possible, epSquare)) { // take to right e.p.
			result.add(positionsToMove(pos, possible));
		}
	}

	private void validBlackPawnMoves(final int[] pos, HashSet<int[]> result) {
		int[] possible = new int[] { pos[0] - 1, pos[1] }; // forward one
		if (getPiece(possible) == ' ') {
			result.add(positionsToMove(pos, possible));
		}
		possible = new int[] { pos[0] - 2, pos[1] };
		int[] intermediateSquare = new int[] { pos[0] - 1, pos[1]};
		if (pos[0] == 6 && getPiece(possible) == ' ' && getPiece(intermediateSquare) == ' ') { // forward two
			result.add(positionsToMove(pos, possible));
		}
		possible = new int[] { pos[0] - 1, pos[1] + 1 };
		if (pos[1] < 7 && isWhite(getPiece(possible))) { // take to left
			result.add(positionsToMove(pos, possible));
		}
		if (Arrays.equals(possible, epSquare)) { // take to left e.p.
			result.add(positionsToMove(pos, possible));
		}
		possible = new int[] { pos[0] - 1, pos[1] - 1 }; // take to right
		if (pos[1] > 0 && isWhite(getPiece(possible))) {
			result.add(positionsToMove(pos, possible));
		}
		if (Arrays.equals(possible, epSquare)) { // take to right e.p.
			result.add(positionsToMove(pos, possible));
		}
	}

	private void validKnightMoves(final int[] pos, HashSet<int[]> result) {
		for (int one = -1; one <= 1; one = one + 2) {

			for (int two = -2; two <= 2; two = two + 4) {

				int[] possible = new int[] { pos[0] + one, pos[1] + two };
				if (validSquare(possible)) {
					if (getPiece(possible) == ' '
							|| pieceColour(getPiece(possible)) == oppositeColour(sideToMove)) {
						result.add(positionsToMove(pos, possible));
					}
				}

				possible = new int[] { pos[0] + two, pos[1] + one };
				if (validSquare(possible)) {
					if (getPiece(possible) == ' '
							|| pieceColour(getPiece(possible)) == oppositeColour(sideToMove)) {
						result.add(positionsToMove(pos, possible));
					}
				}

			}

		}
	}

	private void validRookMoves(final int[] pos, HashSet<int[]> result) {
		for (int x = -1; x <= 1; x = x + 2) {

			for (int[] possible = new int[] { pos[0] + x, pos[1] }; validSquare(possible); possible[0] += x) {

				if (getPiece(possible) == ' ') {
					result.add(positionsToMove(pos, possible));
				} else {
					if (pieceColour(getPiece(possible)) == oppositeColour(sideToMove)) {
						result.add(positionsToMove(pos, possible));
					}
					break;
				}
			}

		}

		for (int y = -1; y <= 1; y = y + 2) {

			for (int[] possible = new int[] { pos[0], pos[1] + y }; validSquare(possible); possible[1] += y) {

				if (getPiece(possible) == ' ') {
					result.add(positionsToMove(pos, possible));
				} else {
					if (pieceColour(getPiece(possible)) == oppositeColour(sideToMove)) {
						result.add(positionsToMove(pos, possible));
					}
					break;
				}
			}

		}
	}

	private void validBishopMoves(final int[] pos, HashSet<int[]> result) {
		for (int x = -1; x <= 1; x = x + 2) {

			for (int y = -1; y <= 1; y = y + 2) {

				for (int[] possible = new int[] { pos[0] + x, pos[1] + y }; validSquare(possible); possible[0] += x, possible[1] += y) {

					if (getPiece(possible) == ' ') {
						result.add(positionsToMove(pos, possible));
					} else {
						if (pieceColour(getPiece(possible)) == oppositeColour(sideToMove)) {
							result.add(positionsToMove(pos, possible));
						}
						break;
					}
				}

			}

		}
	}

	public static boolean validSquare(int[] possible) {
		return possible[0] >= 0 && possible[0] < 8 && possible[1] >= 0
				&& possible[1] < 8;
	}

	public int[] bestMove() {
		Set<int[]> moves = allPseudoValidMoves();
		float bestEval;
		if(sideToMove == Colour.WHITE) {
			bestEval = -1000000;
		} else {
			bestEval = 1000000;
		}
		int[] bestMove = null;
		for(int[] testMove : moves) {
			Position resultingPosition = new Position(this);
			resultingPosition.move(testMove);
			if(resultingPosition.inCheck(sideToMove)) {
				continue;
			}
			float moveEval = resultingPosition.evaluate();
			if(sideToMove == Colour.WHITE && moveEval > bestEval) {
				bestEval = moveEval;
				bestMove = testMove;
			} else if(sideToMove == Colour.BLACK && moveEval < bestEval) {
				bestEval = moveEval;
				bestMove = testMove;
			}
		}
		return bestMove;
	}

	private static int[] positionsToMove(int[] from, int[] to) {
		int[] result = Arrays.copyOf(from, 4);
		result[2] = to[0];
		result[3] = to[1];
		return result;
	}

	public static Colour oppositeColour(Colour colour) {
		if (Colour.WHITE.equals(colour)) {
			return Colour.BLACK;
		} else if (Colour.BLACK.equals(colour)) {
			return Colour.WHITE;
		} else {
			return null;
		}
	}

	public static Colour pieceColour(char piece) {
		if (isWhite(piece)) {
			return Colour.WHITE;
		} else if (isBlack(piece)) {
			return Colour.BLACK;
		} else {
			return null;
		}
	}

	public static boolean isWhite(char piece) {
		return Arrays.binarySearch(WHITE_PIECES, piece) >= 0;
	}

	public static boolean isBlack(char piece) {
		return Arrays.binarySearch(BLACK_PIECES, piece) >= 0;
	}

	public Set<int[]> piecePositions(char piece) {
		Set<int[]> result = new HashSet<int[]>();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (pieces[i][j] == piece) {
					result.add(new int[] { i, j });
				}
			}
		}
		return result;
	}

	private int[] coordPair(String coord) throws NotationException {
		if (coord.length() != 2) {
			throw new NotationException(
					"Co-ordinates must be 2 characters long");
		}
		int[] result = new int[2];
		// These look the wrong way round, but are correct!
		result[0] = coord.charAt(1) - '1';
		result[1] = coord.charAt(0) - 'A';
		return result;
	}
	
	public float evaluate() {
		
		// Test material
		float blackMaterial = 0;
		float whiteMaterial = 0;
		
		for(int i = 1; i <= 8; i++) {
			
			for(int j = 1; j <= 8; j++) {
				
				char piece = getPiece(i, j);
				switch (piece) {
				case 'p':
					blackMaterial++;
					break;
				case 'P':
					whiteMaterial++;
					break;
				case 'r':
					blackMaterial += 500;
					break;
				case 'R':
					whiteMaterial += 500;
					break;
				case 'n':
					blackMaterial += 300;
					break;
				case 'N':
					whiteMaterial += 300;
					break;
				case 'b':
					blackMaterial += 320;
					break;
				case 'B': 
					whiteMaterial += 320;
					break;
				case 'q':
					blackMaterial += 940;
					break;
				case 'Q':
					whiteMaterial += 940;
					break;
				case 'k':
					blackMaterial += 1000000;
					break;
				case 'K':
					whiteMaterial += 1000000;
					break;
				default:
					break;
				}
			}
			
		}
		
		return whiteMaterial / (whiteMaterial + blackMaterial);
	}
	

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		return this.toFEN().equals(other.toFEN());
	}

	public Position clone() {
		try {
			return (Position) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	// Getters and setters
	public char[][] getPieces() {
		return pieces;
	}

	public void setPieces(char[][] pieces) {
		this.pieces = pieces;
	}

	public Colour getSideToMove() {
		return sideToMove;
	}

	public void setSideToMove(Colour sideToMove) {
		this.sideToMove = sideToMove;
	}

	public String getCastling() {
		return castling;
	}

	public void setCastling(String castling) {
		this.castling = castling;
	}

	public int[] getEpSquare() {
		return epSquare;
	}

	public void setEpSquare(int[] epSquare) {
		this.epSquare = epSquare;
	}

	public int getHalfmove() {
		return halfmove;
	}

	public void setHalfmove(int halfmove) {
		this.halfmove = halfmove;
	}

	public int getFullmove() {
		return fullmove;
	}

	public void setFullmove(int fullmove) {
		this.fullmove = fullmove;
	}

}
