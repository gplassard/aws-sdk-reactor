package fr.gplassard.awssdkreactor.logs;

import com.amazonaws.services.logs.AWSLogsAsync;
import com.amazonaws.services.logs.model.FilterLogEventsRequest;
import com.amazonaws.services.logs.model.FilterLogEventsResult;
import fr.gplassard.awssdkreactor.logs.internal.CompletableFutureHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReactorLogsTest {
    @Mock
    private AWSLogsAsync awsLogsAsync;

    @InjectMocks
    private ReactorLogs reactorLogs;

    @Test
    public void filterLogEventsSuccesses() {
        FilterLogEventsRequest request = new FilterLogEventsRequest();
        FilterLogEventsResult result1 = new FilterLogEventsResult().withNextToken("token1");
        FilterLogEventsResult result2 = new FilterLogEventsResult().withNextToken("token2");
        FilterLogEventsResult result3 = new FilterLogEventsResult().withNextToken("token3");
        FilterLogEventsResult resultEnd = new FilterLogEventsResult().withNextToken(null);

        when(awsLogsAsync.filterLogEventsAsync(any(), any()))
                .thenAnswer(invocation -> {
                    CompletableFutureHandler<FilterLogEventsRequest, FilterLogEventsResult> handler = invocation.getArgument(1);
                    if (((FilterLogEventsRequest) invocation.getArgument(0)).getNextToken() == null) {
                        handler.onSuccess(request, result1);
                    } else if (((FilterLogEventsRequest) invocation.getArgument(0)).getNextToken().equals("token1")) {
                        handler.onSuccess(request, result2);
                    } else if (((FilterLogEventsRequest) invocation.getArgument(0)).getNextToken().equals("token2")) {
                        handler.onSuccess(request, result3);
                    } else if (((FilterLogEventsRequest) invocation.getArgument(0)).getNextToken().equals("token3")) {
                        handler.onSuccess(request, resultEnd);
                    }
                    return null;
                });

        Flux<FilterLogEventsResult> result = reactorLogs.filterLogEvents(request);

        StepVerifier.create(result)
                .expectNext(result1)
                .expectNext(result2)
                .expectNext(result3)
                .expectNext(resultEnd)
                .expectComplete()
                .verify();
    }

    @Test
    public void filterLogEventsError() {
        FilterLogEventsRequest request = new FilterLogEventsRequest();
        FilterLogEventsResult result1 = new FilterLogEventsResult().withNextToken("token1");
        FilterLogEventsResult result2 = new FilterLogEventsResult().withNextToken("token2");
        RuntimeException error = new RuntimeException("oups");

        when(awsLogsAsync.filterLogEventsAsync(any(), any()))
                .thenAnswer(invocation -> {
                    CompletableFutureHandler<FilterLogEventsRequest, FilterLogEventsResult> handler = invocation.getArgument(1);
                    if (((FilterLogEventsRequest) invocation.getArgument(0)).getNextToken() == null) {
                        handler.onSuccess(request, result1);
                    } else if (((FilterLogEventsRequest) invocation.getArgument(0)).getNextToken().equals("token1")) {
                        handler.onSuccess(request, result2);
                    } else if (((FilterLogEventsRequest) invocation.getArgument(0)).getNextToken().equals("token2")) {
                        handler.onError(error);
                    }
                    return null;
                });

        Flux<FilterLogEventsResult> result = reactorLogs.filterLogEvents(request);

        StepVerifier.create(result)
                .expectNext(result1)
                .expectNext(result2)
                .expectErrorMessage("oups")
                .verify();
    }

}
