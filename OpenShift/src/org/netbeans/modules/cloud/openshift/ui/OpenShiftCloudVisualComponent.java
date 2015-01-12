package org.netbeans.modules.cloud.openshift.ui;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.validation.api.builtin.stringvalidation.StringValidators;
import org.netbeans.validation.api.ui.ValidationGroup;
public class OpenShiftCloudVisualComponent extends javax.swing.JPanel {
    private boolean isValid = false;
    public static final String PROP_USER_NAME="user name";
    public static final String PROP_PASSWORD="password";
//    public static final String PROP_DISPLAY_TEXT="display text";
    public ValidationGroup group = null;
    public OpenShiftCloudVisualComponent() {
        initComponents();
        group = validationPanel1.getValidationGroup();
        passwordField.setName("\"Password\"");
        userField.setName("\"User Name\"");
//        displayTextField.setName("\"Display Text\"");
        group.add(passwordField, StringValidators.REQUIRE_NON_EMPTY_STRING);
        group.add(userField, StringValidators.REQUIRE_NON_EMPTY_STRING);
//        group.add(displayTextField, StringValidators.REQUIRE_NON_EMPTY_STRING);
        passwordField.getDocument().addDocumentListener(new ValidationDocumentListener());
        userField.getDocument().addDocumentListener(new ValidationDocumentListener());
//        displayTextField.getDocument().addDocumentListener(new ValidationDocumentListener());
    }
    private class ValidationDocumentListener implements DocumentListener{
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
//            if(displayTextField.getDocument()==de.getDocument()){
//                firePropertyChange(PROP_DISPLAY_TEXT, 0, 1);
//            }
            if(userField.getDocument()==de.getDocument()){
                firePropertyChange(PROP_USER_NAME, 0, 1);
            }
            if(passwordField.getDocument()==de.getDocument()){
                firePropertyChange(PROP_PASSWORD, 0, 1);
            }
        }
    }
    @Override
    public boolean isValid() {
        return isValid;
    }
//    public String getDisplayText() {
//        return displayTextField.getText();
//    }
    public String getUserName() {
        return userField.getText();
    }
    public String getPassword() {
        return String.valueOf(passwordField.getPassword());
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        userField = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        validationPanel1 = new org.netbeans.validation.api.ui.swing.ValidationPanel();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(OpenShiftCloudVisualComponent.class, "OpenShiftCloudVisualComponent.jPanel1.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(OpenShiftCloudVisualComponent.class, "OpenShiftCloudVisualComponent.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(OpenShiftCloudVisualComponent.class, "OpenShiftCloudVisualComponent.jLabel2.text")); // NOI18N

        userField.setText(org.openide.util.NbBundle.getMessage(OpenShiftCloudVisualComponent.class, "OpenShiftCloudVisualComponent.userField.text")); // NOI18N

        passwordField.setText(org.openide.util.NbBundle.getMessage(OpenShiftCloudVisualComponent.class, "OpenShiftCloudVisualComponent.passwordField.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(userField)
                    .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(userField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {passwordField, userField});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(validationPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addComponent(validationPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(107, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JTextField userField;
    private org.netbeans.validation.api.ui.swing.ValidationPanel validationPanel1;
    // End of variables declaration//GEN-END:variables
}
