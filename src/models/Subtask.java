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

    @Override
    public String toString() {
        return "\nПодзадача c ID " + ID +
                "\nНазвание: " + name +
                "\nОписание: " + description +
                "\nСтатус: " + taskStatus +
                "\nID эпика, к которому относится - " + epicID;
    }

}
