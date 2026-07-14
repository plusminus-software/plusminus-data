package software.plusminus.sync.dehydration;

import lombok.Data;
import software.plusminus.sync.models.AbstractEntity;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Data
@Entity
public class C extends AbstractEntity {

    private String name;

    @OneToMany(mappedBy = "entityC")
    private List<A> as;

}
