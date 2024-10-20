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

public class HttpTaskServerEpicsTest {
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
    public void epicShouldBeAddedCorrectlyViaServer() throws IOException, InterruptedException {
        // создаём эпик
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        // конвертируем его в JSON
        String epicJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание эпика
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode(),"Код ответа не 201");

        // проверяем, что создался один эпик с корректным именем
        List<Epic> epicsFromManager = taskManager.listOfEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Epic 1", epicsFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void epicShouldGetCorrectlyViaServer() throws IOException, InterruptedException {
        // создаём эпик и добавляем его в менеджер
        Epic epic = new Epic("Epic 2", "Testing epic 2");
        taskManager.addEpicTask(epic);

        // создаём HTTP-клиент и запрос для получения эпика
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за получение эпика
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode(),"Код ответа не 200");

        System.out.println(response.body());

        // проверяем, что эпик возвращён корректно
        Epic returnedEpic = gson.fromJson(response.body(), Epic.class);
        assertNotNull(returnedEpic, "Эпик не возвращается");
        assertEquals(epic.getName(), returnedEpic.getName(), "Некорректное имя эпика");
    }

    @Test
    public void epicShouldUpdateCorrectlyViaServer() throws IOException, InterruptedException {
        // создаём эпик и добавляем его в менеджер
        Epic epic = new Epic("Epic 3", "Testing epic 3");
        taskManager.addEpicTask(epic);

        // обновляем эпик
        epic.setName("Updated Epic 3");
        epic.setID(1);
        String updatedEpicJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос для обновления эпика
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedEpicJson)).build();

        // вызываем рест, отвечающий за обновление эпика
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode(),"Код ответа не 201");

        // проверяем, что эпик обновлён корректно
        Task updatedEpic = taskManager.getTaskInfo(epic.getID());
        assertEquals("Updated Epic 3", updatedEpic.getName(), "Эпик не был обновлён");
    }

    @Test
    public void epicShouldBeDeletedCorrectlyViaServer() throws IOException, InterruptedException {
        // создаём эпик и добавляем его в менеджер
        Epic epic = new Epic("Epic 4", "Testing epic 4");
        taskManager.addEpicTask(epic);

        // создаём HTTP-клиент и запрос для удаления эпика
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за удаление эпика
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode(),"Код ответа не 200");

        // проверяем, что эпик удалён корректно
        assertTrue(taskManager.listOfEpics().isEmpty(), "Эпик не был удалён");
    }

    @Test
    public void allEpicsAtOnceShouldBeDeletedCorrectlyViaServer() throws IOException, InterruptedException {
        // создаём эпики и добавляем их в менеджер
        taskManager.addEpicTask(new Epic("Epic 5", "Description 5"));
        taskManager.addEpicTask(new Epic("Epic 6", "Description 6"));

        // создаём HTTP-клиент и запрос для удаления всех эпиков
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за удаление всех эпиков
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode(),"Код ответа не 200");

        // проверяем, что все эпики удалены корректно
        assertTrue(taskManager.listOfEpics().isEmpty(), "Все эпики не были удалены");
    }

    @Test
    public void allEpicsAtOnceShouldGetCorrectlyViaServer() throws IOException, InterruptedException {
        // создаём эпики и добавляем их в менеджер
        Epic epic1 = new Epic("Epic 7", "Description 7");
        Epic epic2 = new Epic("Epic 8", "Description 8");
        taskManager.addEpicTask(epic1);
        taskManager.addEpicTask(epic2);

        // создаём HTTP-клиент и запрос для получения всех эпиков
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за получение всех эпиков
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode(),"Код ответа не 200");

        // проверяем, что эпики возвращены корректно
        Epic[] returnedEpics = gson.fromJson(response.body(), Epic[].class);
        assertNotNull(returnedEpics, "Эпики не возвращаются");
        assertEquals(2, returnedEpics.length, "Некорректное количество эпиков");
        assertEquals(returnedEpics[0].getName(), "Epic 7", "Эпик 7 не вернулся");
        assertEquals(returnedEpics[1].getName(), "Epic 8", "Эпик 8 не вернулся");
    }

    @Test
    public void allEpicSubtasksShouldGetCorrectlyViaServer() throws IOException, InterruptedException {
        // создаём эпик и добавляем его в менеджер
        Epic epic = new Epic("Epic 9", "Testing epic 9");
        taskManager.addEpicTask(epic);

        // добавляем подзадачи к эпику
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask 1 description");
        subtask1.setEpicID(1);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Subtask 2 description");
        subtask2.setEpicID(1);
        taskManager.addSubtask(subtask2);

        // создаём HTTP-клиент и запрос для получения всех подзадач эпика
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за получение подзадач эпика
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode(),"Код ответа не 200");

        // проверяем, что подзадачи возвращены корректно
        Subtask[] returnedSubtasks = gson.fromJson(response.body(), Subtask[].class);

        assertNotNull(returnedSubtasks, "Подзадачи не возвращаются");
        assertEquals(2, returnedSubtasks.length, "Некорректное количество подзадач");
        assertEquals(returnedSubtasks[0].getName(), "Subtask 1", "Подзадача 1 не вернулась");
        assertEquals(returnedSubtasks[1].getName(), "Subtask 2", "Подзадача 2 не вернулась");
    }
}
