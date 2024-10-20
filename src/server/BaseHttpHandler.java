package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler implements HttpHandler {
    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        // Отправляет текстовый ответ с указанным статусом
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        // Устанавливает заголовок Content-Type для указания формата JSON
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    // Отправляет ответ с кодом 404 (Не найдено), если задача отсутствует
    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\": \"Not Found\"}", 404);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        // Отправляет ответ с кодом 406 (Недопустимо), если задача конфликтует с существующими задачами
        sendText(exchange,
                "{\"error\": \"Task conflicts with existing tasks or id in task update does not exist\"}",
                406);
    }

    protected void sendServerError(HttpExchange exchange) throws IOException {
        // Отправляет ответ с кодом 500 (Внутренняя ошибка сервера), если произошла ошибка при обработке запроса
        sendText(exchange, "{\"error\": \"Internal Server Error\"}", 500);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Для наследуемых классов (обработчиков)
    }
}