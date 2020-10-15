package com.wang.gmall.annotations;

        import java.lang.annotation.ElementType;
        import java.lang.annotation.Retention;
        import java.lang.annotation.RetentionPolicy;
        import java.lang.annotation.Target;

@Target( ElementType.METHOD)
@Retention( RetentionPolicy.RUNTIME)
public @interface LoginRequire {
  boolean mustLoginSuccess() default true;
}
