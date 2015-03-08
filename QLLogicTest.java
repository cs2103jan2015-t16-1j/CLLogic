import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

public class QLLogicTest {
	
	/* Messages for feedback */
	private static final String MESSAGE_NO_TASK_SATISFY_CRITERIA = "No task satisfies criteria entered.";
	private static final String MESSAGE_INVALID_PRIORITY_LEVEL = "Invalid priority level.";
	private static final String MESSAGE_INVALID_MONTH = "Invalid month entered.";
	private static final String MESSAGE_INVALID_DAY = "Invalid day entered.";
	private static final String MESSAGE_INVALID_DATE_FORMAT = "Invalid date format entered.";
	
	@Before
	public void setup() {
		QLLogic.setupStub();
	}

	@Test
	public void testExecuteCommand() {
		
		/** Add **/
		StringBuilder feedback = new StringBuilder();
		LinkedList<Task> testList; 
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		
		testList = QLLogic.executeCommand("add task one -p L   -d 2202 ", feedback);
		assertEquals(feedback.toString(), "");
		assertEquals(testList.peekLast().getName(), "task one");
		assertEquals(testList.peekLast().getPriority(), "L");
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
		assertEquals(feedback.toString(), "Invalid task name entered. Nothing is executed.");
		assertEquals(testList.peekLast().getName(), "task one");
		feedback.setLength(0);
		
		testList = QLLogic.executeCommand("add task two -d 32111990", feedback);
		assertEquals(feedback.toString(), MESSAGE_INVALID_DAY);
		assertEquals(testList.peekLast().getName(), "task two");
		feedback.setLength(0);
		
		testList = QLLogic.executeCommand("add task two -d 31131990", feedback);
		assertEquals(feedback.toString(), MESSAGE_INVALID_MONTH);
		feedback.setLength(0);
		
		testList = QLLogic.executeCommand("add task two -d 32131990", feedback);
		assertEquals(feedback.toString(), MESSAGE_INVALID_DAY);
		feedback.setLength(0);
		
		testList = QLLogic.executeCommand("add task 3 -d", feedback);
		assertEquals(feedback.toString(), "Invalid date format entered.");
		assertEquals(testList.peekLast().getName(), "task 3");
		feedback.setLength(0);
		
		testList = QLLogic.executeCommand("add task 4 -p", feedback);
		assertEquals(feedback.toString(), MESSAGE_INVALID_PRIORITY_LEVEL);
		assertEquals(testList.peekLast().getName(), "task 4");
		feedback.setLength(0);
		
		testList = QLLogic.executeCommand("add task 1 -p P", feedback);
		assertEquals(feedback.toString(), MESSAGE_INVALID_PRIORITY_LEVEL);
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
		
		/** Edit **/
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 2202", feedback);
		QLLogic.executeCommand("add task two -p M -d 2302", feedback);
		QLLogic.executeCommand("add task three -d 24022016 -p H", feedback);
		
		testList = QLLogic.executeCommand("edit 1 -n task 1", feedback);
		assertEquals(testList.get(0).getName(), "task 1");
		testList = QLLogic.executeCommand("edit 2 -pL", feedback);
		assertEquals(testList.get(1).getPriority(), "L");
		testList = QLLogic.executeCommand("edit 3 -d  24022015", feedback);
		assertEquals(sdf.format(testList.get(2).getDueDate().getTime()), "24.02.2015");
		
		testList = QLLogic.executeCommand("edit 1 -p H -d 2302 -n task one", feedback);
		assertEquals(testList.get(0).getName(), "task one");
		assertEquals(testList.get(0).getPriority(), "H");
		assertEquals(sdf.format(testList.get(0).getDueDate().getTime()), "23.02.2015");
		
		feedback.setLength(0);
		testList = QLLogic.executeCommand("edit 1 -e H -d 2202 -q task one", feedback);
		assertEquals(sdf.format(testList.get(0).getDueDate().getTime()), "22.02.2015");
		assertEquals(feedback.toString(), "Invalid field type \"e\".\nInvalid field type \"q\".\n");
		
		feedback.setLength(0);
		testList = QLLogic.executeCommand("edit 1 -d 12341990 -n task 1", feedback);
		assertEquals(testList.get(0).getName(), "task 1");
		assertEquals(feedback.toString(), MESSAGE_INVALID_MONTH);
		
		feedback.setLength(0);
		testList = QLLogic.executeCommand("edit 1 -d 1234199", feedback);
		assertEquals(feedback.toString(), "Invalid date format entered.");
		
		feedback.setLength(0);
		testList = QLLogic.executeCommand("edit 1 -n ", feedback);
		assertEquals(feedback.toString(), "Invalid task name entered. Nothing is executed.");
		
		/** Delete **/
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 2202", feedback);
		QLLogic.executeCommand("add task two -p M -d 2302", feedback);
		QLLogic.executeCommand("add task three -d 24022016 -p H", feedback);
		
		testList = QLLogic.executeCommand("delete 2", feedback);
		assertEquals(testList.get(1).getName(), "task three");
		
		feedback.setLength(0);
		testList = QLLogic.executeCommand("d 4 ", feedback);
		assertEquals(feedback.toString(), "Task number entered out of range. Nothing is executed.");
		
		feedback.setLength(0);
		testList = QLLogic.executeCommand("d  ", feedback);
		assertEquals(feedback.toString(), "Invalid task number entered. Nothing is executed.");
		
		/** Completed **/
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 2202", feedback);
		QLLogic.executeCommand("add task two -p M -d 2302", feedback);
		QLLogic.executeCommand("add task three -d 24022016 -p H", feedback);
		
		testList = QLLogic.executeCommand("c 2", feedback);
		assertTrue(testList.get(1).getIsCompleted());
		
		testList = QLLogic.executeCommand("c 2", feedback);
		assertFalse(testList.get(1).getIsCompleted());
		
		feedback.setLength(0);
		testList = QLLogic.executeCommand("c 4 ", feedback);
		assertEquals(feedback.toString(), "Task number entered out of range. Nothing is executed.");
		
		feedback.setLength(0);
		testList = QLLogic.executeCommand("complete  ", feedback);
		assertEquals(feedback.toString(), "Invalid task number entered. Nothing is executed.");
		
		/** List **/
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 0102", feedback);
		QLLogic.executeCommand("add task two  -d 2302 -p M", feedback);
		QLLogic.executeCommand("add task three -d 2402 -p H", feedback);
		
		feedback.setLength(0);
		testList = QLLogic.executeCommand("find -d 2302", feedback);
		assertEquals(testList.peek().getName(), "task two");
		
		testList = QLLogic.executeCommand("find -d TDY", feedback);
		assertEquals(testList.peek().getName(), "task two");
		
		feedback.setLength(0);
		testList = QLLogic.executeCommand("find -d TMR", feedback);
		assertEquals(feedback.toString(), "No matches found for criteria entered.");
		
		feedback.setLength(0);
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 0102", feedback);
		QLLogic.executeCommand("add task two  -d 2302 -p M", feedback);
		QLLogic.executeCommand("add task three -d 2402 -p H", feedback);
		testList = QLLogic.executeCommand("find -d 2302:2402", feedback);
		assertEquals(testList.get(0).getName(), "task two");
		assertEquals(testList.get(1).getName(), "task three");
		
		feedback.setLength(0);
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 0102", feedback);
		QLLogic.executeCommand("add task two  -d 2302 -p M", feedback);
		QLLogic.executeCommand("add task three -d 2402 -p H", feedback);
		testList = QLLogic.executeCommand("find -d TDY:TMR", feedback);
		assertEquals(testList.get(0).getName(), "task one");
		assertEquals(testList.get(1).getName(), "task two");
		
		feedback.setLength(0);
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 0102", feedback);
		QLLogic.executeCommand("add task two  -d 2302 -p M", feedback);
		QLLogic.executeCommand("add task three -d 2402 -p H", feedback);
		testList = QLLogic.executeCommand("find -d 0101:TDY", feedback);
		assertEquals(testList.get(0).getName(), "task one");
		assertEquals(testList.get(1).getName(), "task two");
		
		feedback.setLength(0);
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 0102", feedback);
		QLLogic.executeCommand("add task two  -d 2302 -p M", feedback);
		QLLogic.executeCommand("add task three -d 2402 -p H", feedback);
		testList = QLLogic.executeCommand("find -d 0202:TDY", feedback);
		assertEquals(testList.get(0).getName(), "task two");
		
		feedback.setLength(0);
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 0102", feedback);
		QLLogic.executeCommand("add task two  -d TDY -p M", feedback);
		QLLogic.executeCommand("add task three -d 2402 -p H", feedback);
		testList = QLLogic.executeCommand("find -d TDY", feedback);
		assertEquals(testList.get(0).getName(), "task two");
		
		feedback.setLength(0);
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 0102", feedback);
		QLLogic.executeCommand("add task two  -d 0202 -p M", feedback);
		QLLogic.executeCommand("add task three -d 0902 -p H", feedback);
		testList = QLLogic.executeCommand("find -d 0902:TDY", feedback);
		assertEquals(testList.get(0).getName(), "task three");
		
		feedback.setLength(0);
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 0102", feedback);
		QLLogic.executeCommand("add task two  -d 0202 -p M", feedback);
		QLLogic.executeCommand("add task three -d 0902 -p H", feedback);
		testList = QLLogic.executeCommand("find -d 0102:0902", feedback);
		assertEquals(testList.get(0).getName(), "task one");
		assertEquals(testList.get(1).getName(), "task two");
		assertEquals(testList.get(2).getName(), "task three");
		
		feedback.setLength(0);
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 0102", feedback);
		QLLogic.executeCommand("add task two  -d 2302 -p M", feedback);
		QLLogic.executeCommand("add task three -d 2402 -p H", feedback);
		testList = QLLogic.executeCommand("find -d 3201:TDY", feedback);
		assertEquals(testList.get(0).getName(), "task one");
		assertEquals(feedback.toString(), MESSAGE_INVALID_DAY);
		
		feedback.setLength(0);
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 0102", feedback);
		testList = QLLogic.executeCommand("find -d 2301:2214", feedback);
		assertEquals(testList.get(0).getName(), "task one");
		assertEquals(feedback.toString(), MESSAGE_INVALID_MONTH);
		
		feedback.setLength(0);
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 0102", feedback);
		testList = QLLogic.executeCommand("find -d 321:123", feedback);
		assertEquals(testList.get(0).getName(), "task one");
		assertEquals(feedback.toString(), MESSAGE_INVALID_DATE_FORMAT);
		
		feedback.setLength(0);
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 0102", feedback);
		QLLogic.executeCommand("add task two  -d 0502 -p M", feedback);
		QLLogic.executeCommand("add task three -d 0902 -p H", feedback);
		testList = QLLogic.executeCommand("find -d 0102:0602 -p M", feedback);
		assertEquals(feedback.toString(), "");
		assertEquals(testList.get(0).getName(), "task two");
		
		feedback.setLength(0);
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 0102", feedback);
		QLLogic.executeCommand("add task two  -d 0502 -p M", feedback);
		QLLogic.executeCommand("add task three -d 0902 -p H", feedback);
		QLLogic.executeCommand("add task four -d 0702 -p M", feedback);
		testList = QLLogic.executeCommand("find -d 0102:0802 -p M", feedback);
		assertEquals(feedback.toString(), "");
		assertEquals(testList.get(0).getName(), "task two");
		assertEquals(testList.get(1).getName(), "task four");
		
		feedback.setLength(0);
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 0102", feedback);
		QLLogic.executeCommand("add task two  -d 0502 -p M", feedback);
		QLLogic.executeCommand("add task three -d 0902 -p H", feedback);
		QLLogic.executeCommand("add task four -d 0702 -p M", feedback);
		testList = QLLogic.executeCommand("find -d 0102:0802 -p M", feedback);
		assertEquals(feedback.toString(), "");
		assertEquals(testList.get(0).getName(), "task two");
		assertEquals(testList.get(1).getName(), "task four");
		
		feedback.setLength(0);
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 0102", feedback);
		testList = QLLogic.executeCommand("find -p G", feedback);
		assertEquals(feedback.toString(), MESSAGE_INVALID_PRIORITY_LEVEL);
		
		feedback.setLength(0);
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 0102", feedback);
		QLLogic.executeCommand("add task two  -d 0502 -p M", feedback);
		QLLogic.executeCommand("add task three -d 0902 -p H", feedback);
		QLLogic.executeCommand("add task four -d 0702 -p M", feedback);
		testList = QLLogic.executeCommand("find -c Y", feedback);
		assertEquals(feedback.toString(), "No matches found for criteria entered.");
		
		feedback.setLength(0);
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 0102", feedback);
		QLLogic.executeCommand("add task two  -d 0502 -p M", feedback);
		QLLogic.executeCommand("add task three -d 0902 -p H", feedback);
		QLLogic.executeCommand("add task four -d 0702 -p M", feedback);
		QLLogic.executeCommand("c 3", feedback);
		testList = QLLogic.executeCommand("find -c Y", feedback);
		assertEquals(testList.get(0).getName(), "task three");
		
		feedback.setLength(0);
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 0102", feedback);
		QLLogic.executeCommand("add task two  -d 0502 -p M", feedback);
		QLLogic.executeCommand("add task three -d 0902 -p H", feedback);
		QLLogic.executeCommand("add task four -d 0702 -p M", feedback);
		testList = QLLogic.executeCommand("find -o N", feedback);
		assertEquals(feedback.toString(), "No matches found for criteria entered.");
		
		feedback.setLength(0);
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 0102", feedback);
		QLLogic.executeCommand("add task two  -d 2803 -p M", feedback);
		QLLogic.executeCommand("add task three -d 2503 -p H", feedback);
		QLLogic.executeCommand("add task four -d 0902 -p M", feedback);
		testList = QLLogic.executeCommand("find -o Y", feedback);
		assertEquals(testList.get(0).getName(), "task one");
		assertEquals(testList.get(1).getName(), "task four");
		assertEquals(feedback.toString(), "");
		
		feedback.setLength(0);
		QLLogic.clearWorkingList(); 
		testList = QLLogic.executeCommand("find -l Y", feedback);
		assertEquals(feedback.toString(), "Invalid field type \"l\".\n" + "No matches found for criteria entered.");
		
		feedback.setLength(0);
		QLLogic.clearWorkingList(); 
		QLLogic.executeCommand("add task one -p L -d 0102", feedback);
		QLLogic.executeCommand("add task two  -d 2802 -p M", feedback);
		QLLogic.executeCommand("add task three -d 2502 -p H", feedback);
		QLLogic.executeCommand("add task four -d 0902 -p M", feedback);
		testList = QLLogic.executeCommand("find -d 2502", feedback);
		assertEquals(testList.get(0).getName(), "task three");
		testList = QLLogic.executeCommand("find", feedback);
		assertEquals(feedback.toString(), "No matches found for criteria entered.");
		
	}
	
}
