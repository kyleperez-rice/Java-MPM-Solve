/*
	Contains:
		class: MaterialPoint
			(Describes a material point of the system)

			time
			length
			mass
			xpos
			pnt_xvel
			xvel
			stress
			strain
			ymodulus
			function: shapef
				(linear shape function of a material point)
			function: d_shapef
				(derivative of shapef)
*/


// Defines the 'MaterialPoint' class
// which tells us about a material point, which is a little bit of continua
// between nodes
//
// Much in similarlity with the Node Class
public class MaterialPoint {

	
	// Standard attributes
	public double time;
	public double length;
	public double mass; // Mass of the material point


	// Position and physical velocity
	public double xpos;
	public double pnt_xvel; // Not the same as xvel! Physical velocity of particle


	// Measurable material properties
	public double xvel; // Contributes to node velocity measurement

	public double stress;
	public double strain;


	// Other continua properties
	public double ymodulus;


	// Shape function and its derivative
	public double shapef( double x ) {


		double lpoint = this.xpos - this.length;
		double rpoint = this.xpos - this.length;

		
		if ( x > lpoint && x <= xpos ) {

			return 1. + (x - this.xpos)/this.length;

		}
		else if ( x > xpos && x < rpoint ) {
		
			return 1. - (x - this.xpos)/this.length;

		}
		else {

			return 0.;

		}//end if


	}//end shapef

	public double d_shapef( double x ) {


		double lpoint = this.xpos - this.length;
		double rpoint = this.xpos - this.length;

		
		if ( x > lpoint && x < xpos ) {

			return 1./this.length;

		}
		else if ( x > xpos && x < rpoint ) {
		
			return -1./this.length;

		}
		else if ( x == lpoint) {

			return 0.5/this.length;

		}
		else if (x == rpoint) {

			return -0.5/this.length;

		}
		else {

			return 0.;

		}//end if


	}//end d_shapef


}//end MaterialPoint
