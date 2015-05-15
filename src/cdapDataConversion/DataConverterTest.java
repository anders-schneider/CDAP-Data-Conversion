package cdapDataConversion;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DataConverterTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testParseRosters() {
		String line1 = "Teacher,Student";
		String line2 = "\"Shoemaker, Kimberly Crandall\",\"Student1,5115   Abdullah-Tucker, Ahmad\"";
		String line3 = "\"Shoemaker, Kimberly Crandall\",\"Student2,5062   Amaya, Tatiana\"";
		String line4 = "\"Shoemaker, Kimberly Crandall\",\"Student3,5063   Artis, Makeda\"";
		String line5 = "\"Shoemaker, Daniel Hecht\",\"Student62,5063   Artis, Makeda\"";
		
		String[] rostersText = {line1, line2, line3, line4, line5};
		
		DataConverter dc = new DataConverter();
		dc.parseRosters(rostersText);
		
		assertEquals(2, dc.teachers.size());
		assertEquals(3, dc.students.size());
		
		assertTrue(dc.teachers.containsKey("Shoemaker, Kimberly Crandall"));
		assertTrue(dc.teachers.containsKey("Shoemaker, Daniel Hecht"));
		
		Teacher t1 = dc.teachers.get("Shoemaker, Kimberly Crandall");
		Teacher t2 = dc.teachers.get("Shoemaker, Daniel Hecht");
		
		assertNotNull(t1.getStudent(3));
		assertNotNull(t2.getStudent(62));
		assertEquals(t1.getStudent(3), t2.getStudent(62));
	}

	@Test
	public void testParseRosterLine() {
		String line1 = "\"Shoemaker, Kimberly Crandall\",\"Student1,5115   Abdullah-Tucker, Ahmad\"";
		String line2 = "\"Shoemaker, Kimberly Crandall\",\"Student2,5062   Amaya, Tatiana\"";
		String line3 = "\"Shoemaker, Daniel Hecht\",\"Student1,5033   Basiege, Jayen\"";
		
		DataConverter dc = new DataConverter();
		
		dc.parseRosterLine(line1);
		assertTrue(dc.teachers.containsKey("Shoemaker, Kimberly Crandall"));
		
		Teacher t1 = dc.teachers.get("Shoemaker, Kimberly Crandall");
		assertNotNull(t1.getStudent(1));
		
		Student ahmad = t1.getStudent(1);
		assertEquals(5115, ahmad.id);
		
		dc.parseRosterLine(line2);
		dc.parseRosterLine(line3);
		
		assertEquals(2, dc.teachers.size());
		
		assertNotNull(t1.getStudent(2));
		
		Student tatiana = t1.getStudent(2);
		assertEquals(5062, tatiana.id);
	}

	@Test
	public void testParseStudent() {
		DataConverter dc = new DataConverter();
		
		String line1 = "Student1,5115   Abdullah-Tucker, Ahmad";
		dc.parseStudent(line1);
		
		assertTrue(dc.students.containsKey(5115));
		assertEquals(1, dc.students.get(5115).lastInClassID);
		
		String line2 = "Student17, 3032   Andy Schneider";
		dc.parseStudent(line2);
		
		assertTrue(dc.students.containsKey(3032));
		assertEquals(17, dc.students.get(3032).lastInClassID);
		
		String line3 = "Student0, 30312   Andy Schneider";
		dc.parseStudent(line3);
		
		assertTrue(dc.students.containsKey(30312));
		assertEquals(0, dc.students.get(30312).lastInClassID);		
	}

	@Test
	public void testParseTeacher() {
		DataConverter dc = new DataConverter();
		
		String sLine1 = "Student1,5115   Abdullah-Tucker, Ahmad";
		Student ahmad = dc.parseStudent(sLine1);
		
		String sLine2 = "Student17, 3032   Andy Schneider";
		Student andy = dc.parseStudent(sLine2);
				
		String sLine3 = "Student0, 30312   Alisa Yu";
		Student alisa = dc.parseStudent(sLine3);
		
		String line1 = "Shoemaker, Daniel Hecht";
		String line2 = "Pickett, Alisa Yu";
		
		dc.parseTeacher(line1, ahmad);
		
		assertTrue(dc.teachers.containsKey(line1));
		
		Teacher t1 = dc.teachers.get(line1);
		assertEquals(ahmad, t1.getStudent(1));
		assertEquals(line1, t1.name);
		assertNull(t1.getStudent(2));
		
		dc.parseTeacher(line2, andy);
		dc.parseTeacher(line2, alisa);
		
		assertEquals(2, dc.teachers.size());
		
		Teacher t2 = dc.teachers.get(line2);
		assertEquals(andy, t2.getStudent(17));
		assertEquals(alisa, t2.getStudent(0));
	}
	
	@Test
	public void testExtractHabit() {
		String header1 = "Grit (Page 1) /    / Finishes whatever he/she begins /   /  Works very hard and keeps working when others...-${e://Field/Student1}";
		String header2 = "Self-Control Work (Page /  1) /    / Came to class prepared. /   /  Followed directions. /   /  Got to work rig...-${e://Field/Student8}";
		String header3 = "Self-Control Interpersonal (Page /  1) /    / Stayed calm even when others bothered or criticized her/h...-${e://Field/Student16}";
		
		DataConverter dc = new DataConverter();
		
		assertEquals("Grit", dc.extractHabit(header1));
		assertEquals("Self-Control Work", dc.extractHabit(header2));
		assertEquals("Self-Control Interpersonal", dc.extractHabit(header3));
	}
	
	@Test
	public void testExtractClassID() {
		String header1 = "Grit (Page 1) /    / Finishes whatever he/she begins /   /  Works very hard and keeps working when others...-${e://Field/Student1}";
		String header2 = "Self-Control Work (Page /  1) /    / Came to class prepared. /   /  Followed directions. /   /  Got to work rig...-${e://Field/Student8}";
		String header3 = "Self-Control Interpersonal (Page /  1) /    / Stayed calm even when others bothered or criticized her/h...-${e://Field/Student16}";
		
		DataConverter dc = new DataConverter();
		
		assertEquals(1, dc.extractClassID(header1));
		assertEquals(8, dc.extractClassID(header2));
		assertEquals(16, dc.extractClassID(header3));
	}
	
	@Test
	public void testParseHeaders() {
		String header0 = "Name";
		String header1 = "Subject";
		String header2 = "Grit (Page 1) /    / Finishes whatever he/she begins /   /  Works very hard and keeps working when others...-${e://Field/Student1}";
		String header3 = "Self-Control Work (Page /  1) /    / Came to class prepared. /   /  Followed directions. /   /  Got to work rig...-${e://Field/Student8}";
		String header4 = "Self-Control Interpersonal (Page /  1) /    / Stayed calm even when others bothered or criticized her/h...-${e://Field/Student16}";

		String fullHeader = header0 + "," + header1 + "," + header2 + "," + header3 + "," + header4;
		
		DataConverter dc = new DataConverter();
		
		dc.parseHeaders(fullHeader);
		
		assertEquals(3, dc.classIDMap.length);
		assertEquals(3, dc.habitMap.length);
		
		String[] expectedHabits = {"Grit", "Self-Control Work", "Self-Control Interpersonal"};
		int[] expectedClassIDs = {1, 8, 16};
		
		assertArrayEquals(expectedHabits, dc.habitMap);
		assertArrayEquals(expectedClassIDs, dc.classIDMap);
	}
	
	@Test
	public void testParseSurveyData() {
		String line1 = "Teacher,Student";
		String line2 = "\"Shoemaker, Kimberly Crandall\",\"Student1,5115   Abdullah-Tucker, Ahmad\"";
		String line3 = "\"Shoemaker, Kimberly Crandall\",\"Student2,5062   Amaya, Tatiana\"";
		String line4 = "\"Shoemaker, Kimberly Crandall\",\"Student3,5063   Artis, Makeda\"";
		String line5 = "\"Shoemaker, Daniel Hecht\",\"Student62,5063   Artis, Makeda\"";
		
		String[] rostersText = {line1, line2, line3, line4, line5};
		
		DataConverter dc = new DataConverter();
		dc.parseRosters(rostersText);
		
		String h0 = "Name";
		String h1 = "Subject";
		String h2 = "Grit (Page 1) /    / Finishes whatever he/she begins /   /  Works very hard and keeps working when others...-${e://Field/Student2}";
		String h3 = "Grit (Page 1) /    / Finishes whatever he/she begins /   /  Works very hard and keeps working when others...-${e://Field/Student3}";
		String h4 = "Grit (Page 1) /    / Finishes whatever he/she begins /   /  Works very hard and keeps working when others...-${e://Field/Student62}";
		String h5 = "Grit (Page 1) /    / Finishes whatever he/she begins /   /  Works very hard and keeps working when others...-${e://Field/Student1}";
		String h6 = "Gratitude (Page 3) /    / Appreciated when other people helped her/him. /   /  Showed that s/he cared and...-${e://Field/Student2}";
		String h7 = "Gratitude (Page 3) /    / Appreciated when other people helped her/him. /   /  Showed that s/he cared and...-${e://Field/Student3}";
		String h8 = "Gratitude (Page 3) /    / Appreciated when other people helped her/him. /   /  Showed that s/he cared and...-${e://Field/Student62}";
		String h9 = "Gratitude (Page 3) /    / Appreciated when other people helped her/him. /   /  Showed that s/he cared and...-${e://Field/Student1}";
		
		String fullHeader = h0 + "," + h1 + "," + h2 + "," + h3 + "," + h4 + "," + h5 + "," + h6 + "," + h7 + "," + h8 + "," + h9;
		
		String dataLine1 = "\"Shoemaker, Kimberly Crandall\",History,3,4,,1,5,,,2";
		String dataLine2 = "\"Shoemaker, Daniel Hecht\",Science,,,1,,,,2,";
		
		String[] surveyData = {fullHeader, dataLine1, null, dataLine2, null};
		
		assertEquals(-1, dc.students.get(5115).getRating("Grit", "History"));
		assertEquals(-1, dc.students.get(5115).getRating("Gratitude", "History"));
		assertEquals(-1, dc.students.get(5115).getRating("Gratitude", "Science"));
		
		assertEquals(-1, dc.students.get(5062).getRating("Grit", "History"));
		assertEquals(-1, dc.students.get(5062).getRating("Gratitude", "History"));
		assertEquals(-1, dc.students.get(5062).getRating("Gratitude", "Science"));
		
		assertEquals(-1, dc.students.get(5063).getRating("Grit", "History"));
		assertEquals(-1, dc.students.get(5063).getRating("Gratitude", "History"));
		assertEquals(-1, dc.students.get(5063).getRating("Grit", "Science"));
		assertEquals(-1, dc.students.get(5063).getRating("Gratitude", "Science"));
		
		dc.parseSurveyData(surveyData);
		
		assertEquals(1, dc.students.get(5115).getRating("Grit", "History"));
		assertEquals(2, dc.students.get(5115).getRating("Gratitude", "History"));
		assertEquals(-1, dc.students.get(5115).getRating("Gratitude", "Science"));
		
		assertEquals(3, dc.students.get(5062).getRating("Grit", "History"));
		assertEquals(5, dc.students.get(5062).getRating("Gratitude", "History"));
		assertEquals(-1, dc.students.get(5062).getRating("Gratitude", "Science"));
		
		assertEquals(4, dc.students.get(5063).getRating("Grit", "History"));
		assertEquals(-1, dc.students.get(5063).getRating("Gratitude", "History"));
		assertEquals(1, dc.students.get(5063).getRating("Grit", "Science"));
		assertEquals(2, dc.students.get(5063).getRating("Gratitude", "Science"));
	}
	
	@Test
	public void testParseDataLine() {
		String line1 = "Teacher,Student";
		String line2 = "\"Shoemaker, Kimberly Crandall\",\"Student1,5115   Abdullah-Tucker, Ahmad\"";
		String line3 = "\"Shoemaker, Kimberly Crandall\",\"Student2,5062   Amaya, Tatiana\"";
		String line4 = "\"Shoemaker, Kimberly Crandall\",\"Student3,5063   Artis, Makeda\"";
		String line5 = "\"Shoemaker, Daniel Hecht\",\"Student62,5063   Artis, Makeda\"";
		
		String[] rostersText = {line1, line2, line3, line4, line5};
		
		DataConverter dc = new DataConverter();
		dc.parseRosters(rostersText);
		
		dc.classIDMap = new int[8];
		dc.classIDMap[0] = 2;
		dc.classIDMap[1] = 3;
		dc.classIDMap[2] = 62;
		dc.classIDMap[3] = 1;
		dc.classIDMap[4] = 2;
		dc.classIDMap[5] = 3;
		dc.classIDMap[6] = 62;
		dc.classIDMap[7] = 1;
		
		dc.habitMap = new String[8];
		dc.habitMap[0] = "Grit";
		dc.habitMap[1] = "Grit";
		dc.habitMap[2] = "Grit";
		dc.habitMap[3] = "Grit";
		dc.habitMap[4] = "Optimism";
		dc.habitMap[5] = "Optimism";
		dc.habitMap[6] = "Optimism";
		dc.habitMap[7] = "Optimism";
		
		String dataLine1 = "\"Shoemaker, Kimberly Crandall\",History,3,4,,1,5,,,2";
		
		dc.parseDataLine(dataLine1);
		
		assertEquals(1, dc.students.get(5115).getRating("Grit", "History"));
		assertEquals(2, dc.students.get(5115).getRating("Optimism", "History"));
		assertEquals(-1, dc.students.get(5115).getRating("Optimism", "Science"));
		
		assertEquals(3, dc.students.get(5062).getRating("Grit", "History"));
		assertEquals(5, dc.students.get(5062).getRating("Optimism", "History"));
		assertEquals(-1, dc.students.get(5062).getRating("Optimism", "Science"));
		
		assertEquals(4, dc.students.get(5063).getRating("Grit", "History"));
		assertEquals(-1, dc.students.get(5063).getRating("Optimism", "History"));
		assertEquals(-1, dc.students.get(5063).getRating("Optimism", "Science"));

		String dataLine2 = "\"Shoemaker, Daniel Hecht\",Science,,,1,,,,2,";
		
		dc.parseDataLine(dataLine2);
		
		assertEquals(1, dc.students.get(5063).getRating("Grit", "Science"));
		assertEquals(2, dc.students.get(5063).getRating("Optimism", "Science"));
	}
	
	@Test
	public void testParseData() {
		String line1 = "Teacher,Student";
		String line2 = "\"Shoemaker, Kimberly Crandall\",\"Student1,5115   Abdullah-Tucker, Ahmad\"";
		String line3 = "\"Shoemaker, Kimberly Crandall\",\"Student2,5062   Amaya, Tatiana\"";
		String line4 = "\"Shoemaker, Kimberly Crandall\",\"Student3,5063   Artis, Makeda\"";
		String line5 = "\"Shoemaker, Daniel Hecht\",\"Student62,5063   Artis, Makeda\"";
		
		String[] rostersText = {line1, line2, line3, line4, line5};
		
		DataConverter dc = new DataConverter();
		dc.parseRosters(rostersText);
		
		dc.classIDMap = new int[8];
		dc.classIDMap[0] = 2;
		dc.classIDMap[1] = 3;
		dc.classIDMap[2] = 62;
		dc.classIDMap[3] = 1;
		dc.classIDMap[4] = 2;
		dc.classIDMap[5] = 3;
		dc.classIDMap[6] = 62;
		dc.classIDMap[7] = 1;
		
		dc.habitMap = new String[8];
		dc.habitMap[0] = "Grit";
		dc.habitMap[1] = "Grit";
		dc.habitMap[2] = "Grit";
		dc.habitMap[3] = "Grit";
		dc.habitMap[4] = "Optimism";
		dc.habitMap[5] = "Optimism";
		dc.habitMap[6] = "Optimism";
		dc.habitMap[7] = "Optimism";
		
		Teacher t1 = dc.teachers.get("Shoemaker, Kimberly Crandall");
		t1.subject = "History";
		
		String[] data = {"3", "4", "", "1", "5", "", "", "2"};
		
		dc.parseData(data, t1);
		
		assertEquals(1, dc.students.get(5115).getRating("Grit", "History"));
		assertEquals(2, dc.students.get(5115).getRating("Optimism", "History"));
		assertEquals(-1, dc.students.get(5115).getRating("Optimism", "Science"));
		
		assertEquals(3, dc.students.get(5062).getRating("Grit", "History"));
		assertEquals(5, dc.students.get(5062).getRating("Optimism", "History"));
		assertEquals(-1, dc.students.get(5062).getRating("Optimism", "Science"));
		
		assertEquals(4, dc.students.get(5063).getRating("Grit", "History"));
		assertEquals(-1, dc.students.get(5063).getRating("Optimism", "History"));
		assertEquals(-1, dc.students.get(5063).getRating("Optimism", "Science"));
	}
	
	@Test
	public void testGenerateHeader(){
		DataConverter dc = new DataConverter();
		
		String[] habitArr = {"Grit", "Gratitude"};
		String[] subjArr = {"Science", "History", "Math"};
		
		String expected = "Student ID,Grit-Science,Grit-History,Grit-Math,"
							+ "Gratitude-Science,Gratitude-History,Gratitude-Math";
		
		assertEquals(expected, dc.generateHeader(habitArr, subjArr));
	}
	
	@Test
	public void testGenerateStudentLine(){
		DataConverter dc = new DataConverter();
		
		String[] habitArr = {"Grit", "Gratitude"};
		String[] subjArr = {"Science", "History", "Math"};
		
		Student s1 = new Student(1234);
		s1.setRating("Grit", "Science", 3);
		s1.setRating("Grit", "History", 4);
		s1.setRating("Grit", "Math", 2);
		s1.setRating("Gratitude", "Math", 1);
		
		String expected = "1234,3,4,2,,,1";
		
		assertEquals(expected, dc.generateStudentLine(s1, habitArr, subjArr));
	}
	
	@Test
	public void testGenerateOutput(){
		DataConverter dc = new DataConverter();
		
		dc.habits.add("Gratitude");
		dc.habits.add("Grit");
		
		dc.subjects.add("Science");
		dc.subjects.add("History");
		dc.subjects.add("Math");
		
		String header = "Student ID,Gratitude-Science,Gratitude-History,Gratitude-Math,"
				+ "Grit-Science,Grit-History,Grit-Math";
		
		Student s1 = new Student(1234);
		s1.setRating("Grit", "Science", 3);
		s1.setRating("Grit", "History", 4);
		s1.setRating("Grit", "Math", 2);
		s1.setRating("Gratitude", "Math", 1);
		
		String line1 = "1234,,,1,3,4,2";
		
		dc.students.put(1234, s1);
		
		Student s2 = new Student(2345);
		s2.setRating("Grit", "History", 5);
		s2.setRating("Grit", "Science", 5);
		s2.setRating("Gratitude", "Math", 5);
		s2.setRating("Gratitude", "Science", 5);
		
		String line2 = "2345,5,,5,5,5,";
		
		dc.students.put(2345, s2);
		
		String[] expected = {header, line1, line2};
		
		assertArrayEquals(expected, dc.generateOutput());
	}
}
