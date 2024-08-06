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

}
