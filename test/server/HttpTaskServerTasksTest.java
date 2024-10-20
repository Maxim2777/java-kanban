package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskServerTasksTest {
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
    public void taskShouldBeAddedCorrectlyViaServer() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", Duration.ofMinutes(5), LocalDateTime.now());
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode(),"Код ответа не 201");

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = taskManager.listOfTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void taskShouldGetCorrectlyViaServer() throws IOException, InterruptedException {
        // создаём задачу и добавляем её в менеджер
        Task task = new Task("Test 3", "Testing task 3", Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addTask(task);

        // создаём HTTP-клиент и запрос для получения задачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за получение задачи
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode(),"Код ответа не 200");

        // проверяем, что задача возвращена корректно
        Task returnedTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(returnedTask, "Задача не возвращается");
        assertEquals(task.getName(), returnedTask.getName(), "Некорректное имя задачи");
    }

    @Test
    public void taskShouldUpdateCorrectlyViaServer() throws IOException, InterruptedException {
        // создаём задачу и добавляем её в менеджер
        Task task = new Task("Test 4", "Testing task 4", Duration.ofMinutes(15), LocalDateTime.now());
        taskManager.addTask(task);

        // обновляем задачу
        task.setName("Updated Test 4");
        task.setID(1);
        String updatedTaskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос для обновления задачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson)).build();

        // вызываем рест, отвечающий за обновление задачи
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode(),"Код ответа не 201");

        // проверяем, что задача обновлена корректно
        Task updatedTask = taskManager.getTaskInfo(task.getID());
        assertEquals("Updated Test 4", updatedTask.getName(), "Задача не была обновлена");
    }

    @Test
    public void taskShouldBeDeletedCorrectlyViaServer() throws IOException, InterruptedException {
        // создаём задачу и добавляем её в менеджер
        Task task = new Task("Test 5", "Testing task 5", Duration.ofMinutes(20), LocalDateTime.now());
        taskManager.addTask(task);

        // создаём HTTP-клиент и запрос для удаления задачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за удаление задачи
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode(),"Код ответа не 200");

        // проверяем, что задача удалена корректно
        assertTrue(taskManager.listOfTasks().isEmpty(), "Задача не была удалена");
    }

    @Test
    public void allTasksAtOnceShouldBeDeletedCorrectlyViaServer() throws IOException, InterruptedException {
        // создаём задачи и добавляем их в менеджер
        taskManager.addTask(new Task("Task 6", "Description 6", Duration.ofMinutes(25), LocalDateTime.now()));
        taskManager.addTask(new Task("Task 7", "Description 7", Duration.ofMinutes(30), LocalDateTime.now()));

        // создаём HTTP-клиент и запрос для удаления всех задач
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за удаление всех задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode(),"Код ответа не 200");

        // проверяем, что все задачи удалены корректно
        assertTrue(taskManager.listOfTasks().isEmpty(), "Все задачи не были удалены");
    }

    @Test
    public void allTasksAtOnceShouldGetCorrectlyViaServer() throws IOException, InterruptedException {
        // создаём задачи и добавляем их в менеджер
        taskManager.addTask(new Task("Task 8", "Description 8", Duration.ofMinutes(25), LocalDateTime.now()));
        taskManager.addTask(new Task("Task 9", "Description 9", Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(100)));

        // создаём HTTP-клиент и запрос для удаления всех задач
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за получение всех задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode(),"Код ответа не 200");

        // проверяем, что задачи возвращены корректно
        Task[] returnedTasks = gson.fromJson(response.body(), Task[].class);

        assertNotNull(returnedTasks, "Задачи не возвращаются");
        assertEquals(2, returnedTasks.length, "Некорректное количество задач");
        assertEquals(returnedTasks[0].getName(), "Task 8", "Задача 8 не вернулась");
        assertEquals(returnedTasks[1].getName(), "Task 9", "Задача 9 не вернулась");
    }
}