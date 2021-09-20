package software.plusminus.data.service.entity.test;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Data
@Embeddable
public class EmbeddableClass {

    @ManyToOne
    @PrimaryKeyJoinColumn
    private MultiEntity entity;

}