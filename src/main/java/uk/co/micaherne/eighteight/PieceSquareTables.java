package uk.co.micaherne.eighteight;

public class PieceSquareTables {
	
	public static final int[] centre = new int[]{-10,  -5,  +0,  +5,  +5,  +0,  -5, -10};

	public static int pieceValue(int piece, int square) {
		if ((square & 0x88) != 0) return 0; // TODO: throw exception
		int result = 0;

		int pieceType = piece & 31;
		switch (pieceType) {
		case Position.ROOK:
			result = 510;
			break;
		case Position.KNIGHT:
			result = 300;
			break;
		case Position.BISHOP:
			result = 325;
			break;
		case Position.QUEEN:
			result = 940;
			break;
		case Position.KING:
			result = 2000;
			break;
		case Position.PAWN:
			result = 100;
			break;
		}
		
		// Tweak the evaluation for centralisation
		if(pieceType != Position.PAWN && pieceType != Position.KING) {
			int rank = square >>> 4;
			int file = square & 7;
			result += centre[rank];
			result += centre[file];
		}
		
		return result;
	}
	
}
