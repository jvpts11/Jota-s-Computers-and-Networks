package net.jota.computers_and_networks.network;

import net.jota.computers_and_networks.network.interfaces.NetworkDevice;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class NetworkManager {
    private final Map<UUID, LogisticNetwork> networks;
    private final Map<BlockPos, UUID> devicePositions;


    public NetworkManager() {
        this.networks = new HashMap<>();
        this.devicePositions = new HashMap<>();
    }

    public void tick() {
        networks.values().forEach(network -> {
            if (network.isValid()) {
                network.getBuffer().tick(network);
            }
        });
    }

    public Optional<LogisticNetwork> createNetwork() {
        LogisticNetwork network = new LogisticNetwork();
        networks.put(network.getId(), network);
        return Optional.of(network);
    }

    public Optional<LogisticNetwork> getNetwork(UUID networkId) {
        return Optional.ofNullable(networks.get(networkId));
    }

    public Optional<LogisticNetwork> getNetworkByDevice(UUID deviceId) {
        return networks.values().stream()
                .filter(network -> network.getDevices().containsKey(deviceId))
                .findFirst();
    }

    public Optional<LogisticNetwork> getNetworkByDevicePosition(BlockPos pos) {
        UUID deviceId = devicePositions.get(pos);
        if (deviceId != null) {
            return getNetworkByDevice(deviceId);
        }
        return Optional.empty();
    }

    public boolean connectDevice(BlockPos pos, NetworkDevice device, LogisticNetwork network) {
        if (network.addDevice(device)) {
            devicePositions.put(pos, device.getId());
            device.onNetworkJoin(network);
            return true;
        }
        return false;
    }

    public void disconnectDevice(BlockPos pos) {
        UUID deviceId = devicePositions.remove(pos);
        if (deviceId != null) {
            networks.values().forEach(network -> {
                NetworkDevice device = network.getDevices().get(deviceId);
                if (device != null) {
                    device.onNetworkLeave();
                    network.removeDevice(deviceId);
                }
            });
        }
    }

}
