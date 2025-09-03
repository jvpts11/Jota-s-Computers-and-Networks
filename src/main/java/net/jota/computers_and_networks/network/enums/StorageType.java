package net.jota.computers_and_networks.network.enums;

public enum StorageType {
        HDD_ITEM("hdd_item"),
        HDD_FLUID("hdd_fluid"),
        SSD_ITEM("ssd_item"),
        SSD_FLUID("ssd_fluid"),
        EXTERNAL_DEVICE("external_device");

        private final String name;

        StorageType(String name) {
            this.name = name;
        }

        public String getType(){return name;}
}
