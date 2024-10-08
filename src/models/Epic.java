package models;

import java.util.ArrayList;
import java.util.Map;

public class Epic extends Task {

    private final ArrayList<Integer> subtasksID = new ArrayList<>(); //Массив с ID подзадач

    public ArrayList<Integer> getSubtasksID() {
        return subtasksID;
    }

    public boolean emptySubtasksID() {
        return subtasksID.isEmpty();
    }

    public void deleteSubtaskID(int deleteTaskID) {
        subtasksID.remove(Integer.valueOf(deleteTaskID));
    }

    public void deleteAllSubtasksID() {
        subtasksID.clear();
    }

    public void subtasksAdd(int taskID) {
        subtasksID.add(taskID);
    }

    public Epic(String epicName, String epicDescription) {
        super(epicName, epicDescription);
    }

    //Измение стауса эпика вслед за подзадачами
    public void changeEpicStatusFromSubtask(Map<Integer, Subtask> subtasks) {
        int epicNotDoneYet = 0;
        for (int subtaskID : subtasksID) {
            Subtask subtask = subtasks.get(subtaskID);
            if (subtask.getTaskStatus() == TaskStatus.NEW
                    || subtask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                epicNotDoneYet = 1;
                break;
            }
        }
        if (epicNotDoneYet == 0) {
            taskStatus = TaskStatus.DONE;
        } else if (taskStatus == TaskStatus.NEW) {
            taskStatus = TaskStatus.IN_PROGRESS;
        }
    }

    //Измение стауса эпика после удаления подзадачами
    public void changeEpicStatusFromSubtaskAfterDelete(Map<Integer, Subtask> subtasks) {
        if (!subtasksID.isEmpty()) {
            boolean epicIsDone = true;
            for (int subtaskID : subtasksID) {
                Subtask subtask = subtasks.get(subtaskID);
                if (subtask.getTaskStatus() == TaskStatus.NEW
                        || subtask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                    epicIsDone = false;
                    break;
                }
            }
            if (epicIsDone) {
                taskStatus = TaskStatus.DONE;
            }

            if (!epicIsDone) {
                boolean epicIsNew = true;
                for (int subtaskID : subtasksID) {
                    Subtask subtask = subtasks.get(subtaskID);
                    if (subtask.getTaskStatus() == TaskStatus.DONE
                            || subtask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                        epicIsNew = false;
                        break;
                    }
                }
                if (epicIsNew) {
                    taskStatus = TaskStatus.NEW;
                }
            }
        } else {
            taskStatus = TaskStatus.NEW;
        }
    }

    //Измение стауса эпика после добавдения новой подзадачами
    public void changeEpicStatusFromNewSubtask() {
        if (taskStatus == TaskStatus.DONE) {
            taskStatus = TaskStatus.IN_PROGRESS;
        }
    }

    //создание эпика из строки с параметрами
    public static Epic fromString(String value) {
        String[] taskParameters = value.split(",");
        Epic epic = new Epic(taskParameters[2], taskParameters[4]);
        epic.setID(Integer.parseInt(taskParameters[0]));
        switch (taskParameters[2]) {
            case "NEW" -> epic.setTaskStatus(TaskStatus.NEW);
            case "IN_PROGRESS" -> epic.setTaskStatus(TaskStatus.IN_PROGRESS);
            case "DONE" -> epic.setTaskStatus(TaskStatus.DONE);
        }
        return epic;
    }

    @Override
    public String toString() {
        return "\nЭпик c ID " + ID +
                "\nНазвание: " + name +
                "\nОписание: " + description +
                "\nСтатус: " + taskStatus +
                "\nID подзадач, которые относятся к данному эпику: " + subtasksID;
    }
}
