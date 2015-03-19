import java.util.LinkedList;

public abstract class TaskAction {
	
	public abstract void execute(LinkedList<Task> workingList,
			LinkedList<Task> workingListMaster);
}
