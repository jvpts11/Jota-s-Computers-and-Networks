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
        // APENAS componentes de processamento na placa-mãe
        // HDDs/SSDs são instalados diretamente no computador
        switch (supportedComputerType) {
            case MAINFRAME:
                componentSlots.put(ComponentType.CPU, 4);
                componentSlots.put(ComponentType.RAM, 16);
                componentSlots.put(ComponentType.GPU, 2);
                break;
            case SERVER:
                componentSlots.put(ComponentType.CPU, 6);
                componentSlots.put(ComponentType.RAM, 32);
                componentSlots.put(ComponentType.GPU, 4);
                break;
            case PERSONAL_COMPUTER:
                componentSlots.put(ComponentType.CPU, 1);
                componentSlots.put(ComponentType.RAM, 4);
                componentSlots.put(ComponentType.GPU, 2);
                break;
        }

        for (ComponentType type : componentSlots.keySet()) {
            installedComponents.put(type, new ArrayList<>());
        }
    }

    public boolean installComponent(IComputerComponent component) {
        ComponentType type;
        try {
            type = ComponentType.valueOf(component.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            return false; // Tipo de componente não suportado na placa-mãe
        }

        List<IComputerComponent> components = installedComponents.get(type);
        if (components == null) {
            return false; // Tipo não suportado por esta placa-mãe
        }

        if (components.size() < componentSlots.get(type)) {
            components.add(component);
            return true;
        }
        return false;
    }

    public boolean removeComponent(IComputerComponent component) {
        ComponentType type;
        try {
            type = ComponentType.valueOf(component.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            return false;
        }

        List<IComputerComponent> components = installedComponents.get(type);
        return components != null && components.remove(component);
    }

    public List<IComputerComponent> getComponents(ComponentType type) {
        return Collections.unmodifiableList(installedComponents.getOrDefault(type, new ArrayList<>()));
    }

    public int getAvailableSlots(ComponentType type) {
        return componentSlots.getOrDefault(type, 0) - installedComponents.getOrDefault(type, new ArrayList<>()).size();
    }

    public int getTotalSlots(ComponentType type) {
        return componentSlots.getOrDefault(type, 0);
    }

    @Override
    public ComponentTier getTier() { return tier; }
    @Override
    public String getType() { return ComponentType.MOTHERBOARD.getName(); }

    public ComputerType getSupportedComputerType() { return supportedComputerType; }
}
