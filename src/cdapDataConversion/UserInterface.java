package cdapDataConversion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;

/**
 * This class handles all of the input and output and interaction with the user
 * 
 * @author Anders Schneider
 *
 */
public class UserInterface {

	public static void main(String[] args) {
		String[] rostersText = loadRosters();
		DataConverter dc = new DataConverter();
		dc.parseRosters(rostersText);
	}
	
	static String[] loadRosters() {
		String[] lines = new String[10000]; // Capacity for up to 10,000 student-teacher pairs
        BufferedReader reader;

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Please select the roster file");
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
	            	loadRosters();
	            }
            }
        }
        return lines;
	}
	
	static void displayError(String exceptionMsg) {
		System.out.println(exceptionMsg);
	}

}
