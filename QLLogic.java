import java.util.LinkedList;
import java.util.Scanner;

public class QLLogic {

	public static LinkedList<Task> _displayList; // TODO change back to private
	private static LinkedList<Task> _masterList;
	private static String _filepath;
	
	private static HistoryManager _historyMgnr;

	/** General methods **/
	public static void setup(String fileName) {
		_filepath = fileName;
		_displayList = QLStorage.loadFile(new LinkedList<Task>(), fileName);
		_masterList = new LinkedList<Task>();
		copyList(_displayList, _masterList);
		_historyMgnr = new HistoryManager(_displayList, _masterList);
	}

	// Stub
	public static void setupStub() {
		_displayList = new LinkedList<Task>();
		_masterList = new LinkedList<Task>();
		_historyMgnr = new HistoryManager(_displayList, _masterList);
	}

	// Stub
	public static void displayStub(StringBuilder feedback) {
		System.out.println("Feedback: " + feedback.toString());
		System.out.println("Name: start date: due date:");
		for (int i = 0; i < _displayList.size(); i++) {
			System.out.print(_displayList.get(i).getName() + " ");
			try {
				System.out
						.print(_displayList.get(i).getStartDateString() + " ");
			} catch (NullPointerException e) {
				System.out.print("        ");
			}
			try {
				System.out.print(_displayList.get(i).getDueDateString() + " ");
			} catch (NullPointerException e) {
				System.out.print("        ");
			}
			if (_displayList.get(i).getPriority() != null) {
				System.out.print(_displayList.get(i).getPriority() + " ");
			}
			System.out.println();
		}

		/*
		 * System.out.println("	workingListMaster: "); for(int i = 0; i <
		 * _workingListMaster.size(); i++) {
		 * System.out.print(_workingListMaster.get(i).getName() + " "); try {
		 * System.out.print(_workingListMaster.get(i).getStartDateString() +
		 * " "); } catch(NullPointerException e) {} try {
		 * System.out.print(_workingListMaster.get(i).getDueDateString() + " ");
		 * } catch(NullPointerException e) {}
		 * if(_workingListMaster.get(i).getPriority() != null) {
		 * System.out.print(_workingListMaster.get(i).getPriority() + " "); }
		 * System.out.println(); }
		 */

		System.out.println();
		feedback.setLength(0);
	}

	public static LinkedList<Task> getDisplayList() {
		return _displayList;
	}

	public static LinkedList<Task> getFullList() {
		return _masterList;
	}

	public static void executeCommand(String command, StringBuilder feedback) {

		if (command.trim().equalsIgnoreCase("undo")
				|| command.trim().equalsIgnoreCase("u")) {
			_historyMgnr.undo(feedback);
			_displayList = _historyMgnr.getDisplayList();
			_masterList = _historyMgnr.getMasterList();
			QLStorage.saveFile(_masterList, _filepath);
			return;
		}

		if (command.trim().equalsIgnoreCase("redo")
				|| command.trim().equalsIgnoreCase("r")) {
			_historyMgnr.redo(feedback);
			_displayList = _historyMgnr.getDisplayList();
			_masterList = _historyMgnr.getMasterList();
			QLStorage.saveFile(_masterList, _filepath);
			return;
		}

		if (command.indexOf(' ') != -1) {
			String commandType = command.substring(0, command.indexOf(' '))
					.trim();
			if (commandType.equalsIgnoreCase("sync")
					|| commandType.equalsIgnoreCase("sg")) {

				String content = command.replaceFirst(commandType, "").trim();

				if (content.indexOf(' ') != -1) {
					String toFrom = content.substring(0, content.indexOf(' '))
							.trim();
					String userID = content.replaceFirst(toFrom, "").trim();

					try {
						if (toFrom.equalsIgnoreCase("from")) {
							QLGoogleIntegration.syncFrom(userID, "quicklyst",
									_masterList);
							feedback.append("Synced from Google Calendar. ");
						} else if (toFrom.equalsIgnoreCase("to")) {
							QLGoogleIntegration.syncTo(userID, "quicklyst",
									_masterList);
							feedback.append("Synced to Google Calendar. ");
						} else {
							feedback.append("Invalid sync action. ");
						}
					} catch (Error e) {
						feedback.append(e.getMessage());
					}

				} else {
					feedback.append("Invalid sync action. ");
				}

				return;
			}
		}

		if (command.indexOf(' ') != -1) {
			String commandType = command.substring(0, command.indexOf(' '))
					.trim();
			String filepath = command.replaceFirst(commandType, " ").trim();

			if (commandType.equalsIgnoreCase("load")
					|| commandType.equalsIgnoreCase("l")) {

				try {
					setup(filepath);
					feedback.append("Loaded from: " + filepath);
					return;
				} catch (Error e) {
					feedback.append(e.getMessage());
					return;
				}
			} else if (commandType.equalsIgnoreCase("save")
					|| commandType.equalsIgnoreCase("s")) {

				try {
					QLStorage.saveFile(_masterList, filepath);
					feedback.append("Saved to: " + filepath);
				} catch (Error e) {
					feedback.append(e.getMessage());
				}
				return;
			}
		}

		CommandParser cp = new CommandParser(command);
		feedback.append(cp.getFeedback().toString());

		/*
		// test
		for (Field field : cp.getFields()) {
			System.out.println(field);
		}
		*/

		Action action = cp.getAction();
		if(action == null) {
			return;
		}
		
		action.execute(_displayList, _masterList);
		feedback.append(action.getFeedback().toString());
		
		if (action.isSuccess()) {
			QLStorage.saveFile(_masterList, _filepath);
			_historyMgnr.updateUndoStack(_displayList, _masterList);
		}
	}

	/** Multi-command methods **/
	
	private static <E> void copyList(LinkedList<E> fromList,
			LinkedList<E> toList) {
		toList.clear();
		for (int i = 0; i < fromList.size(); i++)
			toList.add(fromList.get(i));
	}

	/** Main method **/
	@SuppressWarnings("resource")
	public static void main(String args[]) {
		setupStub();
		StringBuilder feedback = new StringBuilder();
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("Enter command: ");
			String command = sc.nextLine();
			executeCommand(command, feedback);
			displayStub(feedback);
		}
	}

}
