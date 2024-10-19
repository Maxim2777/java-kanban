package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import services.TaskManager;

import java.io.IOException;

public class PrioritizedTasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedTasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            if ("GET".equals(method)) {
                handleGet(exchange);
            } else {
                sendNotFound(exchange); // Если метод не поддерживается, вернуть 404 (Не найдено)
            }
        } catch (Exception e) {
            sendServerError(exchange); // Если произошла ошибка, вернуть 500 (Внутренняя ошибка сервера)
        }
    }

    // Обработка запроса на получение списка задач по приоритету
    private void handleGet(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getPrioritizedTasks());
        sendText(exchange, response, 200); // Отправляем ответ с кодом 200 (Успех)
    }
}