package middlewares;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class SessionAuthenticationMiddleware extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        return ctx.session().get("username");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(controllers.routes.HomeController.login());
    }
}