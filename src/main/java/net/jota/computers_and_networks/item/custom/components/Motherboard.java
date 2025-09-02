package net.jota.computers_and_networks.item.custom.components;

import net.jota.computers_and_networks.block.custom.enums.ComputerType;
import net.jota.computers_and_networks.item.custom.enums.ComponentTier;
import net.jota.computers_and_networks.item.custom.enums.ComponentType;
import net.jota.computers_and_networks.item.custom.interfaces.IComputerComponent;

import java.util.*;

public class Motherboard implements IComputerComponent {
    private final ComputerType supportedComputerType;
    private final ComponentTier tier;
    private final Map<ComponentType, Integer> componentSlots;
    private final Map<ComponentType, List<IComputerComponent>> installedComponents;

    public Motherboard(ComputerType supportedComputerType, ComponentTier tier) {
        this.supportedComputerType = supportedComputerType;
        this.tier = tier;
        this.componentSlots = new HashMap<>();
        this.installedComponents = new HashMap<>();
        initializeSlots();
    }

    private void initializeSlots() {
        switch (supportedComputerType) {
            case MAINFRAME:
                componentSlots.put(ComponentType.CPU, 4);
                componentSlots.put(ComponentType.RAM, 16);
                componentSlots.put(ComponentType.GPU, 2);
                componentSlots.put(ComponentType.HDD_ITEM, 8);
                componentSlots.put(ComponentType.HDD_FLUID, 8);
                break;
            case SERVER:
                componentSlots.put(ComponentType.CPU, 6);
                componentSlots.put(ComponentType.RAM, 32);
                componentSlots.put(ComponentType.GPU, 4);
                componentSlots.put(ComponentType.HDD_ITEM, 12);
                componentSlots.put(ComponentType.HDD_FLUID, 12);
                break;
            case PERSONAL_COMPUTER:
                componentSlots.put(ComponentType.CPU, 1);
                componentSlots.put(ComponentType.RAM, 4);
                componentSlots.put(ComponentType.GPU, 2);
                componentSlots.put(ComponentType.HDD_ITEM, 2);
                componentSlots.put(ComponentType.HDD_FLUID, 2);
                break;
        }

        for (ComponentType type : componentSlots.keySet()) {
            installedComponents.put(type, new ArrayList<>());
        }
    }

    public boolean installComponent(IComputerComponent component) {
        ComponentType type = ComponentType.valueOf(component.getType().toUpperCase());
        List<IComputerComponent> components = installedComponents.get(type);

        if (components.size() < componentSlots.get(type)) {
            components.add(component);
            return true;
        }
        return false;
    }

    public boolean removeComponent(IComputerComponent component) {
        ComponentType type = ComponentType.valueOf(component.getType().toUpperCase());
        return installedComponents.get(type).remove(component);
    }

    public List<IComputerComponent> getComponents(ComponentType type) {
        return Collections.unmodifiableList(installedComponents.get(type));
    }

    public int getAvailableSlots(ComponentType type) {
        return componentSlots.get(type) - installedComponents.get(type).size();
    }

    public int getTotalSlots(ComponentType type) {
        return componentSlots.get(type);
    }

    @Override
    public ComponentTier getTier() { return tier; }
    @Override
    public String getType() { return ComponentType.MOTHERBOARD.getName(); }

    public ComputerType getSupportedComputerType() { return supportedComputerType; }
}
