package middlewares;

import play.http.HttpErrorHandler;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.mvc.Results.*;

@Singleton
public class ErrorHandlerMiddleware implements HttpErrorHandler {

    @Override
    public CompletionStage<Result> onClientError(Http.RequestHeader request, int statusCode, String message) {
        return CompletableFuture.supplyAsync(() ->
                status(statusCode,message));
    }

    @Override
    public CompletionStage<Result> onServerError(Http.RequestHeader request, Throwable exception) {
        return CompletableFuture.supplyAsync(() ->
                internalServerError(exception.getLocalizedMessage()));
    }
}
