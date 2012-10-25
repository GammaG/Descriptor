package hander;

import java.io.InputStream;


import java.util.ArrayList;
import java.util.Collection;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;


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
	private ArrayList<String> ids = new ArrayList<String>();

	/**
	 * public reference of the Handler class
	 * 
	 * @param file
	 *            give RDF file
	 * @return true if success, false if failed
	 */
	public boolean putFile(String file) {
		try {
			this.file = file;
			getSet();
			parseModel();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			set.close();
		}
		return false;
	}

	/**
	 * here the model gets created given file will read and write in a model
	 */
	private void parseModel() {
		model = ModelFactory.createDefaultModel();

		InputStream in = FileManager.get().open(file);
		if (in == null) {
			throw new IllegalArgumentException("File: " + file
					+ " not found or valid!");
		}

		// read the RDF/XML file
		model.read(in, null);
		// model.write(System.out);

	}

	/**
	 * get a collection of possible ids search for a possible on and writes it
	 * with the found id in the dataset, in form of "namedModel"
	 * 
	 * @param liste
	 * @return true if an unused id was found.
	 */
	@SuppressWarnings({ "unused" })
	private boolean getDescription(final Collection<String> liste) {
		ids.addAll(liste);

		for (String s : ids) {
			if (!set.containsNamedModel(s)) {
				if (model != null) {
					set.addNamedModel(s, model);
					return true;
				}

			}

		}

		return false;
	}

	/**
	 * gets a reference to the Dataset starts the Dataset
	 */
	private void getSet() {
		set = DataSet.getSet();
		set.begin(ReadWrite.WRITE);
	}

}
