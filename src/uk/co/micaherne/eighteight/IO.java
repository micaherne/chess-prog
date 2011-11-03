package uk.co.micaherne.eighteight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Set;

import uk.co.micaherne.FENException;
import uk.co.micaherne.NotationException;
import uk.co.micaherne.Position.NotationType;
import uk.co.micaherne.UCIException;

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
	Position currentPosition;


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
		if ("position".equals(keyword)) {
			commandPosition(input);
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
	private void commandPosition(String input) throws UCIException{
		String[] tokens = input.split("\\s+");
		if(!"position".equals(tokens[0])){
			throw new UCIException();
		}
		if("startpos".equals(tokens[1])) {
			currentPosition = new Position();
			currentPosition.initialPosition();
			if(tokens.length > 3 && "moves".equals(tokens[2])){
				for(int i = 3; i < tokens.length; i++) {
					currentPosition.move(tokens[i]);
				}
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
		int[] bestMove = new int[] { -1 , -1 };
		try {
			AlphaBetaSearch search = new AlphaBetaSearch();
			bestMove = search.bestMove(currentPosition, 4);
			doOutput("info nodes " + search.nodesSearched);
			if(bestMove[0] == -1) {
				doOutput("quit");
			} else {
				doOutput("info bestmove: " + bestMove[0] + ", " + bestMove[1]);
				doOutput("bestmove " + Position.moveToNotation(bestMove));
				currentPosition.move(bestMove);
			}
		} catch (Exception e) {
			doOutput("info movegen exception " + e.getMessage());
			doOutput("quit");
		}
		
	}


	public Position getCurrentPosition() {
		return currentPosition;
	}

	
}
