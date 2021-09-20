package software.plusminus.data.service.entity.test;

import lombok.Data;

import javax.persistence.Embedded;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrimaryKeyJoinColumn;

@Data
@MappedSuperclass
public abstract class AbstractMultiEntity {

    @ManyToOne
    @PrimaryKeyJoinColumn
    private MultiEntity left;

    @Embedded
    private EmbeddableClass embeddable;

}
