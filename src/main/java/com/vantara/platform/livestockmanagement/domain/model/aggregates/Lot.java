package com.vantara.platform.livestockmanagement.domain.model.aggregates;

/** A physical ranch area used to group livestock. */
public final class Lot {
    private final String id;
    private final String rancherId;
    private String name;
    private String purpose;
    private String status;

    private Lot(String id, String rancherId, String name, String purpose, String status) {
        this.id = required(id, "Lot id");
        this.rancherId = required(rancherId, "Rancher id");
        this.name = required(name, "Lot name");
        this.purpose = required(purpose, "Lot purpose");
        this.status = required(status, "Lot status");
    }

    public static Lot register(String id, String rancherId, String name, String purpose, String status) {
        return new Lot(id, rancherId, name, purpose, status);
    }

    public void update(String name, String purpose, String status) {
        this.name = required(name, "Lot name");
        this.purpose = required(purpose, "Lot purpose");
        this.status = required(status, "Lot status");
    }

    private static String required(String value, String label) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(label + " cannot be blank");
        return value.trim();
    }

    public String getId() { return id; }
    public String getRancherId() { return rancherId; }
    public String getName() { return name; }
    public String getPurpose() { return purpose; }
    public String getStatus() { return status; }
}
