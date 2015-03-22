import java.util.LinkedList;

public class AddAction extends Action {
	
	private Task _newTask; 
	private EditAction _editAction;
	
	public AddAction(String taskName,
			LinkedList<Field> fields,
			StringBuilder feedback) {
		
		this._feedback = feedback;
		this._type= ActionType.ADD;
		
		if(taskName != null) {
			_newTask = new Task(taskName);
			
		}
			
		if(fields != null) {
			_editAction = new EditAction(_newTask, 
					fields, 
					_feedback);
		}
	}
	
	public void execute(LinkedList<Task> workingList,
			LinkedList<Task> workingListMaster) {
		
		if(_newTask == null) {
			this._feedback.append(
					"No task name entered. Nothing is added");
			return;
		}
		
		workingList.add(_newTask);
		workingListMaster.add(_newTask);
		
		this._feedback.append("Task: \"" +
				_newTask.getName() + 
				"\" added. ");
		
		if(_editAction != null) {
			_editAction.execute();
		}
	}
}
