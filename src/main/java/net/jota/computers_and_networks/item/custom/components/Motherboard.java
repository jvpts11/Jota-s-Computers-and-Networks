package net.jota.computers_and_networks.item.custom.components;

import net.jota.computers_and_networks.block.custom.enums.ComputerType;
import net.jota.computers_and_networks.item.custom.enums.ComponentTier;
import net.jota.computers_and_networks.item.custom.enums.ComponentType;
import net.jota.computers_and_networks.item.custom.interfaces.ComputerComponent;

import java.util.*;

public class Motherboard implements ComputerComponent {
    private final ComputerType supportedComputerType;
    private final ComponentTier tier;
    private final Map<ComponentType, Integer> componentSlots;
    private final Map<ComponentType, List<ComputerComponent>> installedComponents;

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
                componentSlots.put(ComponentType.HDD, 8);
                break;
            case SERVER:
                componentSlots.put(ComponentType.CPU, 6);
                componentSlots.put(ComponentType.RAM, 32);
                componentSlots.put(ComponentType.GPU, 4);
                componentSlots.put(ComponentType.HDD, 12);
                break;
            case PERSONAL_COMPUTER:
                componentSlots.put(ComponentType.CPU, 1);
                componentSlots.put(ComponentType.RAM, 4);
                componentSlots.put(ComponentType.GPU, 2);
                componentSlots.put(ComponentType.HDD, 2);
                break;
        }

        // Inicializa listas vazias para cada tipo de componente
        for (ComponentType type : componentSlots.keySet()) {
            installedComponents.put(type, new ArrayList<>());
        }
    }

    public boolean installComponent(ComputerComponent component) {
        ComponentType type = ComponentType.valueOf(component.getType().toUpperCase());
        List<ComputerComponent> components = installedComponents.get(type);

        if (components.size() < componentSlots.get(type)) {
            components.add(component);
            return true;
        }
        return false;
    }

    public boolean removeComponent(ComputerComponent component) {
        ComponentType type = ComponentType.valueOf(component.getType().toUpperCase());
        return installedComponents.get(type).remove(component);
    }

    public List<ComputerComponent> getComponents(ComponentType type) {
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
