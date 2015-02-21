import java.text.SimpleDateFormat;
import java.util.LinkedList;

public class QLLogic {
	
	
	private static final int INDEX_FIELD_CONTENT_START = 1;
	private static final int INDEX_FIELD_TYPE = 0;
	private static final int INDEX_PRIORITY_LEVEL = 0;
	
	private static final String MESSAGE_INVALID_PRIORITY_LEVEL = "Invalid priority level. Priority level not set. Please set priority level using EDIT.";
	private static final String MESSAGE_INVALID_FIELD_TYPE = "Invalid field type \"%1$s\".";
	private static final String MESSAGE_INVALID_COMMAND = "Invalid command. No command executed.";
	private static final String MESSAGE_INVALID_TASK_NAME = "Invalid task name entered. No task is added.";
	private static final String MESSAGE_INVALID_MONTH = "Invalid month entered. Date not set. Please set due/start date using EDIT.";
	private static final String MESSAGE_INVALID_DAY = "Invalid day entered. Date not set. Please set due/start date using EDIT.";
	private static final String MESSAGE_INVALID_DATE_FORMAT = "Invalid date format entered. Date not set. Please set due/start date using EDIT.";
	
	private static final int INDEX_COMMAND = 0;
	private static final int INDEX_FIELDS = 1;
	
	private static final int NUM_SPLIT_TWO = 2;
	
	private static final String COMMAND_ADD_ABBREV = "a";
	private static final String COMMAND_ADD = "add";
	
	private static final String STRING_NO_CHAR = "";
	private static final String STRING_BLANK_SPACE = " ";
	private static final String STRING_DASH = "-";

	public static LinkedList<Task> _workingList;	//TODO change back to private
	private static String _fileName;
	
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
		
		else {
			feedback.append(MESSAGE_INVALID_COMMAND);
			return null;
		}
			
	}

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
	
	private static String extractAndCheckTaskName(String fieldLine, StringBuilder feedback) {
		int taskNameIndexEnd = extractTaskNameEndIndex(fieldLine);
		String taskName = extractTaskName(fieldLine, taskNameIndexEnd);
		if(isValidTaskName(taskName, feedback)) {
			return taskName;
		} 
		else {
			return null;
		}
	}
	
	private static String extractTaskName(String fieldLine, int taskNameIndexEnd) {
		return fieldLine.substring(0, taskNameIndexEnd).trim();
	}

	private static int extractTaskNameEndIndex(String fieldLine) {
		int i;
		for(i = 0; i < fieldLine.length(); i++) {
			if(fieldLine.charAt(i) == '-') {
				break;
			}
		}
		return i;
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
	
	private static void updateField(String field, Task task, StringBuilder feedback) {
		if(!field.equals(STRING_NO_CHAR)) {
			char fieldType = field.charAt(INDEX_FIELD_TYPE);
			String fieldContent = field.substring(INDEX_FIELD_CONTENT_START).trim();
			
			switch(fieldType) {
			case 'd':		
				updateDueDate(task, feedback, fieldContent);
				break;
			
			case 'p':
				updatePriority(task, feedback, fieldContent);
				break;
			
			default: 
				feedback.append(String.format(MESSAGE_INVALID_FIELD_TYPE, fieldType)).append("\n");
				break;
			}
		}
	}

	private static void updateDueDate(Task task, StringBuilder feedback, String fieldContent) {
		if(!isCorrectDateFormat(fieldContent, feedback)) {
			return;
		}
		task.setDueDate(fieldContent);
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
			int year = Task.decodeYearFromDate(dateInt);
			
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

	private static void updatePriority(Task task, StringBuilder feedback, String fieldContent) {
		if(fieldContent.equals("")) {
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
	
	public static void main(String args[]) {
		_workingList = new LinkedList<Task>();
		
		StringBuilder feedback = new StringBuilder();
		LinkedList<Task> testList; 
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		
		testList = executeCommand("add task one -p L -d 2202", feedback);
	
	}
	
}
