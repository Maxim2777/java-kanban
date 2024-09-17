package models;

public class Subtask extends Task {
    private int epicID; //id эпика к которому относиться

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    public Subtask(String subtaskName, String subtaskDescription) {
        super(subtaskName, subtaskDescription);
    }

    public static Subtask fromString(String value) {
        String[] taskParameters = value.split(",");
        Subtask subtask = new Subtask(taskParameters[2], taskParameters[4]);
        subtask.setID(Integer.parseInt(taskParameters[0]));
        switch (taskParameters[2]) {
            case "NEW" -> subtask.setTaskStatus(TaskStatus.NEW);
            case "IN_PROGRESS" -> subtask.setTaskStatus(TaskStatus.IN_PROGRESS);
            case "DONE" -> subtask.setTaskStatus(TaskStatus.DONE);
        }
        subtask.setEpicID(Integer.parseInt(taskParameters[5]));
        return subtask;
    }

    //создание подзадачи из строки с параметрами
    @Override
    public String toString() {
        return "\nПодзадача c ID " + ID +
                "\nНазвание: " + name +
                "\nОписание: " + description +
                "\nСтатус: " + taskStatus +
                "\nID эпика, к которому относится - " + epicID;
    }

}
