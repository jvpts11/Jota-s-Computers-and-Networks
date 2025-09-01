package net.jota.computers_and_networks.item.custom;

import net.jota.computers_and_networks.item.custom.components.*;
import net.jota.computers_and_networks.item.custom.enums.ComponentType;
import net.jota.computers_and_networks.item.custom.interfaces.ComputerComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComputerComponents {
    private Motherboard motherboard;
    private final List<CPUComponent> cpus;
    private final List<GPUComponent> gpus;
    private final List<RAMComponent> rams;
    private final List<HDDComponent> hdds;

    public ComputerComponents() {
        this.cpus = new ArrayList<>();
        this.gpus = new ArrayList<>();
        this.rams = new ArrayList<>();
        this.hdds = new ArrayList<>();
    }

    public boolean addComponent(ComputerComponent component) {
        if (motherboard == null) return false;

        ComponentType type = ComponentType.valueOf(component.getType().toUpperCase());

        switch (type) {
            case CPU:
                if (cpus.size() < motherboard.getTotalSlots(ComponentType.CPU)) {
                    cpus.add((CPUComponent) component);
                    return true;
                }
                break;
            case GPU:
                if (gpus.size() < motherboard.getTotalSlots(ComponentType.GPU)) {
                    gpus.add((GPUComponent) component);
                    return true;
                }
                break;
            case RAM:
                if (rams.size() < motherboard.getTotalSlots(ComponentType.RAM)) {
                    rams.add((RAMComponent) component);
                    return true;
                }
                break;
            case HDD:
                if (hdds.size() < motherboard.getTotalSlots(ComponentType.HDD)) {
                    hdds.add((HDDComponent) component);
                    return true;
                }
                break;
        }
        return false;
    }

    public int getTotalDownloadSpeed() {
        return cpus.stream()
                .mapToInt(CPUComponent::getDownloadSpeed)
                .sum();
    }

    public int getTotalUploadSpeed() {
        return cpus.stream()
                .mapToInt(CPUComponent::getUploadSpeed)
                .sum();
    }

    public float getTotalGPUMultiplier() {
        return gpus.stream()
                .map(GPUComponent::getPerformanceMultiplier)
                .reduce(1.0f, (a, b) -> a * b);
    }

    public int getTotalRAMCapacity() {
        return rams.stream()
                .mapToInt(RAMComponent::getCapacityMB)
                .sum();
    }

    public int getTotalHDDCapacity() {
        return hdds.stream()
                .mapToInt(HDDComponent::getCapacity)
                .sum();
    }

    public boolean setMotherboard(Motherboard motherboard) {
        if (this.motherboard == null) {
            this.motherboard = motherboard;
            return true;
        }
        return false;
    }

    public List<CPUComponent> getCpus() { return Collections.unmodifiableList(cpus); }
    public List<GPUComponent> getGpus() { return Collections.unmodifiableList(gpus); }
    public List<RAMComponent> getRams() { return Collections.unmodifiableList(rams); }
    public List<HDDComponent> getHdds() { return Collections.unmodifiableList(hdds); }
    public Motherboard getMotherboard() { return motherboard; }
}
