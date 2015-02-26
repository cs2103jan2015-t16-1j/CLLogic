import java.util.Calendar;
import java.util.GregorianCalendar;

public class Task {
	
	private static final char CHAR_NO_PRIORITY_LEVEL = 'N';
	private static final int CALENDAR_MONTH_OFFSET = -1;
	
	private static final int NUM_0_SEC = 0;
	private static final int NUM_0_MIN = 0;
	private static final int NUM_0_HOUR = 0;
	private static final int NUM_59_SEC = 59;
	private static final int NUM_59_MIN = 59;
	private static final int NUM_23_HOUR = 23;
	
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
			_priority = 'L';
			break;
		case 'M': 
			_priority = 'M';
			break;
		case 'H': 
			_priority = 'H';
			break;
		default: 
			break;
		}
		
	}
	
	public void setStartDate (String startDateString) {
		int startDateInt = DateHandler.changeFromDateStringToDDMMYYYY(startDateString);
		
		int startDay = DateHandler.decodeDayFromDate(startDateInt);
		int startMonth = DateHandler.decodeMonthFromDate(startDateInt);
		int startYear = DateHandler.decodeYearFromDate(startDateInt);
		
		_startDate = new GregorianCalendar(startYear, startMonth + CALENDAR_MONTH_OFFSET, startDay, NUM_0_HOUR, NUM_0_MIN, NUM_0_SEC);
	}
	
	public void setDueDate (String dueDateString) {
		int dueDateInt = DateHandler.changeFromDateStringToDDMMYYYY(dueDateString);
		
		int dueDay = DateHandler.decodeDayFromDate(dueDateInt);
		int dueMonth = DateHandler.decodeMonthFromDate(dueDateInt);
		int dueYear = DateHandler.decodeYearFromDate(dueDateInt);
		
		_dueDate = new GregorianCalendar(dueYear, dueMonth + CALENDAR_MONTH_OFFSET, dueDay, NUM_23_HOUR, NUM_59_MIN, NUM_59_SEC);
		
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
	
	public Calendar getStartDate() {
		return _startDate;
	}
	
	public Calendar getDueDate() {
		return _dueDate;
	}
	
	public String getStartDateString() {
		int day = _startDate.get(Calendar.DAY_OF_MONTH);
		int month = _startDate.get(Calendar.MONTH) - CALENDAR_MONTH_OFFSET;
		int year = _startDate.get(Calendar.YEAR);
		String dateString;
		if(month < 10) {
			dateString = String.valueOf(day) + "0" + String.valueOf(month) + String.valueOf(year);
		}
		else {
			dateString = String.valueOf(day) + String.valueOf(month) + String.valueOf(year);
		}
		return dateString;
	}
	
	public String getDueDateString() {
		int day = _dueDate.get(Calendar.DAY_OF_MONTH);
		int month = _dueDate.get(Calendar.MONTH) - CALENDAR_MONTH_OFFSET;
		int year = _dueDate.get(Calendar.YEAR);
		String dateString;
		if(month < 10) {
			dateString = String.valueOf(day) + "0" + String.valueOf(month) + String.valueOf(year);
		}
		else {
			dateString = String.valueOf(day) + String.valueOf(month) + String.valueOf(year);
		}
		return dateString;
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
