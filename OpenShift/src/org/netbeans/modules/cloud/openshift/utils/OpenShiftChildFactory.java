package org.netbeans.modules.cloud.openshift.utils;
import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IUser;
import com.openshift.client.cartridge.ICartridge;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.beans.IntrospectionException;
import java.net.URL;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.cloud.openshift.OpenShiftCloudInstance;
//import org.netbeans.modules.git.ui.actions.ContextHolder;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.openide.awt.HtmlBrowser;
import org.openide.nodes.BeanNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.netbeans.modules.web.common.api.WebUtils;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Children;
import org.openide.util.Cancellable;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;
public class OpenShiftChildFactory extends ChildFactory<IApplication> {
    private final String username;
    private final String password;
    public OpenShiftChildFactory(String username, String password) {
        this.username = username;
        this.password = password;
    }
    @Override
    protected boolean createKeys(List<IApplication> list) {
        ProgressHandle h = ProgressHandleFactory.createHandle("Please be patient. Retrieving your applications...", new Cancellable() {
            @Override
            public boolean cancel() {
                return true;
            }
        });
        h.start();
        try {
            IUser user
                    = OpenShiftUtilities.connect2OpenShift(username,password);
            for (IDomain domain : user.getDomains()) {
                list.addAll(domain.getApplications());
            }
        } catch (Exception e) {
            StatusDisplayer.getDefault().setStatusText("Failed to connect: " + e.getMessage());
        }
        h.finish();
        return true;
    }
    
    //TODO: ContextHolder in Lookup?
//    private class MyCH extends ContextHolder {
//        public MyCH() {
//            super(null);
//        }
//    }
    @Override
    protected Node createNodeForKey(final IApplication key) {
        BeanNode<IApplication> applicationNode = null;
        try {
            applicationNode = new BeanNode<IApplication>(key, Children.create(new CartridgeChildFactory(key), true)) {
                @Override
                public Action[] getActions(boolean context) {
                    return new Action[]{
                        new AbstractAction("View") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String appURL = key.getApplicationUrl();
                                URL url = WebUtils.stringToUrl(appURL);
                                HtmlBrowser.URLDisplayer.getDefault().showURL(url);
                            }
                        },
                        new AbstractAction("Git Sources") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                                clipboard.setContents(new StringSelection(key.getGitUrl()), null);
                                NotificationDisplayer.getDefault().notify("Git URL", ImageUtilities.loadImageIcon(OpenShiftCloudInstance.ICON, false), key.getGitUrl(), null);
                                Action gitCloneAction = FileUtil.getConfigObject("Actions/Git/org-netbeans-modules-git-ui-clone-CloneAction.instance", Action.class);
                                gitCloneAction.actionPerformed(e);
                            }
                        }};
                }
                @Override
                public Image getIcon(int type) {
                    return badgeIcon(UISupport.getIcon(UISupport.ServerIcon.WAR_ARCHIVE));
                }
                @Override
                public Image getOpenedIcon(int type) {
                    return badgeIcon(UISupport.getIcon(UISupport.ServerIcon.WAR_ARCHIVE));
                }
                private Image badgeIcon(Image origImg) {
                    Image badge = null;
                    return badge != null ? ImageUtilities.mergeImages(origImg, badge, 15, 8) : origImg;
                }
            };
            applicationNode.setDisplayName(key.getName());
            applicationNode.setShortDescription(key.getCartridge().getDisplayName());
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return applicationNode;
    }
    private class CartridgeChildFactory extends ChildFactory<ICartridge> {
        private final IApplication application;
        public CartridgeChildFactory(IApplication application) {
            this.application = application;
        }
        @Override
        protected boolean createKeys(List<ICartridge> list) {
            list.addAll(application.getEmbeddedCartridges());
            return true;
        }
        @Override
        protected Node createNodeForKey(final ICartridge key) {
            BeanNode node = null;
            try {
                node = new BeanNode(key);
                node.setDisplayName(key.getDisplayName());
                node.setShortDescription(key.getDescription());
            } catch (IntrospectionException ex) {
                Exceptions.printStackTrace(ex);
            }
            return node;
        }
    }
}
