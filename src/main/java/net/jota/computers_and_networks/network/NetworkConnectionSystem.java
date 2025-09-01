package net.jota.computers_and_networks.network;

import net.jota.computers_and_networks.block.custom.CableManager;
import net.minecraft.core.BlockPos;

import java.util.*;

public class NetworkConnectionSystem {
    private final CableManager cableManager;
    private final NetworkManager networkManager;
    private final Map<BlockPos, NetworkComputer> computerBlocks;

    public NetworkConnectionSystem(CableManager cableManager, NetworkManager networkManager) {
        this.cableManager = cableManager;
        this.networkManager = networkManager;
        this.computerBlocks = new HashMap<>();
    }

    public boolean connectComputerToNetwork(BlockPos computerPos, NetworkComputer computer) {
        computerBlocks.put(computerPos, computer);

        Optional<UUID> networkId = findNetworkThroughCables(computerPos);

        if (networkId.isPresent()) {
            LogisticNetwork network = networkManager.getNetwork(networkId.get()).orElse(null);
            if (network != null && computer.canJoinNetwork(network)) {
                return networkManager.connectComputer(computerPos, computer, network);
            }
        }
        return false;
    }

    public boolean disconnectComputer(BlockPos computerPos) {
        NetworkComputer removed = computerBlocks.remove(computerPos);
        if (removed != null) {
            networkManager.disconnectComputer(computerPos);
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
            visited.add(current);

            // Verifica se esta posição é um cabo com rede
            Optional<NetworkCable> cable = cableManager.getCable(current);
            if (cable.isPresent() && cable.get().getNetworkId().isPresent()) {
                return cable.get().getNetworkId();
            }

            // Verifica se esta posição é um computador já conectado
            NetworkComputer computer = computerBlocks.get(current);
            if (computer != null && computer.getNetworkId() != null) {
                return Optional.of(computer.getNetworkId());
            }

            if (cable.isPresent()) {
                cable.get().getConnections().stream()
                        .filter(pos -> !visited.contains(pos))
                        .forEach(toVisit::add);
            }
        }

        return Optional.empty();
    }

    public void updateNetworkConnections() {
        computerBlocks.forEach((pos, computer) -> {
            if (computer.getNetworkId() == null) {
                connectComputerToNetwork(pos, computer);
            }
        });
    }

    public Optional<NetworkComputer> getComputerAt(BlockPos pos) {
        return Optional.ofNullable(computerBlocks.get(pos));
    }
}
