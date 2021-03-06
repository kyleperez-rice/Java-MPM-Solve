import java.util.List; // Get us lists

/*
	Contains:
		class: Incrementation
			(Lets us increment material points/ node quantities)

			class: IncrementMaterialPoint
				(increment material point quantity)

				function: time
					(increment mp in time)
				function: xpos

			class: IncrementNode
				(Increment node quantity)
				
				function: time
					(increment node in time)
*/


// Lets us increment a specific variable for a material point or node
// IE: time, position, etc
public class Incrementation {



	// Increment a material point variable
	public static class IncrementMaterialPoint {


		// Increment time
		public static void time( List<MaterialPoint> mps, double dt ) {


			for (int i = 0; i < mps.size(); ++i) {

				
				mps.get(i).time += dt;


			}//end for
			

		}//end time



		// For the material point position
		public static void xpos( List<MaterialPoint> mps, double dt ) {


			for (int i = 0; i < mps.size(); ++i) {


				mps.get(i).xpos += mps.get(i).pnt_xvel * dt;


			}//end for


		}//end xpos


	}//end MaterialPoint



//-------------------------------------------------------------------------------------------


	// For nodes
	public static class IncrementNode {


		public static void time( List<Node> nodes, double dt ) {


			for (int i = 0; i < nodes.size(); ++i) {


				nodes.get(i).time += dt;


			}//end for


		}//end time


	}//end Node



}//end Incrementation
