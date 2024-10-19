package models;

import java.time.Duration;
import java.time.LocalDateTime;

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

    public Subtask(String subtaskName, String subtaskDescription, Duration subtaskDuration, LocalDateTime subtaskStartTime) {
        super(subtaskName, subtaskDescription, subtaskDuration, subtaskStartTime);
    }

    public static Subtask fromString(String value) {
        String[] taskParameters = value.split(",", -1);
        Subtask subtask = new Subtask(taskParameters[2], taskParameters[4]);
        subtask.setID(Integer.parseInt(taskParameters[0]));
        switch (taskParameters[2]) {
            case "NEW" -> subtask.setTaskStatus(TaskStatus.NEW);
            case "IN_PROGRESS" -> subtask.setTaskStatus(TaskStatus.IN_PROGRESS);
            case "DONE" -> subtask.setTaskStatus(TaskStatus.DONE);
        }
        subtask.setEpicID(Integer.parseInt(taskParameters[7]));
        if (!taskParameters[5].isEmpty()) {
            long minutes = Long.parseLong(taskParameters[5]);
            Duration duration = Duration.ofMinutes(minutes);
            subtask.setDuration(duration);
        }
        if (!taskParameters[6].isEmpty()) {
            LocalDateTime startTime = LocalDateTime.parse(taskParameters[6]);
            subtask.setStartTime(startTime);
        }
        return subtask;
    }

    //создание подзадачи из строки с параметрами
    @Override
    public String toString() {
        return "\nПодзадача c ID " + ID +
                "\nНазвание: " + name +
                "\nОписание: " + description +
                "\nСтатус: " + taskStatus +
                "\nID эпика, к которому относится - " + epicID +
                (getStartTime() != null ? "\nНачало: " + startTime.format(formatter) : "") +
                (getStartTime() != null ? "\nКонец: " + getEndTime().format(formatter) : "") +
                (getDuration() != null ? "\nДлительность: " + duration.toMinutes() + " минут" : "");
    }

}
