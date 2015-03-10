import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;

public class QLGUI extends JFrame{
    private static final String TITLE = "Quicklyst";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int INNER_WIDTH = 783;
    private static final int INNER_HEIGHT = 557;
    private static final int PADDING_LEFT = 10;
    private static final int PADDING_TOP = 10;
    
    private static final int TASKLIST_OFFSET_X = PADDING_LEFT;
    private static final int TASKLIST_OFFSET_Y = PADDING_TOP;
    private static final int TASKLIST_WIDTH = 390;
    private static final int TASKLIST_HEIGHT = 500;
    
    private static final int COMMAND_OFFSET_X = PADDING_LEFT;
    private static final int COMMAND_OFFSET_Y = 2*PADDING_TOP+TASKLIST_HEIGHT;
    private static final int COMMAND_WIDTH = INNER_WIDTH-2*PADDING_LEFT;
    private static final int COMMAND_HEIGHT = INNER_HEIGHT-3*PADDING_TOP-TASKLIST_HEIGHT;
    
    private final static int OVERVIEW_OFFSET_X = 2*PADDING_LEFT+TASKLIST_WIDTH;
    private final static int OVERVIEW_OFFSET_Y = PADDING_TOP;
    private final static int OVERVIEW_WIDTH = INNER_WIDTH-TASKLIST_WIDTH-3*PADDING_LEFT;
    private final static int OVERVIEW_HEIGHT = (TASKLIST_HEIGHT-PADDING_TOP)/2;
    
    private static final int FEEDBACK_OFFSET_X = 2*PADDING_LEFT+TASKLIST_WIDTH;
    private static final int FEEDBACK_OFFSET_Y = (TASKLIST_HEIGHT-PADDING_TOP)/2+2*PADDING_TOP;
    private static final int FEEDBACK_WIDTH = INNER_WIDTH-TASKLIST_WIDTH-3*PADDING_LEFT;
    private static final int FEEDBACK_HEIGHT = (TASKLIST_HEIGHT-PADDING_TOP)/2;
    
    private JPanel _taskList;
    private JLabel _overview;
    private JTextArea _feedback;
    private JTextField _command;
    
    public QLGUI() {
        super(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Container contentPane = this.getContentPane();
        SpringLayout layout = new SpringLayout();
        
        contentPane.setLayout(layout);
        
        _taskList = new JPanel(new GridBagLayout());
        JPanel taskListBorderPane = new JPanel(new BorderLayout());
        taskListBorderPane.add(_taskList, BorderLayout.NORTH);
        JScrollPane taskListScroll = new JScrollPane(taskListBorderPane);
        
        JPanel overviewPane = new JPanel(new BorderLayout());
        overviewPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
                                                            Color.BLACK));
        
        _overview = new JLabel();
        _overview.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        overviewPane.add(_overview, BorderLayout.NORTH);
        _overview.setText(String.format("<html><u>Overview</u><br>" +
                                             "%d due today<br>" +
                                             "%d due tomorrow<br>" +
                                             "%d overdue<br>" +
                                             "%d completed</html>",
                                             0, 0, 0, 0));
        
        _feedback = new JTextArea();
        _feedback.setEditable(false);
        _feedback.setLineWrap(true);
        _feedback.setWrapStyleWord(true);
        JPanel feedbackBorderPane = new JPanel(new BorderLayout());
        feedbackBorderPane.setBackground(_feedback.getBackground());
        feedbackBorderPane.add(_feedback, BorderLayout.SOUTH);
        JScrollPane feedbackScroll = new JScrollPane(feedbackBorderPane);        
       
        _command = new JTextField();
        _command.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e){
                StringBuilder fb = new StringBuilder();
                List<Task> tasks = QLLogic.executeCommand(_command.getText(), fb);
                if (!fb.toString().isEmpty()) {
                    _feedback.append(fb.toString() + "\r\n");
                }
                _taskList.removeAll();
                int i = 1;
                for (Task task : tasks)
                {
                    SpringLayout singleTaskLayout = new SpringLayout();
                    JPanel singleTaskPane = new JPanel(singleTaskLayout);
                    singleTaskPane.setBorder(new LineBorder(Color.BLACK));
                    
                    JPanel priorityColorPane = new JPanel();
                    JLabel name = new JLabel(task.getName());
                    JLabel index = new JLabel("#" + i);
                    JLabel date = new JLabel(task.getStartDate() + " - " + task.getDueDate());
                    JLabel priority = new JLabel((task.getPriority()).toString());
                    
                    switch (task.getPriority()) {
                        case "H" : 
                            priorityColorPane.setBackground(Color.RED);
                            break;
                        case "M" : 
                            priorityColorPane.setBackground(Color.ORANGE);
                            break;
                        case "L" : 
                            priorityColorPane.setBackground(Color.YELLOW);
                            break;
                        default :
                            System.out.println("Invalid priority type " + task.getPriority());
                            break;
                    }
  
                    singleTaskPane.add(priorityColorPane);
                    singleTaskPane.add(name);
                    singleTaskPane.add(index);
                    singleTaskPane.add(date);
                    singleTaskPane.add(priority);
                    
                    singleTaskLayout.putConstraint(SpringLayout.SOUTH, singleTaskPane, 5,
                                                   SpringLayout.SOUTH, date);
                    
                    singleTaskLayout.putConstraint(SpringLayout.WEST, priorityColorPane, 5,
                                                   SpringLayout.WEST, singleTaskPane);
                    singleTaskLayout.putConstraint(SpringLayout.NORTH, priorityColorPane, 5,
                                                   SpringLayout.NORTH, singleTaskPane);
                    singleTaskLayout.putConstraint(SpringLayout.SOUTH, priorityColorPane, -5,
                                                   SpringLayout.SOUTH, singleTaskPane);
                    
                    singleTaskLayout.putConstraint(SpringLayout.WEST, name, 5,
                                                   SpringLayout.EAST, priorityColorPane);
                    singleTaskLayout.putConstraint(SpringLayout.NORTH, name, 5,
                                                   SpringLayout.NORTH, singleTaskPane);
                    singleTaskLayout.putConstraint(SpringLayout.EAST, name, -5,
                                                   SpringLayout.WEST, index);
                    
                    singleTaskLayout.putConstraint(SpringLayout.EAST, index, -5,
                                                   SpringLayout.EAST, singleTaskPane);
                    singleTaskLayout.putConstraint(SpringLayout.NORTH, index, 5,
                                                   SpringLayout.NORTH, singleTaskPane);
                  
                    singleTaskLayout.putConstraint(SpringLayout.WEST, date, 5,
                                                   SpringLayout.EAST, priorityColorPane);
                    singleTaskLayout.putConstraint(SpringLayout.NORTH, date, 5,
                                                   SpringLayout.SOUTH, name);
                    singleTaskLayout.putConstraint(SpringLayout.EAST, date, -5,
                                                   SpringLayout.WEST, priority);
                    
                    singleTaskLayout.putConstraint(SpringLayout.SOUTH, priority, -5,
                                                   SpringLayout.SOUTH, singleTaskPane);
                    singleTaskLayout.putConstraint(SpringLayout.EAST, priority, -5,
                                                   SpringLayout.EAST, singleTaskPane);
                                       
                    GridBagConstraints con = new GridBagConstraints();
                    con.insets = new Insets(5, 5, 5, 5);
                    con.weightx = 1;
                    con.anchor = GridBagConstraints.NORTHEAST;
                    con.fill = GridBagConstraints.HORIZONTAL;
                    con.gridheight = 1;
                    con.gridwidth = 1;
                    con.gridx = 0;
                    con.gridy = i-1;
                    _taskList.add(singleTaskPane, con);
                    i++;
                }
                _taskList.revalidate();
                _taskList.repaint();
                
                // update the overview based on dates
                int dueToday = 0, dueTomorrow = 0, overdue = 0, completed = 0;
                Calendar now = Calendar.getInstance();
                Calendar today = (Calendar) now.clone();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);
                Calendar tomorrow = (Calendar) today.clone();
                tomorrow.add(Calendar.DAY_OF_MONTH, 1);
                Calendar twoDaysAfter = (Calendar) tomorrow.clone();
                twoDaysAfter.add(Calendar.DAY_OF_MONTH, 1);
                
                for (int j = 0; j < tasks.size(); ++j) {
                    if (tasks.get(j).getIsCompleted()) {
                        completed++;
                        continue;
                    }
                    Calendar due = tasks.get(j).getDueDate();
                    if (due == null) {
                        continue;
                    }
                    if ((due.compareTo(today) >= 0) &&
                        (due.compareTo(tomorrow) < 0)) {
                        dueToday++;
                    } else if ((due.compareTo(tomorrow) >= 0) &&
                               (due.compareTo(twoDaysAfter) < 0)) {
                        dueTomorrow++;
                    } 
                    if (due.compareTo(now) < 0) {
                        overdue++;
                    } 
                }
                
                _overview.setText(String.format("<html><u>Overview</u><br>" +
                                                "%d due today<br>" +
                                                "%d due tomorrow<br>" +
                                                "%d overdue<br>" +
                                                "%d completed</html>",
                                                dueToday, dueTomorrow, overdue, completed));
            }
          }
        );
        
        add(_command);
        add(taskListScroll);
        add(feedbackScroll);
        add(overviewPane);
        
        layout.putConstraint(SpringLayout.WEST, _command, 10,
                             SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.EAST, _command, -10,
                             SpringLayout.EAST, contentPane); 
        layout.putConstraint(SpringLayout.SOUTH, _command, -10,
                             SpringLayout.SOUTH, contentPane);
        
        layout.putConstraint(SpringLayout.WEST, taskListScroll, 10,
                             SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, taskListScroll, 10,
                             SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.SOUTH, taskListScroll, -10, 
                             SpringLayout.NORTH, _command);
        layout.getConstraints(taskListScroll).setWidth(Spring.constant(385));
        
        layout.putConstraint(SpringLayout.WEST, overviewPane, 10,
                             SpringLayout.EAST, taskListScroll);
        layout.putConstraint(SpringLayout.NORTH, overviewPane, 10,
                             SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.EAST, overviewPane, -10,
                             SpringLayout.EAST, contentPane);
        layout.getConstraints(overviewPane).setHeight(Spring.constant(220));
        
        layout.putConstraint(SpringLayout.WEST, feedbackScroll, 10,
                             SpringLayout.EAST, taskListScroll);
        layout.putConstraint(SpringLayout.NORTH, feedbackScroll, 10,
                             SpringLayout.SOUTH, overviewPane);
        layout.putConstraint(SpringLayout.SOUTH, feedbackScroll, 0,
                             SpringLayout.SOUTH, taskListScroll);
        layout.putConstraint(SpringLayout.EAST, feedbackScroll, -10,
                             SpringLayout.EAST, contentPane);
        
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setVisible(true);
 
    }
    public static void main(String[] args) {
        QLLogic.clearWorkingList();
        QLGUI g = new QLGUI();
        
    }
   
}
