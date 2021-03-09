import java.util.*; // Get lists, etc

// Class that contains functions that allow us to advance
// node/material point quantities in time
public class SimUpdate {
	

	// Compute the mass of a node, given the material point masses/posistions
	public static void ComputeNodeMasses(
		List<Node> nodes,
		List<MaterialPoint> mp_points
	) {


		// Go through each node to compute the mass of it
		for ( int i = 0; i < nodes.size(); ++i ) {


			// Initialize our mass so we can recompute it
			nodes.get(i).mass = 0.;


			// Do the following sum:
			// m_l = Sum_{p: all mps} m_p * shapef_l(x_p)
			for ( int j = 0; j < mp_points.size(); ++j ) {


				nodes.get(i).mass += mp_points.get(i).mass * nodes.get(i).shapef( mp_points.get(j).xpos );


			}//end for


		}//end for


	}//end ComputeNodeMasses



//------------------------------------------------------------------------------------




	// Class that contains functions to update the lagr quantities of a node that
	// evolve via an Euler-type equation
	public static class UpdateNodeLagr {


		// Updates lagr xvelocity of a node
		public static void XVel(
			List<Node> nodes,
			List<MaterialPoint> mp_points,
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
			for ( int i = 0; i < nodes.size(); ++i ) {


				nodes.get(i).l_xvel
				=
				nodes.get(i).xvel
				+ (
					Accelerations.ext_x( nodes.get(i) )
					- (1./nodes.get(i).mass) * MPMMath.RiemannSum.Stress(nodes.get(i), mp_points)
					// Boundary terms ignored!
				)*dt;


			}//end for

			
			// Again, fix the boundaries!
			nodes.get(0).l_xvel = BoundaryConditions.Velocity.Left( nodes.get(0).time );
			nodes.get(nodes.size()-1).l_xvel = BoundaryConditions.Velocity.Right( nodes.get(nodes.size()-1).time );



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


			for ( int i = 0; i < mps.size(); ++i ) {


				mps.get(i).strain += MPMMath.ParticleStrainRate(mps.get(i), nodes)*dt;
	
	
			}//end for


		}//end Strain


	}//end UpdateParticle




	// Update the particle xvelocity
	public static void XVelocity (
		List<MaterialPoint> mps,
		List<Node> nodes,
		double dt
	) {


		for ( int i = 0; i < mps.size(); ++i ) {


			mps.get(i).xvel += MPMMath.LagrDifferSum.XVel(mps.get(i), nodes)*dt;


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


		for ( int i = 0; i < mps.size(); ++i ) {


			mps.get(i).pnt_xvel = MPMMath.LagrVelSum(mps.get(i), nodes);


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
			List<MaterialPoint> mp_points
		) {


			for ( int i = 0; i < nodes.size(); ++i ) {


				nodes.get(i).dens = MPMMath.MPWeightSum.Density(nodes.get(i), mp_points);


			}//end for


			// Boundary nodes are of half-length
			// so need to multiply by 2!
			nodes.get(0).dens = 2. * nodes.get(0).dens;
			nodes.get( nodes.size()-1 ).dens = 2. * nodes.get( nodes.size()-1 ).dens;


		}//end Density




		public static void XVelocity(
			List<Node> nodes,
			List<MaterialPoint> mp_points
		) {


			for ( int i = 0; i < nodes.size(); ++i ) {


				nodes.get(i).xvel = MPMMath.MPWeightSum.XVelocity(nodes.get(i), mp_points);


			}//end for


			// Enforce boundary conditions!
			nodes.get(0).l_xvel = BoundaryConditions.Velocity.Left( nodes.get(0).time );
			nodes.get(nodes.size()-1).l_xvel = BoundaryConditions.Velocity.Right( nodes.get(nodes.size()-1).time );


		}//end Xvelocity



		public static void Strain(
			List<Node> nodes,
			List<MaterialPoint> mp_points
		) {


			for ( int i = 0; i < nodes.size(); ++i ) {


				nodes.get(i).strain = MPMMath.MPWeightSum.Strain(nodes.get(i), mp_points);


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
