package org.netbeans.modules.cloud.openshift.project;
import com.openshift.client.cartridge.ICartridge;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.netbeans.modules.cloud.openshift.ui.OpenShiftCloudWizardPanel;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbPreferences;
public class OpenShiftNewAppPanel implements WizardDescriptor.Panel<WizardDescriptor>, PropertyChangeListener {
    private OpenShiftNewAppPanelVisual component;
    public static final String APPLICATIONNAME = "application name";
    public static final String CARTRIDGENAME = "cartridge name";
    private String userName;
    private String password;
    private boolean isValid = false;
    private final EventListenerList listeners = new EventListenerList();
    @Override
    public OpenShiftNewAppPanelVisual getComponent() {
        if (component == null) {
            component = new OpenShiftNewAppPanelVisual(userName, password);
            component.addPropertyChangeListener(this);
        }
        return component;
    }
    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }
    @Override
    public boolean isValid() {
        return isValid;
    }
    @Override
    public void addChangeListener(ChangeListener l) {
        listeners.add(ChangeListener.class, l);
    }
    @Override
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(ChangeListener.class, l);
    }
    @Override
    public void readSettings(WizardDescriptor wiz) {
        userName = NbPreferences.forModule(OpenShiftCloudWizardPanel.class).get(OpenShiftCloudWizardPanel.USERNAME, "");
        password = NbPreferences.forModule(OpenShiftCloudWizardPanel.class).get(OpenShiftCloudWizardPanel.PASSWORD, "");
        component.init(userName, password);
    }
    @Override
    public void storeSettings(WizardDescriptor settings) {
        if (component != null) {
            String applicationName = component.getApplicationName();
            settings.putProperty(APPLICATIONNAME, applicationName);
            String cartridge = component.getCartridge();
            settings.putProperty(CARTRIDGENAME, cartridge);
            Object[] iCartridges = component.getEmbeddableCartridges().toArray();
            for (int i = 0; i < iCartridges.length; i++) {
                ICartridge iCartridge = (ICartridge) iCartridges[i];
                NbPreferences.forModule(OpenShiftNewAppPanel.class).put(applicationName + "-cartridge-" + i, iCartridge.getName());
            }
            int size = iCartridges.length;
            NbPreferences.forModule(OpenShiftNewAppPanel.class).putInt(applicationName + "-cartridge-length", size);
//            NbPreferences.forModule(OpenShiftCloudWizardPanel.class).put(APPLICATIONNAME, component.getApplicationName());
        }
    }
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals(OpenShiftNewAppPanelVisual.PROP_APPLICATION_NAME)) {
            boolean oldState = isValid;
            isValid = checkValidatity();
            fireChangeEvent(this, oldState, isValid);
        }
    }
    private void fireChangeEvent(OpenShiftNewAppPanel source, boolean oldState, boolean newState) {
        if (oldState != newState) {
            ChangeEvent ce = new ChangeEvent(source);
            for (ChangeListener listener : listeners.getListeners(ChangeListener.class)) {
                listener.stateChanged(ce);
            }
        }
    }
    private boolean checkValidatity() {
        if (getComponent().getApplicationName().isEmpty()) {
            return false;
        }
        return true;
    }
}
