package net.jota.computers_and_networks.block.custom;

import net.jota.computers_and_networks.network.NetworkCable;
import net.minecraft.core.BlockPos;

import java.util.*;

public class CableManager {
    private final Map<BlockPos, NetworkCable> cables;
    private final Map<UUID, Set<BlockPos>> networkCables;

    public CableManager() {
        this.cables = new HashMap<>();
        this.networkCables = new HashMap<>();
    }

    public NetworkCable createCable(BlockPos pos) {
        NetworkCable cable = new NetworkCable(pos);
        cables.put(pos, cable);
        return cable;
    }

    public boolean removeCable(BlockPos pos) {
        NetworkCable removed = cables.remove(pos);
        if (removed != null) {
            removed.getConnections().forEach(connectedPos -> {
                NetworkCable connectedCable = cables.get(connectedPos);
                if (connectedCable != null) {
                    connectedCable.disconnectFrom(pos);
                }
            });

            removed.getNetworkId().ifPresent(networkId -> {
                networkCables.getOrDefault(networkId, new HashSet<>()).remove(pos);
            });

            return true;
        }
        return false;
    }

    public boolean connectCables(BlockPos pos1, BlockPos pos2) {
        NetworkCable cable1 = cables.get(pos1);
        NetworkCable cable2 = cables.get(pos2);

        if (cable1 != null && cable2 != null) {
            boolean connected1 = cable1.connectTo(pos2);
            boolean connected2 = cable2.connectTo(pos1);

            if (connected1 && connected2) {
                mergeNetworksIfNeeded(cable1, cable2);
                return true;
            }
        }
        return false;
    }

    private void mergeNetworksIfNeeded(NetworkCable cable1, NetworkCable cable2) {
        Optional<UUID> networkId1 = cable1.getNetworkId();
        Optional<UUID> networkId2 = cable2.getNetworkId();

        if (networkId1.isPresent() && networkId2.isPresent()) {
            if (!networkId1.equals(networkId2)) {
                // TODO: Implement network merging
                System.out.println("Merging networks: " + networkId1 + " e " + networkId2);
            }
        } else if (networkId1.isPresent()) {
            cable2.setNetwork(networkId1.get());
            addCableToNetwork(networkId1.get(), cable2.getPosition());
        } else if (networkId2.isPresent()) {
            cable1.setNetwork(networkId2.get());
            addCableToNetwork(networkId2.get(), cable1.getPosition());
        }
    }

    private void addCableToNetwork(UUID networkId, BlockPos pos) {
        networkCables.computeIfAbsent(networkId, k -> new HashSet<>()).add(pos);
    }

    public Set<BlockPos> getCablesInNetwork(UUID networkId) {
        return Collections.unmodifiableSet(networkCables.getOrDefault(networkId, new HashSet<>()));
    }

    public Optional<NetworkCable> getCable(BlockPos pos) {
        return Optional.ofNullable(cables.get(pos));
    }

    public boolean isPositionConnected(BlockPos pos) {
        return cables.containsKey(pos) && !cables.get(pos).getConnections().isEmpty();
    }
}
