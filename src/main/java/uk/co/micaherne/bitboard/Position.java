package uk.co.micaherne.bitboard;

import java.util.HashMap;
import java.util.Map;

public class Position {
	
	public long BBPawns = 0L;
	public long BBKnights = 0L;
	public long BBBishops = 0L;
	public long BBRooks = 0L;
	public long BBQueens = 0L;
	public long BBKings = 0L;
	public long BBWhitePieces = 0L;
	public long BBBlackPieces = 0L;
	
	// Piece numbers
	public final static int EMPTY_SQUARE = 0;
	public final static int PAWN =   1;
	public final static int KNIGHT = 2;
	public final static int KING   = 3;
	public final static int BISHOP = 5;
	public final static int ROOK =   6;
	public final static int QUEEN  = 7;
	
	public Map<String, Long> bbPieceLookup = new HashMap<String, Long>();

	public byte Castling = 0; // KQkq flags - low 4 bits
	public byte EPSquare = -1;
	public boolean WhiteToMove = true;
	
	public Position() {
		resetPosition();
	}
	
	public void resetPosition() {
		BBPawns = 0L;
		BBKnights = 0L;
		BBBishops = 0L;
		BBRooks = 0L;
		BBQueens = 0L;
		BBKings = 0L;
		BBWhitePieces = 0L;
		BBBlackPieces = 0L;
		
		Castling = 0; // KQkq flags - low 4 bits
		EPSquare = -1;
		WhiteToMove = true;
		
		bbPieceLookup.put("P", BBPawns);
		bbPieceLookup.put("N", BBKnights);
		bbPieceLookup.put("B", BBBishops);
		bbPieceLookup.put("R", BBRooks);
		bbPieceLookup.put("Q", BBQueens);
		bbPieceLookup.put("K", BBKings);
	}
	
	public void fromFEN(String fen) {
		resetPosition();
		String[] fenParts = fen.split(" ");
		String pieceLocations = fenParts[0];
		int bitNo = 56;
		for(int i = 0; i < pieceLocations.length(); i++) {
			char currentChar = pieceLocations.charAt(i);
			if(Character.isDigit(currentChar)){
				bitNo += (currentChar - '0');
				continue;
			}
			
			boolean isPiece = false;
			switch(currentChar) {
				case 'P':
				case 'p':
					BBPawns |= (1L << bitNo);
					isPiece = true;
					break;
				case 'N':
				case 'n':
					BBKnights |= (1L << bitNo);
					isPiece = true;
					break;
				case 'B':
				case 'b':
					BBBishops |= (1L << bitNo);
					isPiece = true;
					break;
				case 'R':
				case 'r':
					BBRooks |= (1L << bitNo);
					isPiece = true;
					break;
				case 'Q':
				case 'q':
					BBQueens |= (1L << bitNo);
					isPiece = true;
					break;
				case 'K':
				case 'k':
					BBKings |= (1L << bitNo);
					isPiece = true;
					break;
				case '/':
					bitNo -= 17;
			}
		
			if (isPiece && currentChar <= 'Z') {
				BBWhitePieces |= (1L << bitNo);
			} else if (isPiece && currentChar >= 'a') {
				BBBlackPieces |= (1L << bitNo);
			}
			bitNo++;
		}
	}

}
