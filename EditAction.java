import java.util.LinkedList;

public class EditAction extends Action {

	private int _taskIndex;
	private LinkedList<Field> _fields;
	private Task _task;

	public EditAction(int taskNumber, LinkedList<Field> fields,
			StringBuilder feedback) {

		this._feedback = feedback;
		this._type = ActionType.EDIT;

		if (taskNumber != 0) {
			_taskIndex = taskNumber - 1;
		} else {
			_taskIndex = -1;
		}

		_fields = fields;
	}

	public EditAction(Task task, LinkedList<Field> fields,
			StringBuilder feedback) {

		this._feedback = feedback;
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
		}
	}

	public void execute() {
		for(Field field: _fields) {
			FieldType fieldType = field.getFieldType();
			switch(fieldType) {
			case TASK_NAME:
				_task.setName(field.getTaskName());
				this._feedback.append("Task name set to \"" + 
						field.getTaskName() +
						"\". ");
				break;
			case START_DATE:
				_task.setStartDate(field.getDate());
				this._feedback.append("Start date set to " + 
						_task.getStartDateString() +
						". ");
				break;
			case DUE_DATE:
				_task.setDueDate(field.getDate());
				this._feedback.append("Due date set to " + 
						_task.getDueDateString() +
						". ");
				break;
			case REMINDER:
				/* not implemented yet
				_task.setReminder(field.getDate());
				this._feedback.append("Reminder set to " + 
						_task.getReminderDateString() +
						". ");
				*/
				break;
			case PRIORITY:
				_task.setPriority(field.getPriority());
				this._feedback.append("Priority set. ");
				break;
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
