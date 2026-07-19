package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.validator.ValidatorDateNotBefore;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(
        validatedBy = ValidatorDateNotBefore.class
)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(DateNotBeforeList.class)
public @interface DateNotBefore {
    String value() default "1895-12-28";

    String message() default "Указанная дата раньше 1895-12-28";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface DateNotBeforeList {
    DateNotBefore[] value();
}