package intsys.games;


import java.util.List;
import aima.core.search.adversarial.Game;



public class Nim implements Game<NimBoard,NimMove,NimPlayer> {


	public NimPlayer[] getPlayers()
	{
		NimPlayer result[]=new NimPlayer[2];
		
		result[1]=NimPlayer.PLAYER_1;
		result[2]=NimPlayer.PLAYER_2;
		return result;
	}
	
	public NimBoard getInitialState()
	{
		return new NimBoard();
	}


	@Override
	public NimPlayer getPlayer(NimBoard state) {
		return state.getPlayer();
	}

	@Override
	public List<NimMove> getActions(NimBoard state) {
		return state.getMoves();

	}

	@Override
	public NimBoard getResult(NimBoard state, NimMove action) {
		NimBoard aState = new NimBoard(state);
		aState.remove(action.heap(), action.removeItems());
		
		return aState;
		
	}

	@Override
	public boolean isTerminal(NimBoard state) {
		// TODO: Provide a terminal test
		return true;		
	}

	@Override
	public double getUtility(NimBoard state, NimPlayer player) {
		// TODO: Provide an utility function
		return 0;
	}

}
