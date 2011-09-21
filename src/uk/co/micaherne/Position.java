package uk.co.micaherne;


public class Position {
	
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
		
		for(int i = 0; i < 8; i++) {
			String rank = null;
			try {
				rank  = boardParts[7 - i];
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new FENException("Not enough ranks");
			}
			StringBuilder builder = new StringBuilder();
			for(String c : rank.split("")) {
				if(!c.matches("\\d")) {
					builder.append(c);
				} else {
					int cint = Integer.parseInt(c);
					while(cint > 0) {
						builder.append(" ");
						cint--;
					}
				}
			}
			if(builder.length() != 8) {
				throw new FENException("Not enough files");
			}
			for(int j = 0; j < 8; j++) {
				result.pieces[i][j] = builder.charAt(j);
			}
		}
		
		return result;
	}

	private char[][] pieces;

	public char getPiece(int rank, int file) {
		return this.pieces[rank - 1][file - 1];
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(int i = 7; i >= 0; i--) {
			for(int j = 0; j < 8; j++ ) {
				builder.append(this.pieces[i][j]);
			}
			if(i != 0) builder.append("\n");
		}
		return builder.toString();
	}
	
	public void move(String move) throws NotationException {
		move(move, Position.NotationType.COORDINATE);
	}

	public void move(String move, NotationType notationType) throws NotationException {
		if(notationType == NotationType.COORDINATE) {
			if(move.length() != 5) {
				throw new NotationException("Co-ordinate notation must be in form XX-XX");
			}
			String[] parts = move.split("-");
			if(parts.length != 2) {
				throw new NotationException("Co-ordinate notation must have only 2 parts");
			}
			int[] from = coordPair(parts[0]);
			int[] to = coordPair(parts[1]);
			
			this.pieces[to[0]][to[1]] = this.pieces[from[0]][from[1]];
			this.pieces[from[0]][from[1]] = ' ';
		}
	}
	
	private int[] coordPair(String coord) throws NotationException {
		if(coord.length() != 2) {
			throw new NotationException("Co-ordinates must be 2 characters long");
		}
		int[] result = new int[2];
		// These look the wrong way round, but are correct!
		result[0] = coord.charAt(1) - '1';
		result[1] = coord.charAt(0) - 'A';
		return result;
	}

}
