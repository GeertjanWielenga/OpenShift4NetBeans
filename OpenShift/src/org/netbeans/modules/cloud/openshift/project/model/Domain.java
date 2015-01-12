package org.netbeans.modules.cloud.openshift.project.model;
import com.openshift.client.IDomain;
public class Domain {
    private final IDomain domain;
    public Domain(IDomain domain) {
        this.domain = domain;
    }
    public IDomain getDomain() {
        return domain;
    }
    @Override
    public String toString() {
        return domain.getId();
    }
}
