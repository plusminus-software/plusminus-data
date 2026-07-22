package software.plusminus.data.util;

import lombok.experimental.UtilityClass;
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
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

@UtilityClass
public class TemporalUtil {

    private final Map<Class<?>, Supplier<?>> currentTimeSuppliers = createCurrentTimeSuppliers();

    public boolean isTemporalType(Class<?> type) {
        return currentTimeSuppliers.containsKey(type);
    }

    public <T> T now(Class<T> type) {
        Supplier<?> currentTimeSupplier = currentTimeSuppliers.get(type);
        if (currentTimeSupplier == null) {
            throw new DataException("Unknown type to represent current time: " + type.getName());
        }
        return type.cast(currentTimeSupplier.get());
    }

    private Map<Class<?>, Supplier<?>> createCurrentTimeSuppliers() {
        Map<Class<?>, Supplier<?>> suppliers = new IdentityHashMap<>();
        suppliers.put(Instant.class, Instant::now);
        suppliers.put(ZonedDateTime.class, ZonedDateTime::now);
        suppliers.put(OffsetDateTime.class, OffsetDateTime::now);
        suppliers.put(OffsetTime.class, OffsetTime::now);
        suppliers.put(LocalDateTime.class, LocalDateTime::now);
        suppliers.put(LocalDate.class, LocalDate::now);
        suppliers.put(LocalTime.class, LocalTime::now);
        suppliers.put(HijrahDate.class, HijrahDate::now);
        suppliers.put(JapaneseDate.class, JapaneseDate::now);
        suppliers.put(MinguoDate.class, MinguoDate::now);
        suppliers.put(ThaiBuddhistDate.class, ThaiBuddhistDate::now);
        suppliers.put(Year.class, Year::now);
        suppliers.put(YearMonth.class, YearMonth::now);
        suppliers.put(Date.class, Date::new);
        suppliers.put(Long.class, System::currentTimeMillis);
        suppliers.put(String.class, () -> ZonedDateTime.now().toString());
        return suppliers;
    }
}
