package com.g18.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FromTimeValidation.class)
public @interface FromTime {
    String message() default "Event's from time must before to time ";
    Class<?>[] groups() default {};

    public abstract Class<? extends Payload>[] payload() default {};

}
