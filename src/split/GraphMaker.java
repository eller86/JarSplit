package split;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphMaker {

	private Set<String> getRealRoots(Set<String> provisionalRoots, Map<String, Node> nodes){
		Set<String> ret = new HashSet<String>();
		for(String key : provisionalRoots){
			ret.add(key);
		}
		for(String rootName : provisionalRoots){
			Node rootNode = nodes.get(rootName);
			for(Node childNode : rootNode.getChildren()){
				String childName = childNode.getData();
				if(ret.contains(childName)){
					ret.remove(childName);
					System.out.println("remove provisional root " + childName);
				}
			}
		}
		return ret;
	}

	
	private Map<String, Node> convertToNode(Map<String, Set<String>> dependency){
		Map<String, Node> nodes = new HashMap<String, Node>();
		for(Map.Entry<String, Set<String>> entry : dependency.entrySet()){
			String key = entry.getKey();
			if(!nodes.containsKey(key)){
				nodes.put(key, new Node(key));
			}
			for(String key2 : entry.getValue()){
				if(!nodes.containsKey(key2)){
					nodes.put(key2, new Node(key2));
				}
			}
		}
		return nodes;
	}

	
	private void addRelation(Map<String, Set<String>> dependency, Map<String, Node> nodes, Set<String> roots){
		for(Map.Entry<String, Set<String>> entry : dependency.entrySet()){
			Node node = nodes.get(entry.getKey());
			for(String key2 : entry.getValue()){
				node.addChild(nodes.get(key2));
				roots.remove(key2);
			}
		}
	}

	private void addNonMySuperClass(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs, Map<String, Node> nodes, Set<String> roots){
		for(Map.Entry<String, Set<String>> entry : super2subs.entrySet()){
			String superName = entry.getKey();
			Set<String> subNames = entry.getValue();
			Set<Node> subNodes = new HashSet<Node>();
			for(String subName : entry.getValue()){
				subNodes.add(nodes.get(subName));
			}
			
			for(Map.Entry<String, Set<String>> depends : dependency.entrySet()){
				String key = depends.getKey();
				Node targetNode = nodes.get(key);

				if(depends.getValue().contains(superName) && !subNames.contains(key)){
					for(Node subNode : subNodes){
						targetNode.addChild(subNode);
						roots.remove(subNode.getData());
					}
					
				}
			}
			
		}
	}
	
	
	public Set<Node> makeGraph(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs){
		Map<String, Node> nodes = convertToNode(dependency);
		Set<String> roots = new HashSet<String>(dependency.keySet());
		addRelation(dependency, nodes, roots);
		addNonMySuperClass(dependency, super2subs, nodes, roots);
		
		Set<Node> rootNodes = new HashSet<Node>();
		//for(String root : getRealRoots(dependency.keySet(), nodes)){
		for(String root : roots){
			rootNodes.add(nodes.get(root));
		}
		
		// return rootNodes;
		return this.simplifyGraph(rootNodes, 3);
	}
	
	private Set<Node> simplifyGraph(Set<Node> roots, int numRoots){
		List<Node> ret = new ArrayList<Node>();
		for(int i=0; i<numRoots; i++){
			ret.add(new Node("DUMMY_ROOT_" + i));
		}
		int counter = 0;
		for(Node root : roots){
			ret.get(counter).addChild(root);
			counter++;
			if(counter==numRoots){
				counter=0;
			}			
		}
		return new HashSet<Node>(ret);
	}


}
