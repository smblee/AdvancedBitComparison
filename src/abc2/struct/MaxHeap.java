package abc2.struct;

import java.util.Arrays;
import java.util.Comparator;

public class MaxHeap<T>{
	static int HEAP_SIZE = 5;
	
	@SuppressWarnings("unchecked")
		T[] heap = (T[]) new Object[HEAP_SIZE];
	Comparator<T> comp;
	int size = 0;
	

	public MaxHeap(Comparator<T> _comp){
		Arrays.fill(heap, null);
		comp = _comp;
	}

	public int size(){
		return size;
	}
	

	public T[] toArray() { return heap; }
	
	public String toArrayString(){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<size; i++){
		//int i=0;
		//while(heap[i + root] != null){
			sb.append(heap[i + root]);
			sb.append(" ");
			//i++;
		}
		if(sb.length() != 0)
			sb.deleteCharAt(sb.length() -1 );
		return sb.toString();
	}


	final int root = 1;
	int LC(int r){
		return 2 * r;
	}

	int RC(int r){
		return 2 * r + 1;
	}

	int P(int r){
		return r/2;
	}

	public void insert(T t){
		checksize();
		heap[root + size] = t;
		siftup();
		size++;
	}


	public T poll(){
		T ret = heap[root];
		if(ret == null)
			return ret;
		heap[root] = heap[root + size - 1];
		heap[root + size - 1] = null;
		size--;
		siftdown();
		return ret;
	}
	
	public T peek(){
		return heap[root];
	}

	private void swap(int a, int b){
		T temp = heap[a];
		heap[a] = heap[b];
		heap[b] = temp;
	}

	private void checksize(){
		if(size + root >= HEAP_SIZE - 1){
			@SuppressWarnings("unchecked")
				T[] heap1 = (T[])new Object[heap.length + HEAP_SIZE];
			Arrays.fill(heap1, null);
			for(int i=0; i< heap.length; i++)
				heap1[i] = heap[i];

			heap = heap1;
		}
	}


	private void siftup(){
		int r = root + size;
		while(r != root){
			if(comp.compare(heap[P(r)], heap[r]) > 0){
				return;
			}
			else{
				swap(r, P(r));
				r = P(r);
			}
		}
	}

	private void siftdown(){
		int r = root;
		label1:
		while(r < root + size){
			T LC = heap[LC(r)];
			T RC = heap[RC(r)];
			if(LC == null){
				return;
			}
			if(RC == null){
				if(comp.compare(LC, heap[r]) > 0){
					swap(r, LC(r));
					r = LC(r);
					continue label1;
				}else{
					return;
				}
			}else{
				if(comp.compare(LC, RC) == 0){
					swap(r, LC(r));
					r = LC(r);
					continue label1;
				}
				int winner = comp.compare(LC, RC) > 0? LC(r) : RC(r);
				int loser = comp.compare(LC, RC) > 0? RC(r) : LC(r);
				if(comp.compare(heap[winner], heap[r]) > 0){
					swap(r, winner);
					r = winner;
					continue label1;
				}else if(comp.compare(heap[loser], heap[r]) > 0){
					swap(r, loser);
					r = loser;
					continue label1;
				}else{
					return;
				}
			}
		}
	}
}