import java.util.*; // Get lists, etc


/*
	Contains:
		class MPMMath
			(Functions/Classes that perform vital mathematical operations to update)
			(the system)

			function: GetNearNodes
				(Gets the 2 nearest nodes to a material point)

			class: RiemannSum
				(Does a Riemann Approximation of an integral)

				function: Stress
					(Does Riemann Sum over stress)

			function: ParticleStrainRate
				(Calculates a particle's strain rate)

			class: LagrDifferSum
				(Computes the term that we add to a quantity to advance it in time)

				function: XVel
					(Computes above term for the particle x velocity)

			function: LagrVelSum
				(Computes the physical x velocity of a material point)

			class: MPWeightSum
				(Computes density/xvelocity, etc of a node given that of a materia point)

				function: Density
				function: XVelocity
				function: Strain
					(epsilon = du/dx)
				function: Stress
					(sigma = Y*Strain)
*/

// A math class that contains methods useful to solving via MPM
public class MPMMath {


	// A fast way to get the two nodes nearest to a material point
	// given an equally spaced mesh
	public static List<Node> GetNearNodes(
		List<Node> nodes,
		MaterialPoint mp
	) {


		// Make our list to return
		List<Node> nearnodes = new ArrayList<Node>();


		// Default cause of the nearest node: the last one
		int min_index = nodes.size() - 1;


		// Go through all of the elements
		// Look for the element when the distance between the node and our
		// material point stops getting smaller, and begins to increase.
		// At this transition, we've reached the closest node, so record
		// the index of the node and break
		for ( int i = 0; i < nodes.size()-1; ++i ) {


			if ( Math.abs( nodes.get(i).xpos - mp.xpos ) <= Math.abs( nodes.get(i).xpos - mp.xpos ) ) {

				min_index = i;
				break;

			}//end if


		}//end for


		// Save the index of the closest node
		nearnodes.add( nodes.get(min_index) );


		// Then, check the difference in xposition between the node and particle
		// For an equally spaced mesh, this tells us about which node is the next closest
		// The sign of the difference tells us if it is on the left or right
		//	Negative --> next nearest point is on right
		//	Positive --> next nearest point is on the left
		//	0		 --> just include both left and right nodes
		if ( nearnodes.get(0).xpos - mp.xpos < 0. ) {

			nearnodes.add( nodes.get( min_index +  1 ) );

		}
		else if ( nearnodes.get(0).xpos - mp.xpos > 0. ) {

			nearnodes.add( nodes.get( min_index -  1 ) );

		}
		else {

			nearnodes.add( nodes.get( min_index +  1 ) );
			nearnodes.add( nodes.get( min_index -  1 ) );

		}//end if


		return nearnodes;


	}//end GetNearNodes



//------------------------------------------------------------------------------------




	// Members of the RiemannSum class provide an approximation
	// of an integral
	/// We want to model the following sum:
	// Sum_{p: all material points} vol_p * stress_i(x_p) * d_shapef_l(x_p)
	// which approximates
	// Integral_V stress_i(x) d_shapef(x) dV
	public static class RiemannSum {


		public static double Stress(
			Node nd,
			List<MaterialPoint> mps
		) {


			double sum = 0.;

			for ( int i = 0; i < mps.size(); ++i ) {


				sum += mps.get(i).length * mps.get(i).stress * nd.d_shapef( mps.get(i).xpos );


			}//end for


			return sum;


		}//end Stress

		// Can add more members is necessary; for now, just have the single one!


	}//end RiemannSum



//-----------------------------------------------------------------------------------



	// Calculates the strain rate on a particle
	// Uses the fact that we have
	// epsilon = du/dx
	// --> depislon/dt = dv/dx
	// Make into a sub p each side (for a particle)
	// Then write v_p = sum_nodes( v_l shapef_l(mp.xpos) )
	// Expand out, and done
	public static double ParticleStrainRate (
		MaterialPoint mp,
		List<Node> nodes
	) {


		double sum = 0.;

		// Get our nearest nodes to the material point
		List<Node> nearnodes = MPMMath.GetNearNodes(nodes, mp);


		for ( int i = 0; i < nearnodes.size(); ++i ) {


			sum += nearnodes.get(i).xvel * nearnodes.get(i).d_shapef( mp.xpos );


		}//end for

		// Maybe you didn't use a linear shape function, etc
		// can use this more general loop
		/*
		for ( int i = 0; i < nodes.size(); ++i ) {


			sum += nodes.get(i).xvel * nodes.get(i).d_shapef( mp.xpos );


		}//end for
		*/


		return sum;


	}//end ParticleStrainRate



//-------------------------------------------------------------------------------------



	// Computes the amount we add to a quantity q_kp(t) so that we can get
	// q_kp(t + dt) for a given material point
	// It computes:
	//	Sum_{l: all nodes} ( q_kl^L - q_kl^n )*shapef_l(x_p)
	//
	// ^L implies the lagrangian quantity for a node, and ^n the std one at current time
	//
	// Different types, depending on quantities that evolve via an Euler-type equation
	public static class LagrDifferSum {



		public static double XVel(
			MaterialPoint mp,
			List<Node> nodes
		) {


			double sum = 0.;

			// Get those nearest nodes to our material points
			List<Node> nearnodes = MPMMath.GetNearNodes(nodes, mp);


			for ( int i = 0; i < nearnodes.size(); ++i ) {


				sum += ( nearnodes.get(i).l_xvel - nearnodes.get(i).xvel ) * nearnodes.get(i).shapef( mp.xpos );


			}//end for


			// Maybe you didn't use linear shape function, if so, use this slower
			// but more complete loop:
			/*
			for ( int i = 0; i < nodes.size(); ++i ) {


				sum += ( nodes.get(i).l_xvel - nodes.get(i).xvel ) * nodes.get(i).shapef( mp.xpos );


			}//end for
			*/


			return sum;


		}//end XVel


	}//end LagrDifferSum



//----------------------------------------------------------------------------------------



	// This function takes all of the lagrangian velocities of the nodes and then
	// sums them all up, weighted by their respective shape functions evaluated at
	// the material point's x position
	// Does:
	//	Sum_{p: all nodes} l_xvel_p * shapef_p( mp xpos )
	//
	// We use this to modify particle class member pnt_xvel
	public static double LagrVelSum(
		MaterialPoint mp,
		List<Node> nodes
	) {


		double sum = 0.;

		// Get those near nodes
		List<Node> nearnodes = MPMMath.GetNearNodes(nodes, mp);

		for ( int i = 0; i < nearnodes.size(); ++i ) {


			sum += nearnodes.get(i).l_xvel * nearnodes.get(i).shapef( mp.xpos );


		}//end for


		// Maybe you didn't use the nearnodes portion. Do this:
		/*
		for ( int i = 0; i < nodes.size(); ++i ) {


			sum += nodes.get(i).l_xvel * nodes.get(i).shapef( mp.xpos );


		}//end for
		*/


		return sum;


	}//end LagrVelSum



//----------------------------------------------------------------------------------


	// Uses the mass/equivalent of a node and all the material points to compute
	// some quantity q_k at a node l
	//
	// Does: q_kl = Sum_{p: all material points} weight_kp * q_kp * shapef_l(x_p) / weight_kl
	//
	// Previous operations should have all of these masses precomputed!
	public static class MPWeightSum {


		// Calculates the density of a node
		// only needs material point mass (which is immutable)
		// and node length (also immutible)
		public static double Density(
			Node nd,
			List<MaterialPoint> mps
		) {


			double dens = 0.;

			for ( int i = 0; i < mps.size(); ++i ) {


				dens += mps.get(i).mass * nd.shapef( mps.get(i).xpos );


			}//end for

			dens = dens/nd.length;

			return dens;


		}//end Density



		// Get the node velocity
		// Note that the node itself does not move; but it has a velocity assigned to it
		// IE: it's Eulerian!
		public static double XVelocity(
			Node nd,
			List<MaterialPoint> mps
		) {


			double xvel = 0.;

			for ( int i = 0; i < mps.size(); ++i ) {


				xvel += mps.get(i).mass * mps.get(i).xvel * nd.shapef( mps.get(i).xpos );


			}//end for

			xvel = xvel/nd.mass;

			return xvel;


		}//end XVelocity




		// Get the node strain
		// Note that this isn't actually something we need to do, but it's still
		// a nice step
		public static double Strain(
			Node nd,
			List<MaterialPoint> mps
		) {


			double strain = 0.;

			for ( int i = 0; i < mps.size(); ++i ) {


				strain += mps.get(i).mass * mps.get(i).strain * nd.shapef( mps.get(i).xpos );


			}//end for

			strain = strain/nd.mass;

			return strain;


		}//end Strain




		// Gets you the stress on a node given the strain
		// Depends on the stress-strain relation!
		// IE: equation of state / constituitive relation
		// So this is kind of hardcoded in there!
		public static double Stress(
			Node nd
		) {


			return nd.strain * nd.ymodulus;


		}//end Stress
		


	}//end MPWeightSum



}//end MPMMATH