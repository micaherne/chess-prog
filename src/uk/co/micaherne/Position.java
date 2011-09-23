package uk.co.micaherne;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uk.co.micaherne.Position.Colour;

public class Position {

	private char[][] pieces;
	private Colour sideToMove = Colour.WHITE;
	private String castling = "KQkq";
	private int[] epSquare = null;
	private int halfmove = 0;
	private int fullmove = 1;
	
	// CONSTANTS
	// piece names
	private static final char[] WHITE_PIECES = new char[]{'B', 'K', 'N', 'P', 'Q', 'R'};
	private static final char[] BLACK_PIECES = new char[]{'b', 'k', 'n', 'p', 'q', 'r'};
	
	public static enum Colour {
		BLACK,
		WHITE
	}
	public static enum NotationType {
		COORDINATE
	}

	public Position() {
		this.pieces = new char[8][8];
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
		
		result.sideToMove = "w".equals(fenParts[1]) ? Colour.WHITE : Colour.BLACK ;
		result.castling = fenParts[2];
		if(!"-".equals(fenParts[3])) {
			result.epSquare[1] = fenParts[3].charAt(0) - 'a';
			result.epSquare[0] = fenParts[3].charAt(1);
		}

		if(fenParts.length > 4) {
			try {
				result.halfmove = Integer.parseInt(fenParts[4]);
			} catch (NumberFormatException e) {
				throw new FENException("halfmove must be integer", e);
			}
		}
		
		if(fenParts.length > 5) {
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
		if(castling.length() > 0) {
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
			
			if(sideToMove == Colour.WHITE) {
				sideToMove = Colour.BLACK;
			} else if(sideToMove == Colour.BLACK) {
				sideToMove = Colour.WHITE;
			}
			
			if(sideToMove == Colour.WHITE) {
				fullmove++;
			}
			
			char movedPiece = this.pieces[from[0]][from[1]];
			char takenPiece = this.pieces[to[0]][to[1]];
			
			// Castling - rook moves
			if(from[0] == 0 && from[1] == 0 && castling.contains("Q")) {
				castling = castling.replace("Q", "");
			}
			if(from[0] == 0 && from[1] == 7 && castling.contains("K")) {
				castling = castling.replace("K", "");
			}
			if(from[0] == 7 && from[1] == 0 && castling.contains("q")) {
				castling = castling.replace("q", "");
			}
			if(from[0] == 7 && from[1] == 7 && castling.contains("k")) {
				castling = castling.replace("k", "");
			}
			
			// Castling - king moves
			if(movedPiece == 'K') {
				castling = castling.replace("K", "").replace("Q", "");
			}
			if(movedPiece == 'k') {
				castling = castling.replace("k", "").replace("q", "");
			}

			
			if(movedPiece == 'p' || movedPiece == 'P' || takenPiece != ' ') {
				halfmove = 0;
			} else {
				halfmove++;
			}
			this.pieces[to[0]][to[1]] = movedPiece;
			this.pieces[from[0]][from[1]] = ' ';
		}
	}
	
	public Set<int[]> allValidMoves(){
		Set<int[]> result = new HashSet<int[]>();
		char[] pieceNames;
		if(sideToMove == Colour.WHITE) {
			pieceNames = WHITE_PIECES;
		} else if(sideToMove == Colour.BLACK) {
			pieceNames = BLACK_PIECES;
		} else {
			return null;
		}
		
		for(char pieceName : pieceNames) {
			Set<int[]> positions = piecePositions(pieceName);
			for(int[] pos : positions) {
				result.addAll(validMoves(pos));
			}
		}
		
		return result;
	}
	
	/** Find all valid moves for the piece at the given position
	 * @param pos array of rank, file
	 * @return Set of all valid moves as 4-long arrays
	 */
	public Set<int[]> validMoves(int[] pos) {
		HashSet<int[]> result = new HashSet<int[]>();
		
		char piece = pieces[pos[0]][pos[1]];
		if(piece == ' ') {
			return result;
		}
		
		if(piece == 'P') {
			int[] possible = new int[] { pos[0] + 1, pos[1] }; // forward one
			if(getPiece(possible) == ' ') {
				result.add(positionsToMove(pos, possible));
			}
			possible = new int[] { pos[0] + 2, pos[1] };
			if(pos[0] == 1 && getPiece(possible) == ' ') { // forward two
				result.add(positionsToMove(pos, possible));
			}
			possible = new int[] { pos[0] + 1, pos[1] - 1 };
			if(pos[1] > 0 && isBlack(getPiece(possible))) { // take to left
				result.add(positionsToMove(pos, possible));
			}
			if(Arrays.equals(possible, epSquare)) { // take to left e.p.
				result.add(positionsToMove(pos, possible));
			}
			possible = new int[] { pos[0] + 1, pos[1] + 1 }; // take to right
			if(pos[1] < 7 && isBlack(getPiece(possible))) {
				result.add(positionsToMove(pos, possible));
			}
			if(Arrays.equals(possible, epSquare)) { // take to right e.p.
				result.add(positionsToMove(pos, possible));
			}
		}
		
		return result;
	}
	
	private static int[] positionsToMove(int[] from, int[] to) {
		int[] result = Arrays.copyOf(from, 4);
		result[2] = to[0];
		result[3] = to[1];
		return result;
	}
	
	public static Colour oppositeColour(Colour colour) {
		if(Colour.WHITE.equals(colour)) {
			return Colour.BLACK;
		} else if(Colour.BLACK.equals(colour)) {
			return Colour.WHITE;
		} else {
			return null;
		}
	}
	
	public static Colour pieceColour(char piece) {
		if(isWhite(piece)) {
			return Colour.WHITE;
		} else if(isBlack(piece)) {
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
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(pieces[i][j] == piece) {
					result.add(new int[]{ i, j });
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
