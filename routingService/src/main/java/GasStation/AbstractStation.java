package GasStation;


import com.google.common.base.Function;

import java.util.Collection;

public abstract class AbstractStation implements IStation {
    public static Collection<GasStation> getByID(int id, Function<String, Collection<GasStation>> queryFunction) {
        return queryFunction.apply("SELECT * FROM stations where id = " + id);
    }


}
