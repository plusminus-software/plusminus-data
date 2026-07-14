package software.plusminus.sync.dehydration;

import lombok.Data;
import software.plusminus.sync.models.AbstractEntity;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;

@Data
@Entity
public class A extends AbstractEntity {

    private String name;

    @OneToMany(mappedBy = "entityA")
    private List<B> bs;

    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private C entityC;

}
