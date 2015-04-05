import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

public class QLLogic {

	private static final String MESSAGE_NOTHING_TO_REDO = "Nothing to redo. ";
	private static final String MESSAGE_NOTHING_TO_UNDO = "Nothing to undo. ";

	public static LinkedList<Task> _workingList; // TODO change back to private
	private static LinkedList<Task> _workingListMaster;
	private static Stack<LinkedList<Task>> _undoStack;
	private static Stack<LinkedList<Task>> _redoStack;
	private static String _filepath;

	/** General methods **/
	public static void setup(String fileName) {
		_filepath = fileName;
		_undoStack = new Stack<LinkedList<Task>>();
		_redoStack = new Stack<LinkedList<Task>>();
		_workingList = QLStorage.loadFile(new LinkedList<Task>(), fileName);
		_workingListMaster = new LinkedList<Task>();
		copyList(_workingList, _workingListMaster);
		_undoStack.push(_workingListMaster);
		_undoStack.push(_workingList);
	}

	// Stub
	public static void setupStub() {
		_undoStack = new Stack<LinkedList<Task>>();
		_redoStack = new Stack<LinkedList<Task>>();
		_workingList = new LinkedList<Task>();
		_workingListMaster = new LinkedList<Task>();
		_undoStack.push(new LinkedList<Task>());
		_undoStack.push(new LinkedList<Task>());
	}

	// stub
	private static void printStack(Stack<LinkedList<Task>> stack) {
		Stack<LinkedList<Task>> buffer = new Stack<LinkedList<Task>>();
		int stackCount = 0;
		while (!stack.isEmpty()) {
			stackCount++;
			buffer.push(stack.pop());
			LinkedList<Task> list = buffer.peek();
			if (stackCount % 2 != 0) {
				System.out.println("Stack " + stackCount);
				for (Task task : list) {
					System.out.println(task.getName());
				}
			}
		}

		while (!buffer.isEmpty()) {
			stack.push(buffer.pop());
		}
	}

	// Stub
	public static void displayStub(StringBuilder feedback) {
		System.out.println("Feedback: " + feedback.toString());
		System.out.println("Name: start date: due date:");
		for (int i = 0; i < _workingList.size(); i++) {
			System.out.print(_workingList.get(i).getName() + " ");
			try {
				System.out
						.print(_workingList.get(i).getStartDateString() + " ");
			} catch (NullPointerException e) {
				System.out.print("        ");
			}
			try {
				System.out.print(_workingList.get(i).getDueDateString() + " ");
			} catch (NullPointerException e) {
				System.out.print("        ");
			}
			if (_workingList.get(i).getPriority() != null) {
				System.out.print(_workingList.get(i).getPriority() + " ");
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
		return _workingList;
	}

	public static LinkedList<Task> getFullList() {
		return _workingListMaster;
	}

	public static void executeCommand(String command, StringBuilder feedback) {

		if (command.trim().equalsIgnoreCase("undo")
				|| command.trim().equalsIgnoreCase("u")) {
			undo(feedback);
			printStack(_undoStack);
			return;
		}

		if (command.trim().equalsIgnoreCase("redo")
				|| command.trim().equalsIgnoreCase("r")) {
			redo(feedback);
			printStack(_undoStack);
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
									_workingListMaster);
							feedback.append("Synced from Google Calendar. ");
						} else if (toFrom.equalsIgnoreCase("to")) {
							QLGoogleIntegration.syncTo(userID, "quicklyst",
									_workingListMaster);
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
					QLStorage.saveFile(_workingListMaster, filepath);
					feedback.append("Saved to: " + filepath);
				} catch (Error e) {
					feedback.append(e.getMessage());
				}
				return;
			}
		}

		CommandParser cp = new CommandParser(command);
		feedback.append(cp.getFeedback().toString());

		// test
		for (Field field : cp.getFields()) {
			System.out.println(field);
		}

		Action action = cp.getAction();
		feedback.append(cp.getFeedback());
		if(action == null) {
			
			return;
		}
		action.execute(_workingList, _workingListMaster);
		feedback.append(action.getFeedback().toString());
		
		if (action.isSuccess()) {
			QLStorage.saveFile(_workingListMaster, _filepath);
			updateUndoStack();
		}

		// printStack(_undoStack);
	}

	/** Multi-command methods **/

	private static <E> void copyList(LinkedList<E> fromList,
			LinkedList<E> toList) {
		toList.clear();
		for (int i = 0; i < fromList.size(); i++)
			toList.add(fromList.get(i));
	}

	private static void copyListWithClone(LinkedList<Task> list,
			LinkedList<Task> listMaster, LinkedList<Task> listNew,
			LinkedList<Task> listMasterNew) {
		
		LinkedList<Integer> indexesInListMasterForRepeatTask = new LinkedList<Integer>();
		for (int i = 0; i < list.size(); i++) {
			indexesInListMasterForRepeatTask
					.add(listMaster.indexOf(list.get(i)));
		}
		for (int i = 0; i < listMaster.size(); i++) {
			listMasterNew.add(listMaster.get(i).clone());
		}
		for (int i = 0; i < indexesInListMasterForRepeatTask.size(); i++) {
			listNew.add(listMasterNew.get(indexesInListMasterForRepeatTask
					.get(i)));
		}
	}

	private static void updateUndoStack() {
		LinkedList<Task> workingListMaster = new LinkedList<Task>();
		LinkedList<Task> workingList = new LinkedList<Task>();
		copyListWithClone(_workingList, _workingListMaster, workingList,
				workingListMaster);

		_undoStack.push(workingListMaster);
		_undoStack.push(workingList);
		_redoStack.clear();
	}

	private static void undo(StringBuilder feedback) {
		if (_undoStack.size() == 2) {
			feedback.append(MESSAGE_NOTHING_TO_UNDO);
			return;
		}
		_redoStack.push(_undoStack.pop());
		_redoStack.push(_undoStack.pop());

		_workingList = _undoStack.pop();
		_workingListMaster = _undoStack.pop();
		LinkedList<Task> updatedWL = new LinkedList<Task>();
		LinkedList<Task> updatedWLM = new LinkedList<Task>();

		copyListWithClone(_workingList, _workingListMaster, updatedWL,
				updatedWLM);

		_undoStack.push(_workingListMaster);
		_undoStack.push(_workingList);

		_workingList = updatedWL;
		_workingListMaster = updatedWLM;

		QLStorage.saveFile(_workingListMaster, _filepath);
	}

	private static void redo(StringBuilder feedback) {
		if (_redoStack.isEmpty()) {
			feedback.append(MESSAGE_NOTHING_TO_REDO);
			return;
		}
		
		_workingListMaster = _redoStack.pop();
		_workingList = _redoStack.pop();

		LinkedList<Task> updatedWL = new LinkedList<Task>();
		LinkedList<Task> updatedWLM = new LinkedList<Task>();

		copyListWithClone(_workingList, _workingListMaster, updatedWL,
				updatedWLM);

		_undoStack.push(_workingListMaster);
		_undoStack.push(_workingList);

		_workingList = updatedWL;
		_workingListMaster = updatedWLM;

		QLStorage.saveFile(_workingListMaster, _filepath);
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
