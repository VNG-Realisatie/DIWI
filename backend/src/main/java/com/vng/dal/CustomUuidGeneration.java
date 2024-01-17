package com.vng.dal;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@IdGeneratorType( CustomUuidGenerator.class )
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface CustomUuidGeneration {
    // any config to expose to user
}
