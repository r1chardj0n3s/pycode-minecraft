package net.mechanicalcat.pycode;

public class Reference {
    public static final String MODID = "pycode";
    public static final String MODNAME = "Python Code";
    public static final String VERSION = "1.0";
    public static final String CLIENT_PROXY_CLASS = "net.mechanicalcat.pycode.proxy.ClientProxy";
    public static final String SERVER_PROXY_CLASS = "net.mechanicalcat.pycode.proxy.ServerProxy";
    public static final String ACCEPTED_VERSIONS = "[1.10.2]";

    public static enum PyCodeRegistrations {
        WAND("python_wand", "PythonWandItem"),
        BOOK("python_book", "PythonBookItem"),
        BLOCK("python_block", "PythonBlock"),
        HAND("python_hand", "HandItem");

        private String unlocalizedName;
        private String registryName;

        PyCodeRegistrations(String unlocalizedName, String registryName) {
            this.unlocalizedName = unlocalizedName;
            this.registryName = registryName;
        }

        public String getRegistryName() {
            return registryName;
        }

        public String getUnlocalizedName() {
            return unlocalizedName;
        }
    }
}
