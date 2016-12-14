
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

public class Search{

	public static void main(String[] args) throws IOException {
		BufferedReader br=new BufferedReader(new FileReader("Z:/MS Books/AI/input.txt"));
		BufferedWriter brw=new BufferedWriter(new FileWriter("Z:/MS Books/AI/output.txt"));
		String algoName=br.readLine();
		String startState=br.readLine();
		String goalState=br.readLine();
		int i;//Loop variable
		List<String> trafficList=new ArrayList<String>();
		List<String> sunTrafficList=new ArrayList<String>();
		int numLiveTrafficLines=Integer.parseInt(br.readLine());
		for(i=0;i<numLiveTrafficLines;i++){
			trafficList.add(br.readLine());
		}
		int numSunTrafficLines=Integer.parseInt(br.readLine());
		for(i=0;i<numSunTrafficLines;i++){
			sunTrafficList.add(br.readLine());
		}
		br.close();
		List<String> outList=new ArrayList<String>();//outList: List that stores the output 
		if(startState.equals(goalState)){
			outList.add(startState+" "+"0");
			Iterator outputListIterator=outList.iterator();
			while(outputListIterator.hasNext()){
				String output=outputListIterator.next().toString();
				brw.write(output);
				brw.newLine();
			}
			brw.close();
		}
		else{
			Node[] sourceAndGoal=createGraph(startState,goalState,trafficList,numLiveTrafficLines,algoName,sunTrafficList);//Function that creates the graph and returns start and goal states
			Node sourceNode=sourceAndGoal[0];
			Node goalNode=sourceAndGoal[1];
			switch(algoName){
			case "BFS":{
				BFS(sourceNode,goalNode,goalState);
				outputFileWriter(goalNode,outList,brw,algoName);
				break;
			}
			case "DFS":{
				DFS(sourceNode,goalNode,goalState);
				outputFileWriter(goalNode,outList,brw,algoName);
				break;
			}
			case "UCS":{
				UCS(sourceNode,goalNode,goalState);
				outputFileWriter(goalNode,outList,brw,algoName);
				break;
			}
			case "A*":{
				AStar(sourceNode,goalNode,goalState);
				outputFileWriter(goalNode,outList,brw,algoName);
				break;
			}
			}
		}		
		}
/* *************************************END OF MAIN********************************************************** */	
/***********************************CREATING THE GRAPH *******************************************************/
	private static Node[] createGraph(String startState, String goalState, List<String> trafficList, int numLiveTrafficLines, String algoName, List<String> sunTrafficList) {
		Map<String,Integer> nodeMap=new HashMap<String,Integer>();
		Queue<String> encountered=new LinkedList<String>();
		List<String> explored=new ArrayList<String>();
		Node[] sourceAndGoal = null;
		Iterator trafficListIterator=trafficList.iterator();
		int nodecounter=0;			
		List<Node> nodes=new ArrayList<Node>();
		Node sourceNode = null,goalNode = null;
		while(trafficListIterator.hasNext()){				
			String line=trafficListIterator.next().toString();
			String[] lineSplit=line.split("\\s+");
			String source=lineSplit[0];
			String sink=lineSplit[1];
			int cost,heuristicCost;
			if(algoName.equals("BFS")||algoName.equals("DFS")){
				cost=1;
			}
			else{
				cost=Integer.parseInt(lineSplit[2]);
			} 
			String dropped = source;
			if(!(encountered.contains(source)) && !(explored.contains(source))){
				heuristicCost=Integer.parseInt(sunTrafficList.get(nodecounter).split("\\s+")[1]);
				nodes.add(new Node(source,heuristicCost));
				nodeMap.put(source, nodecounter);
				explored.add(source);
				if(source.equals(startState)){
					sourceNode=nodes.get(nodecounter);
				}
				nodecounter++;
				encountered.add(source);
			}		
			if(!(encountered.contains(sink)) && !(explored.contains(sink))){
				heuristicCost=Integer.parseInt(sunTrafficList.get(nodecounter).split("\\s+")[1]);
				nodes.add(new Node(sink,heuristicCost));
				if(sink.equals(goalState)){
					goalNode=nodes.get(nodecounter);
				}
				nodeMap.put(sink, nodecounter);
				encountered.add(sink);
				explored.add(sink);
				nodecounter++;
			}
			nodes.get(nodeMap.get(source)).adjacencies.add(new Edge(nodes.get(nodeMap.get(sink)),cost));
		}
		sourceAndGoal=new Node[]{sourceNode,goalNode};
		return sourceAndGoal;
}
/* ***************************************END OF CREATE GRAPH****************************************** */
/*****************************************CREATING PATH*************************************************/
	private static Map<Integer, Node> printPath(Node goalNode, String algoName) {
		Map<Integer,Node> pathMap = new TreeMap<Integer,Node>();
        for(Node node = goalNode; node!=null; node = node.parent){
        	if(algoName.equals("A*")){
        		pathMap.put((node.pathCost-node.heuristicCost),node);
        	}
        	else{
        		pathMap.put(node.pathCost, node);
        	}           
        }
        return pathMap;
	}
/* *************************************END OF CREATING PATH******************************************** */
/**************************************OUTPUT FILE WRITER ***********************************************/
	private static void outputFileWriter(Node goalNode, List<String> outList, BufferedWriter brw,String algoName) throws IOException {
		Map<Integer,Node> pathMap = printPath(goalNode,algoName);
        for(Map.Entry<Integer, Node> m:pathMap.entrySet()){
        	outList.add(m.getValue()+" "+m.getKey());
        }
		Iterator outputListIterator=outList.iterator();
		while(outputListIterator.hasNext()){
			String output=outputListIterator.next().toString();
			brw.write(output);
			brw.newLine();
		}
		brw.close();		
	}	
/* ***********************************END OF OUTPUT FILE WRITER******************************************* */
/*****************************************BFS FUNCTION*****************************************************/
	private static void BFS(Node sourceNode, Node goalNode, String goalState) {
		sourceNode.pathCost=0;
		Queue<Node> bfsQueue=new LinkedList<Node>();
		bfsQueue.add(sourceNode);
		Set<Node> explored = new HashSet<Node>();
        do{
        	Node current=bfsQueue.remove();
        	explored.add(current);
        	for(Edge e: current.adjacencies){
                Node child = e.target;
                int cost = e.cost;
                child.tempCost=child.pathCost;
                child.pathCost = current.pathCost + cost;                              
                if(!explored.contains(child) && !bfsQueue.contains(child)){               	
                    child.parent = current;
                    bfsQueue.add(child);
                }
                else if((bfsQueue.contains(child))&&(child.pathCost<child.tempCost)){
                    child.parent=current;
                }
                else if(child.pathCost>=child.tempCost){
                	child.pathCost=child.tempCost;
                }
        	}        	        	
        }while(!bfsQueue.isEmpty());
	}
/* *************************************END OF BFS********************************************************* */
/**************************************DFS******************************************************************/
	private static void DFS(Node sourceNode, Node goalNode, String goalState) {
		sourceNode.pathCost=0;
		Stack<Node> dfsStack=new Stack<Node>();
		dfsStack.push(sourceNode);
		Set<Node> explored=new HashSet<Node>();
		int flag=0;
		do{
			Node current=dfsStack.pop();
			explored.add(current);
			Collections.reverse(current.adjacencies);
			for(Edge e: current.adjacencies){
				Node child=e.target;
				int cost=e.cost;
				if(!explored.contains(child) && !dfsStack.contains(child)){               	
                    child.parent = current;
                    child.pathCost=current.pathCost+cost;
                    dfsStack.push(child);
                    if(child.value.equals(goalState)){
                    	flag=1;
                    }
                }				
			}
		}while(!dfsStack.isEmpty() && flag==0);
	}
/* ************************************END OF DFS*********************************************************** */
/**************************************UCS*******************************************************************/
	private static void UCS(Node sourceNode,Node goalNode,String goalState) {
		sourceNode.pathCost=0;
		int currentChildPathCost=0;
		PriorityQueue<Node> queue = new PriorityQueue<Node>(20, 
	            new Comparator<Node>(){
	                public int compare(Node i, Node j){
	                    if(i.pathCost > j.pathCost){
	                        return 1;
	                    }
	                    else if (i.pathCost < j.pathCost){
	                        return -1;
	                    }
	                    else{
	                        return 0;
	                    }
	                }
	            }
	        );
		queue.add(sourceNode);
        Set<Node> explored = new HashSet<Node>();
        do{
            Node current = queue.poll();
            explored.add(current);
            for(Edge e: current.adjacencies){
                Node child = e.target;
                int cost = e.cost;
                child.tempCost=child.pathCost;
                child.pathCost = current.pathCost + cost;
                if(!explored.contains(child) && !queue.contains(child)){
                    child.parent = current;
                    queue.add(child);
                    if(child.value.equals(goalState)){
                    	currentChildPathCost=child.pathCost;
                    }
                }
                else if((child.pathCost<child.tempCost)){
                    child.parent=current;
                }
                else if(child.pathCost>=child.tempCost){
                	child.pathCost=child.tempCost;
                }
            }
        }while(!queue.isEmpty());
	}
/* ***********************************END OF UCS******************************************************************** */
/*************************************A STAR*************************************************************************/
	private static void AStar(Node sourceNode, Node goalNode, String goalState) {
		sourceNode.pathCost=0+sourceNode.heuristicCost;
		int currentChildPathCost=0;
		PriorityQueue<Node> queue = new PriorityQueue<Node>(20, 
	            new Comparator<Node>(){
	                public int compare(Node i, Node j){
	                    if(i.pathCost > j.pathCost){
	                        return 1;
	                    }
	                    else if (i.pathCost < j.pathCost){
	                        return -1;
	                    }
	                    else{
	                        return 0;
	                    }
	                }
	            }
	        );
		queue.add(sourceNode);
        Set<Node> explored = new HashSet<Node>();
        do{
            Node current = queue.poll();
            explored.add(current);
            for(Edge e: current.adjacencies){
                Node child = e.target;
                int cost = e.cost+child.heuristicCost;
                child.tempCost=child.pathCost;
                child.pathCost = current.pathCost-current.heuristicCost + cost;
                if(!explored.contains(child) && !queue.contains(child)){
                    child.parent = current;
                    queue.add(child);
                    if(child.value.equals(goalState)){
                    	currentChildPathCost=child.pathCost;
                    }
                }
                else if((queue.contains(child))&&(child.pathCost<child.tempCost)){
                    child.parent=current;                }
                else if(child.pathCost>=child.tempCost){
                	child.pathCost=child.tempCost;
                }
            }
        }while(!queue.isEmpty());		
	}
/* ***********************************END OF A STAR******************************************************* */
}
/*******************************************NODE CLASS******************************************************/
class Node{
	public final String value;
    public int pathCost,tempCost;
    public int heuristicCost;
    public List<Edge> adjacencies;
    public Node parent;
    public Node(String val, int heuristicCost){
        value = val;
        adjacencies=new ArrayList<>();
        this.heuristicCost=heuristicCost;
    }
    public String toString(){
        return value;
    } 
}
/* ***********************************END OF NODE CLASS************************************************** */
/************************************EDGE CLASS***********************************************************/
class Edge{
	public final int cost;
    public final Node target;
    public Edge(Node targetNode, int costVal){
        cost = costVal;
        target = targetNode;
    }
}
/* **********************************END OF EDGE CLASS************************************************** */


