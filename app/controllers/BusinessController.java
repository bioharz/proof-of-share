package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class BusinessController extends Controller {

    public Result business() {
        return ok(views.html.business.render(null));

    }
}
