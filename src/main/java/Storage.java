import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Deals with loading tasks from a file and saving tasks to the file
 */
public class Storage {

    String filePath;
    ArrayList<Task> tasks;
    File myFile;

    public Storage(String filePath) throws IOException{
        this.filePath = filePath;
        this.tasks = new ArrayList<>();
        this.myFile = new File(filePath);
        if (!myFile.createNewFile()) {
            scanTaskList();
        }
    }

    /**
     * Scans the file and save the tasks into an ArrayList of Task
     * @throws FileNotFoundException is thrown when there the file could not be found
     */
    void scanTaskList() throws FileNotFoundException {
        Scanner sc = new Scanner(myFile);

        while (sc.hasNext()) {
            String input = sc.nextLine();
            Task task;

            if (input.contains("[T]")) {
                String[] tokens = input.split("] ", 2);
                String taskInfo = tokens[1];
                task = new ToDo(taskInfo);
            } else if (input.contains("[D]")) {
                String[] tokens = input.split("] ", 2);
                String[] nextTokens = tokens[1].split(" ", 2);
                String date = nextTokens[1].substring(nextTokens[1].indexOf(':') + 2, nextTokens[1].indexOf(')'));
                task = new Deadline(nextTokens[0], date);
            } else {
                String[] tokens = input.split("] ", 2);
                String[] nextTokens = tokens[1].split(" ", 2);
                String date = nextTokens[1].substring(nextTokens[1].indexOf(':') + 2, nextTokens[1].indexOf(')'));
                task = new Event(nextTokens[0], date);
            }
            if (input.contains("\u2713")) {
                task.markAsDone();
            }
            tasks.add(task);
        }
    }

    /**
     * Stores the <code>TaskList</code> into a file
     * @param tasklist A class that stores the ArrayList of <code>Tasks</code>
     * @throws IOException is thrown when there is an error related to input and output
     */
    public void writeToFile(TaskList tasklist) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        ArrayList<Task> tasks = tasklist.getList();
        for (Task t : tasks) {
            fw.write(t + "\n");
        }
        fw.close();
    }

    /**
     * Loads the ArrayList
     * @return Returns the ArrayList of <code>Task</code>
     */
    public ArrayList<Task> load() {
        return tasks;
    }

}
