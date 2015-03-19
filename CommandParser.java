import java.util.LinkedList;

public class CommandParser {
	
	private StringBuilder _feedback; 
	private ActionType _actionType;
	private LinkedList<Field> _fields;
	
	private String taskName;
	private int taskNumber;
	
	public CommandParser(StringBuilder feedback) {
		_feedback = feedback;
	}
	
	public ActionType getActionType() {
		return _actionType;
	}
	
	public LinkedList<Field> getFields() {
		return _fields;
	}
	
	public void processCmdString(String cmdString) {
		
		if(cmdString.trim().equals("")) {
			_feedback.append("Please enter a command. ");
			return;
		}
		
		String[] actionAndFields = splitActionAndFields(cmdString.trim());
		String actionString = actionAndFields[0];
		determineActionType(actionString);
		
		switch(_actionType) {
		case ADD:
			extractTaskName();
			break;
		case EDIT:
		case DELETE:
			extractTaskNumber();
			break;
		default:
			break;
		}
		
		String fieldsString = actionAndFields[1];
		determineFieldsPrim(fieldsString);
	}
	
	private String[] splitActionAndFields(String command) {
		String[] splittedInstruction = command.split(" ", 2);
		splittedInstruction[0] = splittedInstruction[0].trim();
		if(splittedInstruction.length == 1) {
			splittedInstruction[1] = "";
		} else {
			splittedInstruction[1] = splittedInstruction[1].trim();
		}
		return splittedInstruction;
	}
	
	
	private void determineActionType(String actionString) {
		if(actionString.equalsIgnoreCase("ADD") 
				|| actionString.equalsIgnoreCase("A")) {
			
			_actionType = ActionType.ADD;
			
		} else if(actionString.equalsIgnoreCase("EDIT") 
				|| actionString.equalsIgnoreCase("E")) {
			
			_actionType = ActionType.DELETE;
			
		} else if(actionString.equalsIgnoreCase("DELETE") 
				|| actionString.equalsIgnoreCase("DEL")
				|| actionString.equalsIgnoreCase("D")) {
			
			_actionType = ActionType.DELETE;
			
		} else if(actionString.equalsIgnoreCase("FIND") 
				|| actionString.equalsIgnoreCase("F")) {
			
			_actionType = ActionType.FIND;
			
		} else if(actionString.equalsIgnoreCase("SORT") 
				|| actionString.equalsIgnoreCase("S")) {
			
			_actionType = ActionType.SORT;
			
		} else {
			_actionType = null;
		}
	}


	private void determineFieldsPrim(String fieldsString) {
		String[] fieldStringArray = fieldsString.split(" -");
		
		for(String fieldString : fieldStringArray){
			fieldString = fieldString.trim();
			if(!fieldString.equals("")) {
				Field field  = parseField(fieldString);
				if(field != null) {
					_fields.add(field);
				}
			}
		}
	}

	private Field parseField(String fieldString) {
		// TODO Auto-generated method stub
		return null;
	}
}
