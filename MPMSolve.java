import java.io.IOException;
import java.util.*; // Get lists, etc


// Our main program
// Puts everything together
public class MPMSolve {



	public static void main( String args[] ) throws IOException {


		// Our time
		double t = 0.;



		// Geometry of the system
		int num_nodes = 51;

		double xlowbnd = 0.; // xpoint we start at
		double xhighbnd = 1.; // xpoint we end at

		double dx = (xhighbnd - xlowbnd)/(double)(num_nodes-1); // cell size



		// Particle properties
		int num_mps = 2; // Number of mps we have in a cell



		// Time properties
		double tmax = 0.1; // Max time to solve until
		int tsteps = 1000; // Number of steps to take

		double dt = tmax/(double)tsteps; // Size of a timestep


		// Our current-time data
		List<Node> nodes = new ArrayList<Node>();
		List<MaterialPoint> mp_points = new ArrayList<MaterialPoint>();


		// Our data for all times
		List<List<Node>> nodeData = new ArrayList<List<Node>>();
		List<List<MaterialPoint>> mpData = new ArrayList<List<MaterialPoint>>();



		// Populate our lists with enough entries to make an initial state
		for ( int i = 0; i < num_nodes; ++i ) {


			nodes.add( new Node() );


		}//end for

		for ( int i = 0; i < (nodes.size()-1)*num_mps; ++i ) {


			mp_points.add( new MaterialPoint() );


		}//end for
		




		// Then make the initial state
		Initializations.InitializeMesh(nodes, xlowbnd, dx);
		Initializations.InitializeMaterialPoints(mp_points, nodes, num_mps, dx, xlowbnd);


		// Then get the nearest nodes to our material points
		// and find the initial node masses
		MPMMath.GetNearNodes(mp_points, dx, xlowbnd);
		SimUpdate.ComputeNodeMasses(nodes, mp_points);





		// Then evolve the system in time
		for ( int tt = 0; tt < tsteps; ++tt ) {


			// Save data here!
			nodeData.add( nodes );
			mpData.add( mp_points );



			// First find the lagr xvel for each node
			SimUpdate.UpdateVelocity(nodes, mp_points, t, dt);


			// Then use these Lagrangian quantities to find the Eulerian ones
			// At the next timestep
			SimUpdate.UpdateParticle.Strain(mp_points, nodes, dt);
			SimUpdate.UpdateParticle.Stress(mp_points, nodes, dt);


			// Then update the node quantities
			SimUpdate.UpdateNode.Density(nodes, mp_points);
			SimUpdate.UpdateNode.Strain(nodes, mp_points);
			SimUpdate.UpdateNode.Stress(nodes, mp_points);



			// Advance system in time
			t += dt;



		}//end for



		// Lists for csv labels
		List<String> mp_titles = Arrays.asList(
					"xpos",
					"mass",
					"strain",
					"stress",
					"xvel"
		);

		List<String> node_titles = Arrays.asList(
					"xpos",
					"stress",
					"strain",
					"dens",
					"xvel"	
		);



		// Write files
		DataWrite.Node("test.csv", node_titles, nodeData, dt);
		DataWrite.MaterialPoint("mp_test.csv", mp_titles, mpData, dt);


		// Big data dump
		Debug.DataDump(nodeData, mpData, dt);


	}//end main
	




}//end MPMSolve
