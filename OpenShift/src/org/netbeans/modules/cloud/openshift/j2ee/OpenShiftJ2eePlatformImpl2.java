package org.netbeans.modules.cloud.openshift.j2ee;
import java.awt.Image;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.ArrayList;
import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.api.*;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.j2ee.core.Profile;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.cloud.openshift.OpenShiftCloudInstance;
//import org.netbeans.modules.cloud.oracle.OracleInstance;
import org.netbeans.modules.j2ee.deployment.common.api.J2eeLibraryTypeProvider;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule.Type;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformImpl2;
//import org.netbeans.modules.j2ee.deployment.plugins.spi.support.LookupProviderSupport;
//import org.netbeans.libs.oracle.cloud.api.WhiteListQuerySupport;
import org.netbeans.modules.j2ee.deployment.common.api.Version;
import org.netbeans.modules.j2ee.deployment.plugins.api.ServerLibraryDependency;
//import org.netbeans.modules.javaee.specs.support.spi.JpaProviderFactory;
import org.netbeans.spi.project.libraries.LibraryImplementation;
import org.openide.util.ImageUtilities;
public class OpenShiftJ2eePlatformImpl2 extends J2eePlatformImpl2 {
    private OpenShiftDeploymentManager dm;
    private final Set<Type> moduleTypes = new HashSet<Type>();
    public OpenShiftJ2eePlatformImpl2(DeploymentManager dm) {
        assert dm instanceof OpenShiftDeploymentManager;
        this.dm = (OpenShiftDeploymentManager) dm;
        moduleTypes.add(Type.WAR);
        // deployment of EJB standalone module is not supported but user
        // should be able to create EJB project for cloud and deploy it
        // as part of EAR
        moduleTypes.add(Type.EJB);
        moduleTypes.add(Type.EAR);
    }
    @Override
    public File getServerHome() {
        return null;
    }
    @Override
    public File getDomainHome() {
        return null;
    }
    @Override
    public File getMiddlewareHome() {
        return null;
    }
    @Override
    public LibraryImplementation[] getLibraries() {
        Library l = LibraryManager.getDefault().getLibrary("javaee-api-7.0");
        LibraryImplementation library = new J2eeLibraryTypeProvider().createLibrary();
        library.setContent(J2eeLibraryTypeProvider.VOLUME_TYPE_CLASSPATH, l.getContent("classpath"));
        return new LibraryImplementation[]{library};
    }
    @Override
    public Set<Type> getSupportedTypes() {
        return moduleTypes;
    }
    @Override
    public Set<Profile> getSupportedProfiles() {
        return new HashSet<Profile>(Arrays.<Profile>asList(new Profile[]{Profile.JAVA_EE_7_WEB, Profile.JAVA_EE_6_WEB}));
    }
    @Override
    public Set<Profile> getSupportedProfiles(Type moduleType) {
        return getSupportedProfiles();
    }
    @Override
    public String getDisplayName() {
        return null;
    }
    @Override
    public Image getIcon() {
        return ImageUtilities.loadImage(OpenShiftCloudInstance.ICON); // NOI18N
    }
    @Override
    public File[] getPlatformRoots() {
        return new File[0];
    }
    @Override
    public File[] getToolClasspathEntries(String toolName) {
        return new File[0];
    }
    @Override
    @Deprecated
    public boolean isToolSupported(String toolName) {
        return false;
    }
    @Override
    public Set getSupportedJavaPlatformVersions() {
        return new HashSet<String>(Arrays.asList(new String[]{"1.6"}));
    }
    @Override
    public JavaPlatform getJavaPlatform() {
        return JavaPlatformManager.getDefault().getDefaultPlatform();
    }
}
