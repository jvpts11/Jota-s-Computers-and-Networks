package net.jota.computers_and_networks.network;

import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class BufferItem {
    private final UUID operationId;
    private final ItemStack item;
    private final int totalAmount;
    private final UUID source;
    private final UUID destination;
    private int uploadedAmount;
    private int downloadedAmount;
    private boolean uploadComplete;
    private boolean downloadComplete;

    public BufferItem(UUID operationId, ItemStack item, int totalAmount, UUID source, UUID destination) {
        this.operationId = operationId;
        this.item = item;
        this.totalAmount = totalAmount;
        this.source = source;
        this.destination = destination;
        this.uploadedAmount = 0;
        this.downloadedAmount = 0;
        this.uploadComplete = false;
        this.downloadComplete = false;
    }

    public void upload(int amount) {
        uploadedAmount += amount;
        if (uploadedAmount >= totalAmount) {
            uploadComplete = true;
            uploadedAmount = totalAmount;
        }
    }

    public void download(int amount) {
        downloadedAmount += amount;
        if (downloadedAmount >= totalAmount) {
            downloadComplete = true;
            downloadedAmount = totalAmount;
        }
    }

    public boolean isUploadComplete() { return uploadComplete; }
    public boolean isDownloadComplete() { return downloadComplete; }
    public boolean isComplete() { return uploadComplete && downloadComplete; }
    public int getRemainingUpload() { return totalAmount - uploadedAmount; }
    public int getRemainingDownload() { return totalAmount - downloadedAmount; }

    // Getters
    public UUID getOperationId() { return operationId; }
    public ItemStack getItem() { return item; }
    public int getTotalAmount() { return totalAmount; }
    public UUID getSource() { return source; }
    public UUID getDestination() { return destination; }
    public int getUploadedAmount() { return uploadedAmount; }
    public int getDownloadedAmount() { return downloadedAmount; }
}
