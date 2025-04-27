package com.app.smart_document_vault.dto;

import java.util.Map;

public class AdminDashboardDto {
    private long totalUsers;
    private long activeUsers;
    private long deactivatedUsers;
    private long totalDocuments;
    private long documentsThisWeek;
    private long totalStorageUsedInMB;

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public long getDeactivatedUsers() {
        return deactivatedUsers;
    }

    public void setDeactivatedUsers(long deactivatedUsers) {
        this.deactivatedUsers = deactivatedUsers;
    }

    public long getTotalDocuments() {
        return totalDocuments;
    }

    public void setTotalDocuments(long totalDocuments) {
        this.totalDocuments = totalDocuments;
    }

    public long getDocumentsThisWeek() {
        return documentsThisWeek;
    }

    public void setDocumentsThisWeek(long documentsThisWeek) {
        this.documentsThisWeek = documentsThisWeek;
    }

    public long getTotalStorageUsedInMB() {
        return totalStorageUsedInMB;
    }

    public void setTotalStorageUsedInMB(long totalStorageUsedInMB) {
        this.totalStorageUsedInMB = totalStorageUsedInMB;
    }
}
