package org.netbeans.modules.cloud.openshift;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.netbeans.modules.cloud.openshift.serverplugin.OpenShiftServerInstanceProvider;
import org.netbeans.spi.server.ServerInstanceImplementation;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;
public class OpenShiftCloudInstanceImplementation implements ServerInstanceImplementation {
    private final OpenShiftCloudInstance OpenShiftCloudInstance;
    public OpenShiftCloudInstanceImplementation(OpenShiftCloudInstance OpenShiftInstance) {
        this.OpenShiftCloudInstance = OpenShiftInstance;
    }
    @Override
    public String getDisplayName() {
        return OpenShiftCloudInstance.TYPE;
    }
    @Override
    public String getServerDisplayName() {
        return OpenShiftCloudInstance.TYPE;
    }
    @Override
    public Node getFullNode() {
        return getBasicNode();
    }
    @Override
    public Node getBasicNode() {
        AbstractNode node = new AbstractNode(Children.LEAF, Lookups.singleton(OpenShiftCloudInstance)) {
            @Override
            public Action[] getActions(boolean context) {
                return new Action[]{
                    new AbstractAction("Refresh") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            OpenShiftServerInstanceProvider.getProvider().refreshServers();
                        }
                    },
                    null,
                    new AbstractAction("Remove") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            OpenShiftCloudInstance.getServerInstance().remove();
                        }
                    },
                    null,
                    new AbstractAction("Properties") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Action serverManagerAction = FileUtil.getConfigObject("Actions/Tools/org-netbeans-modules-server-ui-manager-CloudManagerAction.instance", Action.class);
                            serverManagerAction.actionPerformed(e);
                        }
                    }
                };
            }
        };
        node.setDisplayName(OpenShiftCloudInstance.TYPE);
        node.setIconBaseWithExtension(OpenShiftCloudInstance.ICON);
        return node;
    }
    @Override
    public JComponent getCustomizer() {
        return new JPanel();
    }
    @Override
    public void remove() {
        OpenShiftCloudInstanceManager.getDefault().remove(OpenShiftCloudInstance);
    }
    @Override
    public boolean isRemovable() {
        return true;
    }
}
