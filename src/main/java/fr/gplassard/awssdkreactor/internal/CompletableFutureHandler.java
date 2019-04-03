package fr.gplassard.awssdkreactor.internal;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.handlers.AsyncHandler;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureHandler<REQUEST extends AmazonWebServiceRequest, RESULT> implements AsyncHandler<REQUEST, RESULT> {
    private final CompletableFuture<RESULT> future;

    public CompletableFutureHandler(CompletableFuture<RESULT> future) {
        this.future = future;
    }

    @Override
    public void onError(Exception exception) {
        future.completeExceptionally(exception);
    }

    @Override
    public void onSuccess(REQUEST request, RESULT result) {
        future.complete(result);
    }
}
