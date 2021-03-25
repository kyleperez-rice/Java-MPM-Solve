import java.io.*;
import java.util.*; // Get lists, etc


// Our main program
// Puts everything together
public class MPMSolve {



	public static void main( String args[] ) throws IOException {


//---------------------------------------------------------------
		// CONSTANTS and OPTIONS



		// Our time
		double t = 0.;



		// Geometry of the system
		int num_nodes = 201;

		double xlowbnd = 0.; // xpoint we start at
		double xhighbnd = 1.; // xpoint we end at

		double dx = (xhighbnd - xlowbnd)/(double)(num_nodes-1); // cell size



		// Particle properties
		int num_mps = 2; // Number of mps we have in a cell

		boolean move_particles = true;



		// Time properties
		double tmax = 0.3; // Max time to solve until
		int tsteps = 3000; // Number of steps to take

		double dt = tmax/(double)tsteps; // Size of a timestep




//-------------------------------------------------------------------------------------------------------




		// Data writing properties
		String node_csv_name = "test.csv";
		String mp_csv_name = "mp_test.csv";

		// Make a big debug file?
		boolean debug_file = true;


		// Our data files
		FileWriter nodeData = new FileWriter(node_csv_name);
		FileWriter mpData = new FileWriter(mp_csv_name);
		FileWriter debugData = new FileWriter("debug.txt");

		if ( debug_file == false ) {

			debugData.append("I don't know how to limit my output without making a file in the first place.\n");
			debugData.append("I have to declare the file in my main's scope or else I get an error since it will\n");
			debugData.append("deallocate the variable if it's in an 'if' statement. ;_;\n");
			debugData.flush();
			debugData.close();		

		}//end if

		


		// Lists for csv labels
		//	See documentation on when to change
		List<String> node_titles = Arrays.asList(
					"time",
					"xpos",
					"stress",
					"strain",
					"dens",
					"xvel"	
		);

		List<String> mp_titles = Arrays.asList(
					"time",
					"xpos",
					"mass",
					"strain",
					"stress",
					"xvel"
		);

		


		// Give our data files some headers!
		DataWrite.MakeHeaders(nodeData, node_titles);
		DataWrite.MakeHeaders(mpData, mp_titles);

		if ( debug_file == true ) {

			Debug.DataDumpHeader(debugData);

		}//end if



//-----------------------------------------------------------------
		// Creating data arrays



		// Our current-time data
		List<Node> nodes = new ArrayList<Node>();
		List<MaterialPoint> mp_points = new ArrayList<MaterialPoint>();



		// Populate our lists with enough entries to make an initial state
		for ( int i = 0; i < num_nodes; ++i ) {


			nodes.add( new Node() );


		}//end for

		for ( int i = 0; i < (nodes.size()-1)*num_mps; ++i ) {


			mp_points.add( new MaterialPoint() );


		}//end for




//-----------------------------------------------------------------
		// Initializing our system		




		// Then make the initial state
		Initializations.InitializeMesh(nodes, xlowbnd, dx);
		Initializations.InitializeMaterialPoints(mp_points, nodes, num_mps, dx, xlowbnd);
		


		// Then get the nearest nodes to our material points
		// and find the initial node masses
		MPMMath.GetNearNodes(mp_points, dx, xlowbnd);
		SimUpdate.ComputeNodeMasses(nodes, mp_points);




//-----------------------------------------------------------------
		// Time evolution




		// Then evolve the system in time
		for ( int tt = 0; tt < tsteps; ++tt ) {


			// First save our data
			DataWrite.Node(nodeData, nodes, t);
			DataWrite.MaterialPoint(mpData, mp_points, t);

			if ( debug_file == true ) {

				Debug.DataDump(debugData, nodes, mp_points, t);

			}//end if


			// Then use these Lagrangian quantities to find the Eulerian ones
			// At the next timestep
			SimUpdate.UpdateParticle.Strain(mp_points, nodes, dt);
			SimUpdate.UpdateVelocity(nodes, mp_points, t, dt, move_particles);
			SimUpdate.UpdateParticle.Stress(mp_points, nodes, dt);



			// Then update the node quantities
			SimUpdate.UpdateNode.Density(nodes, mp_points);
			SimUpdate.UpdateNode.Strain(nodes, mp_points);
			SimUpdate.UpdateNode.Stress(nodes, mp_points);



			// Advance system in time
			t += dt;




		}//end for


		// Final save of the data
		DataWrite.Node(nodeData, nodes, t);
		DataWrite.MaterialPoint(mpData, mp_points, t);




		// Flush and close our files
		nodeData.flush();
		mpData.flush();

		nodeData.close();
		mpData.close();


		if ( debug_file == true)  {

			debugData.flush();
			debugData.close();

		}//end if



		



		// Big data dump
		//if (debug_file == true) {

		//	Debug.DataDump(nodeData, mpData, dt);

		//}//end if
		


	}//end main
	




}//end MPMSolve
