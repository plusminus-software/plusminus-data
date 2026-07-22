package software.plusminus.data.util;

import org.junit.Test;
import software.plusminus.data.exception.DataException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.chrono.HijrahDate;
import java.time.chrono.JapaneseDate;
import java.time.chrono.MinguoDate;
import java.time.chrono.ThaiBuddhistDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static software.plusminus.check.Checks.check;

public class TemporalUtilTest {

    private static final List<Class<?>> TEMPORAL_TYPES = Arrays.asList(
            Instant.class, ZonedDateTime.class, OffsetDateTime.class, OffsetTime.class,
            LocalDateTime.class, LocalDate.class, LocalTime.class,
            HijrahDate.class, JapaneseDate.class, MinguoDate.class, ThaiBuddhistDate.class,
            Year.class, YearMonth.class,
            Date.class, Long.class, String.class);

    @Test
    public void isTemporalTypeReturnsTrueForSupportedTypes() {
        TEMPORAL_TYPES.forEach(type ->
                check(TemporalUtil.isTemporalType(type)).is(true));
    }

    @Test
    public void isTemporalTypeReturnsFalseForUnsupportedType() {
        check(TemporalUtil.isTemporalType(Integer.class)).is(false);
    }

    @Test
    public void nowReturnsValueOfRequestedType() {
        TEMPORAL_TYPES.forEach(type ->
                check(TemporalUtil.now(type)).isInstanceOf(type));
    }

    @Test(expected = DataException.class)
    public void nowFailsOnUnsupportedType() {
        TemporalUtil.now(Integer.class);
    }
}
