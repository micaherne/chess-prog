package uk.co.micaherne.bitboard;

public class Position {
	
	public long BBPawns = 0L;
	public long BBKnights = 0L;
	public long BBBishops = 0L;
	public long BBRooks = 0L;
	public long BBQueens = 0L;
	public long BBKings = 0L;
	public long BBWhitePieces = 0L;
	public long BBBlackPieces = 0L;
	
	public byte Castling = 0; // KQkq flags - low 4 bits
	public byte EPSquare = -1;
	public boolean WhiteToMove = true;
	
	public void fromFEN(String fen) {
		
	}

}
