package org.netbeans.modules.cloud.openshift.ui;
import org.netbeans.modules.cloud.openshift.OpenShiftCloudInstance;
import org.netbeans.spi.server.ServerWizardProvider;
import org.openide.WizardDescriptor.InstantiatingIterator;
public class OpenShiftCloudWizardProvider implements ServerWizardProvider {
    @Override
    public String getDisplayName() {
        return OpenShiftCloudInstance.TYPE;
    }
    @Override
    public InstantiatingIterator getInstantiatingIterator() {
        return new OpenShiftCloudWizardIterator();
    }
}
