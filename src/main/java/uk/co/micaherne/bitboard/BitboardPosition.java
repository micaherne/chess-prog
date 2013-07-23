package uk.co.micaherne.bitboard;

import java.util.ArrayList;
import java.util.List;

import uk.co.micaherne.FENException;
import uk.co.micaherne.NotationException;

public class BitboardPosition {
	
	// Constants
	// Bitboard indices
	public final static int WHITE_PAWN =   0;
	public final static int WHITE_ROOK =   1;
	public final static int WHITE_KNIGHT = 2;
	public final static int WHITE_BISHOP = 3;
	public final static int WHITE_QUEEN  = 4;
	public final static int WHITE_KING   = 5;
	public final static int BLACK_PAWN   = 6;
	public final static int BLACK_ROOK   = 7;
	public final static int BLACK_KNIGHT = 8;
	public final static int BLACK_BISHOP = 9;
	public final static int BLACK_QUEEN  = 10;
	public final static int BLACK_KING   = 11;
	
	public static enum NotationType {
		COORDINATE, LONG_ALGEBRAIC
	}
	
	public long[] pieceBitboards = new long[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	
	public final static char[] pieceNames = new char[] { 'P', 'R', 'N', 'B', 'Q', 'K', 'p', 'r', 'n', 'b', 'q', 'k' };
	
	long epSquare;
	long castlingRights; // bitboard of valid rooks
	int fullmove = 0;
	int halfmove = 0;
	
	boolean whiteToMove = true;
	
	public static BitboardPosition fromFEN(String fen) throws FENException {
		BitboardPosition result = new BitboardPosition();
		String[] fenParts = fen.split(" ");
		String pieceLocations = fenParts[0];
		int bitNo = 56;
		for(int i = 0; i < pieceLocations.length(); i++) {
			char currentChar = pieceLocations.charAt(i);
			if(Character.isDigit(currentChar)){
				bitNo += (currentChar - '0');
				continue;
			}
			switch(currentChar) {
			
			case 'P':
				result.pieceBitboards[WHITE_PAWN] |= (1L << bitNo);
				break;
			case 'R':
				result.pieceBitboards[WHITE_ROOK] |= (1L << bitNo);
				break;
			case 'N':
				result.pieceBitboards[WHITE_KNIGHT] |= (1L << bitNo);
				break;
			case 'B':
				result.pieceBitboards[WHITE_BISHOP] |= (1L << bitNo);
				break;
			case 'Q':
				result.pieceBitboards[WHITE_QUEEN] |= (1L << bitNo);
				break;
			case 'K':
				result.pieceBitboards[WHITE_KING] |= (1L << bitNo);
				break;
			case 'p':
				result.pieceBitboards[BLACK_PAWN] |= (1L << bitNo);
				break;
			case 'r':
				result.pieceBitboards[BLACK_ROOK] |= (1L << bitNo);
				break;
			case 'n':
				result.pieceBitboards[BLACK_KNIGHT] |= (1L << bitNo);
				break;
			case 'b':
				result.pieceBitboards[BLACK_BISHOP] |= (1L << bitNo);
				break;
			case 'q':
				result.pieceBitboards[BLACK_QUEEN] |= (1L << bitNo);
				break;
			case 'k':
				result.pieceBitboards[BLACK_KING] |= (1L << bitNo);
				break;
			case '/':
				bitNo -= 17;
			}
			
			bitNo++;
		}
		
		result.whiteToMove = "w".equals(fenParts[1]);
		
		// TODO: Fix this up
		if(fenParts[2] != null && !"-".equals(fenParts[2]) ) {
			String castling = fenParts[2];
			if(castling.contains("K")) {
				//result.castlingRights &= 0x;
			}
			if(castling.contains("Q")) {
				//result.castlingRights &= 
			}
		}
		
		if (!"-".equals(fenParts[3])) {
			result.epSquare = 1 << squareNameToBitNumber(fenParts[3]);
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

	public char toChar(int idx) {
		for(int i = 0; i < 12; i++) {
			if(((pieceBitboards[i] >>> idx) & 1) == 1) {
				return pieceNames[i];
			}
		}
		return ' ';
	}

	public static String bitNumberToSquareName(int i) {
		return (char)('a' + (i % 8)) + ("" + ((i/8) + 1));
	}
	
	public static int squareNameToBitNumber(String squareName) {
		return (squareName.charAt(0) - 'a') + ((squareName.charAt(1) - '1') * 8);
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for(int i = 7; i >= 0; i--) {
			for(int j = 0; j < 8; j++) {
				s.append(this.toChar(i*8 + j));
			}
			s.append('\n');
		}
		return s.toString();
	}

	public void move(int fromIndex, int toIndex) {
		long fromBit = 1L << fromIndex;
		long toBit = 1L << toIndex;
		
		whiteToMove = !whiteToMove;
		if(whiteToMove) {
			fullmove++;
		}
		
		for(int i = 0; i < 12; i++) {
			if((pieceBitboards[i] & fromBit) > 0) {
				int pieceMoved = i;
				pieceBitboards[i] ^= fromBit;
				pieceBitboards[i] |= toBit;
			} else {
				if((pieceBitboards[i] & toBit) > 0) {
					int pieceTaken = i;
					pieceBitboards[i] &= ~toBit;
				}
			}
		}
		
		// TODO: Work out halfmove
	}

	public void move(String move, NotationType notationType) throws NotationException {
		int[] m = stringToMove(move, NotationType.LONG_ALGEBRAIC);
		move(m[0], m[1]);
	}

	private int[] stringToMove(String move, NotationType notationType) throws NotationException {
		int[] result = new int[3]; // fromIndex, toIndex, promotedPiece
		switch (notationType) {
		case LONG_ALGEBRAIC:
			if (move.length() < 4 || move.length() > 5) {
				throw new NotationException(
						"Long algebraic notation must be in form XXXX");
			}
			if (!move.toLowerCase().equals(move)) {
				throw new NotationException(
						"Long algebraic notation must be in lower case");
			}
			
			result[0] = squareNameToBitNumber(move.substring(0, 2));
			result[1] = squareNameToBitNumber(move.substring(2, 4));
			if(move.length() == 5) {
				for(int i = 0; i < pieceNames.length; i++) {
					if(move.charAt(4) == pieceNames[i]) {
						result[2] = i;
						return result;
					}
				}
			}
			result[2] = -1;
			
			break;

		default:
			break;
		}

		return result;
	}
	
	public List<int[]> generateMoves() {
		ArrayList<int[]> result = new ArrayList<int[]>();
		
		
		
		return result;
	}
	
	protected long whitePieces() {
		long result = 0;
		for(int i = WHITE_PAWN; i < BLACK_PAWN; i++) {
			result |= pieceBitboards[i];
		}
		return result;
	}
	
	protected long blackPieces() {
		long result = 0;
		for(int i = BLACK_PAWN; i <= BLACK_KING; i++) {
			result |= pieceBitboards[i];
		}
		return result;
	}

}
