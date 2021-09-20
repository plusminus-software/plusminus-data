package software.plusminus.data.service.entity.test;

import lombok.Data;

import javax.persistence.Id;

@Data
public class SpringIdAnnotationEntity {
    @Id
    private Integer id;
}