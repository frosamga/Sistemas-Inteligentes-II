package intsys.games;

public class NimMove {

	private int heap;
	private int removeItems;
	
	public NimMove(int h, int items) {
		heap = h;
		removeItems = items;
	}
	
	public int heap() {
		return heap;
	}
	
	public int removeItems() {
		return removeItems;
	}
	
	public boolean isValid(NimBoard board) {
		return (heap>=0) && (heap<NimBoard.HEAP_SIZE.length) &&
				(removeItems>0) && (removeItems<=board.items(heap));
	}
}
