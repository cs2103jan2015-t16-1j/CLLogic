import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

public class DatePlayPlay { 
	
	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		/*
		Calendar date1 = new GregorianCalendar(1990, 7, 16);
		Calendar date2 = new GregorianCalendar();
		System.out.println(sdf.format(date1.getTime()));
		System.out.println(sdf.format(date2.getTime()));
		
		Task newtask = new Task("1");
		newtask.setDueDate("1205");
		newtask.setStartDate("12052009");
		System.out.println(sdf.format(newtask.getDueDate().getTime()));
		System.out.println(sdf.format(newtask.getStartDate().getTime()));
		
		LinkedList<Task> list = new LinkedList<Task>();
		list.add(new Task("task 1"));
		list.add(new Task("task 2"));
		list.add(new Task("task 2"));
		
		Task taskToRemove = list.peek();
		
		list.remove(taskToRemove);
		
		System.out.println(list.peek().getName());
		*/
		
		/*
		Task task = new Task("one");
		System.out.println(task.getIsOverdue());
		System.out.println(task.getIsCompleted());
		
		task.setDueDate("2502");
		Calendar today = new GregorianCalendar();
		System.out.println(task.getIsOverdue());
		task.setDueDate("2602");
		System.out.println(task.getIsOverdue());
		task.setDueDate("2402");
		System.out.println(task.getIsOverdue());
		*/
		
		LinkedList<Task> list = new LinkedList<Task>();
		LinkedList<Task> listM = new LinkedList<Task>();
		TaskAction add = new AddAction(new Task("one"));
		
		add.execute(list, listM);
		
		System.out.println(list.getFirst().getName());
		
	}
}
