package me.liuli.intentmock.context;

import me.liuli.intentmock.HttpContext;

import java.nio.charset.StandardCharsets;

public class UserInfoContext extends HttpContext {

    @Override
    public String getPath() {
        return "/";
    }

    @Override
    protected byte[] getResponseBody() {
        return "{\"username\":\"Liulihaocai\",\"email\":\"liuli@getfdp.today\",\"intent_uid\":\"81317\",\"client_uid\":\"808\",\"discord_tag\":\"Liulihaocai#3747\",\"discord_id\":\"1145141919\",\"twoFactor\":false,\"api_key\":\"rise_is_trash\",\"loggedIn\":true}".getBytes(StandardCharsets.UTF_8);
    }
}
