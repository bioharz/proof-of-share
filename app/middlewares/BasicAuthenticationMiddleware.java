package middlewares;

import models.User;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import services.EbeanUserRepository;

import javax.inject.Inject;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


public class BasicAuthenticationMiddleware extends Action.Simple {


    @Inject
    protected EbeanUserRepository userRepository;

    @Override
    public CompletionStage<Result> call(Http.Context ctx) {

        //Logger.info("Request Path: " + ctx.request().method() + ": " + ctx.request().path());

        Optional<String> header = ctx.request().header("Authorization");

        if (header.isPresent() && header.get().startsWith("Basic ")) {
            String auth = header.get().substring(6);
            auth = new String(Base64.getDecoder().decode(auth));

            String[] credentials = auth.split(":");

            if (credentials.length == 2) {
                User user = userRepository.getUserByUsername(credentials[0]);

                if (user != null && user.comparePasswords(credentials[1])) {
                    return delegate.call(ctx);
                }
            }

        }

        Result result = unauthorized("Authentication required")
                .withHeader("WWW-Authenticate", "Basic realm=Secure Area");

        return CompletableFuture.completedFuture(result);

    }
}
