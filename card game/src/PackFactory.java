public class PackFactory {
    public static Pack createPack(PackType type) {
        switch (type) {
            case NORMAL:
                return new NormalPack();
            default:
                throw new IllegalArgumentException("Invalid pack type: " + type);
        }
    }
}
