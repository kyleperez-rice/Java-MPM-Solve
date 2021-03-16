import java.io.*; // Gets us CSV output
import java.util.List; // Gets us lists

/*
	Contains:
		class: DataWrite
			(functions on writing data for analysis)
			
			function: Node
				(Writes out node data to a csv)
			function: MaterialPoint
				(writes out material point data to a csv)
*/


public class DataWrite {
	

	// Gets us a csv that describes the node data
	public static void Node (
		String filename,
		List<String> titles,
		List<List<Node>> myData,
		double dt
	) throws IOException {


		double t = 0.;
	
		FileWriter myFile = new FileWriter(filename);


		// Write titles
		myFile.append( "time," );
		for (int i = 0; i < titles.size(); ++i) {


			myFile.append( titles.get(i) );

			if ( i != titles.size() - 1 ) {

				myFile.append( ',' );

			}//end if


		}//end for

		myFile.append( '\n' );



		// Then actually write the data
		// Just go through all the class elements of the system
		// Do for all point and all times
		for (int i = 0; i < myData.size(); ++i) {


			for (int j = 0; j < myData.get(i).size(); ++j) {


				myFile.append( String.valueOf(t) );
				myFile.append( ',' );
				myFile.append( String.valueOf(myData.get(i).get(j).xpos) );
				myFile.append( ',' );
				myFile.append( String.valueOf(myData.get(i).get(j).stress) );
				myFile.append( ',' );
				myFile.append( String.valueOf(myData.get(i).get(j).strain) );
				myFile.append( ',' );
				myFile.append( String.valueOf(myData.get(i).get(j).dens) );
				myFile.append( ',' );
				myFile.append( String.valueOf(myData.get(i).get(j).xvel) );
				myFile.append( '\n' );


			}//end for


			t += dt;


		}//end for


		// Flush and close the file
		myFile.flush();
		myFile.close();
	
	
	}//end Node







	// Gets us a csv that describes the node data
	public static void MaterialPoint (
		String filename,
		List<String> titles,
		List<List<MaterialPoint>> myData,
		double dt
	) throws IOException {
	

		double t = 0.;

		FileWriter myFile = new FileWriter(filename);


		// Write titles
		myFile.append( "time," );
		for (int i = 0; i < titles.size(); ++i) {


			myFile.append( titles.get(i) );

			if ( i != titles.size() - 1 ) {

				myFile.append( ',' );

			}//end if


		}//end for

		myFile.append( '\n' );



		// Then actually write the data
		// Just go through all the class elements of the system
		// Do for all point and all times
		for (int i = 0; i < myData.size(); ++i) {


			for (int j = 0; j < myData.get(i).size(); ++j) {


				myFile.append( String.valueOf(t) );
				myFile.append( ',' );
				myFile.append( String.valueOf(myData.get(i).get(j).xpos) );
				myFile.append( ',' );
				myFile.append( String.valueOf(myData.get(i).get(j).mass) );
				myFile.append( ',' );
				myFile.append( String.valueOf(myData.get(i).get(j).stress) );
				myFile.append( ',' );
				myFile.append( String.valueOf(myData.get(i).get(j).strain) );
				myFile.append( ',' );
				myFile.append( String.valueOf(myData.get(i).get(j).xvel) );
				myFile.append( '\n' );


			}//end for


			t += dt;


		}//end for


		// Flush and close the file
		myFile.flush();
		myFile.close();
	
	
	}//end Node




}//end DataWrite
