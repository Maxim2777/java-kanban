package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected int ID;
    protected String name;
    protected String description;
    protected TaskStatus taskStatus = TaskStatus.NEW;
    protected Duration duration;
    protected LocalDateTime startTime;

    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

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

    public Task(String name, String description, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
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
        String[] taskParameters = value.split(",", -1);
        Task task = new Task(taskParameters[2], taskParameters[4]);
        task.setID(Integer.parseInt(taskParameters[0]));
        switch (taskParameters[2]) {
            case "NEW" -> task.setTaskStatus(TaskStatus.NEW);
            case "IN_PROGRESS" -> task.setTaskStatus(TaskStatus.IN_PROGRESS);
            case "DONE" -> task.setTaskStatus(TaskStatus.DONE);
        }
        if (!taskParameters[5].isEmpty()) {
            long minutes = Long.parseLong(taskParameters[5]);
            Duration duration = Duration.ofMinutes(minutes);
            task.setDuration(duration);
        }
        if (!taskParameters[6].isEmpty()) {
            LocalDateTime startTime = LocalDateTime.parse(taskParameters[6]);
            task.setStartTime(startTime);
        }
        return task;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public boolean overlapsWith(Task otherTask) {
        // Проверяем пересечение
        return this.startTime.isBefore(otherTask.getEndTime()) && otherTask.getStartTime().isBefore(this.getEndTime());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Проверка на равенство ссылок
        if (obj == null || getClass() != obj.getClass()) return false; // Проверка на null и тип
        Task task = (Task) obj; // Приведение к типу Task
        return ID == task.ID; // Сравнение по уникальному ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID); // Генерация hash-кода на основе ID
    }

    @Override
    public String toString() {
        return "\nЗадача c ID " + ID +
                "\nНазвание: " + name +
                "\nОписание: " + description +
                "\nСтатус: " + taskStatus +
                (getStartTime() != null ? "\nНачало: " + startTime.format(formatter) : "") +
                (getStartTime() != null ? "\nКонец: " + getEndTime().format(formatter) : "") +
                (getDuration() != null ? "\nДлительность: " + duration.toMinutes() + " минут" : "");
    }
}


