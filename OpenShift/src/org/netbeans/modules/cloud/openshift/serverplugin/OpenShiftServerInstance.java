package org.netbeans.modules.cloud.openshift.serverplugin;
import org.netbeans.api.server.ServerInstance;
import org.netbeans.modules.cloud.common.spi.support.serverplugin.InstanceState;
import org.netbeans.modules.cloud.openshift.OpenShiftCloudInstance;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
public class OpenShiftServerInstance {
    private OpenShiftCloudInstance OpenShiftInstance;
    private InstanceState state;
    private ServerInstance instance;
    private InstanceProperties ip;
    public OpenShiftServerInstance(OpenShiftCloudInstance oracleInstance) {
        this.OpenShiftInstance = oracleInstance;
        this.state = InstanceState.READY;
    }
    public OpenShiftCloudInstance getOpenShiftInstance() {
        return OpenShiftInstance;
    }
    public void updateState(String stateDesc) {
        state = InstanceState.READY;
    }
    public ServerInstance getInstance() {
        return instance;
    }
    void setInstance(ServerInstance instance) {
        this.instance = instance;
    }
    public InstanceProperties getInstanceProperties() {
        return ip;
    }
    void setInstanceProperties(InstanceProperties ip) {
        this.ip = ip;
    }
    public InstanceState getState() {
        return state;
    }
    public void setOpenShiftInstance(OpenShiftCloudInstance OpenShiftInstance) {
        this.OpenShiftInstance = OpenShiftInstance;
    }
    public String getDisplayName() {
        return OpenShiftCloudInstance.TYPE;
    }
    public String getId() {
        return createURL("openshift:", OpenShiftInstance.getUserName(), "BBB");
    }
    public static String createURL(String cloudInstance, String identityDomain, String javaServiceName) {
        return cloudInstance + "." + identityDomain + "." + javaServiceName;
    }
    public void deregister() {
        InstanceProperties.removeInstance(getId());
    }
    @Override
    public String toString() {
        return "OpenShiftServerInstance{" + "OpenShiftServerInstance=" + OpenShiftInstance + ", state=" + state +  "}";
    }
}
