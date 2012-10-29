package hander;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.tdb.TDBFactory;



public class DataSetImpl {
	
	private static String directory = "../DB";

	private static Dataset set = TDBFactory.createDataset(directory);
	
	public static Dataset getSet(){
		return set;
	}
	
	

	
}
