package software.plusminus.dehydration.fixtures;

import lombok.Data;
import org.hibernate.annotations.Type;
import software.plusminus.json.annotation.Uuid;
import software.plusminus.json.model.ApiObject;

import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;

@Data
@Entity
public class A implements ApiObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Uuid
    @Column(unique = true, updatable = false)
    @Type(type = "uuid-char")
    private UUID uuid = UUID.randomUUID();

    private String name;

    @OneToMany(mappedBy = "entityA")
    private List<B> bs;

    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private C entityC;

}
