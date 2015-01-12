package org.netbeans.modules.cloud.openshift;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.api.server.properties.InstanceProperties;
import org.netbeans.api.server.properties.InstancePropertiesManager;
import org.openide.util.ChangeSupport;
public class OpenShiftCloudInstanceManager {
    private static OpenShiftCloudInstanceManager instance;
    private static final String OpenShift_IP_NAMESPACE = "cloud.OpenShift"; // NOI18N
    private final List<OpenShiftCloudInstance> instances = new ArrayList<OpenShiftCloudInstance>();
    private final ChangeSupport listeners;
    public static synchronized OpenShiftCloudInstanceManager getDefault() {
        if (instance == null) {
            instance = new OpenShiftCloudInstanceManager();
        }
        return instance;
    }
    public OpenShiftCloudInstanceManager() {
        listeners = new ChangeSupport(this);
        init();
    }
    private void init() {
        instances.addAll(load());
        notifyChange();
    }
    private void notifyChange() {
        listeners.fireChange();
    }
    public List<OpenShiftCloudInstance> getInstances() {
        return instances;
    }
    public void add(OpenShiftCloudInstance OpenShiftInstance) {
        store(OpenShiftInstance);
        instances.add(OpenShiftInstance);
        notifyChange();
    }
    private static List<OpenShiftCloudInstance> load() {
        List<OpenShiftCloudInstance> result = new ArrayList<OpenShiftCloudInstance>();
        for (InstanceProperties props : InstancePropertiesManager.getInstance().getProperties(OpenShift_IP_NAMESPACE)) {
            String userName = props.getString("userName", null); // NOI18N
            String password = props.getString("password", null); // NOI18N
            result.add(new OpenShiftCloudInstance(userName, password));
        }
        return result;
    }
    private void store(OpenShiftCloudInstance OpenShiftInstance) {
        List<InstanceProperties> list = InstancePropertiesManager.getInstance().getProperties(OpenShift_IP_NAMESPACE);
        if (list == null) {
            InstanceProperties props = InstancePropertiesManager.getInstance().createProperties(OpenShift_IP_NAMESPACE);
            props.putString("userName", OpenShiftInstance.getUserName()); // NOI18N
            props.putString("password", OpenShiftInstance.getPassword()); // NOI18N
        }
    }
    public void addChangeListener(ChangeListener l) {
        listeners.addChangeListener(l);
    }
    public void removeChangeListener(ChangeListener l) {
        listeners.removeChangeListener(l);
    }
    public void remove(OpenShiftCloudInstance fci) {
        for (InstanceProperties props : InstancePropertiesManager.getInstance().getProperties(OpenShift_IP_NAMESPACE)) {
            props.remove();
            break;
        }
        instances.remove(fci);
        fci.deregisterJ2EEServerInstances();
        notifyChange();
    }
}
