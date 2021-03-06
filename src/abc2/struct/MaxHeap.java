package abc2.struct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MaxHeap<T>{
	ArrayList<T> heap = new ArrayList<T>();
	
	Comparator<T> comp;
	int size = 0;
	
	public MaxHeap(Comparator<T> _comp){
		comp = _comp;
	}

	public int size(){
		return size;
	}
	

	public ArrayList<T> toList() { return heap; }
	
	public String toArrayString(){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<size; i++){
		//int i=0;
		//while(heap[i + root] != null){
			sb.append(get_heap(i + root));
			sb.append(" ");
			//i++;
		}
		if(sb.length() != 0)
			sb.deleteCharAt(sb.length() -1 );
		return sb.toString();
	}


	final int root = 0;
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
		heap.add(t);
		siftup();
		size++;
	}


	public T poll(){
		T ret = get_heap(root);
		if(ret == null)
			return ret;
		heap.set(root, get_heap(root + size - 1));
		heap.remove(root + size - 1);
		size--;
		siftdown();
		return ret;
	}
	
	public T peek(){
		return get_heap(root);
	}

	private void swap(int a, int b){
		T temp = get_heap(a);
		heap.set(a, get_heap(b));
		heap.set(b, temp);
	}

	
	private void siftup(){
		int r = root + size;
		while(r != root){
			if(comp.compare(get_heap(P(r)), get_heap(r)) > 0){
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
			T LC = get_heap(LC(r));
			T RC = get_heap(RC(r));
			if(LC == null){
				return;
			}
			if(RC == null){
				if(comp.compare(LC, get_heap(r)) > 0){
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
				if(comp.compare(get_heap(winner), get_heap(r)) > 0){
					swap(r, winner);
					r = winner;
					continue label1;
				}else if(comp.compare(get_heap(loser), get_heap(r)) > 0){
					swap(r, loser);
					r = loser;
					continue label1;
				}else{
					return;
				}
			}
		}
	}
	
	private T get_heap(int index){
		try{
			return heap.get(index);
		}catch(Exception e){
			return null;
		}
	
	}
}