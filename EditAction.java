import java.util.Calendar;
import java.util.LinkedList;

public class EditAction extends Action {

	private int _taskIndex;
	private LinkedList<Field> _fields;
	private Task _task;

	public EditAction(int taskNumber, LinkedList<Field> fields) {

		this._feedback = new StringBuilder();
		this._type = ActionType.EDIT;

		if (taskNumber != 0) {
			_taskIndex = taskNumber - 1;
		} else {
			_taskIndex = -1;
		}

		_fields = fields;
	}

	public EditAction(Task task, LinkedList<Field> fields) {

		this._feedback = new StringBuilder();
		this._type = ActionType.EDIT;
		_task = task;
		_fields = fields;
	}

	@Override
	public void execute(LinkedList<Task> workingList,
			LinkedList<Task> workingListMaster) {

		if (isTaskIndexInRange(workingList)) {
			_task = workingList.get(_taskIndex);
			execute();
		} else {
			this._feedback.append("Task # out of range. ");
		}
	}

	public void execute() {
		for (Field field : _fields) {
			FieldType fieldType = field.getFieldType();
			switch (fieldType) {
			case TASK_NAME:
				_task.setName(field.getTaskName());
				this._feedback.append("Task name set to \""
						+ field.getTaskName() + "\". ");
				break;
			case START_DATE:
				if (field.shouldClearDate()) {
					_task.setStartDate((Calendar) null);
					this._feedback.append("Start date cleared. ");
					break;
				} else if (field.getDate() == null) {
					break;
				} else if (_task.getDueDate() != null
						&& field.getDate().compareTo(_task.getDueDate()) > 0) {
					this._feedback
							.append("Start date/time entered is bigger than due date of task. ");
					break;
				} else if (field.getDateParsed() && field.getTimeParsed()
						&& field.getDate().compareTo(_task.getDueDate()) > 0) {
					_task.setStartDate(field.getDate());
					this._feedback.append("Start date set to "
							+ _task.getStartDateString() + ". ");
					break;
				} else if (field.getDateParsed() && !field.getTimeParsed()) {
					if (_task.getStartDate() == null) {
						_task.setStartDate(field.getDate());
						_task.getStartDate().set(Calendar.HOUR_OF_DAY, 0);
						_task.getStartDate().set(Calendar.MINUTE, 0);
						_task.getStartDate().set(Calendar.SECOND, 0);
					} else {
						_task.getStartDate().set(Calendar.YEAR,
								field.getDate().get(Calendar.YEAR));
						_task.getStartDate().set(Calendar.MONTH,
								field.getDate().get(Calendar.MONTH));
						_task.getStartDate().set(Calendar.DAY_OF_MONTH,
								field.getDate().get(Calendar.DAY_OF_MONTH));
					}
					this._feedback.append("Start date set to "
							+ _task.getStartDateString() + ". ");
					break;
				} else if (!field.getDateParsed() && field.getTimeParsed()) {
					if (_task.getStartDate() == null) {
						_task.setStartDate(field.getDate());
					} else {
						_task.getStartDate().set(Calendar.HOUR_OF_DAY,
								field.getDate().get(Calendar.HOUR_OF_DAY));
						_task.getStartDate().set(Calendar.MINUTE,
								field.getDate().get(Calendar.MINUTE));
						_task.getStartDate().set(Calendar.SECOND,
								field.getDate().get(Calendar.SECOND));
					}
					this._feedback.append("Start date set to "
							+ _task.getStartDateString() + ". ");
					break;
				} else {
					break;
				}

			case DUE_DATE:
				if (field.shouldClearDate()) {
					_task.setDueDate((Calendar) null);
					this._feedback.append("Due date cleared. ");
					break;
				} else if (field.getDate() == null) {
					break;
				} else if (_task.getStartDate() != null
						&& field.getDate().compareTo(_task.getStartDate()) < 0) {
					this._feedback
							.append("Due date/time entered is smaller than due date of task. ");
					break;
				} else if (field.getDateParsed() && field.getTimeParsed()) {
					_task.setDueDate(field.getDate());
					this._feedback.append("Due date set to "
							+ _task.getDueDateString() + ". ");
					break;
				} else if (field.getDateParsed() && !field.getTimeParsed()) {
					if (_task.getDueDate() == null) {
						_task.setDueDate(field.getDate());
						_task.getDueDate().set(Calendar.HOUR_OF_DAY, 23);
						_task.getDueDate().set(Calendar.MINUTE, 59);
						_task.getDueDate().set(Calendar.SECOND, 59);
					} else {
						_task.getDueDate().set(Calendar.YEAR,
								field.getDate().get(Calendar.YEAR));
						_task.getDueDate().set(Calendar.MONTH,
								field.getDate().get(Calendar.MONTH));
						_task.getDueDate().set(Calendar.DAY_OF_MONTH,
								field.getDate().get(Calendar.DAY_OF_MONTH));
					}
					this._feedback.append("Due date set to "
							+ _task.getDueDateString() + ". ");
					break;
				} else if (!field.getDateParsed() && field.getTimeParsed()) {
					if (_task.getDueDate() == null) {
						_task.setDueDate(field.getDate());
					} else {
						_task.getDueDate().set(Calendar.HOUR_OF_DAY,
								field.getDate().get(Calendar.HOUR_OF_DAY));
						_task.getDueDate().set(Calendar.MINUTE,
								field.getDate().get(Calendar.MINUTE));
						_task.getDueDate().set(Calendar.SECOND,
								field.getDate().get(Calendar.SECOND));
					}
					this._feedback.append("Due date set to "
							+ _task.getDueDateString() + ". ");
					break;
				} else {
					break;
				}
			case REMINDER:
				/*
				 * not implemented yet _task.setReminder(field.getDate());
				 * this._feedback.append("Reminder set to " +
				 * _task.getReminderDateString() + ". ");
				 */
				break;
			case PRIORITY:
				if (field.getPriority() == null) {
					break;
				} else if (field.getPriority().equalsIgnoreCase("clr")) {
					_task.setPriority((String) null);
					this._feedback.append("Priority cleared. ");
					break;
				} else {
					_task.setPriority(field.getPriority());
					this._feedback.append("Priority set to \""
							+ field.getPriority() + "\". ");
					break;
				}
			default:
				break;
			}
		}
	}

	private boolean isTaskIndexInRange(LinkedList<Task> workingList) {
		if (_taskIndex < 0 || _taskIndex >= workingList.size()) {
			return false;
		} else {
			return true;
		}
	}

}
