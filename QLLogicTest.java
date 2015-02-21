import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;


public class QLLogicTest {

	@Before
	public void setup() {
		QLLogic._workingList = new LinkedList<Task>();
	}

	@Test
	public void testExecuteCommand() {
		
		// Add command
		StringBuilder feedback = new StringBuilder();
		LinkedList<Task> testList; 
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		
		testList = QLLogic.executeCommand("add task one -p L   -d 2202 ", feedback);
		assertEquals(feedback.toString(), "");
		assertEquals(testList.peekLast().getName(), "task one");
		assertEquals(testList.peekLast().getPriority(), 'L');
		assertEquals(sdf.format(testList.peekLast().getDueDate().getTime()), "22.02.2015");
		
		testList = QLLogic.executeCommand("add task one -d 0202", feedback);
		assertEquals(feedback.toString(), "");
		assertEquals(testList.peekLast().getName(), "task one");
		assertEquals(sdf.format(testList.peekLast().getDueDate().getTime()), "02.02.2015");
		
		testList = QLLogic.executeCommand("add task one -d 01111990", feedback);
		assertEquals(feedback.toString(), "");
		assertEquals(testList.peekLast().getName(), "task one");
		assertEquals(sdf.format(testList.peekLast().getDueDate().getTime()), "01.11.1990");
		
		testList = QLLogic.executeCommand("add", feedback);
		assertEquals(feedback.toString(), "Invalid task name entered. No task is added.");
		assertEquals(testList.peekLast().getName(), "task one");
		feedback.setLength(0);
		
		testList = QLLogic.executeCommand("add task two -d 32111990", feedback);
		assertEquals(feedback.toString(), "Invalid day entered. Date not set. Please set due/start date using EDIT.");
		assertEquals(testList.peekLast().getName(), "task two");
		feedback.setLength(0);
		
		testList = QLLogic.executeCommand("add task two -d 31131990", feedback);
		assertEquals(feedback.toString(), "Invalid month entered. Date not set. Please set due/start date using EDIT.");
		feedback.setLength(0);
		
		testList = QLLogic.executeCommand("add task two -d 32131990", feedback);
		assertEquals(feedback.toString(), "Invalid day entered. Date not set. Please set due/start date using EDIT.");
		feedback.setLength(0);
		
		testList = QLLogic.executeCommand("add task 3 -d", feedback);
		assertEquals(feedback.toString(), "Invalid date format entered. Date not set. Please set due/start date using EDIT.");
		assertEquals(testList.peekLast().getName(), "task 3");
		feedback.setLength(0);
		
		testList = QLLogic.executeCommand("add task 4 -p", feedback);
		assertEquals(feedback.toString(), "Invalid priority level. Priority level not set. Please set priority level using EDIT.");
		assertEquals(testList.peekLast().getName(), "task 4");
		feedback.setLength(0);
		
		testList = QLLogic.executeCommand("add task 1 -p P", feedback);
		assertEquals(feedback.toString(), "Invalid priority level. Priority level not set. Please set priority level using EDIT.");
		assertEquals(testList.peekLast().getName(), "task 1");
		feedback.setLength(0);
		
		testList = QLLogic.executeCommand("add task 5 -q 32131990", feedback);
		assertEquals(feedback.toString(), "Invalid field type \"q\".\n");
		assertEquals(testList.peekLast().getName(), "task 5");
		feedback.setLength(0);
		
		testList = QLLogic.executeCommand("add task 6 -q 32131990 -g H", feedback);
		assertEquals(feedback.toString(), "Invalid field type \"q\".\nInvalid field type \"g\".\n");
		assertEquals(testList.peekLast().getName(), "task 6");
		feedback.setLength(0);
		
		
		
		
	}

}
