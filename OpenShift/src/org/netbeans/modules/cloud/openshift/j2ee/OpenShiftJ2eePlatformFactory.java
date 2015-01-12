package org.netbeans.modules.cloud.openshift.j2ee;
import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformFactory;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformImpl;
public class OpenShiftJ2eePlatformFactory extends J2eePlatformFactory {
    @Override
    public J2eePlatformImpl getJ2eePlatformImpl(DeploymentManager dm) {
        return new OpenShiftJ2eePlatformImpl2(dm);
    }
}
