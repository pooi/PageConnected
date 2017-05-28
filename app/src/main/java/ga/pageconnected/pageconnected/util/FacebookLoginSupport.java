package ga.pageconnected.pageconnected.util;

import com.facebook.FacebookException;
import com.facebook.Profile;

import java.util.HashMap;

/**
 * Created by tw on 2017-05-27.
 */

public interface FacebookLoginSupport {

    void afterFBLoginSuccess(Profile profile, HashMap<String, String> data);
    void afterFBLoginCancel();
    void afterFBLoginError(FacebookException error);
    void afterFBLogout();

}
