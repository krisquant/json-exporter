package com.etbcom.export.jsonexport;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JSONSerializable {
    String key() default "";
}