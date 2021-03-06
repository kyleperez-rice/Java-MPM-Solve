import java.io.*; // Gets us CSV output
import java.util.*; // Gets us lists, array list, Collections, etc

/*
	Contains:
		class: debug
			(Functions that reveal information useful for debugging)

			class: NodeOutput
				(Functions that output node variables of a certain type)

				function: l_xvel
					(outputs l_xvel for a given set of nodes)
				function: dens
				function: xvel
				function: stress
				function: strain
				function: ymodulus
				function: mass

			class: MPOutput
				(Functions that output mp variables of a certain type)

				function: xpos
					(material point x position)
				function: pnt_xvel
				function: xvel
				function: stress
				function: strain
				function: ymodulus
				function: mass
				
			function: DataDump
				(Writes out a ton of data about material point/node)
				(positiion, for all points in time, and all the relevant)
				(quantities)
				(WARNING: produces an extremely long file!)
*/

// General debugging functions
public class Debug {
	



	// Subclass that lets you pick a particular class member
	// of the node class to pick to output the data of
	public static class NodeOutput {



		// Output the l_xvel of a list of Nodes
		public static void l_xvel( List<Node> nodes) {


			for (int i = 0; i < nodes.size(); ++i) {


				System.out.println( "i = " + String.valueOf(i) + " Node Lagr Xvel: " + String.valueOf(nodes.get(i).l_xvel) );


			}//end for


		}//end l_xvel


		// Output the density of a List of nodes
		public static void dens( List<Node> nodes) {


			for (int i = 0; i < nodes.size(); ++i) {


				System.out.println( "i = " + String.valueOf(i) + " Node Dens: " + String.valueOf(nodes.get(i).dens) );


			}//end for


		}//end dens


		// Output the xvel of a list of nodes
		public static void xvel( List<Node> nodes) {


			for (int i = 0; i < nodes.size(); ++i) {


				System.out.println( "i = " + String.valueOf(i) + " Node Xvel: " + String.valueOf(nodes.get(i).xvel) );


			}//end for


		}//end xvel


		// Output the stress of a list of nodes
		public static void stress( List<Node> nodes) {


			for (int i = 0; i < nodes.size(); ++i) {


				System.out.println( "i = " + String.valueOf(i) + " Node Stress: " + String.valueOf(nodes.get(i).stress) );


			}//end for


		}//end stress


		// Output the strain of a list of nodes
		public static void strain( List<Node> nodes) {


			for (int i = 0; i < nodes.size(); ++i) {


				System.out.println( "i = " + String.valueOf(i) + " Node Strain: " + String.valueOf(nodes.get(i).strain) );


			}//end for


		}//end strain


		// Output the strain of a list of nodes
		public static void ymodulus( List<Node> nodes) {


			for (int i = 0; i < nodes.size(); ++i) {


				System.out.println( "i = " + String.valueOf(i) + " Node Young's Modulus: " + String.valueOf(nodes.get(i).ymodulus) );


			}//end for


		}//end ymodulus


		// Output the strain of a list of nodes
		public static void mass( List<Node> nodes) {


			for (int i = 0; i < nodes.size(); ++i) {


				System.out.println( "i = " + String.valueOf(i) + " Node Mass: " + String.valueOf(nodes.get(i).mass) );


			}//end for


		}//end mass



	}//end NodeOutput




//------------------------------------------------------------------------------




	// Subclass that lets you pick a particular class member
	// of the MaterialPoint class to pick to output the data of
	//	mps = material points
	public static class MPOutput {



		// Output the xpos of a list of Material Points
		public static void xpos( List<MaterialPoint> mps) {


			for (int i = 0; i < mps.size(); ++i) {


				System.out.println( "i = " + String.valueOf(i) + " Material Point Xpos: " + String.valueOf(mps.get(i).xpos) );


			}//end for


		}//end xpos


		// Output the physical velocity of a list of Material Points
		public static void pnt_xvel( List<MaterialPoint> mps) {


			for (int i = 0; i < mps.size(); ++i) {


				System.out.println( "i = " + String.valueOf(i) + " Material Point Physical Velocity: " + String.valueOf(mps.get(i).pnt_xvel) );


			}//end for


		}//end pnt_xvel


		// Output the velocity of a list of Material Points
		public static void xvel( List<MaterialPoint> mps) {


			for (int i = 0; i < mps.size(); ++i) {


				System.out.println( "i = " + String.valueOf(i) + " Material Point Velocity: " + String.valueOf(mps.get(i).xvel) );


			}//end for


		}//end xvel


		// Output the stress of a list of Material Points
		public static void stress( List<MaterialPoint> mps) {


			for (int i = 0; i < mps.size(); ++i) {


				System.out.println( "i = " + String.valueOf(i) + " Material Point Stress: " + String.valueOf(mps.get(i).stress) );


			}//end for


		}//end stress


		// Output the strain of a list of Material Points
		public static void strain( List<MaterialPoint> mps) {


			for (int i = 0; i < mps.size(); ++i) {


				System.out.println( "i = " + String.valueOf(i) + " Material Point Strain: " + String.valueOf(mps.get(i).strain) );


			}//end for


		}//end strain


		// Output the ymodulus of a list of Material Points
		public static void ymodulus( List<MaterialPoint> mps) {


			for (int i = 0; i < mps.size(); ++i) {


				System.out.println( "i = " + String.valueOf(i) + " Material Point Young's Modulus: " + String.valueOf(mps.get(i).ymodulus) );


			}//end for


		}//end ymodulus


		// Output the mass of a list of Material Points
		//	Note: the mass of material points don't change over time!
		//	Should only have to run once!
		public static void mass( List<MaterialPoint> mps) {


			for (int i = 0; i < mps.size(); ++i) {


				System.out.println( "i = " + String.valueOf(i) + " Material Point Mass: " + String.valueOf(mps.get(i).mass) );


			}//end for


		}//end mass



	}//end NodeOutput




//-------------------------------------------------------------------------------------




	// Outputs a very verbose, formatted data text file that tells us everything about
	// the nodes at a given time and the material points in their reach
	public static void DataDump(
		List<List<Node>> nodeData,
		List<List<MaterialPoint>> mpData
	) throws IOException {


		FileWriter myFile = new FileWriter("datadump.txt");


		// Create a header
		myFile.append( "This file contains pratically all necessary info to debug your problem.\n" );
		myFile.append( "For each time, it gives a list of all of the nodes, and associated values\n" );
		myFile.append( "of each node, alongside the material points contained within a node's shape\n" );
		myFile.append( "function, and all of the material points' values.\n" );
		myFile.append( "It will also denote if any material points go untouched by a node.\n" );



		// Each element of nodeData describes the system at one element in time
		// So, for each element in time, create a new entry in our file
		for (int t = 0; t < nodeData.size(); ++t) {


			myFile.append( "Time: " + String.valueOf( nodeData.get(t).get(0).time ) + '\n' );


			// Then create a vector of boolean values that tells us whether or not a
			// material point is found in a given node
			List<Boolean> mp_used = new ArrayList<Boolean>( Arrays.asList( new Boolean[mpData.get(t).size()] ) );
			Collections.fill(mp_used, false); // Fill all the entries to be false

			// For a given element of time, print the data relevant to a given node
			for (int i = 0; i < nodeData.get(t).size(); ++i) {


				myFile.append('\t' + "Node " + String.valueOf(i) + '\n');
				// Then, we store the indices of the material points in the given node here:
				List<Integer> mp_indices = new ArrayList<Integer>();


				// Go through all of the material point data
				for (int j = 0; j < mpData.get(t).size(); ++j) {


					// If a material point lays in a given node's 'span', add it
					// to the list and note that it's been found
					//
					// A node's span (for linear shape functions) is just the node's x position
					// plus or minus the node's length.
					if (
						( mpData.get(t).get(j).xpos > (nodeData.get(t).get(i).xpos - nodeData.get(t).get(i).length) )
						&& 
						( mpData.get(t).get(j).xpos < (nodeData.get(t).get(i).xpos + nodeData.get(t).get(i).length) )
					) {

						mp_indices.add(j);
						mp_used.get(j) = true;

					}//end if


				}//end for


				// Print all of the material points contained in our node
				myFile.append( "\t\t" + "Material Points Contained in Node " + String.valueOf(i) + ":\n" );
				for (int j = 0; j < mp_indices.size(); ++j) {


					myFile.append( "\t\t\t" + "MP " + String.valueOf(mp_indices.get(j)) + '\n');


				}//end for


				// Write the x position of the node and its associated mps
				myFile.append( "\t\t" + "Node xpos: " + String.valueOf(nodeData.get(t).get(i).xpos + '\n') );
				for (int j = 0; j < mp_indices.size(); ++j) {


					myFile.append( "\t\t\t" + "MP " + String.valueOf(mp_indices.get(j)) + " xpos: " + String.valueOf(mpData.get(t).get(mp_indices.get(j)).xpos) + '\n');


				}//end for


				// Write the density and mass of the node and its associated mps
				myFile.append( "\t\t" + "Node Density: " + String.valueOf(nodeData.get(t).get(i).dens + '\n') );
				myFile.append( "\t\t" + "Node Mass: " + String.valueOf(nodeData.get(t).get(i).mass + '\n') );
				for (int j = 0; j < mp_indices.size(); ++j) {


					myFile.append( "\t\t\t" + "MP " + String.valueOf(mp_indices.get(j)) + " mass: " + String.valueOf(mpData.get(t).get(mp_indices.get(j)).mass) + '\n');


				}//end for


				// Write the xvelocities of the node and its associated mps
				myFile.append( "\t\t" + "Node xvel: " + String.valueOf(nodeData.get(t).get(i).xvel + '\n') );
				myFile.append( "\t\t" + "Node lagr xvel: " + String.valueOf(nodeData.get(t).get(i).l_xvel + '\n') );
				for (int j = 0; j < mp_indices.size(); ++j) {


					myFile.append( "\t\t\t" + "MP " + String.valueOf(mp_indices.get(j)) + " xvel: " + String.valueOf(mpData.get(t).get(mp_indices.get(j)).xvel) + '\n');
					myFile.append( "\t\t\t" + "MP " + String.valueOf(mp_indices.get(j)) + " pnt_xvel: " + String.valueOf(mpData.get(t).get(mp_indices.get(j)).pnt_xvel) + '\n');


				}//end for


				// Write the Stress/Strain/ymod of the node and its associated mps
				myFile.append( "\t\t" + "Node Stress: " + String.valueOf(nodeData.get(t).get(i).stress + '\n') );
				myFile.append( "\t\t" + "Node Strain: " + String.valueOf(nodeData.get(t).get(i).strain + '\n') );
				myFile.append( "\t\t" + "Node Young's Modulus: " + String.valueOf(nodeData.get(t).get(i).ymodulus + '\n') );
				for (int j = 0; j < mp_indices.size(); ++j) {


					myFile.append( "\t\t\t" + "MP " + String.valueOf(mp_indices.get(j)) + " Stress: " + String.valueOf(mpData.get(t).get(mp_indices.get(j)).stress) + '\n');
					myFile.append( "\t\t\t" + "MP " + String.valueOf(mp_indices.get(j)) + " Strain: " + String.valueOf(mpData.get(t).get(mp_indices.get(j)).strain) + '\n');
					myFile.append( "\t\t\t" + "MP " + String.valueOf(mp_indices.get(j)) + " Young's Modulus: " + String.valueOf(mpData.get(t).get(mp_indices.get(j)).ymodulus) + '\n');
					

				}//end for
				


			}//end for


			// Print the unused material points
			myFile.append( '\t' + "Unused Material Points:" + '\n' );
			for (int j = 0; j < mp_used.size(); ++j) {


				if ( mp_used.get(j) == false ) {

					myFile.append( "\t\t" + "MP " + String.valueOf(j) + '\n' );
					myFile.append( "\t\t\t" + "xpos: " + String.valueOf(mpData.get(t).get(j).xpos) + '\n' );
					myFile.append( "\t\t\t" + "mass: " + String.valueOf(mpData.get(t).get(j).mass) + '\n' );
					myFile.append( "\t\t\t" + "xvel: " + String.valueOf(mpData.get(t).get(j).xvel) + '\n' );
					myFile.append( "\t\t\t" + "pnt_xvel: " + String.valueOf(mpData.get(t).get(j).pnt_xvel) + '\n' );
					myFile.append( "\t\t\t" + "stress: " + String.valueOf(mpData.get(t).get(j).stress) + '\n' );
					myFile.append( "\t\t\t" + "strain: " + String.valueOf(mpData.get(t).get(j).strain) + '\n' );
					myFile.append( "\t\t\t" + "young's modulus: " + String.valueOf(mpData.get(t).get(j).ymodulus) + '\n' );



				}//end if


			}//end for


		}//end for



		// Flush and close the file
		myFile.flush();
		myFile.close();


	}//end DataDump



}//end Debug
