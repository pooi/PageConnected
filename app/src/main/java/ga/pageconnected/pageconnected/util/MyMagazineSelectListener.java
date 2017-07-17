package ga.pageconnected.pageconnected.util;

import java.io.Serializable;

/**
 * Created by tw on 2017. 7. 16..
 */

public interface MyMagazineSelectListener extends Serializable {
    void select(int fragmentPosition, boolean state);
}
