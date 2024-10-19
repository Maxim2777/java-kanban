package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.Managers;
import services.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerHistoryTest {
    private HttpTaskServer server;
    private TaskManager taskManager;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = Managers.getDefaultFileBackedTaskManager();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        server = new HttpTaskServer(taskManager);
        server.start();
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        // создаём задачи и добавляем их в менеджер
        Task task1 = new Task("Task 1", "Description 1", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task1);
        Task task2 = new Task("Task 2", "Description 2", Duration.ofMinutes(45), LocalDateTime.now().plusMinutes(60));
        taskManager.addTask(task2);
        Epic epic = new Epic("Epic 1", "Description x");
        taskManager.addEpicTask(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description x");
        subtask.setEpicID(3);
        taskManager.addSubtask(subtask);

        // Запрашиваем задачи для добавления их в историю
        HttpClient client = HttpClient.newHttpClient();
        URI urlTask = URI.create("http://localhost:8080/tasks/1");
        HttpRequest requestTask = HttpRequest.newBuilder().uri(urlTask).GET().build();
        client.send(requestTask, HttpResponse.BodyHandlers.ofString());
        URI urlTask2 = URI.create("http://localhost:8080/tasks/2");
        HttpRequest requestTask2 = HttpRequest.newBuilder().uri(urlTask2).GET().build();
        client.send(requestTask2, HttpResponse.BodyHandlers.ofString());
        URI urlEpic = URI.create("http://localhost:8080/epics/3");
        HttpRequest requestEpic = HttpRequest.newBuilder().uri(urlEpic).GET().build();
        client.send(requestEpic, HttpResponse.BodyHandlers.ofString());
        URI urlSubtask = URI.create("http://localhost:8080/subtasks/4");
        HttpRequest requestSubtask = HttpRequest.newBuilder().uri(urlSubtask).GET().build();
        client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());

        // создаём HTTP-клиент и запрос для получения истории
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за получение истории
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что история возвращена корректно
        ArrayList<Task> returnedHistory = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>(){}.getType());
        assertNotNull(returnedHistory, "История не возвращается");
        assertEquals(4, returnedHistory.size(), "Некорректное количество задач в истории");
        assertEquals("Task 1", returnedHistory.get(0).getName(), "Первая задача в истории не совпадает");
        assertEquals("Task 2", returnedHistory.get(1).getName(), "Вторая задача в истории не совпадает");
        assertEquals("Epic 1", returnedHistory.get(2).getName(), "Эпик в истории не совпадает");
        assertEquals("Subtask 1", returnedHistory.get(3).getName(), "Подзадача в истории не совпадает");
    }

    @Test
    public void testGetEmptyHistory() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос для получения истории
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за получение истории
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что история пустая
        Task[] returnedHistory = gson.fromJson(response.body(), Task[].class);
        assertNotNull(returnedHistory, "История не возвращается");
        assertEquals(0, returnedHistory.length, "История должна быть пустой");
    }
}
