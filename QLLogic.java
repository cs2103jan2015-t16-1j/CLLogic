import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

public class QLLogic {

	private static final String MESSAGE_INVALID_YES_NO = "Invalid yes/no field. Please use Y for yes and N for no.";
	private static final String MESSAGE_NO_TASK_SATISFY_CRITERIA = "No task satisfies criteria entered.";
	private static final String MESSAGE_NO_DATE_ENTERED = "No date entered.";
	private static final String MESSAGE_INVALID_PRIORITY_LEVEL = "Invalid priority level.";
	private static final String MESSAGE_INVALID_FIELD_TYPE = "Invalid field type \"%1$s\".";
	private static final String MESSAGE_INVALID_COMMAND = "Invalid command. No command executed.";
	private static final String MESSAGE_INVALID_TASK_NAME = "Invalid task name entered. Nothing is executed.";
	private static final String MESSAGE_TASK_NUMBER_OUT_OF_RANGE = "Task number entered out of range. Nothing is executed.";
	private static final String MESSAGE_INVALID_TASK_NUMBER = "Invalid task number entered. Nothing is executed.";
	
	private static final int INDEX_COMMAND = 0;
	private static final int INDEX_FIELDS = 1;
	private static final int INDEX_FIELD_CONTENT_START = 1;
	private static final int INDEX_FIELD_TYPE = 0;
	private static final int INDEX_PRIORITY_LEVEL = 0;
	
	private static final int NUM_SPLIT_TWO = 2;
	private static final int NUM_INVALID_TASK_NUMBER = -1;
	private static final int NUM_0_SEC = 0;
	private static final int NUM_0_MIN = 0;
	private static final int NUM_0_HOUR = 0;
	private static final int NUM_59_SEC = 59;
	private static final int NUM_59_MIN = 59;
	private static final int NUM_23_HOUR = 23;
	
	private static final int OFFSET_TASK_NUMBER_TO_INDEX = -1;
	
	private static final String COMMAND_ADD_ABBREV = "a";
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_EDIT_ABBREV = "e";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_DELETE_ABBREV = "d";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_COMPLETE_ABBREV = "c";
	private static final String COMMAND_COMPLETE = "complete";
	private static final String COMMAND_LIST_ABBREV = "l";
	private static final String COMMAND_LIST = "list";
	
	private static final String STRING_NO_CHAR = "";
	private static final String STRING_BLANK_SPACE = " ";
	private static final String STRING_DASH = "-";
	private static final String STRING_NO = "N";
	private static final String STRING_YES = "Y";
	
	private static final char CHAR_NO_PRIORITY_LEVEL = 'N';
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dMMyyyy");

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
		else if(command.equalsIgnoreCase(COMMAND_LIST) || command.equalsIgnoreCase(COMMAND_LIST_ABBREV)) {
			return executeList(fieldLine, feedback);
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
	
	private static boolean isValidPriorityLevel(String priorityLevelString, StringBuilder feedback) {
		if(priorityLevelString.equals(STRING_NO_CHAR)) {
			feedback.append(MESSAGE_INVALID_PRIORITY_LEVEL);
			return false;
		}
		
		char priority = priorityLevelString.charAt(INDEX_PRIORITY_LEVEL);
		
		if(priority == 'L' || priority == 'M' || priority == 'H') {
			return true;
		} 
		else {
			feedback.append(MESSAGE_INVALID_PRIORITY_LEVEL);
			return false;
		}
	}
	
	private static void copyList(LinkedList<Integer> fromList, LinkedList<Integer> toList) {
		toList.clear();
		for(int i = 0; i < fromList.size(); i++)
			toList.add(fromList.get(i));
	}

	private static <E> boolean isDuplicated(LinkedList<E> list, E e) {
		
		if(list == null) {
			return false;
		}
		
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).equals(e)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isValidYesNo(String yesNoString, StringBuilder feedback) {
		if(yesNoString.equalsIgnoreCase(STRING_YES) || yesNoString.equalsIgnoreCase(STRING_NO)) {
			return true;
		}
		else {
			feedback.append(MESSAGE_INVALID_YES_NO);
			return false;
		}
	}
	
	/** Update methods **/
	private static void updateField(String field, Task task, StringBuilder feedback) {
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
		if(isValidPriorityLevel(fieldContent, feedback)) {
			task.setPriority(fieldContent.charAt(INDEX_PRIORITY_LEVEL));
		}
	}
	
	public static void updateOverdue() {
		for(int i = 0; i < _workingList.size(); i++) {
			_workingList.get(i).updateIsDue();
		}
	}
	
	private static void updateDueDate(Task task, StringBuilder feedback, String fieldContent) {
		if(!DateHandler.isValidDateFormat(fieldContent, feedback)) {
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
	
	/** List methods **/
	private static LinkedList<Task> executeList(String fieldLine, StringBuilder feedback) {
		LinkedList<String> fields = processFieldLine(fieldLine);
		LinkedList<Integer> taskIndexesSatisfyCriteria = new LinkedList<Integer>(); 
		boolean listFiltered = false;
		boolean isFirstPass;
		
		for(int i = 0; i < fields.size(); i++) {
			if(i == 0) {
				isFirstPass = true;
			}
			else {
				isFirstPass = false;
			}
			updateTaskIndexesSatisfyCriteria(taskIndexesSatisfyCriteria, fields.get(i), feedback, isFirstPass);
		}
		
		if(!taskIndexesSatisfyCriteria.isEmpty()) {
			filterWorkingList(taskIndexesSatisfyCriteria);
			listFiltered = true;
		} 
		
		if(!listFiltered) {
			feedback.append(MESSAGE_NO_TASK_SATISFY_CRITERIA);
		}
		
		return _workingList;
	}
	
	private static void filterWorkingList(LinkedList<Integer> requiredIndexes) {
		LinkedList<Task> bufferList = new LinkedList<Task>(); 
		for(int i = 0; i < requiredIndexes.size(); i++) {
			int requiredIndex = requiredIndexes.get(i);
			Task requiredTask = _workingList.get(requiredIndex);
			bufferList.add(requiredTask);
		}
		_workingList = bufferList;
	}

	private static void updateTaskIndexesSatisfyCriteria(LinkedList<Integer> taskIndexesSatisfyCriteria, String field, StringBuilder feedback, boolean isFirstPass) {
		if(field.equals(STRING_BLANK_SPACE)) {
			return;
		}
		
		char fieldType = field.charAt(INDEX_FIELD_TYPE);
		String fieldCriteria = field.substring(INDEX_FIELD_CONTENT_START).trim();
				
		switch(fieldType) {
		case 'd':		
			findIndexesMatchDueDateCriteria(taskIndexesSatisfyCriteria, fieldCriteria, feedback, isFirstPass);
			break;
			
		case 'p':
			findIndexesMatchPriority(taskIndexesSatisfyCriteria, fieldCriteria, feedback, isFirstPass);
			break;
				
		case 'c':
			findIndexesMatchCompleteStatus(taskIndexesSatisfyCriteria, fieldCriteria, feedback, isFirstPass);
			break;
			
		case 'o':
			findIndexesMatchDueStatus(taskIndexesSatisfyCriteria, fieldCriteria, feedback, isFirstPass);
			break;
				
		default: 
			feedback.append(String.format(MESSAGE_INVALID_FIELD_TYPE, fieldType)).append("\n");
			break;
		}
	}

	private static void findIndexesMatchDueStatus(LinkedList<Integer> taskIndexesSatisfyCriteria, String fieldCriteria, StringBuilder feedback, boolean isFirstPass) {
		if(isValidYesNo(fieldCriteria, feedback)) {
			if(fieldCriteria.equals(STRING_YES)) {
				matchDueYes(taskIndexesSatisfyCriteria, feedback, isFirstPass);
			}
			if(fieldCriteria.equals(STRING_NO)) {
				matchDueNo(taskIndexesSatisfyCriteria, feedback, isFirstPass);
			}	
		}
	}

	private static void matchDueNo(LinkedList<Integer> taskIndexesSatisfyCriteria, StringBuilder feedback, boolean isFirstPass) {
		LinkedList<Integer> bufferList = new LinkedList<Integer>();
		for(int i = 0; i < _workingList.size(); i++) {
			if(!_workingList.get(i).getIsDue()) {
				if(isDuplicated(taskIndexesSatisfyCriteria, i) || isFirstPass) {
					System.out.println(_workingList.get(i).getName() + "added to buffer");
					bufferList.add(i);
				}
			} 
		}
		copyList(bufferList, taskIndexesSatisfyCriteria);
	}

	private static void matchDueYes(LinkedList<Integer> taskIndexesSatisfyCriteria, StringBuilder feedback, boolean isFirstPass) {
		LinkedList<Integer> bufferList = new LinkedList<Integer>();
		for(int i = 0; i < _workingList.size(); i++) {
			if(_workingList.get(i).getIsDue()) {
				if(isDuplicated(taskIndexesSatisfyCriteria, i) || isFirstPass) {
					System.out.println(_workingList.get(i).getName() + "added to buffer");
					bufferList.add(i);
				}
			} 
		}
		copyList(bufferList, taskIndexesSatisfyCriteria);
		
	}
	
	private static void findIndexesMatchCompleteStatus(LinkedList<Integer> taskIndexesSatisfyCriteria, String fieldCriteria, StringBuilder feedback, boolean isFirstPass) {
		if(isValidYesNo(fieldCriteria, feedback)) {
			if(fieldCriteria.equalsIgnoreCase(STRING_YES)) {
				matchCompleteYes(taskIndexesSatisfyCriteria, feedback, isFirstPass);
			}
			if(fieldCriteria.equalsIgnoreCase(STRING_NO)) {
				matchCompleteNo(taskIndexesSatisfyCriteria, feedback, isFirstPass);
			}	
		}
	}

	private static void matchCompleteNo(LinkedList<Integer> taskIndexesSatisfyCriteria, StringBuilder feedback, boolean isFirstPass) {
		LinkedList<Integer> bufferList = new LinkedList<Integer>();
		for(int i = 0; i < _workingList.size(); i++) {
			if(!_workingList.get(i).getIsCompleted()) {
				if(isDuplicated(taskIndexesSatisfyCriteria, i) || isFirstPass) {
					bufferList.add(i);
				}
			} 
		}
		copyList(bufferList, taskIndexesSatisfyCriteria);
	}

	private static void matchCompleteYes(LinkedList<Integer> taskIndexesSatisfyCriteria, StringBuilder feedback, boolean isFirstPass) {
		LinkedList<Integer> bufferList = new LinkedList<Integer>();
		for(int i = 0; i < _workingList.size(); i++) {
			if(_workingList.get(i).getIsCompleted()) {
				if(isDuplicated(taskIndexesSatisfyCriteria, i) || isFirstPass) {
					bufferList.add(i);
				}
			} 
		}
		copyList(bufferList, taskIndexesSatisfyCriteria);
		
	}

	private static void findIndexesMatchPriority(LinkedList<Integer> taskIndexesSatisfyCriteria, String fieldCriteria, StringBuilder feedback, boolean isFirstPass) {
		if(isValidPriorityLevel(fieldCriteria, feedback)) {
			char priorityLevel = fieldCriteria.charAt(INDEX_PRIORITY_LEVEL);
			matchPriorityLevelCriteria(taskIndexesSatisfyCriteria, priorityLevel, feedback, isFirstPass);
		}
		
	}

	private static void matchPriorityLevelCriteria(LinkedList<Integer> taskIndexesSatisfyCriteria, char priorityLevel, StringBuilder feedback, boolean isFirstPass) {
		LinkedList<Integer> bufferList = new LinkedList<Integer>();
		for(int i = 0; i < _workingList.size(); i++) {
			char taskPriorityLevel = _workingList.get(i).getPriority();
			if(taskPriorityLevel == CHAR_NO_PRIORITY_LEVEL) {
				return;
			}
			if(taskPriorityLevel == priorityLevel) {
				if(isDuplicated(taskIndexesSatisfyCriteria, i) || isFirstPass) {
					bufferList.add(i);
				}
			} 
		}
		copyList(bufferList, taskIndexesSatisfyCriteria);
	}

	private static void findIndexesMatchDueDateCriteria(LinkedList<Integer> taskIndexesSatisfyCriteria, String dueDateCriteria, StringBuilder feedback, boolean isFirstPass) {
		if(dueDateCriteria.equals(STRING_NO_CHAR)) {
			feedback.append(MESSAGE_NO_DATE_ENTERED);
			return;
		}
		String[] dueDateCriteriaArray = dueDateCriteria.split(":");
		if(dueDateCriteriaArray.length == 1) {
			matchSingleDueDateCriteria(taskIndexesSatisfyCriteria, dueDateCriteriaArray[0], feedback, isFirstPass);		
		} 
		else if(dueDateCriteriaArray.length == 2) {
			matchDueDateRangeCriteria(taskIndexesSatisfyCriteria, dueDateCriteriaArray, feedback, isFirstPass);
		}
	}

	private static void matchDueDateRangeCriteria(LinkedList<Integer> taskIndexesSatisfyCriteria, String[] dueDateCriteriaArray, StringBuilder feedback, boolean isFirstPass) {
		Calendar startDate = DateHandler.getDateCalendar(dueDateCriteriaArray[0], feedback);
		if(startDate == null) {
			return;
		}
		Calendar endDate = DateHandler.getDateCalendar(dueDateCriteriaArray[1], feedback);
		if(endDate == null) {
			return;
		}
		
		startDate.set(Calendar.HOUR, NUM_0_HOUR);
		startDate.set(Calendar.MINUTE, NUM_0_MIN);
		startDate.set(Calendar.SECOND, NUM_0_SEC);
		
		endDate.set(Calendar.HOUR, NUM_23_HOUR);
		endDate.set(Calendar.MINUTE, NUM_59_MIN);
		endDate.set(Calendar.SECOND, NUM_59_SEC);
		
		LinkedList<Integer> bufferList = new LinkedList<Integer>();
		for(int i = 0; i < _workingList.size(); i++) {
			Calendar taskDueDate = _workingList.get(i).getDueDate();
			if(taskDueDate == null) {
				return;
			}
			if(taskDueDate.compareTo(startDate) >= 0 && taskDueDate.compareTo(endDate) <= 0) {
				if(isDuplicated(taskIndexesSatisfyCriteria, i) || isFirstPass) {
					bufferList.add(i);
				}
			}
		}
		
		copyList(bufferList, taskIndexesSatisfyCriteria);
	}

	private static void matchSingleDueDateCriteria(LinkedList<Integer> taskIndexesSatisfyCriteria, String dueDateCriteria, StringBuilder feedback, boolean isFirstPass) {
		if(!DateHandler.isValidDateFormat(dueDateCriteria, feedback)) {
			return;
		}
		
		String dueDateCriteriaString = String.valueOf(DateHandler.changeFromDateStringToDDMMYYYY(dueDateCriteria));
		
		LinkedList<Integer> bufferList = new LinkedList<Integer>();
		for(int i = 0; i < _workingList.size(); i++) {
			String taskDueDateString = _workingList.get(i).getDueDateString();
			if(taskDueDateString == null) {
				return;
			}
			if(taskDueDateString.equals(dueDateCriteriaString)) {
				if(isDuplicated(taskIndexesSatisfyCriteria, i) || isFirstPass) {
					bufferList.add(i);
				}
			} 
		}
		copyList(bufferList, taskIndexesSatisfyCriteria);
	}
	
	/** Main method **/
	public static void main(String args[]) {
		_workingList = new LinkedList<Task>();
		StringBuilder feedback =  new StringBuilder();
		
		executeCommand("add task one -p L -d 0102", feedback);
		executeCommand("add task two  -d 0502 -p M", feedback);
		executeCommand("add task three -d 0902 -p H", feedback);
		executeCommand("add task four -d 0702 -p M", feedback);
		executeCommand("list -o N", feedback);
		
		System.out.println(_workingList.peek().getName());
	}
	
}
