package org.netbeans.modules.cloud.openshift;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.server.ServerInstance;
import org.netbeans.spi.server.ServerInstanceFactory;
import org.netbeans.spi.server.ServerInstanceProvider;
import org.openide.util.ChangeSupport;
public class OpenShiftCloudInstanceProvider implements ServerInstanceProvider, ChangeListener {
    private static OpenShiftCloudInstanceProvider instance;
    private List<ServerInstance> instances;
    private final ChangeSupport listeners;
    private OpenShiftCloudInstanceProvider() {
        listeners = new ChangeSupport(this);
        instances = Collections.<ServerInstance>emptyList();
        OpenShiftCloudInstanceManager.getDefault().addChangeListener(this);
        refreshServers();
    }
    public static synchronized OpenShiftCloudInstanceProvider getProvider() {
        if (instance == null) {
            instance = new OpenShiftCloudInstanceProvider();
        }
        return instance;
    }
    @Override
    public List<ServerInstance> getInstances() {
        return instances;
    }
    @Override
    public void addChangeListener(ChangeListener listener) {
        listeners.addChangeListener(listener);
    }
    @Override
    public void removeChangeListener(ChangeListener listener) {
        listeners.removeChangeListener(listener);
    }
    @Override
    public void stateChanged(ChangeEvent e) {
        refreshServers();
    }
    private void refreshServers() {
        List<ServerInstance> servers = new ArrayList<ServerInstance>();
        for (OpenShiftCloudInstance OpenShiftInstance : OpenShiftCloudInstanceManager.getDefault().getInstances()) {
            servers.add(ServerInstanceFactory.createServerInstance(new OpenShiftCloudInstanceImplementation(OpenShiftInstance)));
        }
        this.instances = servers;
        listeners.fireChange();
    }
}
