package cdapDataConversion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JFileChooser;

/**
 * This class handles all of the input and output and interaction with the user
 * 
 * @author Anders Schneider
 *
 */
public class UserInterface {

	//TODO Generate small, sample test rosters and survey data with all imaginable snafus
	//TODO Decide on error handling and appropriate error messages
	//TODO Turn this into an executable
	//TODO Write a readme and generat Javadocs
	
	public static void main(String[] args) {
		DataConverter dc = new DataConverter();
		
		// Extract information from the rosters first
		String[] rostersText = load("rosters");
		dc.parseRosters(rostersText);
		
		// Then parse the survey data and store the ratings
		String[] surveyData = load("survey data");
		dc.parseSurveyData(surveyData);
		
		// Finally generate the output and save it to a CSV file
		String[] output = dc.generateOutput();
		saveOutput(output);
	}
	
	/**
	 * Loads a file (either a rosters file or a survey data file, according
	 * to the input) and returns an array of strings, where each string
	 * represents an individual line.
	 * 
	 * @param desc A string describing if the rosters should be loaded or the survey data
	 * @return An array of strings representing the file's contents
	 */
	static String[] load(String desc) {
		String[] lines = null;
		if ("rosters".equals(desc)) lines = new String[10000]; // Capacity for up to 10,000 student-teacher pairs
		else if ("survey data".equals(desc)) lines = new String[500]; // Capacity for up to 500 teachers
		
        BufferedReader reader;

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Please select the " + desc + " file");
        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file != null) {
	            try{	
	                String fileName = file.getCanonicalPath();
	                
	                // Check to make sure the file is a CSV file
	                int len = fileName.length();	                
	                if (!".csv".equals(fileName.substring(len - 4, len))) {
	                	// Otherwise throw an exception
	                	throw new IOException("The selected file is not a CSV file.");
	                }
	                
	                reader = new BufferedReader(new FileReader(fileName));
	                String line;
	                int index = 0;
	                while ((line = reader.readLine()) != null) {
	                    lines[index++] = line;
	                }
	                reader.close();
	                return lines;
	            } catch (IOException e) {
	            	displayError(e.getMessage());
	            	load(desc);
	            }
            }
        }
        return lines;
	}
	
	/**
	 * Saves the input array of strings as a file, chosen by the user
	 * with a JFileChooser.
	 * 
	 * @param output An array of strings representing lines of the output
	 */
	static void saveOutput(String[] output) {
		JFileChooser chooser = new JFileChooser();
		
		int response = chooser.showSaveDialog(null);
		if (response == JFileChooser.APPROVE_OPTION) {
			try {
				PrintWriter stream = new PrintWriter(chooser.getSelectedFile());
				for (String line : output) {
					stream.println(line);
				}
				stream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	static void displayError(String exceptionMsg) {
		System.out.println(exceptionMsg);
	}

}
