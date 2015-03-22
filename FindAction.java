import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

public class FindAction extends Action {

	private LinkedList<Field> _fields;

	public FindAction(LinkedList<Field> fields, StringBuilder feedback) {

		this._feedback = feedback;
		this._type = ActionType.FIND;
		_fields = fields;
	}

	@Override
	public void execute(LinkedList<Task> workingList,
			LinkedList<Task> workingListMaster) {
		execute(workingList);
	}

	private void execute(LinkedList<Task> workingList) {

		LinkedList<Task> backUpList = duplicateList(workingList);

		for (Field field : _fields) {
			filterWorkingList(field, workingList);
			
			if (workingList.isEmpty()) {
				_feedback.append("No matches found. ");
				workingList = backUpList;
				break;
			}
		}
		
		_feedback.append(workingList.size() + "matches found. ");
	}

	private void filterWorkingList(Field field, LinkedList<Task> workingList) {

		FieldType fieldType = field.getFieldType();
		FieldCriteria criteria = field.getCriteria();
		switch (fieldType) {
		case DUE_DATE:
		case START_DATE:
			filterByDate(field, workingList);
			break;
		case PRIORITY:
			String priority = field.getPriority();
			filterByPriority(priority, workingList);
			break;
		case COMPLETED:
			filterByCompleteStatus(criteria, workingList);
			break;
		case OVERDUE:
			filterByOverdueStatus(criteria, workingList);
			break;
		case TASK_NAME:
			String taskName = field.getTaskName();
			filterByName(taskName, workingList);
			break;
		default:
			break;
		}
	}

	private void filterByName(String taskName, LinkedList<Task> workingList) {
		if(taskName == null) {
			this._feedback.append("No task name keywords entered. ");
			return;
		}
		String keywords[] = taskName.split(" ");
		
		LinkedList<Object[]> tasksWithMatchScore = new LinkedList<Object[]>();
		
		for(Task currTask: workingList) {
			int matchScore = 0;
			for(String keyword: keywords) {
				keyword = keyword.trim();
				matchScore = matchKeywordScore(currTask, keyword);
			}
			if(matchScore != 0) {
				tasksWithMatchScore.add(new Object[]{currTask, Integer.valueOf(matchScore)});
			}
		}
		workingList = sortFoundTasksByMatchScore(tasksWithMatchScore);
	}

	private LinkedList<Task> sortFoundTasksByMatchScore(
			LinkedList<Object[]> tasksWithMatchScore) {

		for (int i = tasksWithMatchScore.size() - 1; i >= 0; i--) {
			boolean isSorted = true;
			for (int j = 0; j < i; j++) {
				Object[] taskLeft = tasksWithMatchScore.get(j);
				Object[] taskRight = tasksWithMatchScore.get(j + 1);
				if ((int) taskLeft[1] < (int) taskRight[1]) {
					tasksWithMatchScore.set(j + 1, taskLeft);
					tasksWithMatchScore.set(j, taskRight);
					isSorted = false;
				}
			}
			if (isSorted) {
				break;
			}
		}

		LinkedList<Task> newWorkingList = new LinkedList<Task>();
		for (Object[] taskWithMatchScore : tasksWithMatchScore) {
			newWorkingList.add((Task) taskWithMatchScore[0]);
		}
		return newWorkingList;
	}

	private int matchKeywordScore(Task currTask, String keyword) {
		String[] taskNameWords = currTask.getName().split(" ");
		int totalScore = 0;
		for (String currWord : taskNameWords) {
			currWord = currWord.trim();
			if (currWord.contains(keyword)) {
				totalScore++;
			}
			if (currWord.equals(keyword)) {
				totalScore++;
			}
		}
		return totalScore;
	}

	private void filterByOverdueStatus(FieldCriteria criteria,
			LinkedList<Task> workingList) {

		LinkedList<Task> bufferList = new LinkedList<Task>();
		for (Task currTask : workingList) {
			if ((currTask.getIsOverdue() && criteria == FieldCriteria.YES)
					|| (!currTask.getIsOverdue() && criteria == FieldCriteria.NO)) {
				bufferList.add(currTask);
			}
		}
		workingList = duplicateList(bufferList);
	}

	private void filterByCompleteStatus(FieldCriteria criteria,
			LinkedList<Task> workingList) {

		LinkedList<Task> bufferList = new LinkedList<Task>();
		for (Task currTask : workingList) {
			if ((currTask.getIsCompleted() && criteria == FieldCriteria.YES)
					|| (!currTask.getIsCompleted() && criteria == FieldCriteria.NO)) {
				bufferList.add(currTask);
			}
		}
		workingList = duplicateList(bufferList);
	}

	private void filterByPriority(String priority, LinkedList<Task> workingList) {
		LinkedList<Task> bufferList = new LinkedList<Task>();
		for (Task currTask : workingList) {
			if (currTask.getPriority() != null
					&& currTask.getPriority().equalsIgnoreCase(priority)) {
				bufferList.add(currTask);
			}
		}
		workingList = duplicateList(bufferList);
	}

	private void filterByDate(Field field, LinkedList<Task> workingList) {

		FieldType fieldType = field.getFieldType();
		FieldCriteria criteria = field.getCriteria();

		switch (criteria) {
		case BEFORE:
		case AFTER:
		case ON:
			Calendar date = field.getDate();
			filterBySingleDate(date, fieldType, criteria, workingList);
			break;
		case BETWEEN:
			Calendar[] dateRange = field.getDateRange();
			filterByDateRange(dateRange, fieldType, criteria, workingList);
			break;
		default:
			break;
		}
	}

	private void filterByDateRange(Calendar[] dateRange, FieldType fieldType,
			FieldCriteria criteria, LinkedList<Task> workingList) {

		Calendar fromDate = dateRange[0];
		Calendar toDate = dateRange[1];

		fromDate.set(Calendar.HOUR, 0);
		fromDate.set(Calendar.MINUTE, 0);
		fromDate.set(Calendar.SECOND, 0);

		toDate.set(Calendar.HOUR, 23);
		toDate.set(Calendar.MINUTE, 59);
		toDate.set(Calendar.SECOND, 59);

		if (fromDate.compareTo(toDate) > 0) {
			this._feedback
					.append("Invalid date range. Start date is later than end date. ");
			return;
		}

		LinkedList<Task> bufferList = new LinkedList<Task>();

		for (Task currTask : workingList) {

			Calendar currTaskDate;

			switch (fieldType) {
			case START_DATE:
				currTaskDate = currTask.getStartDate();
				break;
			case DUE_DATE:
				currTaskDate = currTask.getDueDate();
				break;
			default:
				currTaskDate = null;
				break;
			}

			if (currTaskDate != null && currTaskDate.compareTo(fromDate) >= 0
					&& currTaskDate.compareTo(toDate) <= 0) {
				bufferList.add(currTask);
			}
		}

		workingList = duplicateList(bufferList);
	}

	private void filterBySingleDate(Calendar date, FieldType fieldType,
			FieldCriteria criteria, LinkedList<Task> workingList) {

		LinkedList<Task> bufferList = new LinkedList<Task>();

		for (Task currTask : workingList) {

			Calendar currTaskDate;

			switch (fieldType) {
			case START_DATE:
				currTaskDate = currTask.getStartDate();
				break;
			case DUE_DATE:
				currTaskDate = currTask.getDueDate();
				break;
			default:
				currTaskDate = null;
				break;
			}

			if (currTaskDate != null) {
				switch (criteria) {
				case BEFORE:
					date.set(Calendar.HOUR_OF_DAY, 23);
					date.set(Calendar.MINUTE, 59);
					date.set(Calendar.SECOND, 59);
					if (currTaskDate.compareTo(date) <= 0) {
						bufferList.add(currTask);
					}
					break;
				case AFTER:
					date.set(Calendar.HOUR_OF_DAY, 0);
					date.set(Calendar.MINUTE, 0);
					date.set(Calendar.SECOND, 0);
					if (currTaskDate.compareTo(date) >= 0) {
						bufferList.add(currTask);
					}
					break;
				case ON:
					date.set(Calendar.HOUR_OF_DAY, 0);
					date.set(Calendar.MINUTE, 0);
					date.set(Calendar.SECOND, 0);
					currTaskDate = new GregorianCalendar(
							currTaskDate.get(Calendar.YEAR),
							currTaskDate.get(Calendar.MONTH),
							currTaskDate.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
					if (currTaskDate.equals(date)) {
						bufferList.add(currTask);
					}
					break;
				default:
					break;
				}
			}
		}

		workingList = duplicateList(bufferList);
	}

	private <E> LinkedList<E> duplicateList(LinkedList<E> original) {
		LinkedList<E> duplicated = new LinkedList<E>();
		for (E e : original) {
			duplicated.add(e);
		}
		return duplicated;
	}

}
