package cn.lt.game.domain.essence;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wcn on 2015/11/17.
 */
public abstract class DomainEssence implements Essence,Serializable{
    private static final int MAX_UID_NUM = 2;
    protected Map<IdentifierType, String> uniqueIdentifier = new HashMap<>(MAX_UID_NUM);

    public abstract DomainType getDomainType();

    public String getUniqueIdentifier() {
        return uniqueIdentifier.get(IdentifierType.ID);
    }

    public String getUniqueIdentifierBy(IdentifierType type) {
        return uniqueIdentifier.get(type);
    }

    public Map<IdentifierType, String> getUniqueIdentifierMap() {
        return uniqueIdentifier;
    }

    public DomainEssence setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier.put(IdentifierType.ID, uniqueIdentifier);
        return this;
    }

    public DomainEssence setUniqueIdentifierByType(IdentifierType type, String uniqueIdentifier) {
        this.uniqueIdentifier.put(type, uniqueIdentifier);
        return this;
    }

    public DomainEssence setUniqueIdentifierMap(Map<IdentifierType, String> uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
        return this;
    }

}
