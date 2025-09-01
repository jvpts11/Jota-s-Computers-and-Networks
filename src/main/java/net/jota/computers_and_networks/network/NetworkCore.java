package net.jota.computers_and_networks.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class NetworkCore {
    private static final Map<UUID, LogisticNetwork> NETWORKS = new HashMap<>();

    public static Optional<LogisticNetwork> getNetwork(UUID networkId) {
        return Optional.ofNullable(NETWORKS.get(networkId));
    }

    public static void registerNetwork(LogisticNetwork network) {
        NETWORKS.put(network.getId(), network);
    }

    public static void removeNetwork(UUID networkId) {
        NETWORKS.remove(networkId);
    }
}
