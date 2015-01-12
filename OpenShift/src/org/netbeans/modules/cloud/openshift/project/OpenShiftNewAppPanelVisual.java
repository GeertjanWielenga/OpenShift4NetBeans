package org.netbeans.modules.cloud.openshift.project;
import org.netbeans.modules.cloud.openshift.project.ProgressGlassPane;
import com.openshift.client.IDomain;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftConnectionFactory;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.cloud.openshift.project.model.Domain;
import org.netbeans.validation.api.builtin.stringvalidation.StringValidators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;
public final class OpenShiftNewAppPanelVisual extends JPanel {
    /**
     * http://wildfly.org/news/2014/04/25/Getting-Started-WildFly-OpenShift/
     */
    private boolean isValid = false;
    public static final String PROP_APPLICATION_NAME = "user name";
    private List<IDomain> domains;
    private ProgressGlassPane glassPane;
    private ICartridge iCartridge;
    private Set<IEmbeddableCartridge> eCartridges;
    public ValidationGroup group = null;
    public OpenShiftNewAppPanelVisual(String userName, String password) {
        eCartridges = new HashSet<IEmbeddableCartridge>();
        initComponents();
        group = validationPanel.getValidationGroup();
        applicationNameField.setName("Application name");
        group.add(applicationNameField, StringValidators.REQUIRE_NON_EMPTY_STRING);
        applicationNameField.getDocument().addDocumentListener(new ValidationDocumentListener());
    }
    private class ValidationDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent de) {
            fireChange(de);
        }
        @Override
        public void removeUpdate(DocumentEvent de) {
            fireChange(de);
        }
        @Override
        public void changedUpdate(DocumentEvent de) {
            fireChange(de);
        }
        private void fireChange(DocumentEvent de) {
            if (applicationNameField.getDocument() == de.getDocument()) {
                firePropertyChange(PROP_APPLICATION_NAME, 0, 1);
            }
        }
    }
    public String getApplicationName() {
        return applicationNameField.getText();
    }
    public String getCartridge() {
        return iCartridge.getName();
    }
    public Set<IEmbeddableCartridge> getEmbeddableCartridges() {
        return eCartridges;
    }
    @Override
    public boolean isValid() {
        return isValid;
    }
    public void init(final String userName, final String password) {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                JRootPane dialog = (JRootPane) getParent().getParent().getParent().getParent().getParent().getParent();
                dialog.setGlassPane(glassPane = new ProgressGlassPane());
                glassPane.setVisible(true);
                startCloudDataAccessThread(userName, password);
            }
        });
    }
    private void startCloudDataAccessThread(final String userName, final String password) {
        Thread downloader = new Thread(new Runnable() {
            @Override
            public void run() {
                glassPane.setProgress(0);
                IUser user = addDomains(userName, password);
                glassPane.setProgress(50);
                addCartridges(user);
                glassPane.setVisible(false);
                glassPane.setProgress(0);
            }
        });
        downloader.start();
    }
    private IUser addDomains(String userName, String password) throws OpenShiftException {
        IOpenShiftConnection connection
                = new OpenShiftConnectionFactory().getConnection(
                        "netbeans",
                        userName,
                        password);
        IUser user = connection.getUser();
        domains = user.getDomains();
        DefaultComboBoxModel domainModel = new DefaultComboBoxModel();
        for (IDomain domain : domains) {
            domainModel.addElement(new Domain(domain));
        }
        domainList.setModel(domainModel);
        domainList.setSelectionInterval(0, 0);
        return user;
    }
    ButtonGroup cartridgesButtonGroup = new ButtonGroup();
    private void addCartridges(IUser user) throws OpenShiftException {
        cartridgesPanel.setLayout(new BoxLayout(cartridgesPanel, BoxLayout.Y_AXIS));
        ecartridgesPanel.setLayout(new BoxLayout(ecartridgesPanel, BoxLayout.Y_AXIS));
        List<ICartridge> cartridges = user.getConnection().getCartridges();
        List<IEmbeddableCartridge> embeddablecartridges = user.getConnection().getEmbeddableCartridges();
        for (final ICartridge cartridge : cartridges) {
            final JCheckBox box = new JCheckBox(cartridge.getDisplayName());
            cartridgesButtonGroup.add(box);
            box.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (box.isSelected()) {
                        iCartridge = cartridge;
                        StatusDisplayer.getDefault().setStatusText("Selected Carttridge: " + cartridge.getDisplayName());
                    }
                }
            });
            cartridgesPanel.add(box);
            cartridgesPanel.updateUI();
        }
        for (final IEmbeddableCartridge ecartridge : embeddablecartridges) {
            final JCheckBox box = new JCheckBox(ecartridge.getDisplayName());
            box.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (box.isSelected()) {
                        eCartridges.add(ecartridge);
                        StatusDisplayer.getDefault().setStatusText("Added: " + ecartridge.getDisplayName() + " (Total embedded cartridges: " + eCartridges.size() + ")");
                    } else {
                        if (eCartridges.contains(ecartridge)) {
                            eCartridges.remove(ecartridge);
                            StatusDisplayer.getDefault().setStatusText("Removed: " + ecartridge.getDisplayName() + " (Total embedded cartridges: " + eCartridges.size() + ")");
                        }
                    }
                }
            });
            ecartridgesPanel.add(box);
            ecartridgesPanel.updateUI();
        }
    }
    @Override
    public String getName() {
        return NbBundle.getMessage(OpenShiftProjectWizardIterator.class, "LBL_CreateProjectStep");
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        domainPane = new javax.swing.JScrollPane();
        domainList = new javax.swing.JList();
        newApplicationPanel = new javax.swing.JPanel();
        applicationNameField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cartridgesScrollPane = new javax.swing.JScrollPane();
        cartridgesPanel = new javax.swing.JPanel();
        validationPanel = new org.netbeans.validation.api.ui.swing.ValidationPanel();
        ecartridgesScrollPane = new javax.swing.JScrollPane();
        ecartridgesPanel = new javax.swing.JPanel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(OpenShiftNewAppPanelVisual.class, "OpenShiftNewAppPanelVisual.jLabel1.text")); // NOI18N

        domainPane.setViewportView(domainList);

        newApplicationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(OpenShiftNewAppPanelVisual.class, "OpenShiftNewAppPanelVisual.newApplicationPanel.border.title"))); // NOI18N

        applicationNameField.setText(org.openide.util.NbBundle.getMessage(OpenShiftNewAppPanelVisual.class, "OpenShiftNewAppPanelVisual.applicationNameField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(OpenShiftNewAppPanelVisual.class, "OpenShiftNewAppPanelVisual.jLabel2.text")); // NOI18N

        cartridgesScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(OpenShiftNewAppPanelVisual.class, "OpenShiftNewAppPanelVisual.cartridgesScrollPane.border.title"))); // NOI18N

        javax.swing.GroupLayout cartridgesPanelLayout = new javax.swing.GroupLayout(cartridgesPanel);
        cartridgesPanel.setLayout(cartridgesPanelLayout);
        cartridgesPanelLayout.setHorizontalGroup(
            cartridgesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 372, Short.MAX_VALUE)
        );
        cartridgesPanelLayout.setVerticalGroup(
            cartridgesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 265, Short.MAX_VALUE)
        );

        cartridgesScrollPane.setViewportView(cartridgesPanel);

        validationPanel.setBorder(null);

        ecartridgesScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(OpenShiftNewAppPanelVisual.class, "OpenShiftNewAppPanelVisual.ecartridgesScrollPane.border.title"))); // NOI18N

        javax.swing.GroupLayout ecartridgesPanelLayout = new javax.swing.GroupLayout(ecartridgesPanel);
        ecartridgesPanel.setLayout(ecartridgesPanelLayout);
        ecartridgesPanelLayout.setHorizontalGroup(
            ecartridgesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        ecartridgesPanelLayout.setVerticalGroup(
            ecartridgesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 108, Short.MAX_VALUE)
        );

        ecartridgesScrollPane.setViewportView(ecartridgesPanel);

        javax.swing.GroupLayout newApplicationPanelLayout = new javax.swing.GroupLayout(newApplicationPanel);
        newApplicationPanel.setLayout(newApplicationPanelLayout);
        newApplicationPanelLayout.setHorizontalGroup(
            newApplicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newApplicationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(newApplicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(newApplicationPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(applicationNameField))
                    .addComponent(cartridgesScrollPane)
                    .addComponent(ecartridgesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(validationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        newApplicationPanelLayout.setVerticalGroup(
            newApplicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newApplicationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(newApplicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(applicationNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cartridgesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ecartridgesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(validationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(domainPane))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(newApplicationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(domainPane, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(newApplicationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField applicationNameField;
    private javax.swing.JPanel cartridgesPanel;
    private javax.swing.JScrollPane cartridgesScrollPane;
    private javax.swing.JList domainList;
    private javax.swing.JScrollPane domainPane;
    private javax.swing.JPanel ecartridgesPanel;
    private javax.swing.JScrollPane ecartridgesScrollPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel newApplicationPanel;
    private org.netbeans.validation.api.ui.swing.ValidationPanel validationPanel;
    // End of variables declaration//GEN-END:variables
}
