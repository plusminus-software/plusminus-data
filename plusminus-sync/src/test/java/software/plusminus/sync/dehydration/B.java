package software.plusminus.sync.dehydration;

import lombok.Data;
import software.plusminus.sync.models.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Data
@Entity
public class B extends AbstractEntity {

    private String name;

    @ManyToOne
    @PrimaryKeyJoinColumn
    private A entityA;

}
