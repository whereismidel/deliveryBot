package midel.Database.order;

import midel.Controller;

import java.util.Date;

public class Order {
    private String orderId;
    private String status;
    /*
    AVAILABLE - Доступен, можно взять
    BLOCKED - Заблокирован по какой-то причине(Например подтверждение у доставщика)
    TAKEN - Взят доставщиком.
    PAID - Оплачен.
    DELIVERED - Доставлен.
     */

    private String from;
    private String executor;
    private String placePurchase;
    private String orderText;
    private String deliveryPlace;
    private double cost = 0;
    private boolean photoCheck = false;
    private Date dateCreation;

    public Order() {
    }
    public Order(String from) {
        this.status = "AVAILABLE";
        this.from = from;
    }
    public Order(String[] orderParams) {
        this.orderId = orderParams[0];
        this.status = orderParams[1];
        this.from = orderParams[2];
        this.executor = orderParams[3];
        this.placePurchase = orderParams[4];
        this.orderText = orderParams[5];
        this.deliveryPlace = orderParams[6];
        this.cost = Double.parseDouble(orderParams[7]);
        this.photoCheck = Boolean.parseBoolean(orderParams[8]);
        this.dateCreation = Controller.stringToDate(orderParams[9]);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", status='" + status + '\'' +
                ", from='" + from + '\'' +
                ", executor='" + executor + '\'' +
                ", placePurchase='" + placePurchase + '\'' +
                ", orderText='" + orderText + '\'' +
                ", deliveryPlace='" + deliveryPlace + '\'' +
                ", cost=" + cost +
                ", photoCheck=" + photoCheck +
                ", dateCreation='" + dateCreation + '\'' +
                '}';
    }
    public String toTempString() {
        return orderId + "\0" +
                status + "\0" +
                from + "\0" +
                executor + "\0" +
                placePurchase + "\0" +
                orderText + "\0" +
                deliveryPlace + "\0" +
                cost + "\0" +
                photoCheck + "\0" +
                Controller.dateToString(dateCreation);
    }

    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }

    public String getExecutor() {
        return executor;
    }
    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public String getPlacePurchase() {
        return placePurchase;
    }
    public void setPlacePurchase(String placePurchase) {
        this.placePurchase = placePurchase;
    }

    public String getOrderText() {
        return orderText;
    }
    public void setOrderText(String orderText) {
        this.orderText = orderText;
    }

    public String getDeliveryPlace() {
        return deliveryPlace;
    }
    public void setDeliveryPlace(String deliveryPlace) {
        this.deliveryPlace = deliveryPlace;
    }

    public double getCost() {
        return cost;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }

    public boolean isPhotoCheck() {
        return photoCheck;
    }
    public void setPhotoCheck(boolean photoCheck) {
        this.photoCheck = photoCheck;
    }

    public Date getDateCreation() {
        return dateCreation;
    }
    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }
}
