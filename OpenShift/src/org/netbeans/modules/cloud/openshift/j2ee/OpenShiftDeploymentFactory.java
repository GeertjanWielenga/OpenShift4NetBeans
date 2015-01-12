package org.netbeans.modules.cloud.openshift.j2ee;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import org.netbeans.modules.cloud.openshift.OpenShiftCloudInstance;
public class OpenShiftDeploymentFactory implements DeploymentFactory {
    @Override
    public boolean handlesURI(String string) {
        return true;
    }
    @Override
    public DeploymentManager getDeploymentManager(String uri, String username, String password) throws DeploymentManagerCreationException {
        return new OpenShiftDeploymentManager();
    }
    @Override
    public DeploymentManager getDisconnectedDeploymentManager(String uri) throws DeploymentManagerCreationException {
        return new OpenShiftDeploymentManager();
    }
    @Override
    public String getDisplayName() {
        return OpenShiftCloudInstance.TYPE;
    }
    @Override
    public String getProductVersion() {
        return "1.0"; // NOI18N
    }
}
