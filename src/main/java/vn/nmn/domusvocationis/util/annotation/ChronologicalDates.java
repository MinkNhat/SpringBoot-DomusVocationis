package vn.nmn.domusvocationis.util.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import vn.nmn.domusvocationis.util.validator.ChronologicalDatesValidator;

import java.lang.annotation.*;

@Repeatable(ChronologicalDates.List.class) // cho phép 1 class có áp dùng nhiều annotation
@Constraint(validatedBy = ChronologicalDatesValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChronologicalDates {
    String message() default "Ngày kết thúc phải sau ngày bắt đầu";
    String startField();
    String endField();

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        ChronologicalDates[] value();
    }
}
