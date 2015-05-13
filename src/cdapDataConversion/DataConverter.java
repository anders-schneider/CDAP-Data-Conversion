package cdapDataConversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
	
	public DataConverter() {
		students = new HashMap<Integer, Student>();
		teachers = new HashMap<String, Teacher>();
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
