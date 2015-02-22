import java.text.SimpleDateFormat;
import java.util.LinkedList;

public class QLLogic {

	private static final String COMMAND_COMPLETE_ABBREV = "c";
	private static final String COMMAND_COMPLETE = "complete";
	private static final String MESSAGE_INVALID_PRIORITY_LEVEL = "Invalid priority level. Priority level not set. Please set priority level using EDIT.";
	private static final String MESSAGE_INVALID_FIELD_TYPE = "Invalid field type \"%1$s\".";
	private static final String MESSAGE_INVALID_COMMAND = "Invalid command. No command executed.";
	private static final String MESSAGE_INVALID_TASK_NAME = "Invalid task name entered. Nothing is executed.";
	private static final String MESSAGE_INVALID_MONTH = "Invalid month entered. Date not set. Please set due/start date using EDIT.";
	private static final String MESSAGE_INVALID_DAY = "Invalid day entered. Date not set. Please set due/start date using EDIT.";
	private static final String MESSAGE_INVALID_DATE_FORMAT = "Invalid date format entered. Date not set. Please set due/start date using EDIT.";
	private static final String MESSAGE_TASK_NUMBER_OUT_OF_RANGE = "Task number entered out of range. Nothing is executed.";
	private static final String MESSAGE_INVALID_TASK_NUMBER = "Invalid task number entered. Nothing is executed.";
	
	private static final int INDEX_COMMAND = 0;
	private static final int INDEX_FIELDS = 1;
	private static final int INDEX_FIELD_CONTENT_START = 1;
	private static final int INDEX_FIELD_TYPE = 0;
	private static final int INDEX_PRIORITY_LEVEL = 0;
	
	private static final int NUM_SPLIT_TWO = 2;
	private static final int NUM_INVALID_TASK_NUMBER = -1;
	
	private static final int OFFSET_TASK_NUMBER_TO_INDEX = -1;
	
	private static final String COMMAND_ADD_ABBREV = "a";
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_EDIT_ABBREV = "e";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_DELETE_ABBREV = "d";
	private static final String COMMAND_DELETE = "delete";
	
	private static final String STRING_NO_CHAR = "";
	private static final String STRING_BLANK_SPACE = " ";
	private static final String STRING_DASH = "-";

	public static LinkedList<Task> _workingList;	//TODO change back to private
	private static String _fileName;
	
	/** General methods **/
	public static void setup(String fileName) {
		_fileName = fileName; 
		_workingList = QLStorage.loadFile(fileName);
	}

	public static LinkedList<Task> executeCommand(String instruction, StringBuilder feedback) {
		String[] splittedInstruction = splitCommandAndFields(instruction);
		
		String command = splittedInstruction[INDEX_COMMAND].trim();
		String fieldLine = splittedInstruction[INDEX_FIELDS].trim();
				
		if(command.equalsIgnoreCase(COMMAND_ADD) || command.equalsIgnoreCase(COMMAND_ADD_ABBREV)) {
			return executeAdd(fieldLine, feedback);
		}
		else if(command.equalsIgnoreCase(COMMAND_EDIT) || command.equalsIgnoreCase(COMMAND_EDIT_ABBREV)) {
			return executeEdit(fieldLine, feedback);
		}
		else if(command.equalsIgnoreCase(COMMAND_DELETE) || command.equalsIgnoreCase(COMMAND_DELETE_ABBREV)) {
			return executeDelete(fieldLine, feedback);
		} 
		else if(command.equalsIgnoreCase(COMMAND_COMPLETE) || command.equalsIgnoreCase(COMMAND_COMPLETE_ABBREV)) {
			return executeComplete(fieldLine, feedback);
		}
		
		else {
			feedback.append(MESSAGE_INVALID_COMMAND);
			return null;
		}
	}

	//TODO change to private
	public static void clearWorkingList() {
		_workingList = new LinkedList<Task>();
	}

	/** Multi-command methods **/ 	
	private static String[] splitCommandAndFields(String instruction) {
		String[] splittedInstruction = instruction.split(STRING_BLANK_SPACE, NUM_SPLIT_TWO);
		if(splittedInstruction.length == 1) {
			String command = splittedInstruction[INDEX_COMMAND];
			splittedInstruction = new String[2];
			splittedInstruction[INDEX_COMMAND] = command;
			splittedInstruction[INDEX_FIELDS] = "";
		}
		return splittedInstruction;
	}
	
	private static LinkedList<String> processFieldLine(String fieldLine) {
		String[] fields_array = fieldLine.split(STRING_DASH);
		
		LinkedList<String> fields_linkedList = new LinkedList<String>();
		for(int i = 0; i < fields_array.length; i++) {
			String field = fields_array[i].trim();
			if(!field.equals(STRING_NO_CHAR)) {
				fields_linkedList.add(field);
			}
		}
		return fields_linkedList;
	}
		
	private static boolean isCorrectDateFormat(String dateString, StringBuilder feedback) {
		if(!(dateString.length() == 4 || dateString.length() == 8)) {
			feedback.append(MESSAGE_INVALID_DATE_FORMAT);
			return false;
		}
		
		try {
			int dateInt = Task.changeFromDateStringToDateInt(dateString);
			int day = Task.decodeDayFromDate(dateInt); 
			int month = Task.decodeMonthFromDate(dateInt);
			
			if(day > 31) {
				feedback.append(MESSAGE_INVALID_DAY);
				return false;
			}
			if(month > 12) {
				feedback.append(MESSAGE_INVALID_MONTH);
				return false;
			}

		} catch(NumberFormatException e) {
			feedback.append(MESSAGE_INVALID_DATE_FORMAT);
			return false;
		}
		return true;
	}

	private static int extractAndCheckTaskNumber(String fieldLineWithTaskNumber, StringBuilder feedback) {
		String taskNumberString = extractTaskNumberString(fieldLineWithTaskNumber);
		if(isValidTaskNumber(taskNumberString, feedback)) {
			return Integer.parseInt(taskNumberString);
		} 
		else {
			return NUM_INVALID_TASK_NUMBER;
		}
	}

	private static String extractTaskNumberString(String fieldLineWithTaskNumber) {
		int taskNumberEndIndex = fieldLineWithTaskNumber.length();
		for(int i = 0; i < fieldLineWithTaskNumber.length(); i++) {
			if(fieldLineWithTaskNumber.charAt(i) == ' ') {
				taskNumberEndIndex = i;
				break;
			}
		}
		return fieldLineWithTaskNumber.substring(0, taskNumberEndIndex).trim();
	}
	
	private static boolean isValidTaskNumber(String taskNumberString, StringBuilder feedback) {
		try {
			if(taskNumberString.equals(STRING_NO_CHAR)) {
				feedback.append(MESSAGE_INVALID_TASK_NUMBER);
				return false;
			} 
			

			if(Integer.parseInt(taskNumberString) > _workingList.size() || Integer.parseInt(taskNumberString) < 1) {
				feedback.append(MESSAGE_TASK_NUMBER_OUT_OF_RANGE);
				return false;
			}
			return true;
		} catch(NumberFormatException e) {
			feedback.append(MESSAGE_INVALID_TASK_NUMBER);
			return false;
		}
	}

	/** Update methods **/
	private static void updateField(String field, Task task, StringBuilder feedback) {
		if(field.equals(STRING_NO_CHAR)) {
			return;
		}
		char fieldType = field.charAt(INDEX_FIELD_TYPE);
		String fieldContent = field.substring(INDEX_FIELD_CONTENT_START).trim();
			
		switch(fieldType) {
		case 'd':		
			updateDueDate(task, feedback, fieldContent);
			break;
			
		case 'p':
			updatePriority(task, feedback, fieldContent);
			break;
				
		case 'n':
			updateName(task, feedback, fieldContent);
			break;
				
		default: 
			feedback.append(String.format(MESSAGE_INVALID_FIELD_TYPE, fieldType)).append("\n");
			break;
		}
	}
	
	private static void updateName(Task task, StringBuilder feedback, String fieldContent) {
		if(fieldContent.equals(STRING_NO_CHAR)) {
			feedback.append(MESSAGE_INVALID_TASK_NAME);
			return;
		}
		task.setName(fieldContent);
	}

	private static void updatePriority(Task task, StringBuilder feedback, String fieldContent) {
		if(fieldContent.equals(STRING_NO_CHAR)) {
			feedback.append(MESSAGE_INVALID_PRIORITY_LEVEL);
			return;
		}
		
		char priority = fieldContent.charAt(INDEX_PRIORITY_LEVEL);
		if(priority == 'L' || priority == 'M' || priority == 'H') {
			task.setPriority(priority);
		} 
		else {
			feedback.append(MESSAGE_INVALID_PRIORITY_LEVEL);
		}
	}
	
	public static void updateOverdue() {
		for(int i = 0; i < _workingList.size(); i++) {
			_workingList.get(i).updateIsDue();
		}
	}
	
	private static void updateDueDate(Task task, StringBuilder feedback, String fieldContent) {
		if(!isCorrectDateFormat(fieldContent, feedback)) {
			return;
		}
		task.setDueDate(fieldContent);
	}
	
	/** Add methods **/
	private static LinkedList<Task> executeAdd(String fieldLineWithName, StringBuilder feedback) {
		String taskName = extractAndCheckTaskName(fieldLineWithName, feedback);
		if(taskName == null) {
			return _workingList;
		}
		
		String fieldLine= fieldLineWithName.replaceFirst(taskName, "").trim();
		LinkedList<String> fields = processFieldLine(fieldLine);
		
		Task newTask = new Task(taskName);
		
		for(int i = 0; i < fields.size(); i++) {
			updateField(fields.get(i), newTask, feedback);
		}
		
		_workingList.add(newTask);
		
		QLStorage.saveFile(_workingList, _fileName);
		return _workingList;
	}
	
	private static String extractAndCheckTaskName(String fieldLineWithName, StringBuilder feedback) {
		String taskName = extractTaskName(fieldLineWithName);
		if(isValidTaskName(taskName, feedback)) {
			return taskName;
		} 
		else {
			return null;
		}
	}
	
	private static String extractTaskName(String fieldLineWithName) {
		int taskNameEndIndex = fieldLineWithName.length();
		for(int i = 0; i < fieldLineWithName.length(); i++) {
			if(fieldLineWithName.charAt(i) == '-') {
				taskNameEndIndex = i;
				break;
			}
		}
		return fieldLineWithName.substring(0, taskNameEndIndex).trim();
	}
	
	private static boolean isValidTaskName(String taskName, StringBuilder feedback) {
		if(taskName.equals(STRING_NO_CHAR)) {
			feedback.append(MESSAGE_INVALID_TASK_NAME);
			return false;
		} 
		else {
			return true;
		}
	}

	/** Edit methods **/
	private static LinkedList<Task> executeEdit(String fieldLineWithTaskNumber, StringBuilder feedback) {
		int taskNumber = extractAndCheckTaskNumber(fieldLineWithTaskNumber, feedback);
		if(taskNumber == NUM_INVALID_TASK_NUMBER) {
			return _workingList;
		}
		
		String fieldLine= fieldLineWithTaskNumber.replaceFirst(String.valueOf(taskNumber), "").trim();
		LinkedList<String> fields = processFieldLine(fieldLine);
		
		Task taskToEdit = _workingList.get(taskNumber + OFFSET_TASK_NUMBER_TO_INDEX);
		
		for(int i = 0; i < fields.size(); i++) {
			updateField(fields.get(i), taskToEdit, feedback);
		}
				
		QLStorage.saveFile(_workingList, _fileName);
		return _workingList;
	}

	/** Delete methods **/
	private static LinkedList<Task> executeDelete(String fieldLineWithTaskNumber, StringBuilder feedback) {
		int taskNumber = extractAndCheckTaskNumber(fieldLineWithTaskNumber, feedback);
		if(taskNumber == NUM_INVALID_TASK_NUMBER) {
			return _workingList;
		}
		
		deleteTask(taskNumber, feedback);
		
		QLStorage.saveFile(_workingList, _fileName);
		return _workingList;
	}

	private static void deleteTask(int taskNumber, StringBuilder feedback) {
		_workingList.remove(taskNumber + OFFSET_TASK_NUMBER_TO_INDEX);
	}

	/** Complete methods **/
	private static LinkedList<Task> executeComplete(String fieldLineWithTaskNumber, StringBuilder feedback) {
		int taskNumber = extractAndCheckTaskNumber(fieldLineWithTaskNumber, feedback);
		if(taskNumber == NUM_INVALID_TASK_NUMBER) {
			return _workingList;
		}
		
		completeTask(taskNumber, feedback);
		
		QLStorage.saveFile(_workingList, _fileName);
		return _workingList;
	}
	
	private static void completeTask(int taskNumber, StringBuilder feedback) {
		Task taskToChange = _workingList.get(taskNumber + OFFSET_TASK_NUMBER_TO_INDEX);
		if(taskToChange.getIsCompleted()) {
			taskToChange.setNotCompleted();
		} 
		else {
			taskToChange.setCompleted();
		}
	}

	/** Main method **/
	public static void main(String args[]) {
	}
	
}
