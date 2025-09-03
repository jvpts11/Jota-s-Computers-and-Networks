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
        boolean hasMainframe = network.getDevices().values().stream()
                .filter(device -> device instanceof NetworkComputer)
                .map(device -> (NetworkComputer) device)
                .anyMatch(comp -> comp.getType() == ComputerType.MAINFRAME);

        boolean hasMinimumComponents = hasMinimumComponents();

        return !hasMainframe && hasMinimumComponents;
    }

    private boolean hasMinimumComponents() {
        return !getComponents().getCpus().isEmpty() &&
                !getComponents().getRams().isEmpty() &&
                !getComponents().getGpus().isEmpty() &&
                getComponents().getMotherboard() != null;
    }

    @Override
    public void onNetworkJoin(LogisticNetwork network) {
        setNetworkId(network.getId());
        network.updateNetworkValidity();
        System.out.println("Mainframe connected to Network: " + network.getId());
    }

    @Override
    public void onNetworkLeave() {
        System.out.println("Mainframe disconnected from network: " + getNetworkId());
        setNetworkId(null);
    }

    public int getNetworkProcessingPower() {
        return getUploadSpeed();
    }
}
