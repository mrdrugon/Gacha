public class PackFactory {
    public static Pack createPack(PackType type) {
        switch (type) {
            case NORMAL:
                return new NormalPack();
            case GEM_STONES:
                return new GemStonesPack();
            case METAL:
                return new MetalPack();
            case BLACK_WHITE:
                return new BlackWhitePack();
            case FOREST:
                return new ForestPack();
            case OCEAN:
                return new OceanPack();
            default:
                throw new IllegalArgumentException("Invalid pack type: " + type);
        }
    }
}
