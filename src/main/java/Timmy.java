import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Scanner;

public class Timmy {

    private int noOfTasks = 0;
    private final ArrayList<Task> tasks;
    private boolean isExceptionCaught;

    public Timmy() {
        this.tasks = new ArrayList<>();
        this.isExceptionCaught = false;
    }

    void greet() {
        System.out.println("------------------------------------------------");
        System.out.println("Hi! I'm Timmy!\nWhat can Timmy note down for you today?");
        System.out.println("Please type in any of these format!");
        System.out.println("todo [title]");
        System.out.println("event [title] /at [yyyy-mm-dd] [HH:MM]");
        System.out.println("deadline [title] /by [yyyy-mm-dd] [HH:MM]");
        System.out.println("list");
        System.out.println("delete [index]");
        System.out.println("done [index]");
        System.out.println("------------------------------------------------");
    }

    void exit() {
        System.out.println("------------------------------------------------");
        System.out.println("Bye! Hope to see you again!");
        System.out.println("------------------------------------------------");
    }

    void printList() {
        System.out.println("------------------------------------------------");
        System.out.println("Here are the tasks in your list:");

        for (int j = 0; j < noOfTasks; j++) {
            System.out.println(j + 1 + "." + tasks.get(j).toString());
        }

        System.out.println("------------------------------------------------");
    }

    void markTask(int taskIndex) throws InvalidDescriptionException{
        if (taskIndex >= noOfTasks) {
            throw new InvalidDescriptionException("");
        }

        tasks.get(taskIndex).markAsDone();
    }

    Task addTask(String taskType, String taskInfo) throws InvalidDescriptionException{
        Task task = new Task();

        switch(taskType) {
        case "todo":
        {
            task = new ToDo(taskInfo);
            break;
        }
        case "deadline":
        {
            if (!(taskInfo.contains("/by"))) {
                throw new InvalidDescriptionException("");
            }
            String[] taskInfoArr = taskInfo.split(" /by ", 2);
            if (taskInfoArr.length < 2) {
                throw new InvalidDescriptionException("");
            }
            String[] dateAndTime = taskInfoArr[1].split(" ");
            String date = parseDate(dateAndTime[0]);
            String time = parseTime(dateAndTime[1]);
            String by = date + " " + time;
            task = new Deadline(taskInfoArr[0], by);
            break;
        }
        case "event":
        {
            if (!(taskInfo.contains("/at"))) {
                throw new InvalidDescriptionException("");
            }
            String[] taskInfoArr = taskInfo.split(" /at ", 2);
            if (taskInfoArr.length < 2) {
                throw new InvalidDescriptionException("");
            }
            String[] dateAndTime = taskInfoArr[1].split(" ");
            String date = parseDate(dateAndTime[0]);
            String time = parseTime(dateAndTime[1]);
            String by = date + " " + time;
            task = new Event(taskInfoArr[0], by);
            break;
        }
        default:
        {
            System.out.println("Invalid task!");
            break;
        }
        }

        tasks.add(noOfTasks, task);
        noOfTasks++;
        return task;
    }

    void deleteTask(int taskIndex) throws InvalidDescriptionException {
        if (taskIndex >= noOfTasks) {
            throw new InvalidDescriptionException("");
        }
        System.out.println("------------------------------------------------");
        System.out.println("Ok! I've removed this task:\n" + tasks.get(taskIndex).toString());
        System.out.println("Currently, you have " + (noOfTasks - 1) + " task(s) in the list!");
        System.out.println("------------------------------------------------");

        tasks.remove(taskIndex);
        noOfTasks--;
    }

    void invalidCommandChecker(String taskType) throws InvalidCommandException {
        if (!(taskType.equals("todo") || taskType.equals("done") || taskType.equals("list") || taskType.equals("event")
                || taskType.equals("deadline") || taskType.equals("delete"))) {
            throw new InvalidCommandException("");
        }
    }

    void emptyDescriptionChecker(String[] tokens) throws EmptyDescriptionException {
        if (tokens.length < 2) {
            throw new EmptyDescriptionException("");
        }
    }

    void checkFile() {
        boolean doesTaskListExist;
        try {
            doesTaskListExist = createFile();
            if (doesTaskListExist) {
                scanTaskList();
            }
        }  catch (Exception e) {
            System.out.println("------------------------------------------------");
            System.out.println("Sorry, an error occurred!");
            System.out.println("------------------------------------------------");

            isExceptionCaught = true;
        }
    }

    void scanTaskList() throws FileNotFoundException, InvalidDescriptionException {
        File f = new File("src\\main\\java\\taskList.txt");
        Scanner sc = new Scanner(f);
        int taskIndex = 0;

        while (sc.hasNext()) {
            String input = sc.nextLine();
            if (input.contains("[T]")) {
                String[] tokens = input.split("] ", 2);
                String taskInfo = tokens[1];
                addTask("todo", taskInfo);
            } else if (input.contains("[D]")) {
                String[] tokens = input.split("] ", 2);
                String[] nextTokens = tokens[1].split(" ", 2);
                String date = nextTokens[1].substring(nextTokens[1].indexOf(':') + 2, nextTokens[1].indexOf(')'));
                Task task = new Deadline(nextTokens[0], date);
                tasks.add(noOfTasks, task);
                noOfTasks++;
            } else {
                String[] tokens = input.split("] ", 2);
                String[] nextTokens = tokens[1].split(" ", 2);
                String date = nextTokens[1].substring(nextTokens[1].indexOf(':') + 2, nextTokens[1].indexOf(')'));
                Task task = new Event(nextTokens[0], date);
                tasks.add(noOfTasks, task);
                noOfTasks++;
            }
            if (input.contains("\u2713")) {
                markTask(taskIndex);
            }
            taskIndex++;
        }
    }

    void writeToFile() throws IOException {
        FileWriter fw = new FileWriter("src\\main\\java\\taskList.txt");
        for (Task t : tasks) {
            fw.write(t + "\n");
        }
        fw.close();
    }

    boolean createFile() throws IOException {
        File myFile = new File("src\\main\\java\\taskList.txt");
        if (myFile.createNewFile()) {
            System.out.println("I have created a file that stores your tasks in the repository! File: "
                    + myFile.getName());
            return false;
        } else {
            return true;
        }
    }

    public String parseDate(String date) {
        LocalDate d1 = LocalDate.parse(date);
        System.out.println(d1);
        return d1.format(DateTimeFormatter.ofPattern("MMM d yyyy"));
    }

    public String parseTime(String time) {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"))
                .format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    void takeCommands() {
        Scanner sc = new Scanner(System.in);
        String input;

        checkFile();

        do {
            try {
                while (!(input = sc.nextLine()).equals("bye")) {
                    String[] tokens = input.split(" ", 2);
                    String taskType = tokens[0];

                    invalidCommandChecker(taskType);

                    switch (taskType) {
                    case "list":
                    {
                        printList();
                        break;
                    }
                    case "done":
                    {
                        emptyDescriptionChecker(tokens);

                        String taskInfo = tokens[1];
                        int taskIndex = Integer.parseInt(taskInfo) - 1;
                        markTask(taskIndex);

                        System.out.println("------------------------------------------------");
                        System.out.println("Nice! I've marked this task as done:\n" + tasks.get(taskIndex).toString());
                        System.out.println("------------------------------------------------");
                        break;
                    }
                    case "delete":
                    {
                        emptyDescriptionChecker(tokens);

                        String taskInfo = tokens[1];
                        int taskIndex = Integer.parseInt(taskInfo) - 1;
                        deleteTask(taskIndex);
                        break;
                    }
                    default:
                    {
                        emptyDescriptionChecker(tokens);

                        String taskInfo = tokens[1];
                        Task task = addTask(taskType, taskInfo);

                        System.out.println("------------------------------------------------");
                        System.out.println("Ok! I've added this task:\n" + task.toString());
                        System.out.println("Currently, you have " + noOfTasks + " task(s) in the list!");
                        System.out.println("------------------------------------------------");
                        break;
                    }
                    }
                    writeToFile();
                }
                isExceptionCaught = false;
            } catch (InvalidCommandException e) {
                System.out.println("------------------------------------------------");
                System.out.println("Sorry, I don't know what that means...");
                System.out.println("------------------------------------------------");

                isExceptionCaught = true;
            } catch (InvalidDescriptionException e) {
                System.out.println("------------------------------------------------");
                System.out.println("Sorry, I am unable to process what was written after the command...");
                System.out.println("------------------------------------------------");

                isExceptionCaught = true;
            } catch (EmptyDescriptionException e) {
                System.out.println("------------------------------------------------");
                System.out.println("Sorry, nothing was written after the command so I am unable to process...");
                System.out.println("------------------------------------------------");

                isExceptionCaught = true;
            } catch (Exception e) {
                System.out.println("------------------------------------------------");
                System.out.println("Sorry, I am unable to process");
                System.out.println("------------------------------------------------");

                isExceptionCaught = true;
            }
        } while (isExceptionCaught);
        sc.close();
    }
}
