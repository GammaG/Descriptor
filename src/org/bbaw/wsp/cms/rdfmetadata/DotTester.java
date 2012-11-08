package org.bbaw.wsp.cms.rdfmetadata;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import hander.RDFDocumentHandler;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class DotTester extends JFrame {

	public static void main(String[] args) {
		String file = null;

		new DotTester();



	}

	private JButton btn_src;
	private JButton btn_go;
	private Label src;
	private Label fin;
	private JButton btn_des;
	private Label des;
	private String srcF;
	private String desF;
	private JenaTester jenatester = new JenaTester();
	private TextArea textArea;

	/**
	 * Opens a now Frame
	 */
	public DotTester() {
		super("RDF2DOT2PNG");
		setSize(600, 300);
		setLocation(300, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		initalisation();
	}

	/**
	 * Creates the Elements of GUI
	 */
	private void initalisation() {
		setLayout(new BorderLayout(5, 5));

		Panel panel = new Panel();
		panel.setLayout(new GridLayout(6, 1));
		btn_src = new JButton("Choose Source Folder");
		src = new Label("no Source Folder selected");
		btn_des = new JButton("Choose Destination Folder");
		des = new Label("no Destination Folder selected");
		btn_go = new JButton("Start");
		fin = new Label("");

		addButtonListener(btn_src);
		addButtonListener(btn_go);
		addButtonListener(btn_des);
		panel.add(btn_src);
		panel.add(src);
		panel.add(btn_des);
		panel.add(des);
		panel.add(btn_go);
		panel.add(fin);

		textArea = new TextArea("Welcome\n");
				
		Panel mainPanel = new Panel();
		mainPanel.setLayout(new GridLayout(1, 2));
		mainPanel.add(panel);
		mainPanel.add(new Panel().add(textArea));

		getContentPane().add(mainPanel);

	}

	/**
	 * Buttonlistener Class for GUI
	 * 
	 * @param b
	 */
	private void addButtonListener(JButton b) {
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ev) {
				if (ev.getActionCommand().equals("Choose Source Folder")) {
					srcF = fileChooser();
					if (srcF == null) {
						src.setText("no Source Folder selected");
					} else
						src.setText(srcF);

				} else if (ev.getActionCommand().equals(
						"Choose Destination Folder")) {

					desF = fileChooser();
					if (desF == null) {
						des.setText("no Destination Folder selected");
					} else
						des.setText(desF);

				} else if (ev.getActionCommand().equals("Start")) {

					if(srcF == null){
						println("no source choosed!");
						return;
					}
					else if(desF == null){
						println("no destination choosed!");
					}
					
					ArrayList<File> liste = new ArrayList<File>();
					liste = scanforRDF(srcF);
					if(liste.isEmpty()){
						println("There are no RDF files in directory");
						return;
					}
					println("There are "+liste.size()+" RDF elements.");
					println("Started!");
					execution(liste);
					
					
					
				}

			}
		});
	}

	private void execution(ArrayList<File> col){
		
		String dot;
		
		
		for (File file : col) {
			dot = jenatester.testStore(file, desF);
						
			String cmd = "dot -Tpng "+dot+" -o "+dot.substring(0, dot.length()-4)+".png";
			bash(cmd ,new File(desF));
			
			println("Graph created for "+file.getName());
		}
		
		
	}
	
	/**
	 * Seperates the files with ending RDF
	 * @param str
	 * @return
	 */
	private ArrayList<File> scanforRDF(String str) {

		File dir = new File(str);
		ArrayList<File> liste = new ArrayList<File>();
		for (File f : dir.listFiles()) {
			if (f.getName().toLowerCase().endsWith(".rdf"))
				liste.add(f);
				println(f.getName());

		}

		return liste;
	}

	/**
	 * Adds a line to the TextArea
	 * 
	 * @param str
	 */
	private void println(String str) {
		textArea.append(str + "\n");
	}

	/**
	 * Is used to Choose the folder and returns the folder path
	 * 
	 * @return
	 */
	private String fileChooser() {
		JFileChooser chooser = new JFileChooser();
		// Note: source for ExampleFileFilter can be found in
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = chooser.showOpenDialog(chooser);

		if (returnVal == JFileChooser.APPROVE_OPTION) {

			return chooser.getSelectedFile().getAbsolutePath();
		}
		return null;
	}

	/**
	 * Executes Bash jobs
	 * 
	 * @param cmd
	 * @param cwd
	 */
	private void bash(String cmd, File cwd) {
		if (System.getProperty("os.name").startsWith("Windows")){
			println("only works with Unix-shell!");
			return;
		}
		try {
			println("Path: "+cwd.getAbsolutePath());
			println(cmd);
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
			if (cwd != null) {
				pb.directory(cwd);
			}
			pb.redirectErrorStream(true);
			Process pr = pb.start();
			BufferedReader input = new BufferedReader(new InputStreamReader(
					pr.getInputStream()));

			String line = null;

			while ((line = input.readLine()) != null) {
				println(line);
			}

			int exitVal = pr.waitFor();
			if (exitVal != 0) {
				throw new Error("Failure while executing bash command '" + cmd
						+ "'. Return code = " + exitVal);
			}
		} catch (Exception e) {
			throw new Error("Could not execute bash command '" + cmd + "'.", e);
		}
	}
}
