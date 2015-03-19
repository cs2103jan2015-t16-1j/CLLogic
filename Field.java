import java.util.Calendar;

public class Field {
	
	FieldType _fieldType;
	
	String _taskName;
	Calendar _startDate;
	Calendar _dueDate;
	Calendar _reminderDate;
	PriorityLevel _priority;
	
	Calendar[] _dateRange;
	
	FieldCriteria _fieldCriteria;
	
	public Field(FieldType fieldType, 
			Object content, 
			FieldCriteria fieldCriteria) {
		
		/* assertion */
		assert fieldType != null;
		assert content != null;
		assert fieldCriteria != null;
		
		_fieldType = fieldType;
		_fieldCriteria = fieldCriteria;
		
		switch(fieldType) {
		case TASK_NAME:
			_taskName = (String)content;
			break;
		case START_DATE:
			_startDate = (Calendar)content;
			break;
		case DUE_DATE:
			_dueDate = (Calendar)content;
			break;
		case REMINDER:
			_reminderDate = (Calendar)content;
			break;
		case DATE_RANGE:
			_dateRange = (Calendar[])content;
			break;
		case PRIORITY:
			_priority = (PriorityLevel)content;
			break;
		case COMPLETED:
		case OVERDUE:
			break;
		default:
			break;
		}
	}
	
	public FieldType getFieldType() {
		return _fieldType;
	}
	
	public String getTaskName() {
		return _taskName;
	}
	
	public Calendar getStartDate() {
		return _startDate;
	}
	
	public Calendar getDueDate() {
		return _dueDate;
	}
	
	public Calendar getReminderDate() {
		return _reminderDate;
	}
	
	public Calendar[] getDateRange() {
		return _dateRange;
	}

	public PriorityLevel getPriority() {
		return _priority; 
	}
	
	public FieldCriteria getCriteria() {
		return _fieldCriteria;
	}
}
