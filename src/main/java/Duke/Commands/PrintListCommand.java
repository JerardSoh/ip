package Duke.Commands;

import Duke.Storage;
import Duke.TaskList;
import Duke.Ui;

/**
 * A command that represents printing the <code>TaskList</code>
 */
public class PrintListCommand extends Command {

    public PrintListCommand() {

    }

    @Override
    public String execute(TaskList tasklist, Ui ui, Storage storage) {
        return ui.printList(tasklist);
    }
}