package fr.gplassard.awssdkreactor.internal;

import java.util.concurrent.CompletableFuture;

public class RequestResponsePair<RQ, RS> {
    private final RQ request;
    private final CompletableFuture<RS> asyncResponse = new CompletableFuture<>();

    public RequestResponsePair(RQ request, Class<RS> clazz) {
        this.request = request;
    }

    public RQ getRequest() {
        return request;
    }

    public CompletableFuture<RS> asyncResponse() {
        return asyncResponse;
    }
}
