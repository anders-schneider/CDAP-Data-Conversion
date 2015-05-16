package cdapDataConversion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.omg.CORBA.UserException;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;

/**
 * This class handles all of the input and output and interaction with the user
 * 
 * @author Anders Schneider
 *
 */
public class UserInterface {

	//TODO Decide on error handling and appropriate error messages
	//TODO Turn this into an executable
	//TODO Write a readme and generate Javadocs
	
	public static void main(String[] args) {
		DataConverter dc = new DataConverter();
		
		JOptionPane.showMessageDialog(null, "You'll first be prompted to select the "
				+ "rosters file.\n\nNote that it must be a .csv file!",
				"Welcome!",
				JOptionPane.DEFAULT_OPTION);
		
		String[] rostersText;
		
		// First load the rosters file
		while (true) {
			try {
				rostersText = load("rosters");
				break;
			} catch (IOException e) {
				displayError(e.getMessage());
			} catch (CannotProceed e) {
				return;
			}
		}
		
		// Next parse the rosters file and store its information
		try {
			dc.parseRosters(rostersText);
		} catch (Exception e) {
			displayError("Error parsing the rosters file:\n\n" + e.getMessage()
					+ "\n\nCheck the rosters file and try again.");
			return;
		}
		
		JOptionPane.showMessageDialog(null, "Great, thanks!\n\nNext you'll need to select "
				+ "the survey data file.\n\n(This one also has to be a .csv file.)");
		
		String[] surveyData;
		
		// Then load the survey data
		while (true) {
			try {
				surveyData = load("survey data");
				break;
			} catch (IOException e) {
				displayError(e.getMessage());
			} catch (CannotProceed e) {
				return;
			}
		}
		
		// And parse the survey data
		try{
			dc.parseSurveyData(surveyData);
		} catch (Exception e) {
			displayError("Error parsing the survey data file:\n\n" + e.getMessage()
					+ "\n\nCheck the survey data file and try again.");
			return;
		}
		
		JOptionPane.showMessageDialog(null, "Awesome - now you just need to select a "
				+ "location to save the output to and you'll be all set!");
		
		// Finally generate the output
		String[] output = dc.generateOutput();
		
		// And save it to an output file
		while (true) {
			try {
				saveOutput(output);
				break;
			} catch (FileNotFoundException e) {
				displayError(e.getMessage());
			} catch (CannotProceed e) {
				return;
			}
		}
		
		JOptionPane.showMessageDialog(null, "You're all done!", "Success!", 
				JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	 * Loads a file (either a rosters file or a survey data file, according
	 * to the input) and returns an array of strings, where each string
	 * represents an individual line.
	 * 
	 * @param desc A string describing if the rosters should be loaded or the survey data
	 * @return An array of strings representing the file's contents
	 * @throws CannotProceed If the user does not select a file
	 * @throws IOException If the file is null or is not a .csv file
	 */
	static String[] load(String desc) throws CannotProceed, IOException {
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
                String fileName = file.getCanonicalPath();
                
                // Check to make sure the file is a CSV file
                int len = fileName.length();	                
                if (!".csv".equals(fileName.substring(len - 4, len))) {
                	// Otherwise throw an exception
                	throw new IOException("The selected file is not a .csv file. Try again!");
                }
                
                reader = new BufferedReader(new FileReader(fileName));
                String line;
                int index = 0;
                while ((line = reader.readLine()) != null) {
                    lines[index++] = line;
                }
                reader.close();
                return lines;
            } else {
            	throw new IOException("Whoops! Something went wrong trying "
            			+ "to open that file. Check the file and try again!");
            }
        } else {
        	throw new CannotProceed();
        }
	}
	
	/**
	 * Saves the input array of strings as a file, chosen by the user
	 * with a JFileChooser.
	 * 
	 * @param output An array of strings representing lines of the output
	 * @throws FileNotFoundException If no file is found
	 * @throws CannotProceed If the user closes the save file dialog
	 */
	static void saveOutput(String[] output) throws FileNotFoundException, CannotProceed {
		JFileChooser chooser = new JFileChooser();
		
		int response = chooser.showSaveDialog(null);
		if (response == JFileChooser.APPROVE_OPTION) {
			PrintWriter stream = new PrintWriter(chooser.getSelectedFile());
			for (String line : output) {
				stream.println(line);
			}
			stream.close();
		} else {
			throw new CannotProceed();
		}
	}
	
	static void displayError(String exceptionMsg) {
		JOptionPane.showMessageDialog(null, exceptionMsg, "ERROR",
				JOptionPane.ERROR_MESSAGE);
	}

}
