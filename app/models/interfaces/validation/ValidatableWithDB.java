package models.interfaces.validation;

import play.db.Database;

public interface ValidatableWithDB<T> {
    public T validate(final Database db);
}