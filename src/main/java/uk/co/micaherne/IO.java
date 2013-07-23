package uk.co.micaherne;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Set;

import uk.co.micaherne.Position.NotationType;

public class IO {

	/*
	 * Input commands
	 * 
	 * * uci debug [ on | off ] 
	 * isready 
	 * setoption name <id> [value <x>] 
	 * register
	 * ucinewgame 
	 * position [fen <fenstring> | startpos ] moves <move1> ....
	 * <movei> 
	 * go 
	 * stop 
	 * ponderhit
	 * quit
	 */
	
	BufferedReader in;
	private Position currentPosition;


	public IO() {
		super();
		in = new BufferedReader(new InputStreamReader(System.in));
	}


	public void startInput() {
		try {
			String input = in.readLine();
			while(!"quit".equals(input)) {
				if(input != null) {
					doInput(input);
				}
				Thread.sleep(50);
				input = in.readLine();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UCIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void doInput(String input) throws UCIException {
		String[] parts = input.split("\\s+", 2);
		String keyword = parts[0];
		
		if("uci".equals(keyword)) {
			commandUci(input);
			return;
		}
		if("isready".equals(keyword)) {
			commandIsReady(input);
		}
		if("ucinewgame".equals(keyword)) {
			commandUciNewGame(input);
		}
		if("position".equals(keyword)) {
			// TODO: Proper error handling
				try {
					commandPosition(input);
				} catch (NotationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		if("go".equals(keyword)) {
			commandGo(input);
		}
		
		//System.out.println(input);
	}
	
	private void doOutput(Object output) {
		System.out.println(output);
	}
	
	private void commandUci(String input) {
		doOutput("id name unidexter");
		doOutput("id author Michael Aherne");
		
		// TODO: send options here
		
		doOutput("uciok");
	}
	
	private void commandIsReady(String input) {
		doOutput("readyok");
	}
	
	private void commandUciNewGame(String input) {
		// TODO: implement
	}
	
	/*position [fen <fenstring> | startpos ]  moves <move1> .... <movei>*/
	private void commandPosition(String input) throws UCIException, NotationException {
		String[] tokens = input.split("\\s+");
		if(!"position".equals(tokens[0])){
			throw new UCIException();
		}
		if("startpos".equals(tokens[1])) {
			try {
				currentPosition = Position.fromFEN(Position.initialFEN);
				if(tokens.length > 3 && "moves".equals(tokens[2])){
					for(int i = 3; i < tokens.length; i++) {
						currentPosition.move(tokens[i], NotationType.LONG_ALGEBRAIC);
					}
				}
			} catch (FENException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if("fen".equals(tokens[1])) {
			/* Think we're looking for everything after fen 
			 * until the word "move" or end of line
			 */
		} else {
			throw new UCIException("Position must be startpos or fen");
		}
	}
	
	private void commandGo(String input) {
		int[] bestMove = currentPosition.bestMove();
		doOutput("info string e.p. " + currentPosition.epSquare[0] + ", " + currentPosition.epSquare[1]);
		doOutput("info nodes " + currentPosition.getNodesSearched());
		doOutput("bestmove " + currentPosition.moveNotation(bestMove, NotationType.LONG_ALGEBRAIC));
		currentPosition.move(bestMove);
	}


	public Position getCurrentPosition() {
		return currentPosition;
	}

	
}
