package org.netbeans.modules.cloud.openshift;
import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftConnectionFactory;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IEmbeddedCartridge;
import com.openshift.client.cartridge.StandaloneCartridge;
import com.openshift.client.cartridge.query.LatestVersionOf;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.server.ServerInstance;
import org.netbeans.modules.cloud.common.spi.support.serverplugin.DeploymentStatus;
import org.netbeans.modules.cloud.common.spi.support.serverplugin.ProgressObjectImpl;
import org.netbeans.modules.cloud.openshift.project.OpenShiftNewAppPanel;
import org.netbeans.modules.cloud.openshift.serverplugin.OpenShiftServerInstance;
import org.netbeans.modules.cloud.openshift.utils.OpenShiftUtilities;
import org.netbeans.modules.web.common.api.WebUtils;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Cancellable;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;
public class OpenShiftCloudInstance {
    @StaticResource
    public static final String ICON = "org/netbeans/modules/cloud/OpenShift/resources/icon.png";
    public static final String TYPE = "OpenShift by Red Hat";
    private static IUser user = null;
    public static String userName = null;
    public static String password = null;
    private static String url;
    private ServerInstance serverInstance;
    private static OpenShiftCloudInstance OpenShiftCloudInstance = null;
    private OpenShiftServerInstance OpenShiftServerInstance;
    private static final Logger LOG = Logger.getLogger(OpenShiftCloudInstance.class.getSimpleName());
    private static final RequestProcessor OpenShift_RP = new RequestProcessor(OpenShiftCloudInstance.TYPE, 1); // NOI18N
    public OpenShiftCloudInstance(String userName, String password) {
        OpenShiftCloudInstance.userName = userName;
        OpenShiftCloudInstance.password = password;
    }
    public static OpenShiftCloudInstance getDefault(String u, String p) {
        if (OpenShiftCloudInstance == null) {
            OpenShiftCloudInstance = new OpenShiftCloudInstance(u, p);
            OpenShiftCloudInstanceManager.getDefault().add(OpenShiftCloudInstance);
        }
        return OpenShiftCloudInstance;
    }
    public static String getUserName() {
        return userName;
    }
    public static String getPassword() {
        return password;
    }
    public ServerInstance getServerInstance() {
        return serverInstance;
    }
    public void setServerInstance(ServerInstance serverInstance) {
        this.serverInstance = serverInstance;
    }
    public static <T> Future<T> runAsynchronously(Callable<T> callable) {
        return runAsynchronously(callable, null);
    }
    public static synchronized <T> Future<T> runAsynchronously(Callable<T> callable, OpenShiftCloudInstance ai) {
        Future<T> f = OpenShift_RP.submit(callable);
        return f;
    }
    public OpenShiftServerInstance readOpenShiftServerInstance() {
        assert !SwingUtilities.isEventDispatchThread();
        OpenShiftServerInstance inst = new OpenShiftServerInstance(this);
        synchronized (this) {
            OpenShiftServerInstance = inst;
            return OpenShiftServerInstance;
        }
    }
    public void deregisterJ2EEServerInstances() {
        OpenShiftServerInstance instance;
        synchronized (this) {
            instance = OpenShiftServerInstance;
        }
        if (instance != null) {
            instance.deregister();
        }
    }
    public static Future<DeploymentStatus> deployAsync(final String userName, final String password, final String applicationName, final String cartridgeName, final ProgressObjectImpl po) {
        return runAsynchronously(new Callable<DeploymentStatus>() {
            @Override
            public DeploymentStatus call() throws Exception {
                DeploymentStatus ds = deploy(userName, password, applicationName, cartridgeName, po);
                LOG.log(Level.INFO, "deployment result: {0}", ds); // NOI18N
                po.updateDepoymentResult(ds, url);
                return ds;
            }
        });
    }
    public static DeploymentStatus deploy(String userName, String password, String applicationName, String cartridgeName, ProgressObjectImpl po) {
        assert !SwingUtilities.isEventDispatchThread();
        ProgressHandle handle = ProgressHandleFactory.createHandle("Uploading " + applicationName + " to OpenShift by Red Hat.", new Cancellable() {
            @Override
            public boolean cancel() {
                return true;
            }
        });
        OutputWriter ow = null;
        OutputWriter owe = null;
        try {
            String tabName = applicationName + " Deployment";
            InputOutput io = IOProvider.getDefault().getIO(tabName, false);
            io.select();
            if (io.isClosed()) {
                io = IOProvider.getDefault().getIO(tabName, true);
            }
            ow = io.getOut();
            owe = io.getErr();
            if (po != null) {
                handle.start(100);
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
                handle.setDisplayName("Preparing to create " + applicationName + " on OpenShift by Red Hat.");
                handle.progress("Preparing to create " + applicationName + " on OpenShift by Red Hat.", 0);
                po.updateDepoymentStage("Prepating...");
                ow.println("Preparing: " + date);
            }
            if (user == null) {
                user = OpenShiftUtilities.connect2OpenShift(userName,password);
//                IOpenShiftConnection connection
//                        = new OpenShiftConnectionFactory().getConnection(
//                                "netbeans",
//                                userName,
//                                password);
//                user = connection.getUser();
            }
            IDomain domain = user.getDefaultDomain();
            url = "https://" + applicationName + "-" + domain.getId() + ".rhcloud.com";
            boolean redeploy = false;
            List<IApplication> apps = domain.getApplications();
            for (IApplication app : apps) {
                if (app.getName().toLowerCase().equals(applicationName.toLowerCase())) {
                    redeploy = true;
                    break;
                }
            }
            if (po != null && redeploy == false) {
                handle.setDisplayName("Login credentials verified...");
                handle.progress("Login credentials verified...", 50);
                po.updateDepoymentStage("Login credentials verified");
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
                ow.println("Login credentials verified: " + date);
                //Creating with cartridge:
//                List<ICartridge> allAvailableCartridges = user.getConnection().getCartridges();
//                IStandaloneCartridge isc;
                IApplication app = null;
                //https://forums.openshift.com/huge-need-in-openshift-java-client-api-update
                List<ICartridge> cartridges = user.getConnection().getCartridges();
                for (ICartridge cartridge : cartridges) {
                    if (cartridge.getName().equals(cartridgeName)) {
                        StandaloneCartridge sc = new StandaloneCartridge(cartridge.getName());
                        final String message = "Creating application with cartridge: " + sc.getName();
                        handle.setDisplayName(message);
                        handle.progress(message, 50);
                        po.updateDepoymentStage(message);
                        ow.println(message);
                        app = domain.createApplication(applicationName, sc);
                    }
                }
                //Adding embedded cartridges:
                String noOfCartridgesProperty = applicationName + "-cartridge-length";
                int size = NbPreferences.forModule(OpenShiftNewAppPanel.class).getInt(noOfCartridgesProperty, 0);
                String array[] = new String[size];
                List<IEmbeddableCartridge> embeddableCartridges = user.getConnection().getEmbeddableCartridges();
                for (int i = 0; i < size; i++) {
                    array[i] = NbPreferences.forModule(OpenShiftNewAppPanel.class).get(applicationName + "-cartridge-" + i, "");
                    for (IEmbeddableCartridge anAvailableCartridge : embeddableCartridges) {
                        if (anAvailableCartridge.getName().equals(array[i])) {
                            try {
                                final String message = "Adding embedded cartridge: " + anAvailableCartridge.getName();
                                handle.setDisplayName(message);
                                handle.progress(message, 50);
                                po.updateDepoymentStage(message);
                                ow.println(message);
                                app.addEmbeddableCartridge(anAvailableCartridge);
                            } catch (Exception e) {
                                StatusDisplayer.getDefault().setStatusText(e.getMessage());
                            }
                        }
                    }
                }
                List<IEmbeddedCartridge> ecs = app.getEmbeddedCartridges();
                for (IEmbeddedCartridge ec : ecs) {
                    ow.println("Cartridge: " + ec.getName());
                }
                ow.print("Application URL: ");
                ow.println(app.getApplicationUrl(), new ApplicationOutputListener(WebUtils.stringToUrl(app.getApplicationUrl())));
                ow.println("SSH URL: " + app.getSshUrl());
                ow.println("Git URL: " + app.getGitUrl());
                ow.println("Creation Log: " + app.getCreationLog());
                String date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
                ow.println("Process completed: " + date2);
                handle.setDisplayName("Process completed.");
                handle.progress("Process completed.", 100);
                handle.finish();
            }
        } catch (Throwable t) {
            if (owe != null) {
                t.printStackTrace(owe);
            }
            return DeploymentStatus.UNKNOWN;
        } finally {
            if (ow != null) {
                ow.close();
            }
            if (owe != null) {
                owe.close();
            }
        }
        return DeploymentStatus.SUCCESS;
    }
//    public static IStandaloneCartridge wildfly8() throws MalformedURLException {
//        return new StandaloneCartridge(WILDFLY_NAME, new URL(WILDFLY_URL));
//    }
    private static class ApplicationOutputListener implements OutputListener {
        private final URL url;
        public ApplicationOutputListener(URL url) {
            this.url = url;
        }
        @Override
        public void outputLineSelected(OutputEvent oe) {
        }
        @Override
        public void outputLineAction(OutputEvent oe) {
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
        }
        @Override
        public void outputLineCleared(OutputEvent oe) {
        }
    }
}
