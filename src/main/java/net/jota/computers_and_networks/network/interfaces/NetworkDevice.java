package net.jota.computers_and_networks.network.interfaces;

import net.jota.computers_and_networks.network.LogisticNetwork;
import net.jota.computers_and_networks.network.enums.DeviceType;

import java.util.UUID;

public interface NetworkDevice {
    UUID getId();
    String getDisplayName();
    DeviceType getDeviceType();
    UUID getNetworkId();
    void setNetworkId(UUID networkId);
    boolean canJoinNetwork(LogisticNetwork network);
    void onNetworkJoin(LogisticNetwork network);
    void onNetworkLeave();

    default boolean canHandleItems() { return false; }
    default boolean canHandleFluids() { return false; }
    default boolean canHandleEnergy() { return false; }
    default boolean canHandleData() { return false; }
}