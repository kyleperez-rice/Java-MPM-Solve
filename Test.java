import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;


public class Test {


	// Supposedly Java isn't pass by reference, but this is what I'd
	// call passing by reference in C++!
	public static void dothing( Node n ) {

	
		n.xpos += 1.;


	}


	public static void main( String args[] ) {


		List<Node> nodes = new ArrayList<Node>();

		nodes.add(new Node());
		nodes.add(new Node());

		nodes.get(0).time = 0.;
		nodes.get(1).time = 0.;

		Incrementation.IncrementNode.time(nodes, 1.);

		System.out.println("Node 0 time: " + nodes.get(0).time);
		System.out.println("Node 1 time: "+ nodes.get(0).time);


	}



}
