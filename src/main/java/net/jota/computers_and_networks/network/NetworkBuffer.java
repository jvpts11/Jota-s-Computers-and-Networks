package net.jota.computers_and_networks.network;

import net.jota.computers_and_networks.network.computers.ServerComputer;
import net.jota.computers_and_networks.network.enums.OperationStatus;
import net.jota.computers_and_networks.network.enums.OperationType;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class NetworkBuffer {
    private final Map<UUID, BufferItem> items;
    private final Queue<NetworkOperation> operationQueue;
    private int currentTransferRate;

    public NetworkBuffer() {
        this.items = new HashMap<>();
        this.operationQueue = new LinkedList<>();
        this.currentTransferRate = 0;
    }

    public boolean processOperation(NetworkOperation operation, LogisticNetwork network) {
        if (operation.getType() == OperationType.ITEM_TRANSFER) {
            operationQueue.add(operation);
            return true;
        }
        return false;
    }

    public void tick(LogisticNetwork network) {
        // Processa operações da fila
        processOperationQueue(network);

        // Processa transferências em andamento
        processActiveTransfers(network);
    }

    private void processOperationQueue(LogisticNetwork network) {
        NetworkComputer mainframe = network.getMainframe().orElse(null);
        if (mainframe == null) return;

        int maxOperationsPerTick = mainframe.getUploadSpeed();
        int processed = 0;

        while (!operationQueue.isEmpty() && processed < maxOperationsPerTick) {
            NetworkOperation operation = operationQueue.poll();
            if (operation != null) {
                startTransfer(operation, network);
                processed++;
            }
        }
    }

    private void startTransfer(NetworkOperation operation, LogisticNetwork network) {
        // Lógica para iniciar transferência
        BufferItem bufferItem = new BufferItem(
                operation.getId(),
                operation.getItem(),
                operation.getAmount(),
                operation.getSource(),
                operation.getDestination()
        );

        items.put(operation.getId(), bufferItem);
        operation.setStatus(OperationStatus.PROCESSING);
    }

    private void processActiveTransfers(LogisticNetwork network) {
        for (BufferItem item : items.values()) {
            if (item.isUploadComplete()) {
                processDownload(item, network);
            } else {
                processUpload(item, network);
            }
        }
    }

    private void processUpload(BufferItem item, LogisticNetwork network) {
        NetworkComputer sourceComputer = network.getComputers().get(item.getSource());
        if (sourceComputer == null) return;

        int uploadSpeed = sourceComputer.getUploadSpeed();
        int amountToUpload = Math.min(item.getRemainingUpload(), uploadSpeed);

        // Verifica se o servidor tem os itens
        if (sourceComputer instanceof ServerComputer server) {
            // Lógica para extrair itens do servidor
            ItemStack extracted = server.extractItem(0, amountToUpload, true);
            if (!extracted.isEmpty() && extracted.getCount() >= amountToUpload) {
                server.extractItem(0, amountToUpload, false);
                item.upload(amountToUpload);
            }
        }
    }

    private void processDownload(BufferItem item, LogisticNetwork network) {
        if (!item.isUploadComplete()) return;

        NetworkComputer destComputer = network.getComputers().get(item.getDestination());
        if (destComputer == null) return;

        int downloadSpeed = destComputer.getDownloadSpeed();
        int amountToDownload = Math.min(item.getRemainingDownload(), downloadSpeed);

        // Lógica para inserir itens no computador destino
        if (destComputer instanceof ServerComputer server) {
            ItemStack toInsert = item.getItem().copy();
            toInsert.setCount(amountToDownload);

            ItemStack remaining = server.insertItem(0, toInsert, true);
            if (remaining.getCount() < amountToDownload) {
                int actuallyInserted = amountToDownload - remaining.getCount();
                server.insertItem(0, toInsert, false);
                item.download(actuallyInserted);
            }
        }
    }
}
