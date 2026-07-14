package software.plusminus.patch.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@CollectionPatch
@Retention(RetentionPolicy.RUNTIME)
public @interface StringCollectionPatch {

    String splitter();

    int index() default 0;

}
