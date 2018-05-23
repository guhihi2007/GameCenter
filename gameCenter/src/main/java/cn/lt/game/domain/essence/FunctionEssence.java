package cn.lt.game.domain.essence;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2015/11/17.
 */
public abstract class FunctionEssence extends UIModuleEssence implements Serializable {
    protected DomainEssence domainEssence = null;
    public int autoMatchPos ;

    public FunctionEssence(DomainType domainType) { this.domainEssence = new DomainEssenceImpl(domainType); }

    public boolean hasSubFuncEss() { return false; }

    public List<FunctionEssence> getSubFuncEss() { return null; }

    public DomainEssence getDomainEssence() { return domainEssence; }

    public String getUniqueIdentifier() {
        return getUniqueIdentifierBy(IdentifierType.ID);
    }

    public String getUniqueIdentifierBy(IdentifierType type) {
        return getDomainEssence().getUniqueIdentifierBy(type);
    }




    protected static class DomainEssenceImpl extends DomainEssence {
        private DomainType domainType;

        DomainEssenceImpl(DomainType domainType) {
            this.domainType = domainType;
        }
        @Override
        public DomainType getDomainType() {
            return domainType;
        }
    }
}
