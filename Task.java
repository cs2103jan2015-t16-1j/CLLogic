import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Task {
	
	private static final char CHAR_PRIORITY_HIGH = 'H';
	private static final char CHAR_PRIORITY_MEDIUM = 'M';
	private static final char CHAR_PRIORITY_LOW = 'L';
	private static final char CHAR_NO_PRIORITY_LEVEL = 'N';
	
	private static final int OFFSET_CALENDAR_MONTH = -1;
	
	private static final int NUM_0_SEC = 0;
	private static final int NUM_0_MIN = 0;
	private static final int NUM_0_HOUR = 0;
	private static final int NUM_59_SEC = 59;
	private static final int NUM_59_MIN = 59;
	private static final int NUM_23_HOUR = 23;
	private static final int NUM_PRIORITY_HIGH = 3;
	private static final int NUM_PRIORITY_MEDIUM = 2;
	private static final int NUM_PRIORITY_LOW = 1;
	
	private String _name; 
	private String _description;
	private char _priority;
	private Calendar _startDate; 
	private Calendar _dueDate; 
	private boolean _isCompleted;
	private boolean _isDue;
	private boolean _shouldSync;
	
	/* Constructors */
	public Task(String name) {
		_name = new String(name);
		_isCompleted = false;
		_isDue = false;
		_priority = CHAR_NO_PRIORITY_LEVEL;	
	}
	
	/* Mutators */
	public void setName(String name) {
		_name = name;
	}
	
	public void setDescription(String description) {
		_description = new String(description);
	}
	
	public void setPriority(char priority) {
		switch (priority) {
		case 'L': 
			_priority = CHAR_PRIORITY_LOW;
			break;
		case 'M': 
			_priority = CHAR_PRIORITY_MEDIUM;
			break;
		case 'H': 
			_priority = CHAR_PRIORITY_HIGH;
			break;
		default: 
			break;
		}
	}
	
	public void setStartDate (String startDateString) {
		int startDateInt = DateHandler.changeFromDateStringToDateInt(startDateString);
		
		int startDay = DateHandler.decodeDayFromDateInt(startDateInt);
		int startMonth = DateHandler.decodeMonthFromDateInt(startDateInt);
		int startYear = DateHandler.decodeYearFromDateInt(startDateInt);
		
		_startDate = new GregorianCalendar(startYear, startMonth + OFFSET_CALENDAR_MONTH, startDay, NUM_0_HOUR, NUM_0_MIN, NUM_0_SEC);
	}
	
	public void setDueDate (String dueDateString) {
		int dueDateInt = DateHandler.changeFromDateStringToDateInt(dueDateString);
		
		int dueDay = DateHandler.decodeDayFromDateInt(dueDateInt);
		int dueMonth = DateHandler.decodeMonthFromDateInt(dueDateInt);
		int dueYear = DateHandler.decodeYearFromDateInt(dueDateInt);
		
		_dueDate = new GregorianCalendar(dueYear, dueMonth + OFFSET_CALENDAR_MONTH, dueDay, NUM_23_HOUR, NUM_59_MIN, NUM_59_SEC);
		
		updateIsDue();
	}
	
	public void setCompleted() {
		_isCompleted = true;
	}
	
	public void setNotCompleted() {
		_isCompleted = false;
	}
	
	public void updateIsDue() {
		Calendar today = new GregorianCalendar();
		if(today.compareTo(_dueDate) < 0) {
			_isDue = false;
		}
		else {
			_isDue = true;
		}
	}
	
	public void setShouldSync() {
		_shouldSync = true;
	}
	
	public void setShouldNotSync() {
		_shouldSync = false;
	}

	/* Accessors */
	public String getName() {
		return _name;
	}
	
	public String getDescription() {
		return _description;
	}
	
	public char getPriority() {
		return _priority;
	}
	
	public int getPriorityInt() {
		switch(_priority) {
		case 'L':
			return NUM_PRIORITY_LOW;
		case 'M':
			return NUM_PRIORITY_MEDIUM;
		case 'H':
			return NUM_PRIORITY_HIGH;
		default:
			return 0;
		}
	}
	
	public Calendar getStartDate() {
		return _startDate;
	}
	
	public Calendar getDueDate() {
		return _dueDate;
	}
	
	public String getStartDateString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(_startDate);
	}
	
	public String getDueDateString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(_dueDate);
	}
	
	public boolean getIsCompleted() {
		return _isCompleted;
	}
	
	public boolean getIsDue() {
		return _isDue;
	}
	
	public boolean getShouldSync() {
		return _shouldSync;
	}
}
