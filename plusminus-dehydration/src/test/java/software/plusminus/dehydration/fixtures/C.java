package software.plusminus.dehydration.fixtures;

import lombok.Data;
import org.hibernate.annotations.Type;
import software.plusminus.json.annotation.Uuid;
import software.plusminus.json.model.ApiObject;

import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Data
@Entity
public class C implements ApiObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Uuid
    @Column(unique = true, updatable = false)
    @Type(type = "uuid-char")
    private UUID uuid = UUID.randomUUID();

    private String name;

    @OneToMany(mappedBy = "entityC")
    private List<A> as;

}
