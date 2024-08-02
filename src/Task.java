public class Task {
    protected int ID;
    protected String name;
    protected String description;
    protected TaskStatus taskStatus = TaskStatus.NEW;

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void changeTaskStatus(int choice) {
        if (choice == 1) {
            taskStatus = TaskStatus.IN_PROGRESS;
        } else if (choice == 2) {
            taskStatus = TaskStatus.DONE;
        } else {
            System.out.println("Такой команды нет");
        }
    }

    public Task(String name, String description, int ID) {
        this.name = name;
        this.description = description;
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
