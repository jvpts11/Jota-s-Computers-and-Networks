package net.jota.computers_and_networks.network;

import net.jota.computers_and_networks.block.custom.enums.ComputerType;
import net.jota.computers_and_networks.item.custom.components.CPUComponent;
import net.jota.computers_and_networks.item.custom.components.GPUComponent;
import net.jota.computers_and_networks.item.custom.components.RAMComponent;

import java.util.*;

public class LogisticNetwork {
    private final UUID id;
    private final Map<UUID, NetworkComputer> computers;
    private final NetworkBuffer buffer;
    private UUID mainframeId;
    private boolean isValid;

    public LogisticNetwork() {
        this.id = UUID.randomUUID();
        this.computers = new HashMap<>();
        this.buffer = new NetworkBuffer();
        this.isValid = false;
        NetworkCore.registerNetwork(this);
    }

    public boolean addComputer(NetworkComputer computer) {
        if (computers.containsKey(computer.getId())) return false;

        computers.put(computer.getId(), computer);
        updateNetworkValidity();
        return true;
    }

    public boolean removeComputer(UUID computerId) {
        NetworkComputer removed = computers.remove(computerId);
        if (removed != null) {
            if (mainframeId.equals(computerId)) {
                mainframeId = null;
            }
            updateNetworkValidity();
            return true;
        }
        return false;
    }

    public void updateNetworkValidity() {
        long mainframeCount = computers.values().stream()
                .filter(comp -> comp.getType() == ComputerType.MAINFRAME)
                .count();

        this.isValid = mainframeCount == 1;

        if (isValid) {
            // Atualiza o ID do mainframe
            mainframeId = computers.values().stream()
                    .filter(comp -> comp.getType() == ComputerType.MAINFRAME)
                    .findFirst()
                    .map(NetworkComputer::getId)
                    .orElse(null);
        }
    }

    public int getNetworkTransferRate() {
        return getMainframe().map(mainframe -> {
            int baseSpeed = mainframe.getUploadSpeed(); // Já soma todas as CPUs
            float gpuMultiplier = mainframe.getGPUMultiplier();
            return (int) (baseSpeed * gpuMultiplier);
        }).orElse(0);
    }

    public int getTotalBufferCapacity() {
        return getMainframe().map(mainframe ->
                mainframe.getRAMCapacity() * 1024 // Converte MB para KB
        ).orElse(0);
    }

    public boolean executeOperation(NetworkOperation operation) {
        if (!isValid) return false;

        // Processa a operação através do buffer
        return buffer.processOperation(operation, this);
    }

    public Map<UUID, NetworkComputer> getComputers() {
        return Collections.unmodifiableMap(computers);
    }

    // Getters
    public UUID getId() { return id; }
    public boolean isValid() { return isValid; }
    public NetworkBuffer getBuffer() { return buffer; }
    public Optional<NetworkComputer> getMainframe() {
        return Optional.ofNullable(computers.get(mainframeId));
    }
}
