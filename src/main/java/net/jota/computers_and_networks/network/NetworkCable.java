package net.jota.computers_and_networks.network;

import net.minecraft.core.BlockPos;

import java.util.*;

public class NetworkCable {

    private final UUID id;
    private final BlockPos position;
    private final Set<BlockPos> connections;
    private UUID networkId;

    public NetworkCable(BlockPos position) {
        this.id = UUID.randomUUID();
        this.position = position;
        this.connections = new HashSet<>();
        this.networkId = null;
    }

    public boolean connectTo(BlockPos otherPos) {
        return connections.add(otherPos);
    }

    public boolean disconnectFrom(BlockPos otherPos) {
        return connections.remove(otherPos);
    }

    public boolean isConnectedTo(BlockPos otherPos) {
        return connections.contains(otherPos);
    }

    public Set<BlockPos> getConnections() {
        return Collections.unmodifiableSet(connections);
    }

    public void setNetwork(UUID networkId) {
        this.networkId = networkId;
    }

    public Optional<UUID> getNetworkId() {
        return Optional.ofNullable(networkId);
    }

    public void clearNetwork() {
        this.networkId = null;
    }

    // Getters
    public UUID getId() { return id; }
    public BlockPos getPosition() { return position; }
}
