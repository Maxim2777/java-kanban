package models;

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

    //создание задачи из строки с параметрами
    public static Task fromString(String value) {
        String[] taskParameters = value.split(",");
        Task task = new Task(taskParameters[2], taskParameters[4]);
        task.setID(Integer.parseInt(taskParameters[0]));
        switch (taskParameters[2]) {
            case "NEW" -> task.setTaskStatus(TaskStatus.NEW);
            case "IN_PROGRESS" -> task.setTaskStatus(TaskStatus.IN_PROGRESS);
            case "DONE" -> task.setTaskStatus(TaskStatus.DONE);
        }
        return task;
    }

    @Override
    public String toString() {
        return "\nЗадача c ID " + ID +
                "\nНазвание: " + name +
                "\nОписание: " + description +
                "\nСтатус: " + taskStatus;
    }
}


