package zad1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.*;

public class UTP10 {
    private JList<TasksContainer>  tasksContainerJList;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    private DefaultListModel<TasksContainer> taskListModel = new DefaultListModel<>();

    private String[] fractalTypes = {
            "Mandelbrot","Julia","Burning Ship"
    };
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> new UTP10().initialize());


    }
    public void initialize(){
        //frame
        JFrame frame = new JFrame("UTP 10");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new BorderLayout());
        frame.setMinimumSize(new Dimension(500,500));
        frame.setBackground(Color.BLACK);
        frame.setForeground(Color.BLACK);

        tasksContainerJList = new JList<>(taskListModel);
        tasksContainerJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);


        //panel and buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton addButton = new JButton("Add Task");
        JButton checkButton = new JButton("Check Status");
        JButton cancelButton = new JButton("Cancel Task");
        JButton showButton = new JButton("Show Result");

        Dimension buttonSize = new Dimension(150, 40);
        addButton.setPreferredSize(buttonSize);
        cancelButton.setPreferredSize(buttonSize);
        checkButton.setPreferredSize(buttonSize);
        showButton.setPreferredSize(buttonSize);

        addButton.setMaximumSize(buttonSize);
        cancelButton.setMaximumSize(buttonSize);
        checkButton.setMaximumSize(buttonSize);
        showButton.setMaximumSize(buttonSize);

        addButton.setMinimumSize(buttonSize);
        cancelButton.setMinimumSize(buttonSize);
        checkButton.setMinimumSize(buttonSize);
        showButton.setMinimumSize(buttonSize);

        //buttons action

        addButton.addActionListener(this::createNewTask);
        cancelButton.addActionListener(e -> stopTask(tasksContainerJList.getSelectedValue()));
        checkButton.addActionListener(e -> checkStatus(tasksContainerJList.getSelectedValue()));
        showButton.addActionListener(e -> displayResult(tasksContainerJList.getSelectedValue()));

        buttonPanel.add(addButton);
        buttonPanel.add(checkButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(showButton);





        frame.add(new JScrollPane(tasksContainerJList), BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.EAST);
        frame.setVisible(true);
    }
    private void createNewTask(ActionEvent event){
        String userInput = JOptionPane.showInputDialog("Enter size of fractal (e.g., 500 for 500x500):");
        if(userInput !=null){
            try{
                int size = Integer.parseInt(userInput);
                if(size>3000){
                    JOptionPane.showMessageDialog(null,"TOO BIG!!!");
                    return;
                }
                JComboBox<String> fractalComboBox = new JComboBox<>(fractalTypes);
                int result = JOptionPane.showConfirmDialog(
                        null,
                        fractalComboBox,
                        "Select Fractal Type",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (result == JOptionPane.OK_OPTION) {
                    String selectedFractal = (String) fractalComboBox.getSelectedItem();
                    FractalGeneration fractalGeneration = new FractalGeneration(size,selectedFractal);
                    FutureTask<BufferedImage> futureTask = new FutureTask<>(fractalGeneration);
                    TasksContainer tasksContainer = new TasksContainer(futureTask);

                    SwingUtilities.invokeLater(() -> taskListModel.addElement(tasksContainer));

                    executorService.submit(futureTask);
                }
            }catch (NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Invalid size.");

            }
        }
    }
    private void stopTask(TasksContainer tasksContainer){
        if(tasksContainer != null){
            Future<?> futureTask = tasksContainer.getTask();
            boolean wasCancelled = futureTask.cancel(true);
            SwingUtilities.invokeLater(() -> {
                String statusMessage = wasCancelled ? "The task has been stopped." : "Failed to stop the task.";
                JOptionPane.showMessageDialog(null, statusMessage);
            });
        }
    }
    private void checkStatus(TasksContainer tasksContainer){
        if (tasksContainer != null) {
            Future<?> future = tasksContainer.getTask();
            SwingUtilities.invokeLater(() -> {
                String status;
                if (future.isCancelled()) {
                    status = "Task is cancelled.";
                } else if (future.isDone()) {
                    status = "Task is completed.";
                } else {
                    status = "Task is still running.";
                }
                JOptionPane.showMessageDialog(null, "Status: " + status);
            });
        } else {
            JOptionPane.showMessageDialog(null, "No task selected.");
        }
    }
    private void displayResult(TasksContainer tasksContainer){
        if (tasksContainer != null) {
            Future<BufferedImage> futureTask = tasksContainer.getTask();
            SwingUtilities.invokeLater(() -> {
                if (futureTask.isDone()) {
                    try {
                        BufferedImage result = futureTask.get();
                        System.out.println("Displaying result with size: " + result.getWidth() + "x" + result.getHeight());
                        JFrame frame = new JFrame("Fractal Result");
                        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        frame.setSize(result.getWidth(), result.getHeight());
                        frame.add(new JLabel(new ImageIcon(result)));
                        frame.setVisible(true);
                    } catch (InterruptedException | ExecutionException ex) {
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Task is still in progress.");
                }
            });
        } else {
            JOptionPane.showMessageDialog(null, "No task selected.");
        }
    }
}
