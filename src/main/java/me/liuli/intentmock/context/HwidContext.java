package me.liuli.intentmock.context;

import me.liuli.intentmock.HttpContext;

import java.nio.charset.StandardCharsets;

public class HwidContext extends HttpContext {

    @Override
    public String getPath() {
        return "/product/25/whitelist";
    }

    @Override
    protected byte[] getResponseBody() {
        return "true".getBytes(StandardCharsets.UTF_8);
    }
}
