package vn.nmn.domusvocationis.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vn.nmn.domusvocationis.util.annotation.ChronologicalDates;

import java.lang.reflect.Field;
import java.time.Instant;

public class ChronologicalDatesValidator implements ConstraintValidator<ChronologicalDates, Object> {

    private String startFieldName;
    private String endFieldName;

    @Override
    public void initialize(ChronologicalDates constraintAnnotation) {
        this.startFieldName = constraintAnnotation.startField();
        this.endFieldName = constraintAnnotation.endField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Field startField = value.getClass().getDeclaredField(startFieldName);
            Field endField = value.getClass().getDeclaredField(endFieldName);

            startField.setAccessible(true);
            endField.setAccessible(true);

            Object startValue = startField.get(value);
            Object endValue = endField.get(value);

            // @NotNull xử lý riêng
            if (startValue == null || endValue == null) {
                return true;
            }

            // bỏ qua nếu không phải Instant
            if (!(startValue instanceof Instant) || !(endValue instanceof Instant)) {
                return true;
            }

            Instant start = (Instant) startValue;
            Instant end = (Instant) endValue;

            if (end.isAfter(start)) {
                return true;
            }

            // Hủy message mặc định (object-level)
            context.disableDefaultConstraintViolation();

            // Gán lỗi vào field cụ thể (endField)
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(endFieldName)
                    .addConstraintViolation();

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}