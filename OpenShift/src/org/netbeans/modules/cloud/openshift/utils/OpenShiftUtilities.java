package org.netbeans.modules.cloud.openshift.utils;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftConnectionFactory;
import com.openshift.client.OpenShiftException;
public class OpenShiftUtilities {
    static IUser user = null;
    public static IUser connect2OpenShift(String username, String password) throws OpenShiftException {
        if (user == null) {
            IOpenShiftConnection connection
                    = new OpenShiftConnectionFactory().getConnection(
                            "netbeans",
                            username,
                            password);
            user = connection.getUser();
        }
        return user;
    }
}
