package controllers;

import models.dto.Business;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class BusinessController extends Controller {
    public Result business() {
        return ok(views.html.business.render(null));
    }

    public Result like() {
        //TODO: Just for testing
        List<Business> businessList = new ArrayList<>();
        businessList.add(new Business("555","1000418875702472710","Should work.",new Date(),1,2));
        businessList.add(new Business("22","-1","Should not work",new Date(),2,3));
        return ok(views.html.like.render(businessList));
    }
}
