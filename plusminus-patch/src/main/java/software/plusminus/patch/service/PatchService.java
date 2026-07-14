package software.plusminus.patch.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.plusminus.patch.service.patcher.Patcher;
import software.plusminus.util.ObjectUtils;

import java.util.Collections;
import java.util.List;

@Service
public class PatchService {

    @Autowired(required = false)
    private List<Patcher> patchers = Collections.emptyList();

    public <T> void patch(T patch, T target) {
        patchers.forEach(p -> p.patch(patch, target));
        String[] nullProperties = ObjectUtils.getNullPropertyNames(patch);
        BeanUtils.copyProperties(patch, target, nullProperties);
    }
}
