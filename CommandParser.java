public class CommandParser {
	
	TaskAction _action;
	
	public CommandParser() {
	}
	
	public void processCmdString(String cmdString) {
		String[] actionAndFields = OldCommandParser.splitActionAndFields(cmdString);
		
		String actionString = actionAndFields[0].trim();
		ActionType  = getActionType(actionString);
		
		switch (ActionType) {
		case SORT:
			_action = new SortAction();
			break;
		case FIND:
			_action = new FindAction();
			break;
		default:
			return;
		}
		
		String fieldsString = actionAndFields[1].trim();
		LinkedList<Field> = _fieldParser.getFields(fieldsString);
	}
	
	public static String[] splitActionAndFields(String command) {
		String[] splittedInstruction = command.split(STRING_BLANK_SPACE, NUM_SPLIT_TWO);
		if(splittedInstruction.length == 1) {
			String action = splittedInstruction[INDEX_ACTION];
			splittedInstruction = new String[2];
			splittedInstruction[INDEX_ACTION] = action;
			splittedInstruction[INDEX_FIELDS] = "";
		}
		return splittedInstruction;
	}
}
