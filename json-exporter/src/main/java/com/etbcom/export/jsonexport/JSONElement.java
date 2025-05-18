package com.etbcom.export.jsonexport;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JSONElement {
    String key() default "";
}