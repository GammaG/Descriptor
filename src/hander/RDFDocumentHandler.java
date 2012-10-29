package hander;

import java.io.InputStream;

import bbaw.wsp.parser.metadata.factory.MetadataParserFactory;
import bbaw.wsp.parser.metadata.parsers.RdfMetadataParser;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.tdb.StoreConnection;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.util.FileManager;

import de.mpg.mpiwg.berlin.mpdl.exception.ApplicationException;

/**
 * 
 * @author shk2 (Marco Seidler)
 * 
 * 
 */
public class RDFDocumentHandler {

	private String file = null;
	private Dataset set = null;
	private Model model = null;
	private String test = null;
	private ModelMaker modelmaker;

	/**
	 * public reference of the Handler class
	 * 
	 * @param file
	 *            give RDF file
	 * @return true if success, false if failed
	 */
	@SuppressWarnings("finally")
	public boolean putFile(final String file) {
		try {
			this.file = file;
			openDataset();
			createModelFactory();
			setModel();
			model = getFreshModel();
			model = readFile(model, file);
			putDescription(scanID(file));
			showSet();
			close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			close();
			return false;
		}

	}

	/**
	 * takes a file and scans it for the about id
	 * 
	 * @param file
	 * @return String as like as ID of the file
	 */

	private String scanID(final String file) {
		try {
			RdfMetadataParser fac = MetadataParserFactory
					.newRdfMetadataParser(file);
			test = fac.getRdfAboutValue();
			return test;

		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * here the model gets created given file will read and write in a model
	 */
	private void setModel() {
		model = set.getDefaultModel();
		TDB.getContext().set(TDB.symUnionDefaultGraph, true);
		

	}

	public void createModelFactory(){
    	modelmaker = ModelFactory.createMemModelMaker();
	}
	
	public Model getFreshModel(){
		Model model = modelmaker.createFreshModel();
		return model;
	}
	
	public void addNamedModel(String name, Model model){
		
		set.addNamedModel(name, model);
	}
	
	
	
	
	/**
	 * INSERT Graph
	 * @param store
	 */
	public Model readFile(Model model, String loc){
		Model moodel = FileManager.get().readModel(model, loc);
		
		return moodel;
	}
	
	/**
	 * get a collection of possible ids search for a possible on and writes it
	 * with the found id in the dataset, in form of "namedModel"
	 * 
	 * @param liste
	 * @return true if an unused id was found.
	 */

	private boolean putDescription(String id) {

		if (!set.containsNamedModel(id)) {
			if (model != null) {
				set.addNamedModel(id, model);
				return true;
			}

		}

		return false;
	}

	/**
	 * gets a reference to the Dataset starts the Dataset
	 */
	public void openDataset(){
		set = DataSetImpl.getSet();
		set.begin(ReadWrite.WRITE);
	}

	/**
	 * 
	 */
	private void showSet() {
		Model m = set.getNamedModel(test);
		System.out.println("ID: " + test);
		m.write(System.out);
	}

	private void close() {
		model.close();
		set.commit();
		set.close();

	}

}
