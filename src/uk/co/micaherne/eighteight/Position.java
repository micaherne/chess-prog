package uk.co.micaherne.eighteight;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import uk.co.micaherne.FENException;

public class Position implements Cloneable {

	byte[] board = new byte[128];
	boolean whiteToMove = true;

	// for undo
	byte[] previousPosition = new byte[128];
	private int halfmove = 0;
	private int fullmove = 1;
	public int nodesSearched;
	public int[] bestMove;

	public static final byte BLACK = 0;
	public static final byte WHITE = 32;

	public static final byte CAN_CASTLE = -128;
	public static final byte EP_SQUARE = 64;
	public static final byte COLOUR = 32;
	public static final byte IS_PAWN = 16;
	public static final byte SINGLE_JUMP = 8;
	public static final byte MOVES_KNIGHT_WISE = 4;
	public static final byte MOVES_LINEAR = 2;
	public static final byte MOVES_DIAGONAL = 1;

	/*
	 * Piece values use the following scheme (bit: meaning):
	 * 
	 * 0: moves diagonally 1: moves linearly 2: moves knight-wise 3: moves
	 * single-jump 4: is a pawn 5: colour 6: e.p. square (may not use) 7: can
	 * castle
	 */
	public static final byte ROOK = 2;
	public static final byte KNIGHT = 12;
	public static final byte BISHOP = 1;
	public static final byte QUEEN = 3;
	public static final byte KING = 11;
	public static final byte PAWN = 16;

	public static final byte EMPTY = 0;

	// Directions
	public static final int DIRECTION_UNDEFINED = 0;

	public static final int DIRECTION_NORTH = 0x10;
	public static final int DIRECTION_SOUTH = -0x10;
	public static final int DIRECTION_EAST = 0x01;
	public static final int DIRECTION_WEST = -0x01;

	public static final int DIRECTION_NORTHEAST = 0x11;
	public static final int DIRECTION_SOUTHEAST = -0x0F;
	public static final int DIRECTION_SOUTHWEST = -0x11;
	public static final int DIRECTION_NORTHWEST = 0x0F;

	public static final int DIRECTION_NORTHNORTHEAST = 0x21;
	public static final int DIRECTION_EASTNORTHEAST = 0x12;
	public static final int DIRECTION_EASTSOUTHEAST = -0x0E;
	public static final int DIRECTION_SOUTHSOUTHEAST = -0x1F;

	public static final int DIRECTION_NORTHNORTHWEST = 0x1F;
	public static final int DIRECTION_WESTNORTHWEST = 0x0E;
	public static final int DIRECTION_WESTSOUTHWEST = -0x12;
	public static final int DIRECTION_SOUTHSOUTHWEST = -0x21;

	// Arrays of possible directions (target less than/greater than source)
	public static final int[] DIRECTIONS_LT = new int[] { DIRECTION_WEST,
			DIRECTION_WESTSOUTHWEST, DIRECTION_SOUTHWEST,
			DIRECTION_SOUTHSOUTHWEST, DIRECTION_SOUTH,
			DIRECTION_SOUTHSOUTHEAST, DIRECTION_SOUTHEAST,
			DIRECTION_EASTSOUTHEAST };
	public static final int[] DIRECTIONS_GT = new int[] { DIRECTION_EAST,
			DIRECTION_EASTNORTHEAST, DIRECTION_NORTHEAST,
			DIRECTION_NORTHNORTHEAST, DIRECTION_NORTH,
			DIRECTION_NORTHNORTHWEST, DIRECTION_NORTHWEST,
			DIRECTION_WESTNORTHWEST };

	// These are sorted to allow Array.binarySearch
	public static final int[] DIRECTIONS_DIAGONAL = new int[] {
			DIRECTION_SOUTHWEST, DIRECTION_SOUTHEAST, DIRECTION_NORTHWEST,
			DIRECTION_NORTHEAST

	};
	public static final int[] DIRECTIONS_LINEAR = new int[] { DIRECTION_SOUTH,
			DIRECTION_WEST, DIRECTION_EAST, DIRECTION_NORTH };
	public static final int[] DIRECTIONS_KNIGHT = new int[] {
			DIRECTION_SOUTHSOUTHWEST, DIRECTION_SOUTHSOUTHEAST,
			DIRECTION_WESTSOUTHWEST, DIRECTION_EASTSOUTHEAST,
			DIRECTION_WESTNORTHWEST, DIRECTION_EASTNORTHEAST,
			DIRECTION_NORTHNORTHWEST, DIRECTION_NORTHNORTHEAST };

	public static final int[] PAWN_ATTACKS_WHITE = new int[] {
			DIRECTION_NORTHWEST, DIRECTION_NORTHEAST };
	public static final int[] PAWN_ATTACKS_BLACK = new int[] {
			DIRECTION_SOUTHWEST, DIRECTION_SOUTHEAST };

	public static final byte[] initPositionBoard = new byte[] {
			WHITE | ROOK | CAN_CASTLE, WHITE | KNIGHT, WHITE | BISHOP,
			WHITE | QUEEN, WHITE | KING | CAN_CASTLE, WHITE | BISHOP,
			WHITE | KNIGHT, WHITE | ROOK | CAN_CASTLE, 0, 0, 0, 0, 0, 0, 0, 0,
			WHITE | PAWN, WHITE | PAWN, WHITE | PAWN, WHITE | PAWN,
			WHITE | PAWN, WHITE | PAWN, WHITE | PAWN, WHITE | PAWN, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, BLACK | PAWN, BLACK | PAWN, BLACK | PAWN, BLACK | PAWN,
			BLACK | PAWN, BLACK | PAWN, BLACK | PAWN, BLACK | PAWN, 0, 0, 0, 0,
			0, 0, 0, 0, BLACK | ROOK | CAN_CASTLE, BLACK | KNIGHT,
			BLACK | BISHOP, BLACK | QUEEN, BLACK | KING | CAN_CASTLE,
			BLACK | BISHOP, BLACK | KNIGHT, BLACK | ROOK | CAN_CASTLE };

	public static final byte[] queeningPieces = new byte[] {
		ROOK, KNIGHT, BISHOP, QUEEN
	};
	
	public void initialPosition() {
		board = initPositionBoard;
		whiteToMove = true;
		fullmove = 1;
		halfmove = 0;
		previousPosition = new byte[128];
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 7; i >= 0; i--) {
			for (int j = 0; j < 8; j++) {
				builder.append(pieceToChar(board[squareNo(i, j)]));
			}
			if (i != 0)
				builder.append("\n");
		}
		return builder.toString();
	}

	public static char pieceToChar(byte b) {
		char result = ' ';
		switch (b & 31) {
		case EMPTY:
			return result;
		case ROOK:
			result = 'r';
			break;
		case KNIGHT:
			result = 'n';
			break;
		case BISHOP:
			result = 'b';
			break;
		case QUEEN:
			result = 'q';
			break;
		case KING:
			result = 'k';
			break;
		case PAWN:
			result = 'p';
			break;
		}

		if ((b & WHITE) == 0) {
			return result;
		} else {
			return Character.toUpperCase(result);
		}
	}

	public static byte charToPiece(char c) {
		byte result = 0;
		if (Character.isUpperCase(c)) {
			result = WHITE;
			c = Character.toLowerCase(c);
		}
		switch (c) {
		case ' ':
			result = EMPTY;
			break;
		case 'r':
			result |= ROOK;
			break;
		case 'n':
			result |= KNIGHT;
			break;
		case 'b':
			result |= BISHOP;
			break;
		case 'q':
			result |= QUEEN;
			break;
		case 'k':
			result |= KING;
			break;
		case 'p':
			result |= PAWN;
			break;
		}
		return result;

	}

	public static int squareNo(int rank7, int file7) {
		return 16 * rank7 + file7;
	}

	public void move(int i, int j) {
		move(new int[] { i, j });
	}

	public void move(int[] move) {
		
		int i = move[0];
		int j = move[1];
		
		previousPosition = Arrays.copyOf(board, 128); // if we're using
														// make/unmake
		byte pieceMoved = board[i];
		
		// reset en passent bits
		for (int t = 0; t < 128; t++) {
			if ((t & 0x88) != 0)
				continue;
			board[t] &= ~EP_SQUARE;
		}
		// en passent
		if ((pieceMoved & PAWN) != 0 && (Math.abs(j - i) == 0x20)) {
			board[i + (16 * ((j - i) / 0x20))] |= EP_SQUARE;
		}

		// castling
		if ((pieceMoved & KING) != 0 && (Math.abs(j - i) == 2)) {
			pieceMoved &= ~CAN_CASTLE;
			if (j == 0x02) {
				board[0x00] = EMPTY;
				board[0x03] = WHITE | ROOK;
			} else if (j == 0x06) {
				board[0x07] = EMPTY;
				board[0x05] = WHITE | ROOK;
			} else if (j == 0x72) {
				board[0x70] = EMPTY;
				board[0x73] = BLACK | ROOK;
			} else if (j == 0x76) {
				board[0x77] = EMPTY;
				board[0x75] = BLACK | ROOK;
			}
		}
		
		// queening
		if(move.length == 3 && move[2] != 0) {
			board[j] = (byte) move[2];
		} else {
			board[j] = pieceMoved;
		}
		board[i] = EMPTY;
		whiteToMove = !whiteToMove;

		if (whiteToMove) {
			fullmove++;
		}
	}



	public void move(String moveString) {
		int[] move = moveFromNotation(moveString);
		move(move);
	}

	public void undoMove() {
		board = previousPosition;
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
				result.board[squareNo(i, j)] = charToPiece(builder.charAt(j));
			}
		}

		result.whiteToMove = "w".equals(fenParts[1]);

		// Castling
		if (fenParts[2].contains("K")) {
			result.board[0x07] |= CAN_CASTLE;
			result.board[0x04] |= CAN_CASTLE;
		}
		if (fenParts[2].contains("Q")) {
			result.board[0x00] |= CAN_CASTLE;
			result.board[0x04] |= CAN_CASTLE;
		}
		if (fenParts[2].contains("k")) {
			result.board[0x77] |= CAN_CASTLE;
			result.board[0x74] |= CAN_CASTLE;
		}
		if (fenParts[2].contains("q")) {
			result.board[0x70] |= CAN_CASTLE;
			result.board[0x74] |= CAN_CASTLE;
		}

		if (!"-".equals(fenParts[3])) {
			int epSquare = squareNo(fenParts[3].charAt(1),
					fenParts[3].charAt(0) - 'a');
			result.board[epSquare] |= EP_SQUARE;
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
				if (empty(16 * i + j)) {
					spaceCount++;
					continue;
				} else {
					if (spaceCount > 0) {
						result.append(spaceCount);
						spaceCount = 0;
					}
					char p = pieceToChar(board[16 * i + j]);
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
		result.append(whiteToMove ? 'w' : 'b');
		result.append(' ');
		boolean castling = false;
		if ((board[0x04] & board[0x07] & CAN_CASTLE) == CAN_CASTLE) {
			result.append("K");
			castling = true;
		}
		if ((board[0x04] & board[0x00] & CAN_CASTLE) == CAN_CASTLE) {
			result.append("Q");
			castling = true;
		}
		if ((board[0x74] & board[0x77] & CAN_CASTLE) == CAN_CASTLE) {
			result.append("k");
			castling = true;
		}
		if ((board[0x74] & board[0x70] & CAN_CASTLE) == CAN_CASTLE) {
			result.append("q");
			castling = true;
		}

		if (!castling) {
			result.append("-");
		}
		result.append(' ');
		String epSquare = "-";
		for (int i = 0; i < 128; i++) {
			if ((i & 0x88) != 0)
				continue;
			if ((board[i] & EP_SQUARE) == EP_SQUARE) {
				epSquare = squareToNotation(i);
				break;
			}
		}
		result.append(epSquare);

		if (full) {
			result.append(' ');
			result.append(halfmove);
			result.append(' ');
			result.append(fullmove);
		}

		return result.toString();
	}

	public boolean empty(int square) {
		return (board[square] & 31) == 0;
	}

	public boolean validSquare(int square) {
		return (square & 0x88) == 0;
	}

	/**
	 * Is the target square attacked? If square is empty we only check for it
	 * being attacked by the given colour, otherwise we observe the normal
	 * definition of attack.
	 * 
	 * @param target
	 * @param colour
	 * @return
	 */
	public boolean attacked(int target, int colour) {
		for (int i = 0; i < 128; i++) {
			if ((i & 0x88) != 0)
				continue;
			if (empty(i))
				continue;
			if (empty(target) && (board[i] & COLOUR) != colour)
				continue;
			if (!empty(target)
					&& (board[i] & COLOUR) == (board[target] & COLOUR))
				continue;
			if (attacks(i, target)) {
				return true;
			}
		}
		return false;
	}

	public boolean isCheck() {
		return isCheck(BLACK) || isCheck(WHITE);
	}

	public boolean isCheck(int colour) {
		for (int i = 0; i < 128; i++) {
			if ((i & 0x88) != 0)
				continue;
			if (empty(i))
				continue;
			byte piece = board[i];
			if ((piece & KING) == KING && (piece & COLOUR) == colour)
				return attacked(i, oppositeColour(colour));
		}
		return false;
	}

	public static int oppositeColour(int colour) {
		return colour ^ COLOUR;
	}

	/**
	 * Does the piece on the source square attack the target square? A friendly
	 * piece on the target square does not count as attacked.
	 * 
	 * @param source
	 * @param target
	 * @param colour
	 * @return
	 */
	public boolean attacks(int source, int target) {
		if (empty(source))
			return false;
		if ((board[source] & COLOUR) == (board[target] & COLOUR)) {
			return false;
		}
		int direction = direction(source, target);
		if (direction == DIRECTION_UNDEFINED)
			return false;
		if ((board[source] & IS_PAWN) == IS_PAWN) {
			int[] tdd = DIRECTIONS_DIAGONAL;
			Arrays.sort(tdd);
			if (!(Arrays.binarySearch(tdd, direction) >= 0)) {
				return false;
			}
			if ((board[source] & COLOUR) == WHITE) {
				if ((direction == DIRECTION_NORTHEAST || direction == DIRECTION_NORTHWEST)
						&& (target - source) == direction) {
					return true;
				}
			} else if ((direction == DIRECTION_SOUTHEAST || direction == DIRECTION_SOUTHWEST)
					&& (target - source) == direction) {
				return true;
			}
			return false;
		}
		boolean correctDirection = false;
		if ((board[source] & MOVES_DIAGONAL) > 0
				&& Arrays.binarySearch(DIRECTIONS_DIAGONAL, direction) >= 0) {
			correctDirection = true;
		} else if ((board[source] & MOVES_LINEAR) > 0
				&& Arrays.binarySearch(DIRECTIONS_LINEAR, direction) >= 0) {
			correctDirection = true;
		} else if ((board[source] & MOVES_KNIGHT_WISE) > 0
				&& Arrays.binarySearch(DIRECTIONS_KNIGHT, direction) >= 0) {
			correctDirection = true;
		}
		if (!correctDirection) {
			return false;
		}
		// check there's nothing in the way
		int jumps = 8; // TODO: we can calculate this more accurately
		int square = source;
		if ((board[source] & SINGLE_JUMP) == SINGLE_JUMP) {
			jumps = 1;
		}
		for (int jump = 0; jump < jumps; jump++) {
			square += direction;
			if (square == target) {
				return true;
			}
			if ((square & 0x88) > 0) {
				return false;
			}
			if (!empty(square)) {
				return false;
			}
		}
		return false;
	}

	public static int direction(int source, int target) {
		int[] possibleDirections = new int[] {};
		int result = DIRECTION_UNDEFINED;
		if (source == target) {
			return DIRECTION_UNDEFINED;
		} else if (target > source) {
			possibleDirections = DIRECTIONS_GT;
		} else if (target < source) {
			possibleDirections = DIRECTIONS_LT;
		}
		for (int possible : possibleDirections) {
			if ((target - source) % possible == 0
					&& Math.abs(possible) > Math.abs(result)) {
				result = possible;
			}
		}
		return result;
	}

	public int[] bestMove(int depth) throws CloneNotSupportedException {
		nodesSearched = 0;
		bestMove = null;
		// negaMax(4);
		alphaBeta(depth, -200000, 200000);
		return bestMove;
	}

	public int alphaBeta(int depth, int alpha, int beta)
			throws CloneNotSupportedException {
		if (depth == 0) {
			nodesSearched++;
			return evaluate();
		}
		int localalpha = alpha;
		int max = Integer.MIN_VALUE;
		Set<int[]> moves = validMoves();
		for (int[] m : moves) {
			Position resultingPosition = this.clone();
			resultingPosition.move(m);
			if (resultingPosition.whiteToMove) {
				if (resultingPosition.isCheck(BLACK))
					continue;
			} else {
				if (resultingPosition.isCheck(WHITE))
					continue;
			}
			int score = -resultingPosition.alphaBeta(depth - 1, -beta,
					-localalpha);
			if (score > max) {
				max = score;
				bestMove = m;
			}
			if (max >= beta) {
				break;
			}
			if (max > localalpha) {
				localalpha = max;
			}
		}
		return max;
	}

	private int evaluate() {
		int centipawns = 0;
		byte sideToMove;
		if (whiteToMove) {
			sideToMove = WHITE;
		} else {
			sideToMove = BLACK;
		}
		for (int i = 0; i < 128; i++) {
			if ((i & 0x88) != 0)
				continue;
			if (empty(i))
				continue;
			int pieceValue = 0;

			switch (board[i] & 31) {
			case ROOK:
				pieceValue = 510;
				break;
			case KNIGHT:
				pieceValue = 300;
				break;
			case BISHOP:
				pieceValue = 325;
				break;
			case QUEEN:
				pieceValue = 940;
				break;
			case KING:
				pieceValue = 2000;
				break;
			case PAWN:
				pieceValue = 100;
				break;
			}

			if ((board[i] & COLOUR) == sideToMove) {
				centipawns += pieceValue;
			} else {
				centipawns -= pieceValue;
			}
		}

		return centipawns;
	}

	public Set<int[]> validMoves() {
		HashSet<int[]> result = new HashSet<int[]>();
		for (int square = 0; square < 128; square++) {
			if ((square & 0x88) != 0)
				continue;
			if (empty(square))
				continue;
			if (whiteToMove != ((board[square] & COLOUR) == WHITE))
				continue;
			Set<int[]> moves = validMoves(square);
			result.addAll(moves);
		}
		return result;
	}

	public Set<int[]> validMoves(int square) {
		HashSet<int[]> result = new HashSet<int[]>();
		if ((square & 0x88) != 0)
			return result;
		if (empty(square))
			return result;
		byte piece = board[square];
		int pieceColour = piece & COLOUR;
		if (whiteToMove != (pieceColour == WHITE))
			return result;

		// TODO: Pawn moves
		if ((piece & IS_PAWN) == IS_PAWN) {
			int[] attacks;
			int forward;
			int jumps = 1;
			if (pieceColour == WHITE) {
				attacks = PAWN_ATTACKS_WHITE;
				forward = DIRECTION_NORTH;
				if (square >>> 4 == 1)
					jumps = 2;
			} else {
				attacks = PAWN_ATTACKS_BLACK;
				forward = DIRECTION_SOUTH;
				if (square >>> 4 == 6)
					jumps = 2;
			}

			for (int jump = 1; jump <= jumps; jump++) {
				int testSquare = square + (forward * jump);
				if(testSquare < 0 || testSquare >= 128 || (square & 0x88) != 0) break;
				if (empty(testSquare)) {
					if (testSquare >>> 4 == 7) { // queened
						for(byte queeningPiece : queeningPieces) {
							result.add(new int[] { square, testSquare, queeningPiece | pieceColour });
						}
					} else {
						result.add(new int[] { square, testSquare });
					}
				} else {
					break;
				}
			}
			for (int attack : attacks) {
				int testSquare = square + attack;
				if(testSquare < 0 || testSquare >= 128 || (square & 0x88) != 0) continue;
				if (empty(testSquare) && (board[testSquare] & EP_SQUARE) == 0)
					continue;
				if (((board[testSquare] & EP_SQUARE) != 0)
						|| (board[testSquare] & COLOUR) != (piece & COLOUR)) {
					if (testSquare >>> 4 == 7) { // queened
						for(byte queeningPiece : queeningPieces) {
							result.add(new int[] { square, testSquare, queeningPiece | pieceColour });
						}
					} else {
						result.add(new int[] { square, testSquare });
					}
				}
			}

			return result;
		}

		if ((piece & MOVES_DIAGONAL) == MOVES_DIAGONAL) {
			result.addAll(validMoves(square, DIRECTIONS_DIAGONAL));
		}
		if ((piece & MOVES_LINEAR) == MOVES_LINEAR) {
			result.addAll(validMoves(square, DIRECTIONS_LINEAR));
		}
		if ((piece & MOVES_KNIGHT_WISE) == MOVES_KNIGHT_WISE) {
			result.addAll(validMoves(square, DIRECTIONS_KNIGHT));
		}

		// Castling
		boolean pieceIsKing = (piece & KING) == KING;
		if (pieceIsKing && (piece & CAN_CASTLE) != CAN_CASTLE) {
			return result;
		}
		if (pieceIsKing) {
			int kingColour = piece & COLOUR;
			int oppositeColour = oppositeColour(kingColour);
			// Kingside
			if (empty(square + 1)
					&& empty(square + 2)
					&& (board[square + 3] & kingColour | ROOK | CAN_CASTLE) == (kingColour
							| ROOK | CAN_CASTLE)
					&& !attacked(square, oppositeColour)
					&& !attacked(square + 1, oppositeColour) // &&
																// !attacked(square
																// + 2)
			) {
				result.add(new int[] { square, square + 2 });
			}
			// Queenside
			if (empty(square - 1)
					&& empty(square - 2)
					&& empty(square - 3)
					&& ((board[square - 4] & kingColour | ROOK | CAN_CASTLE) == (kingColour
							| ROOK | CAN_CASTLE))
					&& !attacked(square, oppositeColour)
					&& !attacked(square - 1, oppositeColour)
					&& !attacked(square - 2, oppositeColour)) {
				result.add(new int[] { square, square - 2 });
			}
		}
		return result;
	}

	public Set<int[]> validMoves(int square, int[] directions) {
		HashSet<int[]> result = new HashSet<int[]>();
		int jumps = 8; // TODO: we can calculate this more accurately
		byte piece = board[square];
		if ((piece & SINGLE_JUMP) == SINGLE_JUMP) {
			jumps = 1;
		}
		for (int direction : directions) {
			int testSquare = square;
			for (int jump = 0; jump < jumps; jump++) {
				testSquare += direction;
				if ((testSquare & 0x88) > 0) {
					break;
				}
				if (empty(testSquare)) {
					result.add(new int[] { square, testSquare });
					continue;
				}
				if ((!empty(testSquare) && (piece & COLOUR) != (board[testSquare] & COLOUR))) {
					result.add(new int[] { square, testSquare });
					break;
				}
				if (!empty(testSquare)) { // we know it's the same colour
					break;
				}
			}
		}
		return result;
	}

	public static String moveToNotation(int[] move) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < 2; i++) {
			int square = move[i];
			result.append(squareToNotation(square));
		}
		if(move.length == 3) {
			result.append(pieceToChar((byte) move[2]));
		}
		return result.toString();
	}
	
	public int[] moveFromNotation(String moveString) {
	int[] result = new int[3];
			// These look the wrong way round, but are correct!
			int rankFrom = moveString.charAt(1) - '1';
			int fileFrom = moveString.charAt(0) - 'a';
			int rankTo = moveString.charAt(3) - '1';
			int fileTo = moveString.charAt(2) - 'a';
			result[0] = squareNo(rankFrom, fileFrom);
			result[1] = squareNo(rankTo, fileTo);
			if (moveString.length() == 5) {
				byte promotionPiece = 0;
				switch(Character.toUpperCase(moveString.charAt(4))) {
				case 'R':
					promotionPiece = ROOK;
					break;
				case 'N':
					promotionPiece = KNIGHT;
					break;
				case 'B':
					promotionPiece = BISHOP;
					break;
				case 'Q':
					promotionPiece = QUEEN;
					break;
				}
				byte pawnPromoting = board[result[0]];
				result[2] = promotionPiece | (pawnPromoting & COLOUR) ;
			} else {
				result[2] = 0;
			}

		return result;
	}

	public static String squareToNotation(int square) {
		StringBuilder result = new StringBuilder();
		int rank = square >>> 4;
		int file = square & 7;
		result.append(Character.toChars('a' + file));
		result.append(rank + 1);
		return result.toString();
	}

	@Override
	protected Position clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return (Position) super.clone();
	}


}
