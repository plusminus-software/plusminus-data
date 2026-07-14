package software.plusminus.data.service.entity.test;

import lombok.Data;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Data
@Entity
public class MultiEntity extends AbstractMultiEntity {

    @Id
    private Long id;

    private String name;

    @ManyToOne
    @PrimaryKeyJoinColumn
    private MultiEntity right;

    @ManyToMany
    private List<MultiEntity> list;
}