package net.jota.computers_and_networks.network;

import net.jota.computers_and_networks.block.custom.CableManager;
import net.jota.computers_and_networks.network.interfaces.NetworkDevice;
import net.minecraft.core.BlockPos;

import java.util.*;

public class NetworkConnectionSystem {
    private final CableManager cableManager;
    private final NetworkManager networkManager;
    private final Map<BlockPos, NetworkDevice> networkDevices;

    public NetworkConnectionSystem(CableManager cableManager, NetworkManager networkManager) {
        this.cableManager = cableManager;
        this.networkManager = networkManager;
        this.networkDevices = new HashMap<>();
    }

    public boolean connectDeviceToNetwork(BlockPos devicePos, NetworkDevice device) {
        networkDevices.put(devicePos, device);

        Optional<UUID> networkId = findNetworkThroughCables(devicePos);

        if (networkId.isPresent()) {
            Optional<LogisticNetwork> networkOpt = networkManager.getNetwork(networkId.get());
            if (networkOpt.isPresent() && device.canJoinNetwork(networkOpt.get())) {
                return networkManager.connectDevice(devicePos, device, networkOpt.get());
            }
        } else {
            Optional<LogisticNetwork> newNetwork = networkManager.createNetwork();
            if (newNetwork.isPresent() && device.canJoinNetwork(newNetwork.get())) {
                return networkManager.connectDevice(devicePos, device, newNetwork.get());
            }
        }
        return false;
    }

    public boolean disconnectDevice(BlockPos devicePos) {
        NetworkDevice removed = networkDevices.remove(devicePos);
        if (removed != null) {
            networkManager.disconnectDevice(devicePos);
            return true;
        }
        return false;
    }

    private Optional<UUID> findNetworkThroughCables(BlockPos startPos) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> toVisit = new LinkedList<>();
        toVisit.add(startPos);

        while (!toVisit.isEmpty()) {
            BlockPos current = toVisit.poll();

            if (visited.contains(current)) continue;
            visited.add(current);

            Optional<NetworkCable> cable = cableManager.getCable(current);
            if (cable.isPresent()) {
                Optional<UUID> cableNetworkId = cable.get().getNetworkId();
                if (cableNetworkId.isPresent()) {
                    return cableNetworkId;
                }

                cable.get().getConnections().stream()
                        .filter(pos -> !visited.contains(pos))
                        .forEach(toVisit::add);
            }

            NetworkDevice device = networkDevices.get(current);
            if (device != null && device.getNetworkId() != null) {
                return Optional.of(device.getNetworkId());
            }
        }

        return Optional.empty();
    }

    public void updateNetworkConnections() {
        networkDevices.forEach((pos, device) -> {
            if (device.getNetworkId() == null) {
                connectDeviceToNetwork(pos, device);
            }
        });
    }

    public Optional<NetworkDevice> getDeviceAt(BlockPos pos) {
        return Optional.ofNullable(networkDevices.get(pos));
    }

    public void onCableNetworkChanged(BlockPos cablePos, UUID newNetworkId) {
        networkDevices.keySet().stream()
                .filter(devicePos -> isDeviceConnectedToCable(devicePos, cablePos))
                .forEach(devicePos -> {
                    NetworkDevice device = networkDevices.get(devicePos);
                    if (device != null) {
                        disconnectDevice(devicePos);
                        connectDeviceToNetwork(devicePos, device);
                    }
                });
    }

    private boolean isDeviceConnectedToCable(BlockPos devicePos, BlockPos cablePos) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> toVisit = new LinkedList<>();
        toVisit.add(devicePos);

        while (!toVisit.isEmpty()) {
            BlockPos current = toVisit.poll();

            if (visited.contains(current)) continue;
            visited.add(current);

            if (current.equals(cablePos)) {
                return true;
            }

            Optional<NetworkCable> cable = cableManager.getCable(current);
            if (cable.isPresent()) {
                cable.get().getConnections().stream()
                        .filter(pos -> !visited.contains(pos))
                        .forEach(toVisit::add);
            }
        }

        return false;
    }
}
