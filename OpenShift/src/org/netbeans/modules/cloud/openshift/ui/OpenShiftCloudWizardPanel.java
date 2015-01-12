package org.netbeans.modules.cloud.openshift.ui;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbPreferences;
public class OpenShiftCloudWizardPanel implements WizardDescriptor.Panel<WizardDescriptor>, PropertyChangeListener {
    private OpenShiftCloudVisualComponent component;
    public static final String USERNAME = "username"; // String
    public static final String PASSWORD = "password"; // String
    public static final String SELECTED_DOMAIN = "domain"; // String
    private boolean isValid = false;
    private final EventListenerList listeners = new EventListenerList();
    @Override
    public OpenShiftCloudVisualComponent getComponent() {
        if (component == null) {
            component = new OpenShiftCloudVisualComponent();
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, new String[]{"Settings"});
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, Integer.valueOf(0));
            component.addPropertyChangeListener(this);
        }
        return component;
    }
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals(OpenShiftCloudVisualComponent.PROP_USER_NAME)
                || event.getPropertyName().equals(OpenShiftCloudVisualComponent.PROP_PASSWORD)) {
            boolean oldState = isValid;
            isValid = checkValidatity();
            fireChangeEvent(this, oldState, isValid);
        }
    }
    private boolean checkValidatity() {
        if (getComponent().getUserName().isEmpty() || getComponent().getPassword().isEmpty()) {
            return false;
        }
        return true;
    }
    @Override
    public void storeSettings(WizardDescriptor settings) {
        if (component != null) {
            settings.putProperty(USERNAME, component.getUserName());
            settings.putProperty(PASSWORD, component.getPassword());
            NbPreferences.forModule(OpenShiftCloudWizardPanel.class).put(USERNAME, component.getUserName());
            NbPreferences.forModule(OpenShiftCloudWizardPanel.class).put(PASSWORD, component.getPassword());
        }
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
    public void readSettings(WizardDescriptor data) {
    }
    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }
    private void fireChangeEvent(OpenShiftCloudWizardPanel source, boolean oldState, boolean newState) {
        if (oldState != newState) {
            ChangeEvent ce = new ChangeEvent(source);
            for (ChangeListener listener : listeners.getListeners(ChangeListener.class)) {
                listener.stateChanged(ce);
            }
        }
    }
}
