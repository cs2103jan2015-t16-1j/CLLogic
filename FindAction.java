import java.util.LinkedList;

public class FindAction extends Action {
	
	public static final ActionType _type= ActionType.FIND;
	private LinkedList<Field> _fields;
	
	public FindAction(LinkedList<Field> fields) {
		_fields = fields;
	}
	
	@Override
	public void execute(LinkedList<Task> workingList,
			LinkedList<Task> workingListMaster) {
		// TODO Auto-generated method stub

	}

}
