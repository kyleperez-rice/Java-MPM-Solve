import java.util.*; // Get lists, etc


// Our main program
// Puts everything together
public class MPMSolve {



	public static void main( String args[] ) {


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
		Initializations.InitializeMaterialPoints(mp_points, nodes, num_mps, dx);


		// Then get the nearest nodes to our material points
		// and find the initial node masses
		MPMMath.GetNearNodes(mp_points, dx, xlowbnd);
		SimUpdate.ComputeNodeMasses(nodes, mp_points);





		// Then evolve the system in time
		for ( int tt = 0; tt < tsteps; ++tt ) {


			// First find the lagr xvel for each node
			SimUpdate.UpdateNodeLagr.XVel(nodes, mp_points, t, dt);


			// Then use these Lagrangian quantities to find the Eulerian ones
			// At the next timestep
			SimUpdate.UpdateParticle.Strain(mp_points, nodes, dt);
			SimUpdate.UpdateParticle.XVelocity(mp_points, nodes, dt);
			SimUpdate.UpdateParticle.Stress(mp_points, nodes, dt);


			// Then update the physical velocity of the particle
			SimUpdate.UpdateParticle.InterpolatedVelocity(mp_points, nodes);


			// Then move the particles
			SimUpdate.MoveParticles(mp_points, dt);


			// Next recompute the node masses
			SimUpdate.ComputeNodeMasses(nodes, mp_points);


			// Then update the node quantities
			SimUpdate.UpdateNode.Density(nodes, mp_points);
			SimUpdate.UpdateNode.XVelocity(nodes, mp_points);
			SimUpdate.UpdateNode.Strain(nodes, mp_points);
			SimUpdate.UpdateNode.Stress(nodes, mp_points);



		}//end for



	}//end main
	




}//end MPMSolve
