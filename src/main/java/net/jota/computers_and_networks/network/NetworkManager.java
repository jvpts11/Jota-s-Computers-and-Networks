package net.jota.computers_and_networks.network;

import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class NetworkManager {
    private final Map<UUID, LogisticNetwork> networks;
    private final Map<BlockPos, UUID> computerPositions;

    public NetworkManager() {
        this.networks = new HashMap<>();
        this.computerPositions = new HashMap<>();
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

    public Optional<LogisticNetwork> getNetworkByComputer(UUID computerId) {
        return networks.values().stream()
                .filter(network -> network.getComputers().containsKey(computerId))
                .findFirst();
    }

    public Optional<LogisticNetwork> getNetworkByComputerPosition(BlockPos pos) {
        UUID computerId = computerPositions.get(pos);
        if (computerId != null) {
            return getNetworkByComputer(computerId);
        }
        return Optional.empty();
    }

    public boolean connectComputer(BlockPos pos, NetworkComputer computer, LogisticNetwork network) {
        if (network.addComputer(computer)) {
            computerPositions.put(pos, computer.getId());
            computer.onNetworkJoin(network);
            return true;
        }
        return false;
    }

    public void disconnectComputer(BlockPos pos) {
        UUID computerId = computerPositions.remove(pos);
        if (computerId != null) {
            networks.values().forEach(network -> {
                NetworkComputer computer = network.getComputers().get(computerId);
                if (computer != null) {
                    computer.onNetworkLeave();
                    network.removeComputer(computerId);
                }
            });
        }
    }

}
