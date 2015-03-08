import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

public class QLLogic {

	private static final String MESSAGE_NO_MATCHES_FOUND = "No matches found for criteria entered.";
	private static final String MESSAGE_NO_TASK_MATCHES_KEYWORD = "No task matches keyword.";
	private static final String MESSAGE_INVALID_SORTING_CRITERIA_TYPE = "Invalid sorting criteria type \"%1$s\"";
	private static final String MESSAGE_INVALID_SORTING_ORDER = "Invalid sorting order \"%1$s\".";
	private static final String MESSAGE_NO_DATE_ENTERED = "No date entered.";
	private static final String MESSAGE_NO_NAME_ENTERED = "No task date entered.";
	private static final String MESSAGE_INVALID_FIELD_TYPE = "Invalid field type \"%1$s\".";
	private static final String MESSAGE_INVALID_COMMAND = "Invalid command. No command executed.";
	private static final String MESSAGE_INVALID_TASK_NAME = "Invalid task name entered. Nothing is executed.";
	
	private static final int INDEX_COMMAND = 0;
	private static final int INDEX_FIELDS = 1;
	private static final int INDEX_FIELD_CONTENT_START = 1;
	private static final int INDEX_FIELD_TYPE = 0;
	private static final int INDEX_PRIORITY_LEVEL = 0;
	
	private static final int NUM_0_SEC = 0;
	private static final int NUM_0_MIN = 0;
	private static final int NUM_0_HOUR = 0;
	private static final int NUM_59_SEC = 59;
	private static final int NUM_59_MIN = 59;
	private static final int NUM_23_HOUR = 23;
	
	private static final int OFFSET_TASK_NUMBER_TO_INDEX = -1;
	
	private static final String COMMAND_FIND = "find";
	private static final String COMMAND_FIND_ABBREV = "f";
	private static final String COMMAND_SORT = "s";
	private static final String COMMAND_SORT_ABBREV = "sort";
	private static final String COMMAND_ADD_ABBREV = "a";
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_EDIT_ABBREV = "e";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_DELETE_ABBREV = "d";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_COMPLETE_ABBREV = "c";
	private static final String COMMAND_COMPLETE = "complete";
	
	private static final String STRING_NO_CHAR = "";
	private static final String STRING_BLANK_SPACE = " ";
	private static final String STRING_NO = "N";
	private static final String STRING_YES = "Y";
	private static final String STRING_NEW_LINE = "\n";
	
	private static final char CHAR_DESCENDING = 'd';
	private static final char CHAR_ASCENDING = 'a';

	
	public static LinkedList<Task> _workingList;	//TODO change back to private
	public static LinkedList<Task> _workingListMaster;
	private static String _fileName;
	
	/** General methods **/
	public static LinkedList<Task> setup(String fileName) {
		_fileName = fileName; 
		_workingList = QLStorage.loadFile(fileName);
		_workingListMaster = new LinkedList<Task>();
		copyList(_workingList, _workingListMaster);
		return _workingList;
	}
	
	private static void recover() {
		copyList(_workingListMaster, _workingList);
	}
	
	// Stub
	public static void setupStub() {
		_workingList = new LinkedList<Task>();
		_workingListMaster = new LinkedList<Task>();
		copyList(_workingList, _workingListMaster);
	}
	
	// Stub
	public static void displayStub(StringBuilder feedback) {
		System.out.println("Feedback: " + feedback.toString());
		
		System.out.println("	workingList: ");
		for(int i = 0; i < _workingList.size(); i++) {
			System.out.print(_workingList.get(i).getName() + " ");
			try {
				System.out.print(_workingList.get(i).getDueDateString() + " ");
			} catch(NullPointerException e) {}
			try {	
				System.out.print(_workingList.get(i).getPriority() + " ");
			} catch(NullPointerException e) {}
			System.out.println();
		}
		
		/*
		System.out.println("	workingListMaster: ");
		for(int i = 0; i < _workingListMaster.size(); i++) {
			System.out.print(_workingListMaster.get(i).getName() + " ");
			try {
				System.out.print(_workingListMaster.get(i).getDueDateString() + " ");
			} catch(NullPointerException e) {}
			try {	
				System.out.print(_workingListMaster.get(i).getPriority() + " ");
			} catch(NullPointerException e) {}
			System.out.println();
		}
		*/
		System.out.println();
		feedback.setLength(0);
	}

	public static LinkedList<Task> executeCommand(String instruction, StringBuilder feedback) {
		String[] splittedInstruction = CommandParser.splitCommandAndFields(instruction);
		
		String command = splittedInstruction[INDEX_COMMAND].trim();
		String fieldLine = splittedInstruction[INDEX_FIELDS].trim();
				
		if(command.equalsIgnoreCase(COMMAND_ADD) || command.equalsIgnoreCase(COMMAND_ADD_ABBREV)) {
			return executeAdd(fieldLine, feedback);
		} else if(command.equalsIgnoreCase(COMMAND_EDIT) || command.equalsIgnoreCase(COMMAND_EDIT_ABBREV)) {
			return executeEdit(fieldLine, feedback);
		} else if(command.equalsIgnoreCase(COMMAND_DELETE) || command.equalsIgnoreCase(COMMAND_DELETE_ABBREV)) {
			return executeDelete(fieldLine, feedback);
		} else if(command.equalsIgnoreCase(COMMAND_COMPLETE) || command.equalsIgnoreCase(COMMAND_COMPLETE_ABBREV)) {
			return executeComplete(fieldLine, feedback);
		} else if(command.equalsIgnoreCase(COMMAND_SORT) || command.equalsIgnoreCase(COMMAND_SORT_ABBREV)) {
			return executeSort(fieldLine, feedback);
		} else if(command.equalsIgnoreCase(COMMAND_FIND) || command.equalsIgnoreCase(COMMAND_FIND_ABBREV)) {
			return executeFind(fieldLine, feedback);
		} else {
			feedback.append(MESSAGE_INVALID_COMMAND);
			return _workingList;
		}
	}

	/** Multi-command methods **/ 
	//TODO change to private
	public static void clearWorkingList() {
		_workingList = new LinkedList<Task>();
	}
	
	private static <E> void copyList(LinkedList<E> fromList, LinkedList<E> toList) {
		toList.clear();
		for(int i = 0; i < fromList.size(); i++)
			toList.add(fromList.get(i));
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
			feedback.append(String.format(MESSAGE_INVALID_FIELD_TYPE, fieldType)).append(STRING_NEW_LINE);
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
		if(CommandParser.isValidPriorityLevel(fieldContent, feedback)) {
			task.setPriority(fieldContent.substring(INDEX_PRIORITY_LEVEL, INDEX_PRIORITY_LEVEL + 1));
		}
	}
	
	public static void updateOverdue() {
		for(int i = 0; i < _workingList.size(); i++) {
			_workingList.get(i).updateIsOverdue();
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
		String taskName = CommandParser.extractTaskName(fieldLineWithName);
		if(!CommandParser.isValidTaskName(taskName, feedback)) {
			return _workingList;
		}
		
		String fieldLine= fieldLineWithName.replaceFirst(taskName, "").trim();
		LinkedList<String> fields = CommandParser.processFieldLine(fieldLine);
		
		Task newTask = new Task(taskName);
		
		for(int i = 0; i < fields.size(); i++) {
			updateField(fields.get(i), newTask, feedback);
		}
		
		_workingList.add(newTask);
		_workingListMaster.add(newTask);
		
		QLStorage.saveFile(_workingListMaster, _fileName);
		return _workingList;
	}

	/** Edit methods **/
	private static LinkedList<Task> executeEdit(String fieldLineWithTaskNumber, StringBuilder feedback) {
		int taskNumber;
		String taskNumberString = CommandParser.extractTaskNumberString(fieldLineWithTaskNumber);
		if(!CommandParser.isValidTaskNumber(taskNumberString, feedback, _workingList.size())) {
			return _workingList;
		} else {
			taskNumber = Integer.parseInt(taskNumberString);
		}
		
		String fieldLine= fieldLineWithTaskNumber.replaceFirst(String.valueOf(taskNumber), "").trim();
		LinkedList<String> fields = CommandParser.processFieldLine(fieldLine);
		
		Task taskToEdit = _workingList.get(taskNumber + OFFSET_TASK_NUMBER_TO_INDEX);
		
		for(int i = 0; i < fields.size(); i++) {
			updateField(fields.get(i), taskToEdit, feedback);
		}
				
		QLStorage.saveFile(_workingListMaster, _fileName);
		return _workingList;
	}

	/** Delete methods **/
	private static LinkedList<Task> executeDelete(String fieldLineWithTaskNumber, StringBuilder feedback) {
		int taskNumber;
		String taskNumberString = CommandParser.extractTaskNumberString(fieldLineWithTaskNumber);
		if(CommandParser.isValidTaskNumber(taskNumberString, feedback, _workingList.size())) {
			taskNumber = Integer.parseInt(taskNumberString);
		} else {
			return _workingList;
		}
		
		Task taskToDelete = _workingList.get(taskNumber + OFFSET_TASK_NUMBER_TO_INDEX);
		deleteTask(taskToDelete);
		
		QLStorage.saveFile(_workingListMaster, _fileName);
		return _workingList;
	}

	private static void deleteTask(Task taskToDelete) {
		_workingList.remove(taskToDelete);
		_workingListMaster.remove(taskToDelete);
	}

	/** Complete methods **/
	private static LinkedList<Task> executeComplete(String fieldLineWithTaskNumber, StringBuilder feedback) {
		int taskNumber;
		String taskNumberString = CommandParser.extractTaskNumberString(fieldLineWithTaskNumber);
		if(CommandParser.isValidTaskNumber(taskNumberString, feedback, _workingList.size())) {
			taskNumber = Integer.parseInt(taskNumberString);
		} else {
			return _workingList;
		}
		
		Task taskToComplete = _workingList.get(taskNumber + OFFSET_TASK_NUMBER_TO_INDEX);
		completeTask(taskToComplete);
		
		QLStorage.saveFile(_workingListMaster, _fileName);
		return _workingList;
	}
	
	private static void completeTask(Task taskToComplete) {
		if(taskToComplete.getIsCompleted()) {
			taskToComplete.setNotCompleted();
		} 
		else {
			taskToComplete.setCompleted();
		}
	}
	
	/** Find methods **/
	private static LinkedList<Task> executeFind(String fieldLine, StringBuilder feedback) {
		LinkedList<String> fields = CommandParser.processFieldLine(fieldLine);
		LinkedList<Task> workingListBackUp = new LinkedList<Task>();
		copyList(_workingList, workingListBackUp);
 		for(int i = 0; i < fields.size(); i++) {
			filterWorkingListByCriteria(fields.get(i), feedback);
		}
		if(_workingList.isEmpty() || fields.isEmpty()) {
 			feedback.append(MESSAGE_NO_MATCHES_FOUND);
 			_workingList = workingListBackUp;
 		}
		return _workingList;
	}

	private static void filterWorkingListByCriteria(String field, StringBuilder feedback) {
		if(field.equals(STRING_BLANK_SPACE)) {
			return;
		}
		char fieldType = field.charAt(INDEX_FIELD_TYPE);
		String fieldCriteria = field.substring(INDEX_FIELD_CONTENT_START).trim();
		switch(fieldType) {
		case 'd':		
			filterByDueDate(fieldCriteria, feedback);
			break;
			
		case 'p':
			filterByPriority(fieldCriteria, feedback);
			break;
				
		case 'c':
			filterByCompleteStatus(fieldCriteria, feedback);
			break;
			
		case 'o':
			filterByOverdueStatus(fieldCriteria, feedback);
			break;
		
		case 'n':
			filterByName(fieldCriteria, feedback);
			break;
				
		default: 
			feedback.append(String.format(MESSAGE_INVALID_FIELD_TYPE, fieldType)).append(STRING_NEW_LINE);
			break;
		}
	}

	private static void filterByName(String fieldCriteria, StringBuilder feedback) {
		if(fieldCriteria.equals(STRING_NO_CHAR)) {
			feedback.append(MESSAGE_NO_NAME_ENTERED);
			return;
		}
		String keywords[] = fieldCriteria.split(STRING_BLANK_SPACE);
		findTasksMatchKeywords(keywords, feedback);
	}

	private static void filterByOverdueStatus(String fieldCriteria, StringBuilder feedback) {
		if(CommandParser.isValidYesNo(fieldCriteria, feedback)) {
			LinkedList<Task> bufferList = new LinkedList<Task>();
			for(int i = 0; i < _workingList.size(); i++) {
				Task currentTask = _workingList.get(i);
				if((currentTask.getIsOverdue() && fieldCriteria.equalsIgnoreCase(STRING_YES))
						|| (!currentTask.getIsOverdue() && fieldCriteria.equalsIgnoreCase(STRING_NO))) {
					bufferList.add(currentTask);
				} 
			}
			copyList(bufferList, _workingList);
		}
	}
	
	private static void filterByCompleteStatus(String fieldCriteria, StringBuilder feedback) {
		if(CommandParser.isValidYesNo(fieldCriteria, feedback)) {
			LinkedList<Task> bufferList = new LinkedList<Task>();
			for(int i = 0; i < _workingList.size(); i++) {
				Task currentTask = _workingList.get(i);
				if((currentTask.getIsCompleted() && fieldCriteria.equalsIgnoreCase(STRING_YES))
						|| (!currentTask.getIsCompleted() && fieldCriteria.equalsIgnoreCase(STRING_NO))) {
					bufferList.add(currentTask);
				} 
			}
			copyList(bufferList, _workingList);
		}
	}
	
	private static void filterByPriority(String fieldCriteria, StringBuilder feedback) {
		if(CommandParser.isValidPriorityLevel(fieldCriteria, feedback)) {
			String priorityLevel = fieldCriteria.substring(INDEX_PRIORITY_LEVEL, INDEX_PRIORITY_LEVEL + 1);
			LinkedList<Task> bufferList = new LinkedList<Task>();
			for(int i = 0; i < _workingList.size(); i++) {
				Task currentTask = _workingList.get(i);
				if(currentTask.getPriority() == null) {
					break;
				}
				if(currentTask.getPriority().equalsIgnoreCase(priorityLevel)) {
					bufferList.add(currentTask);
				} 
			}
			copyList(bufferList, _workingList);
		}
	}

	private static void filterByDueDate(String fieldCriteria, StringBuilder feedback) {
		if(fieldCriteria.equals(STRING_NO_CHAR)) {
			feedback.append(MESSAGE_NO_DATE_ENTERED);
			return;
		}
		String[] dueDateCriteriaArray = fieldCriteria.split(":");
		if(dueDateCriteriaArray.length == 1) {
			filterBySingleDate(dueDateCriteriaArray[0], feedback);		
		} 
		else if(dueDateCriteriaArray.length == 2) {
			filterByDateRange(dueDateCriteriaArray, feedback);
		}
	}

	private static void filterByDateRange(String[] dateCriteriaArray, StringBuilder feedback) {
		Calendar startDate = DateHandler.changeFromDateStringToDateCalendar(dateCriteriaArray[0], feedback);
		if(startDate == null) {
			return;
		}
		Calendar endDate = DateHandler.changeFromDateStringToDateCalendar(dateCriteriaArray[1], feedback);
		if(endDate == null) {
			return;
		}
		startDate.set(Calendar.HOUR, NUM_0_HOUR);
		startDate.set(Calendar.MINUTE, NUM_0_MIN);
		startDate.set(Calendar.SECOND, NUM_0_SEC);
		
		endDate.set(Calendar.HOUR, NUM_23_HOUR);
		endDate.set(Calendar.MINUTE, NUM_59_MIN);
		endDate.set(Calendar.SECOND, NUM_59_SEC);
		
		LinkedList<Task> bufferList = new LinkedList<Task>();
		for(int i = 0; i < _workingList.size(); i++) {
			Task currentTask = _workingList.get(i);
			Calendar currentTaskDueDate = currentTask.getDueDate();
			if(currentTaskDueDate == null) {
				break;
			}
			if(currentTaskDueDate.compareTo(startDate) >= 0 && currentTaskDueDate.compareTo(endDate) <= 0) {
				bufferList.add(currentTask);
			}
		}
		copyList(bufferList, _workingList);
	}

	private static void filterBySingleDate(String singleDateCriteria, StringBuilder feedback) {
		Calendar dateCriteria = DateHandler.changeFromDateStringToDateCalendar(singleDateCriteria, feedback);
		if(dateCriteria == null) {
			return;
		}
		
		LinkedList<Task> bufferList = new LinkedList<Task>();
		for(int i = 0; i < _workingList.size(); i++) {
			Task currentTask = _workingList.get(i);
			Calendar currentTaskDueDate = currentTask.getDueDate();
			if(currentTaskDueDate == null) {
				break;
			}		
			
			int currentTaskDay = currentTaskDueDate.get(Calendar.DAY_OF_MONTH);
			int currentTaskMonth = currentTaskDueDate.get(Calendar.MONTH);
			int currentTaskYear = currentTaskDueDate.get(Calendar.YEAR);; 
			Calendar currentTaskDate = new GregorianCalendar(currentTaskYear, currentTaskMonth, currentTaskDay);

			if(currentTaskDate.equals(dateCriteria)) {
				bufferList.add(currentTask);
			} 
		}
		copyList(bufferList, _workingList);
	}
	
	private static void findTasksMatchKeywords(String[] keywords, StringBuilder feedback) {
		LinkedList<Object[]> foundTasksWithMatchScore = new LinkedList<Object[]>();
		for(int i = 0; i < _workingList.size(); i++) {
			Task currentTask = _workingList.get(i);
			int matchScore = 0;
			for(int j = 0; j < keywords.length; j++) {
				String currentKeyword = keywords[j].trim();
				matchScore = matchKeywordsStore(currentTask, currentKeyword);
			}
			if(matchScore != 0) {
				foundTasksWithMatchScore.add(new Object[]{currentTask, Integer.valueOf(matchScore)});
			}
		}
		if(foundTasksWithMatchScore.isEmpty()) {
			feedback.append(MESSAGE_NO_TASK_MATCHES_KEYWORD);
			return;
		}
		_workingList = sortFoundTasksByMatchScore(foundTasksWithMatchScore);
	}

	private static LinkedList<Task> sortFoundTasksByMatchScore(LinkedList<Object[]> foundTasksWithMatchScore) {
		for(int i = foundTasksWithMatchScore.size() - 1; i >= 0; i--) {
			boolean isSorted = true;
			for(int j = 0; j < i; j++) {
				Object[] taskLeft = foundTasksWithMatchScore.get(j);
				Object[] taskRight = foundTasksWithMatchScore.get(j + 1);
				if((int)taskLeft[1] < (int)taskRight[1]) {
					foundTasksWithMatchScore.set(j + 1, taskLeft);
					foundTasksWithMatchScore.set(j, taskRight);
					isSorted = false;
				}
			}
			if(isSorted) {
				break;
			}
		}
		LinkedList<Task> newWorkingList = new LinkedList<Task>();
		for(int i = 0; i < foundTasksWithMatchScore.size(); i++) {
			Task taskToAdd = (Task)foundTasksWithMatchScore.get(i)[0];
			newWorkingList.add(taskToAdd);
		}
		return newWorkingList;
	}

	private static int matchKeywordsStore(Task currentTask, String currentKeyword) {
		String[] taskNameWords = currentTask.getName().split(STRING_BLANK_SPACE);
		int totalScore = 0;
		for(int i = 0; i < taskNameWords.length; i++) {
			String currentTaskNameWord = taskNameWords[i].trim();
			if(currentTaskNameWord.contains(currentKeyword)) {
				totalScore++;
			}
			if(currentTaskNameWord.equals(currentKeyword)) {
				totalScore ++;
			}
		}
		return totalScore;
	}
	
	/** Sort methods **/
	private static LinkedList<Task> executeSort(String fieldLine, StringBuilder feedback) {
		LinkedList<String> fields = CommandParser.processFieldLine(fieldLine);
		if(fields.size() == 0) {
			feedback.append("No field entered.");
			return _workingList;
		}	
		LinkedList<char[]> sortingCriteria = CommandParser.getSortingCriteria(fields);
		sortByCriteria(sortingCriteria, feedback);
		return _workingList;
		
	}
	
	private static void sortByCriteria(LinkedList<char[]> sortingCriteria, StringBuilder feedback) {
		for(int i = sortingCriteria.size() - 1; i >= 0; i--) {
			char criterionType = sortingCriteria.get(i)[0];
			char criterionOrder = sortingCriteria.get(i)[1];
			switch(criterionType) {
			case 'd':
				sortByDate(criterionOrder, feedback);
				break;
				
			case 'p':
				sortByPriority(criterionOrder,feedback);
				break;
				
			default:
				feedback.append(String.format(MESSAGE_INVALID_SORTING_CRITERIA_TYPE, criterionType)).append(STRING_NEW_LINE);
				break;
			}
		}
	}

	private static void sortByPriority(char order, StringBuilder feedback) {
		LinkedList<Task> tasksWithNoPriority = new LinkedList<Task>();
		for(int i = 0; i < _workingList.size(); i++){
			if(_workingList.get(i).getPriorityInt() == 0) {
				Task removedTask = _workingList.remove(i); 
				tasksWithNoPriority.add(removedTask);
				i--;
			}
		}
		
		for(int i = _workingList.size() - 1; i >= 0; i--) {
			boolean isSorted = true;
			for(int j = 0; j < i; j++) {
				Task taskLeft = _workingList.get(j);
				Task taskRight = _workingList.get(j + 1);
				switch (order) {
				case CHAR_ASCENDING:
					if(taskLeft.getPriorityInt() > taskRight.getPriorityInt()) {
						_workingList.set(j + 1, taskLeft);
						_workingList.set(j, taskRight);
						isSorted = false;
					}
					break;
				
				case CHAR_DESCENDING:
					if(taskLeft.getPriorityInt() < taskRight.getPriorityInt()) {
						_workingList.set(j + 1, taskLeft);
						_workingList.set(j, taskRight);
						isSorted = false;
					}
					break;
				default:
					feedback.append(String.format(MESSAGE_INVALID_SORTING_ORDER, order)).append(STRING_NEW_LINE);
					return;
				}
			}
			if(isSorted) {
				break;
			}
		}
		tasksWithNoPriority.addAll(_workingList);
		_workingList = tasksWithNoPriority;
	}
	
	private static void sortByDate(char order, StringBuilder feedback) {
		LinkedList<Task> tasksWithNoDueDate = new LinkedList<Task>();
		for(int i = 0; i < _workingList.size(); i++){
			if(_workingList.get(i).getDueDate() == null) {
				Task removedTask = _workingList.remove(i); 
				tasksWithNoDueDate.add(removedTask);
				i--;
			}
		}
				
		for(int i = _workingList.size() - 1; i >= 0; i--) {
			boolean isSorted = true;
			for(int j = 0; j < i; j++) {
				Task taskLeft = _workingList.get(j);
				Task taskRight = _workingList.get(j + 1);
				switch (order) {
				case CHAR_ASCENDING:
					if(taskLeft.getDueDate().compareTo(taskRight.getDueDate()) > 0 ) {
						_workingList.set(j + 1, taskLeft);
						_workingList.set(j, taskRight);
						isSorted = false;
					}
					break;
				
				case CHAR_DESCENDING:
					if(taskLeft.getDueDate().compareTo(taskRight.getDueDate()) < 0) {
						_workingList.set(j + 1, taskLeft);
						_workingList.set(j, taskRight);
						isSorted = false;
					}
					break;
					
				default:
					feedback.append(String.format(MESSAGE_INVALID_SORTING_ORDER, order)).append(STRING_NEW_LINE);
					return;
				}
			}
			if(isSorted) {
				break;
			}
		}
		tasksWithNoDueDate.addAll(_workingList);
		_workingList = tasksWithNoDueDate;
	}

	/** Main method **/
	public static void main(String args[]) {
		setupStub();
		StringBuilder feedback =  new StringBuilder();
		
		executeCommand("add task one -p L -d 1502", feedback);
		executeCommand("add task two  -d 1502 -p M", feedback);
		executeCommand("add task three -d 0902 -p H", feedback);
		executeCommand("add task foura -d 1502 -p L", feedback);
		executeCommand("add task fourb -d 0904 -p L", feedback);
		executeCommand("add task four -d 1502 -p M", feedback);
		executeCommand("add task four four -d 1602 -p M", feedback);
		executeCommand("add task five -d 0904 -p L", feedback);
		executeCommand("add task six -p L", feedback);
		executeCommand("add task seven -p H", feedback);
		executeCommand("add task eight", feedback);
		executeCommand("add task nine -d TDY", feedback);
		
		executeCommand("f -d 1502", feedback);
		displayStub(feedback);
		recover();
		executeCommand("f -d 0902:1502", feedback);
		displayStub(feedback);
		recover();
		executeCommand("f -p L", feedback);
		displayStub(feedback);
		recover();
		executeCommand("f -o N", feedback);
		displayStub(feedback);
		recover();
		executeCommand("f -o Y", feedback);
		displayStub(feedback);
		recover();
		
		/*
		executeCommand("c 1", feedback);
		executeCommand("c 2", feedback);
		executeCommand("f -c N", feedback);
		displayStub(feedback);
		executeCommand("s -d a -p d", feedback);
		displayStub(feedback);
		recover();
		executeCommand("f -c Y", feedback);
		displayStub(feedback);
		recover();
		executeCommand("f -d TMR", feedback);
		displayStub(feedback);
		recover();
		executeCommand("f -n four", feedback);
		displayStub(feedback);
		recover();
		*/
		
		/*
		executeCommand("l -d 1502", feedback);
		displayStub(feedback);
		executeCommand("e 2 -n task 2", feedback);
		displayStub(feedback);
		executeCommand("add play -d 0803 -p L", feedback);
		displayStub(feedback);
		executeCommand("delete 3 ", feedback);
		displayStub(feedback);
		*/
	}
	
}
