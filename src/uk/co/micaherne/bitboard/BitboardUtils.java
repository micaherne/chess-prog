package uk.co.micaherne.bitboard;

/** Utility functions and pre-generated bitboards.
 * Much of this is inspired by 
 * http://chessprogramming.wikispaces.com/General+Setwise+Operations
 * @author Michael
 *
 */
public class BitboardUtils {
	
	// Constants for avoiding horizontal wrapping
	public static final long notAFile = 0xfefefefefefefefeL;
	public static final long notHFile = 0x7f7f7f7f7f7f7f7fL;
	public static final long notABFile = 0xfcfcfcfcfcfcfcfcL;
	public static final long notGHFile = 0x3f3f3f3f3f3f3f3fL;
	
	public long[] king_attacks = new long[64];
	public long[] knight_attacks = new long[64];
	
	public void generateKingAttacks() {
		for(int i = 0; i < 64; i++) {
			king_attacks[i] = kingAttacks(1L << i);
		}
	}
	
	public void generateKnightAttacks() {
		for(int i = 0; i < 64; i++) {
			knight_attacks[i] = knightAttacks(1L << i);
		}
	}
	public static long kingAttacks(long kingSet) {
		long attacks = eastOne(kingSet) | westOne(kingSet);
		kingSet |= attacks;
		attacks |= nortOne(kingSet) | soutOne(kingSet);
		return attacks;
	}
	
	public static long knightAttacks(long knightSet) {
		return noNoEa(knightSet) | noEaEa(knightSet) | soEaEa(knightSet)
				| soSoEa(knightSet) | noNoWe(knightSet) | noWeWe(knightSet)
				| soWeWe(knightSet) | soSoWe(knightSet);
	}
	
	public static long soutOne (long b) {return  b >> 8;}
	public static long nortOne (long b) {return  b << 8;}
	public static long eastOne (long b) {return (b << 1) & notAFile;}
	public static long noEaOne (long b) {return (b << 9) & notAFile;}
	public static long soEaOne (long b) {return (b >> 7) & notAFile;}
	public static long westOne (long b) {return (b >> 1) & notHFile;}
	public static long soWeOne (long b) {return (b >> 9) & notHFile;}
	public static long noWeOne (long b) {return (b << 7) & notHFile;}
	
	public static long noNoEa(long b) {return (b << 17) & notAFile ;}
	public static long noEaEa(long b) {return (b << 10) & notABFile;}
	public static long soEaEa(long b) {return (b >>  6) & notABFile;}
	public static long soSoEa(long b) {return (b >> 15) & notAFile ;}
	public static long noNoWe(long b) {return (b << 15) & notHFile ;}
	public static long noWeWe(long b) {return (b <<  6) & notGHFile;}
	public static long soWeWe(long b) {return (b >> 10) & notGHFile;}
	public static long soSoWe(long b) {return (b >> 17) & notHFile ;}
	
	// Occluded fills (include slider but not blocker)
	public static long soutOccl(long gen, long pro) {
		gen |= pro & (gen >> 8);
		pro &= (pro >> 8);
		gen |= pro & (gen >> 16);
		pro &= (pro >> 16);
		gen |= pro & (gen >> 32);
		return gen;
	}

	public static long nortOccl(long gen, long pro) {
		gen |= pro & (gen << 8);
		pro &= (pro << 8);
		gen |= pro & (gen << 16);
		pro &= (pro << 16);
		gen |= pro & (gen << 32);
		return gen;
	}

	public static long eastOccl(long gen, long pro) {
		pro &= notAFile;
		gen |= pro & (gen << 1);
		pro &= (pro << 1);
		gen |= pro & (gen << 2);
		pro &= (pro << 2);
		gen |= pro & (gen << 4);
		return gen;
	}

	public static long noEaOccl(long gen, long pro) {
		pro &= notAFile;
		gen |= pro & (gen << 9);
		pro &= (pro << 9);
		gen |= pro & (gen << 18);
		pro &= (pro << 18);
		gen |= pro & (gen << 36);
		return gen;
	}

	public static long soEaOccl(long gen, long pro) {
		pro &= notAFile;
		gen |= pro & (gen >> 7);
		pro &= (pro >> 7);
		gen |= pro & (gen >> 14);
		pro &= (pro >> 14);
		gen |= pro & (gen >> 28);
		return gen;
	}

	public static long westOccl(long gen, long pro) {
		pro &= notHFile;
		gen |= pro & (gen >> 1);
		pro &= (pro >> 1);
		gen |= pro & (gen >> 2);
		pro &= (pro >> 2);
		gen |= pro & (gen >> 4);
		return gen;
	}

	public static long soWeOccl(long gen, long pro) {
		pro &= notHFile;
		gen |= pro & (gen >> 9);
		pro &= (pro >> 9);
		gen |= pro & (gen >> 18);
		pro &= (pro >> 18);
		gen |= pro & (gen >> 36);
		return gen;
	}

	public static long noWeOccl(long gen, long pro) {
		pro &= notHFile;
		gen |= pro & (gen << 7);
		pro &= (pro << 7);
		gen |= pro & (gen << 14);
		pro &= (pro << 14);
		gen |= pro & (gen << 28);
		return gen;
	}
	
	public static String bitboardToString(long bitboard) {
		StringBuilder result = new StringBuilder();
		long b = bitboard;
		long rankMask = 0xFFL;
		for(int i = 7; i >= 0; i--) {
			StringBuilder s = new StringBuilder();
			s.append( 
					String.format("%8s", Long.toBinaryString(b >>> (8 * i) & rankMask)).replace(' ', '0'));
			s.reverse();
			s.append('\n');
			result.append(s);
		}
		return result.toString();
	}

	/** Zero-based rank for index
	 * @param index
	 * @return
	 */
	public static int rank(int index) {
		return index >>> 3;
	}
	
	/** Zero-based file for index
	 * @param index
	 * @return
	 */
	public static int file(int index) {
		return index % 8;
	}

}
