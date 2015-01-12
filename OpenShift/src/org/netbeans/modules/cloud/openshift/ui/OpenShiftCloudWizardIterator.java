package org.netbeans.modules.cloud.openshift.ui;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.cloud.openshift.OpenShiftCloudInstance;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
public class OpenShiftCloudWizardIterator implements WizardDescriptor.InstantiatingIterator {
//    private final ChangeSupport listeners;
    private WizardDescriptor wizard;
    private OpenShiftCloudWizardPanel panel;
    boolean first = true;
    public OpenShiftCloudWizardIterator() {
//        listeners = new ChangeSupport(this);
    }
    @Override
    public Set instantiate() throws IOException {
        String userName = (String) wizard.getProperty(OpenShiftCloudWizardPanel.USERNAME);
        String password = (String) wizard.getProperty(OpenShiftCloudWizardPanel.PASSWORD);
        OpenShiftCloudInstance instance = OpenShiftCloudInstance.getDefault(userName, password);
//        OpenShiftCloudInstance instance = new OpenShiftCloudInstance(userName,password);
//        OpenShiftCloudInstanceManager.getDefault().add(instance);
        return Collections.singleton(instance.getServerInstance());
    }
    @Override
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }
    @Override
    public void uninitialize(WizardDescriptor wizard) {
        panel = null;
    }
    @Override
    public Panel current() {
        if (panel == null) {
            panel = new OpenShiftCloudWizardPanel();
        }
        return panel;
    }
    @Override
    public String name() {
        return "XXX";
    }
    @Override
    public boolean hasNext() {
        return false;
    }
    @Override
    public boolean hasPrevious() {
        return !first;
    }
    @Override
    public void nextPanel() {
        first = false;
    }
    @Override
    public void previousPanel() {
        first = true;
    }
    @Override
    public void addChangeListener(ChangeListener l) {
//        listeners.addChangeListener(l);
    }
    @Override
    public void removeChangeListener(ChangeListener l) {
//        listeners.removeChangeListener(l);
    }
}
