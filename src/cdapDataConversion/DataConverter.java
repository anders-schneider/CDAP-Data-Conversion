package cdapDataConversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.MissingFormatArgumentException;

/**
 * This program was written exclusively for the Duckworth Lab at UPenn
 * to aggregate and reformat data collected in the course of the Character
 * Development in Adolescence Project (CDAP). This program reads in two 
 * CSV files: one that contains each teacher's roster and the other that
 * consists of data from the surveys filled out by the teachers about their
 * students character habits. This program outputs a CSV where each student's
 * aggregated score for each character habit is displayed on a separate row.
 * 
 * @author Anders Schneider
 *
 */
public class DataConverter {
	
	HashMap<Integer, Student> students;
	HashMap<String, Teacher> teachers;
	
	String[] habitMap;
	int[] classIDMap;
	
	/**
	 * Constructor for the DataConverter type.
	 */
	public DataConverter() {
		students = new HashMap<Integer, Student>();
		teachers = new HashMap<String, Teacher>();
	}
	
	/**
	 * Parses the survey data and gives each student a rating for the 
	 * appropriate class and the appropriate character habit.
	 * 
	 * @param surveyData An array of CSV strings with raw survey data
	 */
	public void parseSurveyData(String[] surveyData) {
		parseHeaders(surveyData[0]);
		
		for (int i = 1; i < surveyData.length; i++) {
			if (surveyData[i] == null) break;
			parseDataLine(surveyData[i]);
		}
	}
	
	/**
	 * Parses an individual line of the survey data and stores the
	 * information.
	 * 
	 * @param line A line of the survey data
	 */
	void parseDataLine(String line) {
		//TODO Comment new code
		
		int start, stop;
		
		if (line.charAt(0) != '"') throw new MissingFormatArgumentException("Was expecting \"Teacher name\", ...");
		
		start = 1;
		stop = start;
		
		while (line.charAt(stop) != '"') stop++;
		String teacherName = line.substring(start, stop);
		
		if (!teachers.containsKey(teacherName)) throw new IllegalArgumentException("The following teacher was not included in the rosters: " + teacherName);
		Teacher teacher = teachers.get(teacherName);
		
		start = stop;
		while (line.charAt(start) != ',') start++;
		start++;
		
		stop = start;
		while (line.charAt(stop) != ',') stop++;
		
		String subject = line.substring(start, stop);
		teacher.subject = subject;
		
		start = ++stop;
		String[] data = line.substring(start, line.length()).split(",");
		
		parseData(data, teacher);
	}
	
	/**
	 * Stores the data contained in the input array of strings for each
	 * student.
	 * 
	 * @param data The columns of the survey data for one row
	 * @param teacher The teacher to whom this row corresponds
	 */
	void parseData(String[] data, Teacher teacher) {
		for (int i = 0; i < data.length; i++) {
			
			if ("".equals(data[i])) continue;
			
			String habit = habitMap[i];
			
			int inClassID = classIDMap[i];
			Student student = teacher.getStudent(inClassID);
			
			int rating = Integer.parseInt(data[i]);
			
			student.setRating(habit, teacher.subject, rating);
		}
	}
	
	/**
	 * Stores information about each section of the header line to be used
	 * when parsing the subsequent lines.
	 * 
	 * @param line A String representing the header line
	 */
	void parseHeaders(String line) {		
		// Divide the CSV string into sections
		String[] headers = line.split(",");
		
		habitMap = new String[headers.length - 2];
		classIDMap = new int[headers.length - 2];
		
		for (int i = 2; i < headers.length; i++) {
			// Fill the habitMap array with corresponding character habits
			habitMap[i - 2] = extractHabit(headers[i]);
			
			// Fill the classIDMap array with corresponding in-class IDs
			classIDMap[i - 2] = extractClassID(headers[i]);
		}
	}
	
	/**
	 * Returns the character habit (as a String) embedded in this section
	 * of the header line
	 * 
	 * @param header A string representing the header of a single column
	 * @return The character habit to which this column refers
	 */
	String extractHabit(String header) {
		int stop = 0;
		
		while (header.charAt(stop) != '(') stop++;
		
		return header.substring(0, stop).trim();
	}
	
	/**
	 * Returns the in-class ID (as an int) embedded in this section of
	 * the header line
	 * 
	 * @param header A string representing the header of a single column
	 * @return The in-class ID to which this column refers
	 */
	int extractClassID(String header) {
		int stop = header.length() - 1;
		int start = stop;
		
		while (header.charAt(start) != 't') start--;
		start++;
		
		return Integer.parseInt(header.substring(start, stop));
	}
	
	/**
	 * Parses the array of strings that constitutes the rosters and stores
	 * all the information regarding students and their teachers.
	 * 
	 * @param rostersText An array of Strings representing all the teachers' rosters
	 */
	public void parseRosters(String[] rostersText) {
		
		// Skip the first line: "Teacher, Student"
		
		// Parse each roster line
		for (int i = 1; i < rostersText.length; i++) {
			if (rostersText[i] == null) break;
			
			parseRosterLine(rostersText[i]);
		}
	}
	
	/**
	 * Parses the input roster line and extracts information about this
	 * specific teacher and student combination.
	 * 
	 * @param line A String representing a line of a roster
	 */
	void parseRosterLine(String line) {
		String parts[] = line.split("\"");
		
		// Extract this student's information
		Student student = parseStudent(parts[3]);
		
		// Combine that with the teacher's information
		parseTeacher(parts[1], student);
	}
	
	/**
	 * Parses a String with information about an individual student and
	 * returns a corresponding Student object.
	 * 
	 * @param sLine A String with information regarding a student
	 * @return A Student object created according to the input information
	 */
	Student parseStudent(String sLine) {
		int start = 0;
		while (Character.isAlphabetic(sLine.charAt(start))) start++;
		
		int stop = start;
		while (sLine.charAt(stop) != ',') stop++;
		
		// Extract the student's ID number (for this class only)
		String inClassIDNum = sLine.substring(start, stop);
		int inClassID = Integer.parseInt(inClassIDNum);
		
		start = stop + 1;
		while (sLine.charAt(start) == ' ') start++;
		
		stop = start;
		while (sLine.charAt(stop) != ' ') stop++;
		
		// Extract the student's overall ID number
		String idNum = sLine.substring(start, stop);
		int id = Integer.parseInt(idNum);
		
		Student student;
		
		// Look up the student by ID number
		if (students.containsKey(id)) {
			student = students.get(id);
		} else {
			// If not already in the collection, add a new student
			student = new Student(id);
			students.put(id, student);
		}

		// Store the class-specific ID number
		student.lastInClassID = inClassID;
		
		return student;
	}
	
	/**
	 * Parses a String with information about a specific teacher and
	 * a specific Student and updates the corresponding Teacher object.
	 * 
	 * @param name A String representing the Teacher's name
	 * @param student A Student object that is in this Teacher's class
	 */
	void parseTeacher(String name, Student student) {
		Teacher teacher;
		
		// Look up the teacher by name
		if (teachers.containsKey(name)) {
			teacher = teachers.get(name);
		} else {
			// If not already in the collection, add a new teacher
			teacher = new Teacher(name);
			teachers.put(name, teacher);
		}
		
		teacher.addStudent(student, student.lastInClassID);
	}
}
