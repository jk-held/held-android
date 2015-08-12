package com.held.retrofit.response;


import java.util.List;

public class PostChatResponse {

    private List<PostChatData> objects;
    private boolean lastPage;

    public boolean isLastPage() {
        return lastPage;
    }

    public List<PostChatData> getObjects() {

        return objects;
    }
}
