package org.netbeans.modules.cloud.openshift.j2ee;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.status.ProgressObject;
import org.netbeans.modules.cloud.openshift.serverplugin.OpenShiftServerInstanceProvider;
import org.netbeans.modules.cloud.openshift.ui.OpenShiftCloudWizardIterator;
import org.netbeans.modules.j2ee.deployment.plugins.api.ServerDebugInfo;
import org.netbeans.modules.j2ee.deployment.plugins.spi.FindJSPServlet;
import org.netbeans.modules.j2ee.deployment.plugins.spi.IncrementalDeployment;
import org.netbeans.modules.j2ee.deployment.plugins.spi.OptionalDeploymentManagerFactory;
import org.netbeans.modules.j2ee.deployment.plugins.spi.ServerInitializationException;
import org.netbeans.modules.j2ee.deployment.plugins.spi.StartServer;
import org.openide.WizardDescriptor.InstantiatingIterator;
public class OpenShiftOptionalDeploymentManagerFactory extends OptionalDeploymentManagerFactory {
    @Override
    public StartServer getStartServer(DeploymentManager dm) {
        return new OpenShiftStartServer();
    }
    @Override
    public IncrementalDeployment getIncrementalDeployment(DeploymentManager dm) {
        return null;
    }
    @Override
    public InstantiatingIterator getAddInstanceIterator() {
        return new OpenShiftCloudWizardIterator();
    }
    @Override
    public FindJSPServlet getFindJSPServlet(DeploymentManager dm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public boolean isCommonUIRequired() {
        return false;
    }
    @Override
    public void finishServerInitialization() throws ServerInitializationException {
        OpenShiftServerInstanceProvider.getProvider().refreshServers();
    }
    public static final class OpenShiftStartServer extends StartServer {
        @Override
        public boolean isAlsoTargetServer(Target target) {
            return true;
        }
        @Override
        public boolean supportsStartDeploymentManager() {
            return false;
        }
        @Override
        public ProgressObject startDeploymentManager() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        @Override
        public ProgressObject stopDeploymentManager() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        @Override
        public boolean needsStartForConfigure() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        @Override
        public boolean needsStartForTargetList() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        @Override
        public boolean needsStartForAdminConfig() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        @Override
        public boolean isRunning() {
            return true;
        }
        @Override
        public boolean isDebuggable(Target target) {
            return false;
        }
        @Override
        public ProgressObject startDebugging(Target target) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        @Override
        public ServerDebugInfo getDebugInfo(Target target) {
            return null;
        }
    }
}
