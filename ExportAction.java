import java.util.LinkedList;

public class ExportAction extends SyncAction {

	public ExportAction(String userName) {
		setUserName(userName);
		setSuccess(false);
		this._feedback = new StringBuilder();
		this._type = ActionType.EXPORT;
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
				QLGoogleIntegration.syncTo(userName, "quicklyst", masterList);
				getFeedback().append("Synced to Google Calendar. ");
				setSuccess(true);
			} catch (Error e) {
				getFeedback().append(e.getMessage());
			}
		}
	}
	

}
