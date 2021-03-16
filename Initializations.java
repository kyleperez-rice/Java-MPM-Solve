import java.util.List; // Get lists

// Class that allows us to initialize the nodes/ material points of
// our system
public class Initializations {
	

	// Initialize our mesh, given number of node points
	// geometry, and step size
	public static void InitializeMesh(
		List<Node> nodes,
		double xlowbnd,
		double dx
	) {


		// Starting with an empty list,
		for (int i = 0; i < nodes.size(); ++i) {


			// Initialize length (immutable mostly)
			nodes.get(i).length = dx;

			// Set the position based on our initial xlowbnd
			nodes.get(i).xpos = xlowbnd + (double)i*dx;

			// Set density, etc from the initial state
			nodes.get(i).dens = InitialState.Density( nodes.get(i).xpos );
			nodes.get(i).strain = InitialState.Strain( nodes.get(i).xpos );
			nodes.get(i).ymodulus = InitialState.YModulus( nodes.get(i).xpos );

			nodes.get(i).stress = nodes.get(i).ymodulus * nodes.get(i).strain;
			nodes.get(i).xvel = InitialState.XVelocity( nodes.get(i).xpos );


		}//end for


	}//end InitializeMesh



	// Initialize our material points, using our nodes,
	// how many material points per cell we have, and our step size
	public static void InitializeMaterialPoints(
		List<MaterialPoint> mp_points,
		List<Node> nodes,
		int num_mps,
		double dx
	) {


		// Generate exactly how many material points we need
		// Note: this is the proper size. We don't have any mps
		// before the first node, or after the second one
		// this means we have nodes.size() - 1 # of cells
		for (int i = 0; i < mp_points.size(); ++i) {


			// Set material point length
			mp_points.get(i).length = dx/(double)num_mps;

			// Set the material point position
			// Check the limiting cases:
			// i = 0 --> xpos = length/2   GOOD
			// i = (nodes.size - 1)*(num_mps-1) --> xpos = (nodes.size-1)*dx - 0.5*length   GOOD
			//
			// HOW THIS SETUP LOOKS:
			//
			//		dx: node size
			// ________________________________________
			// 0									   1
			// |----O----:----O----:----O----:----O----|----O----:----O...
			//      0		  1			2		  3			4		  5
			//		___________
			//			dx/num_mps: Material Point Length
			//
			// Key:
			// |	--> Location of node and end of a material point
			// O	--> Material Point xpos
			// :	--> Shared material point boundary, where both terminate in length
			// -	--> Area in a material point
			mp_points.get(i).xpos = ( (double)i + 0.5 ) * mp_points.get(i).length;


			// Start initializing material point quantities
			mp_points.get(i).mass = 0.;
			mp_points.get(i).xvel = 0.;
			mp_points.get(i).stress = 0.;
			mp_points.get(i).strain = 0.;
			//mp_points.get(i).ymodulus = 0.; // In case you don't have constant Y

			// For constant Ymodulus, this works, but if you have a nonzero distribution, don't do this!
			mp_points.get(i).ymodulus = InitialState.YModulus( mp_points.get(i).xpos ); 


			// Initialize density, etc by summing over the nodes' values
			// weighted by their shape functions evaluated at the
			// material point position
			//
			// IE:
			// q_p = Sum_(n: all nodes) q_n * shapef_n(x_p)
			for (int j = 0; j < nodes.size(); ++j) {


				mp_points.get(i).mass += nodes.get(j).dens * nodes.get(j).shapef( mp_points.get(i).xpos ); // This quantity is a density, not a mass
				mp_points.get(i).xvel += nodes.get(j).xvel * nodes.get(j).shapef( mp_points.get(i).xpos );
				mp_points.get(i).strain += nodes.get(j).strain * nodes.get(j).shapef( mp_points.get(i).xpos );

				// Use below if you don't necessarily have stress = Y*strain for constant Y
				// mp_points.get(i).stress += nodes.get(j).stress * nodes.get(j).shapef( mp_points.get(i).xpos );
				// mp_points.get(i).ymodulus += nodes.get(j).ymodulus * nodes.get(j).shapef( mp_points.get(i).xpos );


			}//end for


			// Set the mass and strain
			// README: Strain set by sigma = y * epsilon
			//	If you have another relation, do something else!
			mp_points.get(i).mass = mp_points.get(i).mass * mp_points.get(i).length;
			mp_points.get(i).stress = mp_points.get(i).strain * mp_points.get(i).ymodulus;


		}//end for


	}//end InitializeMaterialPoints


}//end Initializations
