package Database.user;

import java.util.Date;

public class User {
    private String userId;
    private String status = "ACTIVE";
    private String firstName;
    private String username;
    private String fullName;
    private String phone;
    private String email;
    private String cardNumber;
    private int dormitory;
    private int room;
    private String workArea;
    private boolean deliverySub;
    private double dailyEarn;
    private double totalEarn;
    private double transferTotal;
    private Date timeLastMessage;
    private int warns;
    private String rate;
    private String stage = "NONE";
    private String temp = "NONE";

    public User(){
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", status='" + status + '\'' +
                ", firstName='" + firstName + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", dormitory=" + dormitory +
                ", room=" + room +
                ", workArea='" + workArea + '\'' +
                ", deliverySub=" + deliverySub +
                ", dailyEarn=" + dailyEarn +
                ", totalEarn=" + totalEarn +
                ", transferTotal=" + transferTotal +
                ", timeLastMessage=" + timeLastMessage +
                ", warns=" + warns +
                ", rate='" + rate + '\'' +
                ", stage='" + stage + '\'' +
                ", temp='" + temp + '\'' +
                '}';
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getDormitory() {
        return dormitory;
    }
    public void setDormitory(int dormitory) {
        this.dormitory = dormitory;
    }

    public int getRoom() {
        return room;
    }
    public void setRoom(int room) {
        this.room = room;
    }

    public String getWorkArea() {
        return workArea;
    }
    public void setWorkArea(String workArea) {
        this.workArea = workArea;
    }

    public boolean isDeliverySub() {
        return deliverySub;
    }
    public void setDeliverySub(boolean deliverySub) {
        this.deliverySub = deliverySub;
    }

    public double getDailyEarn() {
        return dailyEarn;
    }
    public void setDailyEarn(double dailyEarn) {
        this.dailyEarn = dailyEarn;
    }

    public double getTotalEarn() {
        return totalEarn;
    }
    public void setTotalEarn(double totalEarn) {
        this.totalEarn = totalEarn;
    }

    public double getTransferTotal() {
        return transferTotal;
    }
    public void setTransferTotal(double transferTotal) {
        this.transferTotal = transferTotal;
    }

    public Date getTimeLastMessage() {
        return timeLastMessage;
    }
    public void setTimeLastMessage(Date timeLastMessage) {
        this.timeLastMessage = timeLastMessage;
    }

    public int getWarns() {
        return warns;
    }
    public void setWarns(int warns) {
        this.warns = warns;
    }

    public String getRate() {
        return rate;
    }
    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getStage() {
        return stage;
    }
    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getTemp() {
        return temp;
    }
    public void setTemp(String temp) {
        this.temp = temp;
    }
}
