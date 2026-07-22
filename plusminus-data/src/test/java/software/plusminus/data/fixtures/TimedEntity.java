package software.plusminus.data.fixtures;

import lombok.Data;
import software.plusminus.data.annotation.ModificationTime;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class TimedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ModificationTime
    private LocalDateTime modificationTime;

    private String myField;

}
