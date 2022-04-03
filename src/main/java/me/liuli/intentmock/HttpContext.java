package me.liuli.intentmock;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class HttpContext implements HttpHandler {

    @Override
    public void handle(HttpExchange t) {
        try {
            Thread.sleep(500);
            byte[] body = getResponseBody();
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            t.sendResponseHeaders(200, body.length);
            t.getResponseBody().write(body);
            Main.log(t.getRequestURI() + " -> " + new String(body, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            Main.log(t.getRequestURI() + " ERROR -> " + e.getMessage());
        }
    }

    public String getURL() {
        return "intent.store";
    }

    public abstract String getPath();

    protected abstract byte[] getResponseBody();
}
