package software.plusminus.data.service.metadata;

public interface MetadataService {

    <T> Class<T> findType(String typeName);

}
