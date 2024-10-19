package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
import java.util.Comparator;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerPrioritizedTasksTest {
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
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        // создаём задачи и добавляем их в менеджер
        Task task = new Task("Task 1", "Description 1", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);

        Task task2 = new Task("Task 2", "Description 2", Duration.ofMinutes(30),
                LocalDateTime.now().minusMonths(1));
        taskManager.addTask(task2);

        Subtask subtask = new Subtask("Subtask 1", "Description 1",
                Duration.ofMinutes(30), LocalDateTime.now().minusMonths(5));
        subtask.setEpicID(3);
        taskManager.addSubtask(subtask);

        // создаём HTTP-клиент и запрос для получения задач по приоритету
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за получение задач по приоритету
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());



        // Добавляем задачи из JSON в TreeSet
        TreeSet<Task> prioritizedTasks = new TreeSet<>(
                Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Task::getID)
        );
        prioritizedTasks.addAll(gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>(){}.getType()));

        // проверяем, что задачи возвращены в правильном порядке, по приоритету
        assertNotNull(prioritizedTasks, "Задачи по приоритету не возвращаются");
        assertEquals(2, prioritizedTasks.size(), "Некорректное количество задач по приоритету");
        assertEquals("Task 2", prioritizedTasks.getFirst().getName(), "Некорректный порядок задач");
        assertEquals("Task 1", prioritizedTasks.getLast().getName(), "Некорректный порядок задач");
    }

    @Test
    public void testGetEmptyPrioritizedTasks() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос для получения задач по приоритету
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за получение задач по приоритету
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что список задач по приоритету пуст
        Task[] prioritizedTasks = gson.fromJson(response.body(), Task[].class);
        assertNotNull(prioritizedTasks, "Задачи по приоритету не возвращаются");
        assertEquals(0, prioritizedTasks.length, "Список задач по приоритету должен быть пуст");
    }
}


