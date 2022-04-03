package me.liuli.intentmock.context;

import me.liuli.intentmock.HttpContext;

import java.nio.charset.StandardCharsets;

public class VersionContext extends HttpContext {

    @Override
    public String getURL() {
        return "pastebin.com";
    }

    @Override
    public String getPath() {
        return "/raw/PyAq9y1q";
    }

    @Override
    protected byte[] getResponseBody() {
        return ("5.42 - true\n" +
                "5.43 - true\n" +
                "5.5 - true\n" +
                "5.51 - true\n" +
                "5.52 - true\n" +
                "5.6 - true\n" +
                "5.69 - true\n" +
                "5.7 - true\n" +
                "5.71 - false").getBytes(StandardCharsets.UTF_8);
    }
}
