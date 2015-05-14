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
		
		assertEquals(5, dc.classIDMap.length);
		assertEquals(5, dc.habitMap.length);
		
		String[] expectedHabits = {null, null, "Grit", "Self-Control Work", "Self-Control Interpersonal"};
		int[] expectedClassIDs = {0, 0, 1, 8, 16};
		
		assertArrayEquals(expectedHabits, dc.habitMap);
		assertArrayEquals(expectedClassIDs, dc.classIDMap);
	}
	
	@Test
	public void testParseDataLine() {
		//TODO Implement parseDataLine test
		fail("Not yet implemented");
	}
	
	@Test
	public void testParseData() {
		//TODO Implement parseData test
		fail("Not yet implemented");
	}
}
