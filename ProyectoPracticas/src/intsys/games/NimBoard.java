package intsys.games;

import java.util.ArrayList;
import java.util.List;

public class NimBoard {

	public static final int[] HEAP_SIZE = {1, 3, 5};
	
	int[] heap;
	int PlayerToMove;
	
	public NimBoard() {
		heap = new int[HEAP_SIZE.length];
		for(int i=0; i<HEAP_SIZE.length; i++) heap[i] = HEAP_SIZE[i];
		PlayerToMove=0;
	}
	
	public NimBoard(NimBoard b)
	{
		heap = new int[HEAP_SIZE.length];
		for(int i=0; i<HEAP_SIZE.length; i++) heap[i] = b.heap[i];
		PlayerToMove=b.PlayerToMove;
	}
	
	public NimPlayer getPlayer()
	{
		if (PlayerToMove==0)
		{
			return NimPlayer.PLAYER_1;
		}
		else
		{
			return NimPlayer.PLAYER_2;			
		}
	}

	public int items(int h) {
		if(h>=0 && h<HEAP_SIZE.length) return heap[h];
		return -1;
	}
	
	public void remove(int h, int items) {
		if(h>=0 && h<HEAP_SIZE.length) {
			heap[h] -= items;
			if(heap[h]<0) heap[h] = 0;
		}
		PlayerToMove=1-PlayerToMove;
	}
	
	public boolean isAlone() {
		int items = 0;
		
		for(int i=0; i<HEAP_SIZE.length; i++) items += heap[i];
		return items==1;
	}
	
	public boolean isEmpty() {
		int items = 0;
		
		for(int i=0; i<HEAP_SIZE.length; i++) items += heap[i];
		return items==0;
	}
	
	public List<NimMove> getMoves() {
		List<NimMove> moves = new ArrayList<NimMove>();
		
		for(int h=0; h<HEAP_SIZE.length; h++) {
			for(int i=items(h); i>0; i--) {
				moves.add(new NimMove(h, i));				
			}
		}
		return moves;
	}
	
	@Override
	protected Object clone() {
		NimBoard newboard = new NimBoard();
		
		for(int i=0; i<HEAP_SIZE.length; i++) newboard.heap[i] = this.heap[i];
		newboard.PlayerToMove=this.PlayerToMove;
		
		return newboard;
	}

	@Override
	public boolean equals(Object obj) {
		NimBoard b = (NimBoard) obj;
		int i = 0;
		while((i<HEAP_SIZE.length) && (this.heap[i]==b.heap[i])) i++;
		return (i>=HEAP_SIZE.length) && (b.PlayerToMove==this.PlayerToMove);
	}
	
	
}
