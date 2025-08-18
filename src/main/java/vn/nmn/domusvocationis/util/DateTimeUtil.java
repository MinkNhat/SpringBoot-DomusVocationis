package vn.nmn.domusvocationis.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class DateTimeUtil {
    private final DateTimeFormatter dateFormatter;

    public DateTimeUtil(@Value("${nmn.timezone:UTC}") String timezone) {
        this.dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy").withZone(ZoneId.of(timezone));
    }

    public String formatInstant(Instant instant) {
        if (instant == null) {
            return null;
        }
        return dateFormatter.format(instant);
    }
}
