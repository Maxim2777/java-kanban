package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskServerSubtasksTest {
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
    public void subtaskShouldBeAddedCorrectlyViaServer() throws IOException, InterruptedException {
        // создаём эпик и добавляем его в менеджер
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        taskManager.addEpicTask(epic);

        // создаём подзадачу и конвертируем её в JSON
        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1");
        subtask.setEpicID(1);
        String taskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос для получения подзадачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за получение подзадачи
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode(), "Код ответа не 201");

        // проверяем, что подзадача добавлена корректно
        List<Subtask> subtasksFromManager = taskManager.listOfSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Subtask 1", subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void subtaskShouldGetCorrectlyViaServer() throws IOException, InterruptedException {
        // создаём эпик и добавляем его в менеджер
        Epic epic = new Epic("Epic 2", "Testing epic 2");
        taskManager.addEpicTask(epic);

        // создаём подзадачу и добавляем её в менеджер
        Subtask subtask = new Subtask("Subtask 2", "Testing subtask 2");
        subtask.setEpicID(1);
        taskManager.addSubtask(subtask);

        // создаём HTTP-клиент и запрос для получения подзадачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за получение подзадачи
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode(),"Код ответа не 200");

        // проверяем, что подзадача возвращена корректно
        Subtask returnedSubtask = gson.fromJson(response.body(), Subtask.class);
        assertNotNull(returnedSubtask, "Подзадача не возвращается");
        assertEquals(subtask.getName(), returnedSubtask.getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void subtaskShouldUpdateCorrectlyViaServer() throws IOException, InterruptedException {
        // создаём эпик и добавляем его в менеджер
        Epic epic = new Epic("Epic 3", "Testing epic 3");
        taskManager.addEpicTask(epic);

        // создаём подзадачу и добавляем её в менеджер
        Subtask subtask = new Subtask("Subtask 3", "Testing subtask 3");
        subtask.setEpicID(epic.getID());
        taskManager.addSubtask(subtask);

        // обновляем подзадачу
        subtask.setName("Updated Subtask 3");
        subtask.setID(2);
        String updatedSubtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос для обновления подзадачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedSubtaskJson)).build();

        // вызываем рест, отвечающий за обновление подзадачи
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode(),"Код ответа не 201");

        // проверяем, что подзадача обновлена корректно
        Task updatedSubtask = taskManager.getTaskInfo(subtask.getID());
        assertEquals("Updated Subtask 3", updatedSubtask.getName(), "Подзадача не была обновлена");
    }

    @Test
    public void subtaskShouldBeDeletedCorrectlyViaServer() throws IOException, InterruptedException {
        // создаём эпик и добавляем его в менеджер
        Epic epic = new Epic("Epic 4", "Testing epic 4");
        taskManager.addEpicTask(epic);

        // создаём подзадачу и добавляем её в менеджер
        Subtask subtask = new Subtask("Subtask 4", "Testing subtask 4");
        subtask.setEpicID(1);
        taskManager.addSubtask(subtask);

        // создаём HTTP-клиент и запрос для удаления подзадачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за удаление подзадачи
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode(),"Код ответа не 200");

        // проверяем, что подзадача удалена корректно
        assertTrue(taskManager.listOfSubtasks().isEmpty(), "Подзадача не была удалена");
    }

    @Test
    public void allSubtasksAtOnceShouldBeDeletedCorrectlyViaServer() throws IOException, InterruptedException {
        // создаём эпик и добавляем его в менеджер
        Epic epic = new Epic("Epic 5", "Testing epic 5");
        taskManager.addEpicTask(epic);

        // создаём подзадачи и добавляем их в менеджер
        Subtask subtask1 = new Subtask("Subtask 5", "Description 5");
        subtask1.setEpicID(1);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 6", "Description 6");
        subtask2.setEpicID(1);
        taskManager.addSubtask(subtask2);

        // создаём HTTP-клиент и запрос для удаления всех подзадач
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за удаление всех подзадач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode(),"Код ответа не 200");

        // проверяем, что все подзадачи удалены корректно
        assertTrue(taskManager.listOfSubtasks().isEmpty(), "Все подзадачи не были удалены");
    }

    @Test
    public void allSubtasksAtOnceShouldGetCorrectlyViaServer() throws IOException, InterruptedException {
        // создаём эпик и добавляем его в менеджер
        Epic epic = new Epic("Epic 6", "Testing epic 6");
        taskManager.addEpicTask(epic);

        // создаём подзадачи и добавляем их в менеджер
        Subtask subtask1 = new Subtask("Subtask 7", "Description 7");
        subtask1.setEpicID(1);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 8", "Description 8");
        subtask2.setEpicID(1);
        taskManager.addSubtask(subtask2);

        // создаём HTTP-клиент и запрос для получения всех подзадач
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за получение всех подзадач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode(),"Код ответа не 200");
        // проверяем, что подзадачи возвращены корректно
        Subtask[] returnedSubtasks = gson.fromJson(response.body(), Subtask[].class);

        assertNotNull(returnedSubtasks, "Подзадачи не возвращаются");
        assertEquals(2, returnedSubtasks.length, "Некорректное количество подзадач");
        assertEquals(returnedSubtasks[0].getName(), "Subtask 7", "Подзадача 1 не вернулась");
        assertEquals(returnedSubtasks[1].getName(), "Subtask 8", "Подзадача 2 не вернулась");
    }
}
