public class Subtask extends Task {
    private int epicID; //id эпика к которому относиться

    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }

    public Subtask(String name, String description) {
        super(name, description);
    }

    public void changeStatusFromEpic(int choice) {
        if (taskStatus == TaskStatus.NEW && choice == 1) {
            taskStatus = TaskStatus.IN_PROGRESS;
        } else if ((taskStatus == TaskStatus.NEW && choice == 2) || (taskStatus == TaskStatus.IN_PROGRESS && choice == 2)) {
            taskStatus = TaskStatus.DONE;
        }
    }
}
