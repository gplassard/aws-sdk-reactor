package fr.gplassard.awssdkreactor.logs;

import com.amazonaws.services.logs.AWSLogsAsync;
import com.amazonaws.services.logs.model.FilterLogEventsRequest;
import com.amazonaws.services.logs.model.FilterLogEventsResult;
import fr.gplassard.awssdkreactor.logs.internal.CompletableFutureHandler;
import fr.gplassard.awssdkreactor.logs.internal.RequestResponsePair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class ReactorLogs {
    private AWSLogsAsync awsLogs;

    public ReactorLogs(AWSLogsAsync awsLogs) {
        this.awsLogs = awsLogs;
    }

    public Flux<FilterLogEventsResult> filterLogEvents(FilterLogEventsRequest request) {
        return Mono.just(new RequestResponsePair<>(request, FilterLogEventsResult.class))
                .expand(r -> Mono.fromFuture(r.asyncResponse())
                        .filter(response -> response.getNextToken() != null)
                        .map(response -> new RequestResponsePair<>(request.clone().withNextToken(response.getNextToken()), FilterLogEventsResult.class))
                )
                .flatMap(pair -> {
                    CompletableFutureHandler<FilterLogEventsRequest, FilterLogEventsResult> handler = new CompletableFutureHandler<>(pair.asyncResponse());
                    this.awsLogs.filterLogEventsAsync(pair.getRequest(), handler);
                    return Mono.fromFuture(pair.asyncResponse());
                });
    }


}
