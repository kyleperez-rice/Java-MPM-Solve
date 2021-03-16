import java.util.*; // Get lists, etc

// Class that contains functions that allow us to advance
// node/material point quantities in time
public class SimUpdate {
	

	// Compute the mass of a node, given the material point masses/posistions
	public static void ComputeNodeMasses(
		List<Node> nodes,
		List<MaterialPoint> mp_points
	) {


		// First set masses to 0
		for ( int i = 0; i < nodes.size(); ++i ) {


			nodes.get(i).mass = 0.;


		}//end for


		// Then sum with respect to material point shape function
		// Do sum over the material points, but use the mp's
		// nearest nodes to cover all of the nodes
		//
		// This works only for linear shape functions!
		for ( int i = 0; i < mp_points.size(); ++i ) {


			nodes.get( mp_points.get(i).left_nn ).mass += mp_points.get(i).mass * nodes.get( mp_points.get(i).left_nn ).shapef( mp_points.get(i).xpos );
			nodes.get( mp_points.get(i).right_nn ).mass += mp_points.get(i).mass * nodes.get( mp_points.get(i).right_nn ).shapef( mp_points.get(i).xpos );


		}//end for


	}//end ComputeNodeMasses




	// Move the particles
	public static void MoveParticles(
		List<MaterialPoint> mps,
		double dt
	) {


		// xposition = xposition + vel*dt
		for ( int i = 0; i < mps.size(); ++i ) {


			mps.get(i).xpos += mps.get(i).pnt_xvel * dt;


		}//end for


	}//end MoveParticles



//------------------------------------------------------------------------------------




	// Class that contains functions to update the lagr quantities of a node that
	// evolve via an Euler-type equation
	public static class UpdateNodeLagr {


		// Updates lagr xvelocity of a node
		public static void XVel(
			List<Node> nodes,
			List<MaterialPoint> mp_points,
			double t,
			double dt
		) {


			// Form: l_xvel = xvel + dv/dt * dt for each node l
			//
			// d xvel_l / dt = 
			//	acceleration 
			//	-1/m * Sum_{p: mps}( vol_p * stress_p * d_shapef_l(x_p) )
			//	+ 1/m * integral( Stress * shapef, boundary(x) )
			//
			// Then, enforce our boundary conditions again!

			

			// First make l_xvel = 0
			for ( int i = 0; i < nodes.size(); ++i ) {


				nodes.get(i).mass = 0.;


			}//end for


			// Then do the sum over the material points
			// Only need to sum over the nearest nodes!
			for ( int i = 0; i < mp_points.size(); ++i ) {


				nodes.get( mp_points.get(i).left_nn ).l_xvel -= mp_points.get(i).length * mp_points.get(i).stress * nodes.get( mp_points.get(i).left_nn ).d_shapef( mp_points.get(i).xpos );
				nodes.get( mp_points.get(i).right_nn ).l_xvel -= mp_points.get(i).length * mp_points.get(i).stress * nodes.get( mp_points.get(i).right_nn ).d_shapef( mp_points.get(i).xpos );


			}//end for


			// Then make the quantity have the correct units and
			// calculate the part that doesn't involve the sum
			for ( int i = 0; i < nodes.size(); ++i ) {


				nodes.get(i).l_xvel /= nodes.get(i).mass;
				nodes.get(i).l_xvel += nodes.get(i).xvel + Accelerations.external_x(nodes.get(i), t)*dt;


			}//end for


			

			
			// Again, fix the boundaries!
			nodes.get(0).l_xvel = BoundaryConditions.Velocity.Left( t );
			nodes.get(nodes.size()-1).l_xvel = BoundaryConditions.Velocity.Right( t );



		}//end XVel


	}//end UpdateNodeLagr




//---------------------------------------------------------------------------------



	// Class that lets us update certain particle quantities
	//
	// For velocity:
	//	v_p(t + dt) = v_p(t) + Sum_{l: all nodes} (v_l^L - v_l)*shapef_l(x_p)
	//	For every material point p
	//
	// For strain:
	//	strain_p(t+dt) = strain_p(t) + Sum_{l: all nodes}(v_l * d_shapef_l(x_p))
	//
	// NOTE:
	//	This is written in the order in which you should call this in your program!
	public static class UpdateParticle {


		// Update the particle strain
		public static void Strain (
			List<MaterialPoint> mps,
			List<Node> nodes,
			double dt
		) {


			// Strain rate is just sum_{nodes l} v_l d_shapef_l (xpos)
			// For linear shape functions, becomes two terms
			for ( int i = 0; i < mps.size(); ++i ) {


				mps.get(i).strain += nodes.get( mps.get(i).left_nn ).xvel * nodes.get( mps.get(i).left_nn ).d_shapef( mps.get(i).xpos )*dt;
				mps.get(i).strain += nodes.get( mps.get(i).right_nn ).xvel * nodes.get( mps.get(i).right_nn ).d_shapef( mps.get(i).xpos )*dt;


			}//end for


		}//end Strain


	}//end UpdateParticle




	// Update the particle xvelocity
	public static void XVelocity (
		List<MaterialPoint> mps,
		List<Node> nodes,
		double dt
	) {


		// Interpolate it to the particle from the nodes
		for ( int i = 0; i < mps.size(); ++i ) {


			mps.get(i).xvel += (nodes.get( mps.get(i).left_nn ).l_xvel - nodes.get( mps.get(i).left_nn ).xvel  ) * nodes.get( mps.get(i).left_nn ).shapef( mps.get(i).xpos );
			mps.get(i).xvel += (nodes.get( mps.get(i).right_nn ).l_xvel - nodes.get( mps.get(i).right_nn ).xvel  ) * nodes.get( mps.get(i).right_nn ).shapef( mps.get(i).xpos );


		}//end for


	}//end XVelocity



	// Update the particle stress
	public static void Stress (
		List<MaterialPoint> mps,
		List<Node> nodes,
		double dt
	) {


		for ( int i = 0; i < mps.size(); ++i ) {


			// Not very general; relies explicitly on stress-strain relation!
			mps.get(i).stress = mps.get(i).strain * mps.get(i).ymodulus;


		}//end for


	}//end Stress



	// Update the particle's physical velocity via interpolation with the nodes
	public static void InterpolatedVelocity (
		List<MaterialPoint> mps,
		List<Node> nodes,
		double dt
	) {


		// Only do sum over nearest nodes
		// Set pnt_xvel to one number and add to it
		for ( int i = 0; i < mps.size(); ++i ) {


			mps.get(i).pnt_xvel = nodes.get( mps.get(i).left_nn ).l_xvel * nodes.get( mps.get(i).left_nn ).shapef( mps.get(i).xpos );
			mps.get(i).pnt_xvel += nodes.get( mps.get(i).right_nn ).l_xvel * nodes.get( mps.get(i).right_nn ).shapef( mps.get(i).xpos );


		}//end for


	}//end InterpolatedVelocity



//-----------------------------------------------------------------------------------



	// Uses material point quantities to calculate node quantities
	// Does a sum of the form:
	// q_kl = 1/m_kl * Sum_{p: all mps} m_kp * q_kp * shapef_l(x_p)
	// for every l in the mesh, given a particular type
	public static class UpdateNode {


		public static void Density(
			List<Node> nodes,
			List<MaterialPoint> mps
		) {

			
			// Node density = nodemass/length
			for ( int i = 0; i < nodes.size(); ++i ) {


				nodes.get(i).dens = nodes.get(i).mass / nodes.get(i).length;


			}//end for


			// Boundary nodes are of half-length
			// so need to multiply by 2!
			nodes.get(0).dens = 2. * nodes.get(0).dens;
			nodes.get( nodes.size()-1 ).dens = 2. * nodes.get( nodes.size()-1 ).dens;


		}//end Density




		public static void XVelocity(
			List<Node> nodes,
			List<MaterialPoint> mps
		) {


			// Set node velocity to be 0
			for ( int i = 0; i < nodes.size(); ++i ) {


				nodes.get(i).xvel = 0.;


			}//end for


			// Then over use the material points' nearest nodes
			// to construct the node velocity
			for ( int i = 0; i < mps.size(); ++i ) {


				nodes.get( mps.get(i).left_nn ).xvel += mps.get(i).mass * mps.get(i).xvel * nodes.get( mps.get(i).left_nn ).shapef( mps.get(i).xpos );
				nodes.get( mps.get(i).right_nn ).xvel += mps.get(i).mass * mps.get(i).xvel * nodes.get( mps.get(i).right_nn ).shapef( mps.get(i).xpos );


			}//end for


			// Then divide by the mass for every node
			for ( int i = 0; i < nodes.size(); ++i ) {


				nodes.get(i).xvel /= nodes.get(i).mass;


			}//end for


		}//end Xvelocity



		public static void Strain(
			List<Node> nodes,
			List<MaterialPoint> mps
		) {


			// Set node strain to be 0
			for ( int i = 0; i < nodes.size(); ++i ) {


				nodes.get(i).strain = 0.;


			}//end for


			// Then over use the material points' nearest nodes
			// to construct the node velocity
			for ( int i = 0; i < mps.size(); ++i ) {


				nodes.get( mps.get(i).left_nn ).xvel += mps.get(i).mass * mps.get(i).strain * nodes.get( mps.get(i).left_nn ).shapef( mps.get(i).xpos );
				nodes.get( mps.get(i).right_nn ).xvel += mps.get(i).mass * mps.get(i).strain * nodes.get( mps.get(i).right_nn ).shapef( mps.get(i).xpos );


			}//end for


			// Then divide by the mass for every node
			for ( int i = 0; i < nodes.size(); ++i ) {


				nodes.get(i).strain /= nodes.get(i).mass;


			}//end for


		}//end Strain




		// Don't actually need to call this one
		public static void Stress(
			List<Node> nodes,
			List<MaterialPoint> mp_points
		) {


			for ( int i = 0; i < nodes.size(); ++i ) {


				// In case you have different stress-strain relation,
				//nodes.get(i).stress = MPMMath.MPWeightSum.Stress(nodes.get(i), mp_points);
				nodes.get(i).stress = nodes.get(i).strain * nodes.get(i).ymodulus;


			}//end for


		}//end Stress




	}//end UpdateNode



}//end SimUpdate
