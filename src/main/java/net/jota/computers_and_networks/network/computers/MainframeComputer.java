package net.jota.computers_and_networks.network.computers;

import net.jota.computers_and_networks.block.custom.enums.ComputerType;
import net.jota.computers_and_networks.network.LogisticNetwork;
import net.jota.computers_and_networks.network.NetworkComputer;

public class MainframeComputer extends NetworkComputer {
    public MainframeComputer() {
        super(ComputerType.MAINFRAME, "Mainframe");
    }

    @Override
    public boolean canJoinNetwork(LogisticNetwork network) {
        boolean hasMainframe = network.getComputers().values().stream()
                .anyMatch(comp -> comp.getType() == ComputerType.MAINFRAME);

        boolean hasMinimumComponents = hasMinimumComponents();

        return !hasMainframe && hasMinimumComponents;
    }

    private boolean hasMinimumComponents() {
        // Mainframe requer pelo menos 1 CPU, 1 RAM, 1 GPU e motherboard
        return !getComponents().getCpus().isEmpty() &&
                !getComponents().getRams().isEmpty() &&
                !getComponents().getGpus().isEmpty() &&
                getComponents().getMotherboard() != null;
    }

    @Override
    public void onNetworkJoin(LogisticNetwork network) {
        this.networkId = network.getId();
        network.updateNetworkValidity();
        System.out.println("Mainframe connected to Network: " + network.getId());
    }

    @Override
    public void onNetworkLeave() {
        System.out.println("Mainframe disconnected from network: " + networkId);
        this.networkId = null;
    }

    public int getNetworkProcessingPower() {
        return getUploadSpeed();
    }
}
