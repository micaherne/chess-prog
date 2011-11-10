package uk.co.micaherne.eighteight;

/**
 * An evaluation object for storing in the transposition table.
 * @author Michael
 *
 */
public class PositionEvaluation {
	
	public int value;
	public int depth;
	public int[] bestMove;
	
	public PositionEvaluation(int value, int depth, int[] bestMove) {
		super();
		this.value = value;
		this.depth = depth;
		this.bestMove = bestMove;
	}

}
