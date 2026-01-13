package fr.elikia.backend.security.jwt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a required role for accessing a controller or method.

 * Used in combination with {@RequiredJWTAuth}.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredRole {
    /**
     * Required role name (ex: ADMIN, MEMBER)
     *
     * @return role value
     */
    String value();
}
