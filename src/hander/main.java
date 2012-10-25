package hander;

import java.io.File;

import javax.swing.JFileChooser;

public class main {

	public static void main(String[] args){
		String file = null;
		JFileChooser chooser = new JFileChooser();
		// Note: source for ExampleFileFilter can be found in FileChooserDemo,
		// under the demo/jfc directory in the JDK.

		int returnVal = chooser.showOpenDialog(chooser);

		if (returnVal == JFileChooser.APPROVE_OPTION) {

			file = chooser.getSelectedFile().getAbsolutePath();
		}
		
		new RDFDocumentHandler().putFile(file);
		
		
	}
	
	
}
