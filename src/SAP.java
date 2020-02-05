import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    	private Digraph digraph;
    	
       // constructor takes a digraph (not necessarily a DAG)
       public SAP(Digraph G) {
    	   if(G == null) {
    		   throw new IllegalArgumentException();
    	   }
    	   
    	   this.digraph = new Digraph(G);
       }

       // length of shortest ancestral path between v and w; -1 if no such path
       public int length(int v, int w) {
    	   if(v < 0 || v > digraph.V() -1 || w < 0 || w > digraph.V() -1) {
    		   throw new IllegalArgumentException();   
    	   }
    	   return findSAP(v,w).size;
       }

       // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
       public int ancestor(int v, int w) {
    	   if(v < 0 || v > digraph.V()-1|| w < 0 || w > digraph.V()-1) {
    		   throw new IllegalArgumentException();   
    	   }
    	   return findSAP(v, w).value;

       }

//       private Ancestor findAncestor(int v, int w) {
//    	   int ancestor = -1;
//    	   //BFS on A
//    	   int firstNoun = v;
//    	   HashMap<Integer, Tuple> mapA = new HashMap<>();
//    	   Queue<Integer> q = new Queue<>();
//    	   q.enqueue(firstNoun);
//    	   mapA.put(firstNoun, new Tuple(-1, 0));
//    	   
//    	   while(!q.isEmpty()) {
//    		   int current = q.dequeue();
//    		   Iterator<Integer> iter = digraph.adj(current).iterator();
//    		  
//    		   while(iter.hasNext()) {
//    			 
//    			   int next = iter.next();
//    			   if(!mapA.containsKey(next)) {
//    			   mapA.put(next, new Tuple(current, (mapA.get(current).distance)+1));
//    			   q.enqueue(next);
//    			   }
//    		   }
//    	   }
//    	   
//    	   //BFS on B
//    	   int secondNoun = w;
//    	   HashMap<Integer, Tuple> mapB = new HashMap<>();
//    	   Queue<Integer> qB = new Queue<>();
//    	   qB.enqueue(secondNoun);
//    	   mapB.put(secondNoun, new Tuple(-1, 0));
//    	   int currentDistance = (int) Double.POSITIVE_INFINITY;
//    	   while(!qB.isEmpty()) {
//    		   int current = qB.dequeue();
//
//    		   if(mapA.containsKey(current)) {
//				   if(mapA.get(current).distance + mapB.get(current).distance < currentDistance) {
//					   currentDistance = mapA.get(current).distance + mapB.get(current).distance;
//					   ancestor = current;
//				   }
//				 
//			   }
//    		   Iterator<Integer> iter = digraph.adj(current).iterator();
//    		
//
//    		   while(iter.hasNext()) {
//    				  
//    			   int next = iter.next();
//    			   if(!mapB.containsKey(next)) {
//    			   mapB.put(next, new Tuple(current, mapB.get(current).distance + 1));
//    			   qB.enqueue(next);
//    			   }
//    			   
//    		   }
//    		  
//    	   }
//    	   return new Ancestor(ancestor, currentDistance);
//	}

	// length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
       public int length(Iterable<Integer> v, Iterable<Integer> w) {
    	   if(v == null || w == null) {
    		   throw new IllegalArgumentException();
    	   }
    	   if(digraph.V() == 0) {
    		   return -1;
    	   }
    	   verifyNullItems(v.iterator());
    	   verifyNullItems(w.iterator());
    	   
    	   int min = (int) Double.POSITIVE_INFINITY;
    	   for(int vertexA : v) {
    		   for(int vertexB: w) {
    			   int current = findSAP(vertexA, vertexB).size;
    			 int dist = current;
    			  if(dist < min) {
    				  min = dist;
    			  }
    		   }
    	   }
    	  return min == (int) Double.POSITIVE_INFINITY ? -1 : min;
    	   }
       
       private void verifyNullItems(Iterator<Integer> iter) {
    	   while(iter.hasNext()) {
    		   Integer v = iter.next();
    		   if(v == null || v < 0 || v > digraph.V() -1) {
    			   throw new IllegalArgumentException();
    	   }
    	   }
       }
       
       // a common ancestor that participates in shortest ancestral path; -1 if no such path
       public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
    	   if(v == null || w == null) {
    		   throw new IllegalArgumentException();
    	   }
    	   if(digraph.V() == 0) {
    		   return -1;
    	   }
    	   verifyNullItems(v.iterator());
    	   verifyNullItems(w.iterator());
    	   
    	   int ancestor =  -1;
    	   int min = (int) Double.POSITIVE_INFINITY;
    	   for(int vertexA : v) {
    		   for(int vertexB: w) {
    			   Ancestor an = findSAP(vertexA, vertexB);
    			 int dist = an.size;
    			  if(dist < min) {
    				  min = dist;
    				  ancestor = an.value;
    			  }
    		   }
    	   }
    	  return ancestor;

       }
       
   
       private Ancestor findSAP(int v, int w) {
	   int ancestor = -1;
	   //BFS on A

	   HashMap<Integer, Tuple> mapA = new HashMap<>();
	   Queue<Integer> q = new Queue<>();
	   q.enqueue(v);
	   mapA.put(v, new Tuple(-1, 0));
	   
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


	   int secondNoun = w;
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
	   
	   //no match
	   if(ancestor == -1) {
		   return new Ancestor(ancestor, path.size() -1);
	   }
	   
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
	   
	   return new Ancestor(ancestor, path.size() -1);
	   
   }

       // do unit testing of this class
       public static void main(String[] args) {
    		    In in = new In(args[0]);
    		    Digraph G = new Digraph(in);
    		    SAP sap = new SAP(G);
    		    while (!StdIn.isEmpty()) {
    		        int v = StdIn.readInt();
    		        int w = StdIn.readInt();
    		        int length   = sap.length(v, w);
    		        int ancestor = sap.ancestor(v, w);
    		        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);

    		}
//    		    List<Integer> listA = new ArrayList<Integer>();
//    		    listA.add(13);
//    		    listA.add(23);
//    		    listA.add(24);
//    		    
//    		    List<Integer> listB = new ArrayList<Integer>();
//    		    listB.add(6);
//    		    listB.add(16);
//    		    listB.add(17);
//    		    System.out.println(sap.ancestor(listA, listB));
//    		    System.out.println(sap.length(listA, listB));
       }
       private class Tuple{
    	   private int parent;
    	   private int distance;
		public Tuple(int parent, int distance) {
			super();
			this.parent = parent;
			this.distance = distance;
		}
    	   
       }
       private class Ancestor{
     	  int value;
     	  int size;
     	  
 		public Ancestor(int value, int size) {
 			super();
 			this.value = value;
 			this.size = size;
 		}
 		
     	  
       } 
    }

