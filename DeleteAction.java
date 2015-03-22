import java.util.LinkedList;

public class DeleteAction extends Action {

	private int _taskIndex;

	public DeleteAction(int taskNumber, StringBuilder feedback) {
		this._feedback = feedback;
		this._type = ActionType.DELETE;

		if (taskNumber != 0) {
			_taskIndex = taskNumber - 1;
		} else {
			_taskIndex = -1;
		}
	}

	public void execute(LinkedList<Task> workingList,
			LinkedList<Task> workingListMaster) {

		if (isTaskIndexInRange(workingList)) {
			Task taskToDel = workingList.get(_taskIndex);
			workingList.remove(taskToDel);
			workingList.remove(taskToDel);
			this._feedback.append("Task #" + 
					(_taskIndex + 1) +
					" deleted. ");
		} else {
			this._feedback.append("Task number not in range. ");
			return;
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
