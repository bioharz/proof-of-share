package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.entities.Category;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import dao.CategoryDao;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.stream.Stream;

public class CategoryController extends Controller {

    @Inject
    protected CategoryDao categoryRepository;

    public Result list(String sort, String query) {
        Stream<Category> categories = categoryRepository.getCategories().stream();
        if (sort != null && !sort.isEmpty()) {
            if (sort.equals("asc")) {
                categories = categories.sorted(Comparator.comparing(Category::getTitle));
            } else if (sort.equals("desc")) {
                categories = categories.sorted(Comparator.comparing(Category::getTitle).reversed());
            }
        }
        if (query != null && !query.isEmpty()) {
            categories = categories.filter(c -> c.getTitle().toLowerCase().matches("(?i)" + query + ".*"));
        }
        return ok(Json.toJson(categories));
    }

    public Result get(int id) {
        Category category = categoryRepository.getCategory(id);

        if (category == null) {
            return badRequest("category does not exist");
        } else {
            return ok(Json.toJson(category));
        }
    }

    public Result delete(int id) {
        categoryRepository.deleteCategory(id);
        return ok();
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result save(int id) {
        JsonNode json = request().body().asJson();

        if (json.findPath("title").textValue() == null ||
                json.findPath("title").textValue().isEmpty()) {
            return badRequest("a title is required for categories");
        }

        if (id > 0 && categoryRepository.getCategory(id) == null) {
            return badRequest("category does not exist");
        }

        Category category = Json.fromJson(json, Category.class);
        category.setId(id);
        categoryRepository.saveCategory(category);

        return ok(Json.toJson(category));
    }

}
