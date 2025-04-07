package nl.vng.diwi.dal.entities.enums;

public enum OwnershipCategory {

    KOOP1(Type.BUY),
    KOOP2(Type.BUY),
    KOOP3(Type.BUY),
    KOOP4(Type.BUY),
    KOOP_ONB(Type.BUY),
    HUUR1(Type.RENT),
    HUUR2(Type.RENT),
    HUUR3(Type.RENT),
    HUUR4(Type.RENT),
    HUUR_ONB(Type.RENT);

    public static enum Type {
        BUY, RENT
    };

    private final Type type;

    OwnershipCategory(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
