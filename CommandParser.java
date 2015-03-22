import java.util.Calendar;
import java.util.LinkedList;

public class CommandParser {

	private StringBuilder _feedback;

	private String _taskName;
	private int _taskNumber;
	private ActionType _actionType;
	private LinkedList<Field> _fields;

	public CommandParser(String command, StringBuilder feedback) {
		_feedback = feedback;
		_fields = new LinkedList<Field>();
		processCmdString(command);
	}

	public StringBuilder getFeedback() {
		return _feedback;
	}

	public Action getAction(String cmdString) {
		processCmdString(cmdString);
		switch (_actionType) {
		case ADD:
			return new AddAction(_taskName, _fields, _feedback);
		case DELETE:
			return new DeleteAction(_taskNumber, _feedback);
		case EDIT:
			return new EditAction(_taskNumber, _fields, _feedback);
		case SORT:
			return new SortAction(_fields, _feedback);
		case FIND:
			return new FindAction(_fields, _feedback);
		default:
			return null;
		}
	}

	private void processCmdString(String cmdString) {

		if (cmdString.trim().equals("")) {
			_feedback.append("Please enter a command. ");
			return;
		}

		String[] actionAndFields = cmdString.split(" ", 2);

		String actionString = actionAndFields[0].trim();

		determineActionType(actionString);

		if (_actionType == null) {
			_feedback.append("Invalid action type. ");
			return;
		}

		if (actionAndFields.length == 1) {
			_feedback.append("Please enter required fields. ");
			return;
		}

		String fieldsString = actionAndFields[1].trim();

		switch (_actionType) {
		case ADD:
			extractTaskName(fieldsString);
			fieldsString = fieldsString.replaceFirst(_taskName, "").trim();
			break;
		case EDIT:
		case DELETE:
			extractTaskNumber(fieldsString);
			fieldsString = fieldsString.replaceFirst(
					String.valueOf(_taskNumber), "").trim();
			break;
		default:
			break;
		}

		if (fieldsString.length() == 0) {
			return;
		}

		if (fieldsString.charAt(0) != '-' && fieldsString.charAt(0) != ' ') {
			String wrongFields = fieldsString.substring(0,
					fieldsString.indexOf('-'));
			_feedback.append("Invalid field format in \"" + wrongFields
					+ "\". ");
			fieldsString.replaceFirst(wrongFields, "");
		}

		determineFieldsPrim(fieldsString);
	}

	private void extractTaskNumber(String fieldsString) {
		String[] numberAndFields = fieldsString.split(" ", 2);
		String taskNumString = numberAndFields[0].trim();
		if (taskNumString.charAt(0) == '-' || taskNumString.charAt(0) == '0') {
			_taskNumber = 0;
			return;
		}
		try {
			_taskNumber = Integer.parseInt(taskNumString);
		} catch (NumberFormatException e) {
			_taskNumber = 0;
		}
	}

	private void extractTaskName(String fieldsString) {
		String[] nameAndFields = fieldsString.split(" ", 2);
		String taskName = nameAndFields[0].trim();
		if (taskName.equals("") || taskName.charAt(0) == '-') {
			return;
		}
		_taskName = taskName;
	}

	private void determineActionType(String actionString) {
		if (actionString.equalsIgnoreCase("ADD")
				|| actionString.equalsIgnoreCase("A")) {

			_actionType = ActionType.ADD;

		} else if (actionString.equalsIgnoreCase("EDIT")
				|| actionString.equalsIgnoreCase("E")) {

			_actionType = ActionType.DELETE;

		} else if (actionString.equalsIgnoreCase("DELETE")
				|| actionString.equalsIgnoreCase("DEL")
				|| actionString.equalsIgnoreCase("D")) {

			_actionType = ActionType.DELETE;

		} else if (actionString.equalsIgnoreCase("FIND")
				|| actionString.equalsIgnoreCase("F")) {

			_actionType = ActionType.FIND;

		} else if (actionString.equalsIgnoreCase("SORT")
				|| actionString.equalsIgnoreCase("S")) {

			_actionType = ActionType.SORT;

		} else {
			return;
		}
	}

	private void determineFieldsPrim(String fieldsString) {
		String[] fieldStringArray = fieldsString.split(" -");

		for (String fieldString : fieldStringArray) {
			fieldString = fieldString.trim();
			if (!fieldString.equals("")) {
				Field field = parseField(fieldString);
				if (field != null) {
					_fields.add(field);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private Field parseField(String fieldString) {
		/* Assertion */
		assert !fieldString.equals("");

		// empty field will not be added to field list
		if (fieldString.length() == 1) {
			return null;
		}

		char fieldTypeChar = fieldString.charAt(0);
		char spaceAftFieldType = fieldString.charAt(1);

		if (spaceAftFieldType != ' ') {
			_feedback.append("Invalid field type \""
					+ fieldString.split(" ", 2)[0].trim() + "\". ");
		}

		String fieldContentString = fieldString.substring(1).trim();

		if (fieldContentString.equals("")) {
			return null;
		}

		FieldType fieldType = null;
		Object fieldContent = null;
		FieldCriteria fieldCriteria = null;

		switch (fieldTypeChar) {
		case 'd':
		case 's':
		case 'r':

			switch (fieldTypeChar) {
			case 'd':
				fieldType = FieldType.DUE_DATE;
				break;
			case 's':
				fieldType = FieldType.START_DATE;
				break;
			case 'r':
				fieldType = FieldType.REMINDER;
				break;
			default:
				fieldType = null;
				break;
			}

			String criteriaAndDate[] = fieldContentString.split(" ", 2);

			String dateString;
			String criteriaString;

			if (criteriaAndDate.length == 2) {
				dateString = criteriaAndDate[1].trim();
				criteriaString = criteriaAndDate[0].trim();
				fieldCriteria = determineFieldCriteria(criteriaString);
			} else {
				dateString = criteriaAndDate[0].trim();
			}

			if (fieldCriteria == FieldCriteria.BETWEEN) {
				String fromAndTo[] = dateString.split(":", 2);
				if (fromAndTo.length == 1) {
					_feedback.append("Date range not valid");
				} else {
					Calendar[] dateRange = {
							DateHandler.convertToDateCalendar(fromAndTo[0]
									.trim()),
							DateHandler.convertToDateCalendar(fromAndTo[1]
									.trim()) };
				}
			} else if (dateString.equalsIgnoreCase("clr")) {
				fieldContent = "clr";
			} else {
				fieldContent = DateHandler.convertToDateCalendar(dateString);
			}
			break;

		case 'l':

			fieldType = FieldType.DURATION;
			fieldCriteria = determineFieldCriteria(fieldContentString);
			break;

		case 'p':

			fieldType = FieldType.PRIORITY;
			fieldCriteria = determineFieldCriteria(fieldContentString);
			fieldContent = determinePriority(fieldContentString);
			break;

		case 'n':

			fieldType = FieldType.TASK_NAME;
			fieldContent = fieldContentString;
			break;

		default:

			_feedback.append("Invalid field type \"" + fieldTypeChar + "\". ");
			// null type will not be added to field list
			return null;
		}

		// field always has valid field type
		return new Field(fieldType, fieldContent, fieldCriteria);
	}

	private String determinePriority(String fieldContentString) {
		if (fieldContentString.equalsIgnoreCase("l")) {
			return "L";
		} else if (fieldContentString.equalsIgnoreCase("m")) {
			return "M";
		} else if (fieldContentString.equalsIgnoreCase("l")) {
			return "L";
		} else if (fieldContentString.equalsIgnoreCase("clr")) {
			return "CLR";
		} else {
			_feedback.append("Invalid priority level \"" + fieldContentString
					+ "\". ");
			return null;
		}
	}

	private FieldCriteria determineFieldCriteria(String criteriaString) {
		if (criteriaString.equalsIgnoreCase("a")) {
			return FieldCriteria.ASCEND;
		} else if (criteriaString.equalsIgnoreCase("d")) {
			return FieldCriteria.DESCEND;
		} else if (criteriaString.equalsIgnoreCase("bf")) {
			return FieldCriteria.BEFORE;
		} else if (criteriaString.equalsIgnoreCase("af")) {
			return FieldCriteria.AFTER;
		} else if (criteriaString.equalsIgnoreCase("on")) {
			return FieldCriteria.ON;
		} else if (criteriaString.equalsIgnoreCase("btw")) {
			return FieldCriteria.BETWEEN;
		} else if (criteriaString.equalsIgnoreCase("y")) {
			return FieldCriteria.YES;
		} else if (criteriaString.equalsIgnoreCase("n")) {
			return FieldCriteria.NO;
		} else {
			_feedback.append("Invalid criteria \"" + criteriaString
					+ "\". ");
			return null;
		}
	}

	public static void main(String args[]) {
	}
}
