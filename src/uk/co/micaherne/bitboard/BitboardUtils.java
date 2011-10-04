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
	
	public long[] king_attacks = new long[64];
	
	public void generateKingAttacks() {
		for(int i = 0; i < 64; i++) {
			king_attacks[i] = kingAttacks(1 << i);
		}
	}
	
	public static long kingAttacks(long kingSet) {
		long attacks = eastOne(kingSet) | westOne(kingSet);
		kingSet |= attacks;
		attacks |= nortOne(kingSet) | soutOne(kingSet);
		return attacks;
	}
	
	public static long soutOne (long b) {return  b >> 8;}
	public static long nortOne (long b) {return  b << 8;}
	public static long eastOne (long b) {return (b << 1) & notAFile;}
	public static long noEaOne (long b) {return (b << 9) & notAFile;}
	public static long soEaOne (long b) {return (b >> 7) & notAFile;}
	public static long westOne (long b) {return (b >> 1) & notHFile;}
	public static long soWeOne (long b) {return (b >> 9) & notHFile;}
	public static long noWeOne (long b) {return (b << 7) & notHFile;}
	
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
