package Database.user;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Date;

import static Database.DataBaseHandler.getDbConnection;

public class UserController {
    public static boolean signUpUser(User user) {
        String insert = "INSERT INTO " + UserTable.TABLE_NAME + "(" +
                UserTable.USER_ID + "," + UserTable.STATUS + ","+
                UserTable.FIRST_NAME + "," + UserTable.USERNAME + "," +
                UserTable.TIME_LAST_MESSAGE + ")" +
                "VALUES(?,?,?,?,?)";
        try {
            PreparedStatement st = getDbConnection().prepareStatement(insert);
            st.setString(1, user.getUserId());
            st.setString(2, user.getStatus());
            st.setString(3, user.getFirstName());
            st.setString(4, user.getUsername());
            st.setObject(5, user.getTimeLastMessage());

            st.executeUpdate();
            st.close();
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }

        System.out.println("LOG: New user registered " + user.getFirstName() + "(" + user.getUserId() + ")");
        return true;
    }

    public static User getUserById(String userId) {
        User user = null;

        ResultSet resSet;
        String select = "SELECT * FROM " + UserTable.TABLE_NAME + " WHERE " +
                UserTable.USER_ID + "=?";

        try {
            PreparedStatement st = getDbConnection().prepareStatement(select);
            st.setString(1, userId);
            resSet = st.executeQuery();

            while (resSet.next()) {
                user = new User();
                user.setUserId(resSet.getString(UserTable.USER_ID));
                user.setStatus(resSet.getString(UserTable.STATUS));
                user.setFirstName(resSet.getString(UserTable.FIRST_NAME));
                user.setUsername(resSet.getString(UserTable.USERNAME));
                user.setFullName(resSet.getString(UserTable.FULL_NAME));
                user.setPhone(resSet.getString(UserTable.PHONE));
                user.setEmail(resSet.getString(UserTable.EMAIL));
                user.setCardNumber(resSet.getString(UserTable.CARD_NUMBER));
                user.setDormitory(resSet.getInt(UserTable.DORMITORY));
                user.setRoom(resSet.getInt(UserTable.ROOM));
                user.setWorkArea(resSet.getString(UserTable.WORK_AREA));
                user.setDeliverySub(resSet.getBoolean(UserTable.DELIVERY_SUB));
                user.setDailyEarn(resSet.getFloat(UserTable.DAILY_EARN));
                user.setTotalEarn(resSet.getFloat(UserTable.TOTAL_EARN));
                user.setTransferTotal(resSet.getFloat(UserTable.TRANSFER_TOTAL));
                user.setTimeLastMessage((Date) resSet.getObject(UserTable.TIME_LAST_MESSAGE));
                user.setWarns(resSet.getInt(UserTable.WARNS));
                user.setRate(resSet.getString(UserTable.RATE));
                user.setStage(resSet.getString(UserTable.STAGE));
                user.setTemp(resSet.getString(UserTable.TEMP));
            }
            resSet.close();
            st.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static void updateUserDB(User user) {
        String insert = "UPDATE " + UserTable.TABLE_NAME + " SET " +
                UserTable.STATUS + " = ?," +
                UserTable.FIRST_NAME + " = ?," +
                UserTable.USERNAME + " = ?," +
                UserTable.FULL_NAME + " = ?," +
                UserTable.PHONE + " = ?," +
                UserTable.EMAIL + " = ?," +
                UserTable.CARD_NUMBER + " = ?," +
                UserTable.DORMITORY + " = ?," +
                UserTable.ROOM + " = ?," +
                UserTable.WORK_AREA + " = ?," +
                UserTable.DELIVERY_SUB + " = ?," +
                UserTable.DAILY_EARN + " = ?," +
                UserTable.TOTAL_EARN + " = ?," +
                UserTable.TRANSFER_TOTAL + " = ?," +
                UserTable.TIME_LAST_MESSAGE + " = ?," +
                UserTable.WARNS + " = ?," +
                UserTable.RATE + " = ?," +
                UserTable.STAGE + " = ?," +
                UserTable.TEMP + " = ? " +
                "WHERE " + UserTable.USER_ID + " = ?";// IF TABLE UPDATE

        try {
            PreparedStatement st = getDbConnection().prepareStatement(insert);

            st.setString(1, user.getStatus());
            st.setString(2, user.getFirstName());
            st.setString(3, user.getUsername());
            st.setString(4, user.getFullName());
            st.setString(5, user.getPhone());
            st.setString(6, user.getEmail());
            st.setString(7, user.getCardNumber());
            st.setInt(8, user.getDormitory());
            st.setInt(9, user.getRoom());
            st.setString(10, user.getWorkArea());
            st.setBoolean(11, user.isDeliverySub());
            st.setDouble(12, user.getDailyEarn());
            st.setDouble(13, user.getTotalEarn());
            st.setDouble(14, user.getTransferTotal());
            st.setObject(15, user.getTimeLastMessage());
            st.setInt(16, user.getWarns());
            st.setString(17, user.getRate());
            st.setString(18, user.getStage());
            st.setString(19, user.getTemp());
            st.setString(20, user.getUserId());

            st.executeUpdate();

            st.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getUsersIdAndAreaWithAlert() {
        ArrayList<String> users = new ArrayList<>();

        String select = "SELECT * FROM " + UserTable.TABLE_NAME + " WHERE " +
                UserTable.DELIVERY_SUB + "= TRUE";

        try {
            PreparedStatement st = getDbConnection().prepareStatement(select);
            ResultSet resSet = st.executeQuery();

            while (resSet.next()) {
                users.add(resSet.getString(UserTable.USER_ID) + ":" + resSet.getString(UserTable.WORK_AREA));
            }

            st.close();
            resSet.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (users.size() == 0) {
            return null;
        } else {
            return users;
        }
    }
}
