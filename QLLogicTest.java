import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

public class QLLogicTest {

	private static StringBuilder _feedback;
	private static LinkedList<Task> _testList;
	SimpleDateFormat _sdf;

	@Before
	public void setup() {
		_feedback = new StringBuilder();
		_sdf = new SimpleDateFormat("dd/MM/yyyy");
		QLLogic.setupStub();
	}

	@Test
	public void testExecuteCommand() {

		/** 'No command' partition **/
		_testList = QLLogic.executeCommand(" ", _feedback);
		assertEquals("Please enter a command. ", _feedback.toString());
		_feedback.setLength(0);

		/** 'Invalid action' partition **/
		_testList = QLLogic.executeCommand("adds one", _feedback);
		assertEquals("Invalid action type. ", _feedback.toString());
		_feedback.setLength(0);

		/** Add action **/
		/* Valid name 'no field' partition */
		_testList = QLLogic.executeCommand("add one", _feedback);
		assertEquals("Task: \"one\" added. ", _feedback.toString());
		assertEquals("one", _testList.getFirst().getName());
		_feedback.setLength(0);

		/* Valid name 'one valid field' partition */
		_testList = QLLogic.executeCommand("add two -s 0304", _feedback);
		assertEquals("Task: \"two\" added. Start date set to 03/04/2015. ",
				_feedback.toString());
		assertEquals("two", _testList.get(1).getName());
		assertEquals("03/04/2015", _testList.get(1).getStartDateString());
		_feedback.setLength(0);

		/* Valid name 'many valid fields' partition */
		_testList = QLLogic.executeCommand("add three -s 0405 -d 0506 -p L",
				_feedback);
		assertEquals(
				"Task: \"three\" added. Start date set to 04/05/2015. Due date set to 05/06/2015. Priority set to \"L\". ",
				_feedback.toString());
		assertEquals("three", _testList.get(2).getName());
		assertEquals("04/05/2015", _testList.get(2).getStartDateString());
		assertEquals("05/06/2015", _testList.get(2).getDueDateString());
		assertEquals("L", _testList.get(2).getPriority());
		_feedback.setLength(0);

		/* Valid name 'one invalid field type' partition */
		_testList = QLLogic.executeCommand("add four -a 0405", _feedback);
		assertEquals("Invalid field type \"a\". Task: \"four\" added. ",
				_feedback.toString());
		assertEquals("four", _testList.get(3).getName());
		_feedback.setLength(0);
		
		/* Valid name 'many invalid field type' partition */
		_testList = QLLogic.executeCommand("add four -a 0405 -i L", _feedback);
		assertEquals("Invalid field type \"a\". Invalid field type \"i\". Task: \"four\" added. ",
				_feedback.toString());
		assertEquals("four", _testList.get(4).getName());
		_feedback.setLength(0);

		/* No name */
		_testList = QLLogic.executeCommand("add ", _feedback);
		assertEquals("No task name entered. Nothing is added. ",
				_feedback.toString());
		assertEquals(_testList.size(), 5);
		_feedback.setLength(0);

		/* Multiple words name partition */
		_testList = QLLogic.executeCommand("add task five", _feedback);
		assertEquals("Task: \"task five\" added. ", _feedback.toString());
		assertEquals("task five", _testList.get(5).getName());
		_feedback.setLength(0);

		/** Edit Action **/
		/* Valid priority */
		_testList = QLLogic.executeCommand("e 1 -p L", _feedback);
		assertEquals("Priority set to \"L\". ", _feedback.toString());
		assertEquals("L", _testList.getFirst().getPriority());
		_feedback.setLength(0);
		
		_testList = QLLogic.executeCommand("e 1 -p clr", _feedback);
		assertEquals("Priority cleared. ", _feedback.toString());
		assertNull(_testList.getFirst().getPriority());
		_feedback.setLength(0);
		
		/* Invalid priority content */
		_testList = QLLogic.executeCommand("e 1 -p HIGH", _feedback);
		assertEquals("Invalid priority level \"HIGH\". ", _feedback.toString());
		_feedback.setLength(0);
		
		/* Valid start date */
		_testList = QLLogic.executeCommand("e 1 -s 0201", _feedback);
		assertEquals("Start date set to 02/01/2015. ", _feedback.toString());
		assertEquals("02/01/2015", _testList.getFirst().getStartDateString());
		_feedback.setLength(0);
		
		_testList = QLLogic.executeCommand("e 1 -s clr", _feedback);
		assertEquals("Start date cleared. ", _feedback.toString());
		assertNull(_testList.getFirst().getStartDate());
		_feedback.setLength(0);
		
		/* Valid start date */
		_testList = QLLogic.executeCommand("e 1 -d 0301", _feedback);
		assertEquals("Due date set to 03/01/2015. ", _feedback.toString());
		assertEquals("03/01/2015", _testList.getFirst().getDueDateString());
		_feedback.setLength(0);
		
		_testList = QLLogic.executeCommand("e 1 -d clr", _feedback);
		assertEquals("Due date cleared. ", _feedback.toString());
		assertNull(_testList.getFirst().getDueDate());
		_feedback.setLength(0);
		
		/* Invalid date*/
		/* 'invalid format' boundary value */
		_testList = QLLogic.executeCommand("e 1 -s 02011", _feedback);
		assertEquals("Invalid date format entered. ", _feedback.toString());
		_feedback.setLength(0);
		/* 'invalid day' boundary value */
		_testList = QLLogic.executeCommand("e 1 -s 3201", _feedback);
		assertEquals("Invalid day entered. ", _feedback.toString());
		_feedback.setLength(0);
		/* 'invalid month' boundary value */
		_testList = QLLogic.executeCommand("e 1 -s 3013", _feedback);
		assertEquals("Invalid month entered. ", _feedback.toString());
		assertEquals("no start date", _testList.getFirst().getStartDateString());
		_feedback.setLength(0);
		
	}
}
