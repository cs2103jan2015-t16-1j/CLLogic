import java.util.LinkedList;

public class ImportAction extends SyncAction {

	public ImportAction(String userName) {
		setUserName(userName);
		setSuccess(false);
		this._feedback = new StringBuilder();
		this._type = ActionType.IMPORT;
	}

	@Override
	public void execute(LinkedList<Task> displayList,
			LinkedList<Task> masterList) {

		String userName = getUserName();
		if (userName == null || userName.isEmpty()) {
			getFeedback().append("User name is blank. ");
			return;
		} else {
			try {
				QLGoogleIntegration.syncFrom(userName, "quicklyst", masterList);
				copyList(masterList, displayList);
				getFeedback().append("Synced from Google Calendar. ");
				setSuccess(true);
			} catch (Error e) {
				getFeedback().append(e.getMessage());
			}
		}
	}

	private static <E> void copyList(LinkedList<E> fromList,
			LinkedList<E> toList) {
		toList.clear();
		for (int i = 0; i < fromList.size(); i++)
			toList.add(fromList.get(i));
	}
}
