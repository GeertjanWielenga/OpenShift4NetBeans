package org.netbeans.modules.cloud.openshift.serverplugin;
import java.awt.Image;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.modules.cloud.openshift.*;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.netbeans.api.annotations.common.StaticResource;
import static org.netbeans.modules.cloud.common.spi.support.serverplugin.InstanceState.LAUNCHING;
import static org.netbeans.modules.cloud.common.spi.support.serverplugin.InstanceState.READY;
import static org.netbeans.modules.cloud.common.spi.support.serverplugin.InstanceState.TERMINATED;
import static org.netbeans.modules.cloud.common.spi.support.serverplugin.InstanceState.TERMINATING;
import static org.netbeans.modules.cloud.common.spi.support.serverplugin.InstanceState.UPDATING;
import org.netbeans.modules.cloud.openshift.OpenShiftCloudInstance;
import org.netbeans.modules.cloud.openshift.utils.OpenShiftChildFactory;
import org.netbeans.spi.server.ServerInstanceImplementation;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;
public class OpenShiftServerInstanceImplementation implements ServerInstanceImplementation {
    private final OpenShiftCloudInstance OpenShiftCloudInstance;
    private final OpenShiftServerInstance OpenShiftServerInstance;
    private final String userName;
    private final String password;
    @StaticResource
    private static final String RUNNING_ICON
            = "org/netbeans/modules/cloud/OpenShift/serverplugin/resources/running.png"; // NOI18N
    @StaticResource
    private static final String WAITING_ICON
            = "org/netbeans/modules/cloud/OpenShift/serverplugin/resources/waiting.png"; // NOI18N
    @StaticResource
    private static final String TERMINATED_ICON
            = "org/netbeans/modules/cloud/OpenShift/serverplugin/resources/terminated.png"; // NOI18N
    @StaticResource
    private static final String FAILED_ICON
            = "org/netbeans/modules/cloud/OpenShift/serverplugin/resources/failed.png"; // NOI18N
    public OpenShiftServerInstanceImplementation(OpenShiftServerInstance OpenShiftServerInstance) {
        this.OpenShiftServerInstance = OpenShiftServerInstance;
        this.OpenShiftCloudInstance = OpenShiftServerInstance.getOpenShiftInstance();
        this.userName = OpenShiftCloudInstance.getUserName();
        this.password = OpenShiftCloudInstance.getPassword();
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
        AbstractNode node = new AbstractNode(Children.create(new OpenShiftChildFactory(userName, password), true), Lookups.singleton(OpenShiftCloudInstance)) {
            @Override
            public Action[] getActions(boolean context) {
                return new Action[]{
                    new AbstractAction("Refresh") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            OpenShiftServerInstanceProvider.getProvider().refreshServers();
                        }
                    },
                    new AbstractAction("Properties") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Action serverManagerAction = FileUtil.getConfigObject("Actions/Tools/org-netbeans-modules-server-ui-manager-ServerManagerAction.instance", Action.class);
                            serverManagerAction.actionPerformed(e);
                        }
                    }
                };
            }
            @Override
            public Image getIcon(int type) {
                return badgeIcon(super.getIcon(type));
            }
            @Override
            public Image getOpenedIcon(int type) {
                return badgeIcon(super.getOpenedIcon(type));
            }
        };
        node.setDisplayName(OpenShiftCloudInstance.TYPE);
        node.setIconBaseWithExtension(OpenShiftCloudInstance.ICON);
        return node;
    }
    private Image badgeIcon(Image origImg) {
        Image badge = null;
        switch (OpenShiftServerInstance.getState()) {
            case UPDATING:
            case LAUNCHING:
            case TERMINATING:
                badge = ImageUtilities.loadImage(WAITING_ICON);
                break;
            case READY:
                badge = ImageUtilities.loadImage(RUNNING_ICON);
                break;
            case TERMINATED:
                badge = ImageUtilities.loadImage(TERMINATED_ICON);
                break;
        }
        return badge != null ? ImageUtilities.mergeImages(origImg, badge, 15, 8) : origImg;
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
