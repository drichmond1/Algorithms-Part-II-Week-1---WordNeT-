import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;


import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

public class WordNet {
    	private Digraph digraph;
    	private Map<String, LinkedList<Integer>> synsets;
    	private Map<Integer, LinkedList<String>> words;
    	private int count;
       // constructor takes the name of the two input files
       public WordNet(String synsets, String hypernyms) {
    	   
    	   if(synsets == null || hypernyms == null) {
    		   throw new IllegalArgumentException();
    	   }
    	     	   
    	   loadSynsets(synsets);

   		   digraph = new Digraph(count);
   		   
    	   loadGraph(hypernyms);
    	   
    	   isRootedDAG();
       }

       private void isRootedDAG() {
    	   DirectedCycle cycle = new DirectedCycle(digraph);
    	   if (cycle.hasCycle()) {
    		   throw new IllegalArgumentException();
    	   }
    	   
    	   boolean foundRoot = false;
    	   for(int x = 0; x < digraph.V(); x ++) {
    		  if(digraph.outdegree(x) == 0 && !foundRoot) {
    			  foundRoot = true;
    		  }
    		  else if(digraph.outdegree(x) == 0 && foundRoot) {
    			  throw new IllegalArgumentException(); 
    		  }
    	   }
    	   if (!foundRoot) {
    		   throw new IllegalArgumentException();
    	   }
	}

	private void loadGraph(String hypernyms) {
		In in = new In(hypernyms);

 	   while(in.hasNextLine()) {
 	   String [] arr = in.readLine().split(",");
 	   for(int i=1; i < arr.length; i++) {
 		  digraph.addEdge(Integer.parseInt(arr[0]), Integer.parseInt(arr[i]));
 	   }
 	   }
	}

	private void loadSynsets(String synsetsFile) {
    	   synsets = new HashMap<>();
    	   words = new HashMap<>();
    	   In in = new In(synsetsFile);

    	   while(in.hasNextLine()) {
    	   String [] arr = in.readLine().split(",");
    	   int index = Integer.parseInt(arr[0]);
    	   
    	   String [] nouns = arr[1].split("\\s+");
    	   for(int i=0; i < nouns.length; i++) {
    		   if(!synsets.containsKey(nouns[i])) {
    			   synsets.put(nouns[i], new LinkedList<>());
    		   }
    		   synsets.get(nouns[i]).add(index); 
    		   if(!words.containsKey(index)) {
    			   words.put(index, new LinkedList<>());
    		   }
    		   words.get(index).add(nouns[i]);
    	   }
    	   	
    	    count ++;
    	   }

	}

	// returns all WordNet nouns
       public Iterable<String> nouns(){
    	   return new Iterable<String>() {
			
			@Override
			public Iterator<String> iterator() {
				return synsets.keySet().iterator();
			}
		};
       }

       // is the word a WordNet noun?
       public boolean isNoun(String word) {
    	   if(word == null) {
    		   throw new IllegalArgumentException();
    	   }
    	   return synsets.containsKey(word);
       }

       // distance between nounA and nounB (defined below)
       public int distance(String nounA, String nounB) {
    	   if( !synsets.containsKey(nounA) || !synsets.containsKey(nounB)) {
    		   throw new IllegalArgumentException();
    	   }
    	   int min = (int) Double.POSITIVE_INFINITY;
    	   for(int vertexA : synsets.get(nounA)) {
    		   for(int vertexB: synsets.get(nounB)) {
    			  int dist = findSAP(vertexA, vertexB).getSize();
    			  if(dist < min) {
    				  min = dist;
    			  }
    		   }
    	   }
    		   
    	   
    	  return min;
       }
       
      

       

	// a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
       // in a shortest ancestral path (defined below)
       public String sap(String nounA, String nounB) {
    	   if( !synsets.containsKey(nounA) || !synsets.containsKey(nounB)) {
    		   throw new IllegalArgumentException();
    	   }
    	   
    	   //List<Integer> path = null;
    	   String ancestor =  null;
    	   int min = (int) Double.POSITIVE_INFINITY;
    	   for(int vertexA : synsets.get(nounA)) {
    		   for(int vertexB: synsets.get(nounB)) {
    			   Ancestor current = findSAP(vertexA, vertexB);
    			 int dist = current.getSize();
    			  if(dist < min) {
    				  min = dist;
    				  ancestor = current.getValue();
    			  }
    		   }
    	   }
    		   
    	   
    	  return ancestor;

       }
       
       private Ancestor findSAP(int A, int B) {
    	   int ancestor = 0;

    	   //BFS on A
    	   int firstNoun = A;
    	   HashMap<Integer, Tuple> mapA = new HashMap<>();
    	   Queue<Integer> q = new Queue<>();
    	   q.enqueue(firstNoun);
    	   mapA.put(firstNoun, new Tuple(-1, 0));
    	   
    	   while(!q.isEmpty()) {
    		   int current = q.dequeue();
    		   Iterator<Integer> iter = digraph.adj(current).iterator();
 
    		   while(iter.hasNext()) {
    			 
    			   int next = iter.next();
    			   if(!mapA.containsKey(next)) {
    			   mapA.put(next, new Tuple(current, (mapA.get(current).distance)+1));
    			   q.enqueue(next);
    			   }
    		   }
    	   }
    	   
    	   //BFS on B
    	   int secondNoun = B;
    	   HashMap<Integer, Tuple> mapB = new HashMap<>();
    	   Queue<Integer> qB = new Queue<>();
    	   qB.enqueue(secondNoun);
    	   mapB.put(secondNoun, new Tuple(-1, 0));
    	   int currentDistance = (int) Double.POSITIVE_INFINITY;
    	   while(!qB.isEmpty()) {
    		   int current = qB.dequeue();
    		   if(mapA.containsKey(current)) {
				   if(mapA.get(current).distance + mapB.get(current).distance < currentDistance) {
					   currentDistance = mapA.get(current).distance + mapB.get(current).distance;
					   ancestor = current;
				   }
				 
			   }
    		   Iterator<Integer> iter = digraph.adj(current).iterator();
    		

    		
    		   while(iter.hasNext()) {
    				  
    			   int next = iter.next();
    			   if(!mapB.containsKey(next)) {
    			   mapB.put(next, new Tuple(current, mapB.get(current).distance + 1));
    			   
    			   
    			   
    			   qB.enqueue(next);
    			   }
    			   
    		   }
    		  
    	   }
    	   LinkedList<Integer> path = new LinkedList<>();
    	   
    	   Stack<Integer> stack = new Stack<>();
    	   stack.push(ancestor);
    	   int parentA = mapA.get(ancestor).parent;
    	   while(parentA != -1) {
    		  stack.push(parentA);
    		  parentA = mapA.get(parentA).parent;
    	   }
    	   
    	   Queue<Integer> queue = new Queue<>();
    	   queue.enqueue(ancestor);
    	   int parentB = mapB.get(ancestor).parent;
    	   while(parentB != -1) {
    		   queue.enqueue(parentB);
    		  parentB = mapB.get(parentB).parent;
    	   }
    	   
    	   
    	   Iterator<Integer> iterA = stack.iterator();
    	   while(iterA.hasNext()) {
    		   path.add(iterA.next());
    	   }
    	   
    	   Iterator<Integer> iterB = queue.iterator();
    	   
    	   iterB.next(); //avoiding duplicate ancestor in path
    	   
    	   while(iterB.hasNext()) {
    		   path.add(iterB.next());
    	   }
    	   StringBuilder builder = new StringBuilder("");
    	   for(String x: words.get(ancestor)) {
    		   builder.append(x);
    		   builder.append(" ");
    	   }

    	   int dist = path.size() -1;
    	   return new Ancestor(builder.toString().trim(), dist);
    	   
       }

       // do unit testing of this class
       public static void main(String[] args) {
//    	   WordNet wn = new WordNet(args[0], args[1]);
//    	   System.out.println(wn.distance("shooting_lodge", "bigamist"));
//    	   System.out.println(wn.sap("shooting_lodge", "bigamist"));
//    	   System.out.println(wn.distance("phonograph_album", "record_album"));
 //   	   System.out.println(wn.sap("Urga", "Ken_Kesey"));
//    	   System.out.println(wn.sap("G", "N"));
       }
       
      private class Ancestor{
    	  String value;
    	  int size;
    	  
		public Ancestor(String value, int size) {
			super();
			this.value = value;
			this.size = size;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public int getSize() {
			return size;
		}
		public void setSize(int size) {
			this.size = size;
		}
    	  
      }
      
      private class Tuple{
   	   private int parent;
   	   private int distance;
		public Tuple(int parent, int distance) {
			super();
			this.parent = parent;
			this.distance = distance;
		}
   	   public String toString() {
   		   return "parent :" + parent + " distance: " + distance;
   	   }
      }
    }

