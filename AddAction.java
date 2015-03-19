import java.util.LinkedList;

public class AddAction extends TaskAction {
	
	Task _newTask; 
	
	public AddAction(Task newTask) {
		_newTask = newTask;
	}

	public void execute(LinkedList<Task> workingList,
			LinkedList<Task> workingListMaster) {
		workingList.add(_newTask);
		workingListMaster.add(_newTask);
	}
}
