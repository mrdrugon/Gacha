public class PackFactory {
    public static Pack createPack(PackType type) {
        switch (type) {
            case NORMAL:
                return new NormalPack();
            case RARE:
                return new RarePack();
            case LEGENDARY:
                return new LegendaryPack();
            default:
                throw new IllegalArgumentException("Invalid pack type: " + type);
        }
    }
}
