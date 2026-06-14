/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package messagemanager;

import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;

public class MessegeManager{

    private static int totalMessages = 0;

    public static void main(String[] args) {

        boolean loggedIn = true;

        if (loggedIn) {
            JOptionPane.showMessageDialog(null, "Welcome to QuickChat");
        } else {
            JOptionPane.showMessageDialog(null, "Login failed");
            return;
        }

        ArrayList<Task> taskList = new ArrayList<>();
        ArrayList<Message> savedMessages = new ArrayList<>();

        int totalHours = 0;

        while (true) {

            String menu = JOptionPane.showInputDialog(
                    "Choose an option:\n\n"
                    + "1. Add Task and Message\n"
                    + "2. View Saved Messages\n"
                    + "3. Show Total Messages Sent\n"
                    + "4. Export Messages to JSON\n"
                    + "5. Exit");

            if (menu == null) {
                break;
            }

            switch (menu) {

                case "1":

                    String taskMessage = JOptionPane.showInputDialog("How many messages do you want to send?");
                    String taskName = JOptionPane.showInputDialog("Task Name:");
                    String taskDescription = JOptionPane.showInputDialog("Task Description:");
                    String developer = JOptionPane.showInputDialog("Developer Full Name:");

                    int duration;

                    try {
                        duration = Integer.parseInt(
                                JOptionPane.showInputDialog("Enter task duration in hours:"));
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Invalid duration entered.");
                        break;
                    }

                    String recipient = JOptionPane.showInputDialog("Enter recipient phone number:");

                    if (recipient == null || recipient.length() > 10 || !recipient.startsWith("+")) {
                        JOptionPane.showMessageDialog(null, "Invalid recipient number");
                        break;
                    }

                    String message = JOptionPane.showInputDialog("Enter a message (max 250 characters):");

                    if (message == null) {
                        break;
                    }

                    if (message.length() > 250) {
                        JOptionPane.showMessageDialog(null,
                                "Please enter a message of less than 250 characters.");
                        break;
                    }

                    totalMessages++;

                    String messageID = generateMessageID();
                    String hash = createHash(messageID, totalMessages, message);

                    String[] statuses = {"To Do", "Doing", "Done"};

                    String status = (String) JOptionPane.showInputDialog(
                            null,
                            "Select status:",
                            "Status",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            statuses,
                            statuses[0]);

                    Task task = new Task(
                            taskName,
                            taskList.size(),
                            taskDescription,
                            developer,
                            duration,
                            status);

                    if (!task.isDescriptionValid()) {
                        JOptionPane.showMessageDialog(null,
                                "Task description must be 50 characters or fewer.");
                        break;
                    }

                    String msgChoice = JOptionPane.showInputDialog(
                            "Choose:\n"
                            + "yes = Send Message\n"
                            + "save = Store Message\n"
                            + "disregard = Delete Message");

                    if (msgChoice == null) {
                        break;
                    }

                    msgChoice = msgChoice.toLowerCase();

                    switch (msgChoice) {

                        case "yes":

                            JOptionPane.showMessageDialog(null,
                                    "Message Successfully Sent\n\n"
                                    + "Message ID: " + messageID
                                    + "\nMessage Hash: " + hash
                                    + "\nRecipient: " + recipient
                                    + "\nMessage: " + message);

                            break;

                        case "save":

                            savedMessages.add(new Message(message, recipient));

                            JOptionPane.showMessageDialog(null,
                                    "Message successfully stored");

                            break;

                        case "disregard":

                            JOptionPane.showMessageDialog(null,
                                    "Message deleted");

                            break;

                        default:

                            JOptionPane.showMessageDialog(null,
                                    "Invalid choice");

                            break;
                    }

                    taskList.add(task);
                    totalHours += task.getTaskDuration();

                    JOptionPane.showMessageDialog(null,
                            task.getTaskDetails());

                    break;

                case "2":

                    if (savedMessages.isEmpty()) {

                        JOptionPane.showMessageDialog(null,
                                "No saved messages found.");

                    } else {

                        StringBuilder msgOutput = new StringBuilder(
                                "Saved Messages:\n\n");

                        for (Message m : savedMessages) {

                            msgOutput.append("To: ")
                                    .append(m.phoneNumber)
                                    .append("\nMessage: ")
                                    .append(m.content)
                                    .append("\n\n");
                        }

                        JOptionPane.showMessageDialog(null,
                                msgOutput.toString());
                    }

                    break;

                case "3":

                    JOptionPane.showMessageDialog(null,
                            "Total Messages Sent: " + totalMessages
                            + "\nTotal Task Hours: " + totalHours);

                    break;

                case "4":

                    StringBuilder jsonOutput = new StringBuilder("[\n");

                    for (int i = 0; i < savedMessages.size(); i++) {

                        Message m = savedMessages.get(i);

                        jsonOutput.append("  {\n")
                                .append("    \"phoneNumber\": \"")
                                .append(m.phoneNumber)
                                .append("\",\n")
                                .append("    \"message\": \"")
                                .append(m.content.replace("\"", "\\\""))
                                .append("\"\n")
                                .append("  }");

                        if (i != savedMessages.size() - 1) {
                            jsonOutput.append(",");
                        }

                        jsonOutput.append("\n");
                    }

                    jsonOutput.append("]");

                    JOptionPane.showMessageDialog(null,
                            "Exported JSON:\n\n" + jsonOutput);

                    break;

                case "5":

                    JOptionPane.showMessageDialog(null,
                            "Good-Bye");

                    return;

                default:

                    JOptionPane.showMessageDialog(null,
                            "Invalid option");
            }
        }
    }

    public static String generateMessageID() {

        Random rand = new Random();

        long number = 1000000000L
                + (long) (rand.nextDouble() * 9000000000L);

        return String.valueOf(number);
    }

    public static String createHash(String id, int msgNum, String message) {

        String[] words = message.split(" ");

        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        String firstTwo = id.substring(0, 2);

        return (firstTwo + ":" + msgNum + ":" + firstWord + lastWord)
                .toUpperCase();
    }

    static class Message {

        String content;
        String phoneNumber;

        public Message(String content, String phoneNumber) {
            this.content = content;
            this.phoneNumber = phoneNumber;
        }
    }

    static class Task {

        private String taskName;
        private int taskNumber;
        private String taskDescription;
        private String developer;
        private int taskDuration;
        private String status;

        public Task(String taskName,
                    int taskNumber,
                    String taskDescription,
                    String developer,
                    int taskDuration,
                    String status) {

            this.taskName = taskName;
            this.taskNumber = taskNumber;
            this.taskDescription = taskDescription;
            this.developer = developer;
            this.taskDuration = taskDuration;
            this.status = status;
        }

        public boolean isDescriptionValid() {
            return taskDescription.length() <= 50;
        }

        public int getTaskDuration() {
            return taskDuration;
        }

        public String getTaskDetails() {

            return "Task Name: " + taskName
                    + "\nTask Number: " + taskNumber
                    + "\nTask Description: " + taskDescription
                    + "\nDeveloper: " + developer
                    + "\nDuration: " + taskDuration + " hours"
                    + "\nStatus: " + status;
        }
    }
}

     
 