package com.held.retrofit.response;


import java.util.List;

public class FriendRequestResponse {

    private List<SearchUserResponse> objects;
    private boolean lastPage;
    private long nextPageStart;

    public long getNextPageStart() {
        return nextPageStart;
    }

    public List<SearchUserResponse> getObjects() {
        return objects;
    }

    public boolean isLastPage() {
        return lastPage;
    }
}
