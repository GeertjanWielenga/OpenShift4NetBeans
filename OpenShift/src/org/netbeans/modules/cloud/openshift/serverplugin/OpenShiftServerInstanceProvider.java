package org.netbeans.modules.cloud.openshift.serverplugin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.server.ServerInstance;
import org.netbeans.modules.cloud.openshift.OpenShiftCloudInstance;
import org.netbeans.modules.cloud.openshift.OpenShiftCloudInstanceManager;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceCreationException;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.spi.server.ServerInstanceFactory;
import org.netbeans.spi.server.ServerInstanceProvider;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
public final class OpenShiftServerInstanceProvider implements ServerInstanceProvider, ChangeListener {
    private final ChangeSupport listeners;
    private List<ServerInstance> instances;
    private static OpenShiftServerInstanceProvider instance;
    private OpenShiftServerInstanceProvider() {
        listeners = new ChangeSupport(this);
        instances = Collections.<ServerInstance>emptyList();
        refreshServers();
    }
    public static synchronized OpenShiftServerInstanceProvider getProvider() {
        if (instance == null) {
            instance = new OpenShiftServerInstanceProvider();
            OpenShiftCloudInstanceManager.getDefault().addChangeListener(instance);
        }
        return instance;
    }
    @Override
    public List<ServerInstance> getInstances() {
        synchronized (this) {
            return new ArrayList<ServerInstance>(instances);
        }
    }
    @Override
    public void addChangeListener(ChangeListener listener) {
        listeners.addChangeListener(listener);
    }
    @Override
    public void removeChangeListener(ChangeListener listener) {
        listeners.removeChangeListener(listener);
    }
    public void refreshServersSynchronously() {
        List<ServerInstance> servers = new ArrayList<ServerInstance>();
        for (OpenShiftCloudInstance OpenShiftCloudInstance : OpenShiftCloudInstanceManager.getDefault().getInstances()) {
            OpenShiftServerInstance OpenShiftServerInstance = OpenShiftCloudInstance.readOpenShiftServerInstance();
            ServerInstance serverInstance = ServerInstanceFactory.createServerInstance(new OpenShiftServerInstanceImplementation(OpenShiftServerInstance));
            InstanceProperties ip = InstanceProperties.getInstanceProperties(OpenShiftServerInstance.getId());
            if (ip == null) {
                Map<String, String> props = new HashMap<String, String>();
                String type = OpenShiftCloudInstance.TYPE;
                String user = OpenShiftCloudInstance.getUserName();
                String password = OpenShiftCloudInstance.getPassword();
                try {
                    ip = InstanceProperties.createInstancePropertiesNonPersistent(OpenShiftServerInstance.getId(),
                            user, password, type, props);
                } catch (InstanceCreationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            OpenShiftCloudInstance.setServerInstance(serverInstance);
            servers.add(serverInstance);
        }
        this.instances = servers;
        listeners.fireChange();
    }
    public final Future<Void> refreshServers() {
        return OpenShiftCloudInstance.runAsynchronously(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                refreshServersSynchronously();
                return null;
            }
        });
    }
    @Override
    public void stateChanged(ChangeEvent e) {
        refreshServers();
    }
}
