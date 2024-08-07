public class Task {
    protected int ID;
    protected String name;
    protected String description;
    protected TaskStatus taskStatus = TaskStatus.NEW;

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }
    @Override
    public String toString() {
        return "\nЗадача c ID " + ID +
                "\nНазвание: " + name +
                "\nОписание: " + description +
                "\nСтатус: " + taskStatus;
    }
}


