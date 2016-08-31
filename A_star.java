/*
 * 
 * @author Sunny Narayan
 */
import java.util.*;

public class A_star {
	
	static Map<String, String> cameFrom = new HashMap<>();
	static Set<String> openSet = new HashSet<>();
	static Set<String> closedSet = new HashSet<>();
	
	public static void printMatrix(String s){
		for (int i = 0; i < s.length(); i++) {
			if (i%3==0){
				System.out.println();
			}
			System.out.print(s.charAt(i) + " ");
		}
		System.out.println();
	}
	
	public static void printSuccess(){
		System.out.println("Total number of states explored = " + closedSet.size());
		System.out.println("........OpenSet.........");
		Iterator iterator = openSet.iterator();
		while (iterator.hasNext()){
		   System.out.print(iterator.next() + " ");  
		}
		System.out.println();
		System.out.println("........ClosedSet.........");
		iterator = closedSet.iterator();
		while (iterator.hasNext()){
		   System.out.print(iterator.next() + " ");  
		}
		System.out.println();
	}
	
	public static int tilesDisplaced(String s){
		int count = 0;
		String ele = "123456780";
		for (int i = 0; i < 9; i++){
			if ((s.charAt(i) != '0') && (s.charAt(i) != ele.charAt(i))){
				count++;
			}
		}
		return count;
	}
	
	public static int manhattanDist(String s){
		int[][] a = new int[3][3];
		int index = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				a[i][j] = Character.getNumericValue(s.charAt(index));
				index++;
			}
		}
		int mDist = 0;
		for(int x = 0; x < 3; x++){
	    	for(int y = 0; y < 3; y++){
	        	int value = a[x][y];
	        	if (value != 0){
	        		int targX = (value-1)/3;
				    int targY = (value-1)%3;
				    int distX = Math.abs(x - targX);
				    int distY = Math.abs(y - targY);
		        	mDist = mDist + distX + distY;
	        	}
	        }
	    }
	    return mDist;
	}
	
	public static int h_value(String s, int cases){
		switch(cases){
		case 0: return 0;
		case 1: return tilesDisplaced(s);
		case 2: return manhattanDist(s);
		case 3: return manhattanDist(s) + 10000;
		}
		return -1;
	}
	
	public static ArrayList<Integer> getIndex(int index){
		ArrayList<Integer> indexes = new ArrayList<>();
		switch(index){
		case 0: indexes.add(1);indexes.add(3);break;
		case 1: indexes.add(0);indexes.add(4);indexes.add(2);break;
		case 2: indexes.add(1);indexes.add(5);break;
		case 3: indexes.add(0);indexes.add(4);indexes.add(6);break;
		case 4: indexes.add(1);indexes.add(3);indexes.add(5);indexes.add(7);break;
		case 5: indexes.add(2);indexes.add(4);indexes.add(8);break;
		case 6: indexes.add(3);indexes.add(7);break;
		case 7: indexes.add(4);indexes.add(6);indexes.add(8);break;
		case 8: indexes.add(5);indexes.add(7);break;
 		}
		
		return (indexes);
	}
	
	public static ArrayList<String> getNeighbour(String current){
		int index=111;
		for (int i = 0; i < 9; i++) {
			if (current.charAt(i) == '0'){
				index = i;
				break;
			}
		}
		
		ArrayList<Integer> indexes = new ArrayList<>();
		indexes = getIndex(index);
		ArrayList<String> n = new ArrayList<>();
		for (int i = 0; i < indexes.size(); i++) {
			char[] arr = current.toCharArray();
			char temp = arr[indexes.get(i)];
			arr[index] = temp;
			arr[indexes.get(i)] = '0';
			n.add(new String(arr));
		}
		return (n);
	}
	
public static Comparator<state> fComparator = new Comparator<state>(){
		@Override
		public int compare(state c1, state c2) {
            return (int) (c1.f - c2.f);
        }
	};
	
	public static int simulation(String start, int cases){
		Queue<state> pq = new PriorityQueue<>(fComparator);//openSet
		Map<String, Integer> f_score = new HashMap<>();//default value infinity
		Map<String, Integer> g_score = new HashMap<>();//default value infinity
		
		openSet.add(start);
		g_score.put(start, 0);
		f_score.put(start, h_value(start, cases));
		pq.add(new state(start,f_score.get(start)));
		while (!openSet.isEmpty()){
			String current = pq.poll().s;
			if (current.equals("123456780")){
				return reconstructPath(current);
			}
				
			openSet.remove(current);
			closedSet.add(current);
			
			ArrayList<String> neighbours = new ArrayList<>();
			neighbours = getNeighbour(current);
			for(String n : neighbours){
				if (closedSet.contains(n))
					continue;
				if (!openSet.contains(n)){
					openSet.add(n);
				}else if (g_score.get(current)+1 >= g_score.get(n)){
					continue;
				}
				
				cameFrom.put(n, current);
				g_score.put(n, g_score.get(current)+1);
				f_score.put(n, g_score.get(n)+h_value(n, cases));
				pq.add(new state(n, f_score.get(n)));
			}
			
		}
	
		return -1;
	}
	
	public static int reconstructPath(String current){
		int moves=0;
		System.out.println(".....Optimal Path.....");
		while (cameFrom.containsKey(current)){
			System.out.println(current);
			current = cameFrom.get(current);
			moves++;
		}
		
		return moves;
	}
	
	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the matrix in String format : ");
		String start = sc.nextLine();
		System.out.println("Enter the number for hueristics :\n0 - h(n)=0\n1. Tiles displaced\n2. Manhattan Distance\n3. h(n) >= h*(n)");
		int cases = Integer.parseInt(sc.nextLine());
		long startTime = System.currentTimeMillis();
		int moves = simulation(start,cases);
		long endTime = System.currentTimeMillis();
		double duration = (endTime - startTime);
		if (moves == -1){
			System.out.println("Failure");
		}else{
			printSuccess();
			System.out.println("Optimal Cost of Path = " + moves);
		}
		System.out.println(".....start state........");
		printMatrix(start);
		System.out.println(".....Goal state........");
		printMatrix("123456780");
		System.out.println();
		System.out.println("Time taken = " + duration + "ms");
		sc.close();
	}
}

class state{
	String s;
	int f;
	
	state(String s, int f){
		this.s = s;
		this.f = f;
	}
}

