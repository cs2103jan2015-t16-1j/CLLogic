import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import org.junit.Before;
import org.junit.Test;

public class QLLogicTest {

	private static StringBuilder _feedback;
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
		QLLogic.executeCommand(" ", _feedback);
		assertEquals("Please enter a command. ", _feedback.toString());
		_feedback.setLength(0);

		/** 'Invalid action' partition **/
		QLLogic.executeCommand("adds one", _feedback);
		assertEquals("Invalid action type. ", _feedback.toString());
		_feedback.setLength(0);

		/** Add action **/
		/* Valid name 'no field' partition */
		QLLogic.executeCommand("add one", _feedback);
		assertEquals("Task: \"one\" added. ", _feedback.toString());
		assertEquals("one", QLLogic.getDisplayList().getFirst().getName());
		_feedback.setLength(0);

		/* Valid name 'one valid field' partition */
		QLLogic.executeCommand("add two -s 0304", _feedback);
		assertEquals("Task: \"two\" added. Start date set to 03/04/2015. ",
				_feedback.toString());
		assertEquals("two", QLLogic.getDisplayList().get(1).getName());
		assertEquals("03/04/2015", QLLogic.getDisplayList().get(1).getStartDateString());
		_feedback.setLength(0);

		/* Valid name 'many valid fields' partition */
		QLLogic.executeCommand("add three -s 0405 -d 0506 -p L",
				_feedback);
		assertEquals(
				"Task: \"three\" added. Start date set to 04/05/2015. Due date set to 05/06/2015. Priority set to \"L\". ",
				_feedback.toString());
		assertEquals("three", QLLogic.getDisplayList().get(2).getName());
		assertEquals("04/05/2015", QLLogic.getDisplayList().get(2).getStartDateString());
		assertEquals("05/06/2015", QLLogic.getDisplayList().get(2).getDueDateString());
		assertEquals("L", QLLogic.getDisplayList().get(2).getPriority());
		_feedback.setLength(0);

		/* Valid name 'one invalid field type' partition */
		QLLogic.executeCommand("add four -a 0405", _feedback);
		assertEquals("Invalid field type \"a\". Task: \"four\" added. ",
				_feedback.toString());
		assertEquals("four", QLLogic.getDisplayList().get(3).getName());
		_feedback.setLength(0);
		
		/* Valid name 'many invalid field type' partition */
		QLLogic.executeCommand("add four -a 0405 -i L", _feedback);
		assertEquals("Invalid field type \"a\". Invalid field type \"i\". Task: \"four\" added. ",
				_feedback.toString());
		assertEquals("four", QLLogic.getDisplayList().get(4).getName());
		_feedback.setLength(0);

		/* No name */
		QLLogic.executeCommand("add ", _feedback);
		assertEquals("No task name entered. Nothing is added. ",
				_feedback.toString());
		assertEquals(QLLogic.getDisplayList().size(), 5);
		_feedback.setLength(0);

		/* Multiple words name partition */
		QLLogic.executeCommand("add task five", _feedback);
		assertEquals("Task: \"task five\" added. ", _feedback.toString());
		assertEquals("task five", QLLogic.getDisplayList().get(5).getName());
		_feedback.setLength(0);

		/** Edit Action **/
		/* Valid priority */
		QLLogic.executeCommand("e 1 -p L", _feedback);
		assertEquals("Priority set to \"L\". ", _feedback.toString());
		assertEquals("L", QLLogic.getDisplayList().getFirst().getPriority());
		_feedback.setLength(0);
		
		QLLogic.executeCommand("e 1 -p clr", _feedback);
		assertEquals("Priority cleared. ", _feedback.toString());
		assertNull(QLLogic.getDisplayList().getFirst().getPriority());
		_feedback.setLength(0);
		
		/* Invalid priority content */
		QLLogic.executeCommand("e 1 -p HIGH", _feedback);
		assertEquals("Invalid priority level \"HIGH\". ", _feedback.toString());
		_feedback.setLength(0);
		
		/* Valid start date */
		QLLogic.executeCommand("e 1 -s 0201", _feedback);
		assertEquals("Start date set to 02/01/2015. ", _feedback.toString());
		assertEquals("02/01/2015", QLLogic.getDisplayList().getFirst().getStartDateString());
		_feedback.setLength(0);
		
		QLLogic.executeCommand("e 1 -s clr", _feedback);
		assertEquals("Start date cleared. ", _feedback.toString());
		assertNull(QLLogic.getDisplayList().getFirst().getStartDate());
		_feedback.setLength(0);
		
		/* Valid start date */
		QLLogic.executeCommand("e 1 -d 0301", _feedback);
		assertEquals("Due date set to 03/01/2015. ", _feedback.toString());
		assertEquals("03/01/2015", QLLogic.getDisplayList().getFirst().getDueDateString());
		_feedback.setLength(0);
		
		QLLogic.executeCommand("e 1 -d clr", _feedback);
		assertEquals("Due date cleared. ", _feedback.toString());
		assertNull(QLLogic.getDisplayList().getFirst().getDueDate());
		_feedback.setLength(0);
		
		/* Invalid date*/
		/* 'invalid format' boundary value */
		QLLogic.executeCommand("e 1 -s 02011", _feedback);
		assertEquals("Invalid date format entered. ", _feedback.toString());
		_feedback.setLength(0);
		/* 'invalid day' boundary value */
		QLLogic.executeCommand("e 1 -s 3201", _feedback);
		assertEquals("Invalid day entered. ", _feedback.toString());
		_feedback.setLength(0);
		/* 'invalid month' boundary value */
		QLLogic.executeCommand("e 1 -s 3013", _feedback);
		assertEquals("Invalid month entered. ", _feedback.toString());
		assertEquals("no start date", QLLogic.getDisplayList().getFirst().getStartDateString());
		_feedback.setLength(0);
		
	}
}
