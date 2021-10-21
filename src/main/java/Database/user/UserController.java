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
                UserTable.USER_ID + "," + UserTable.FIRST_NAME + "," +
                UserTable.USERNAME + "," + UserTable.PHONE + "," +
                UserTable.TIME_LAST_MESSAGE + ")" +
                "VALUES(?,?,?,?,?)";
        try {
            PreparedStatement st = getDbConnection().prepareStatement(insert);
            st.setString(1, user.getUserId());
            st.setString(2, user.getFirstName());
            st.setString(3, user.getUsername());
            st.setString(4, user.getPhone());
            st.setObject(5, user.getTimeLastMessage());

            st.executeUpdate();
            st.close();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("LOG: Пользователь уже существует(Возможно другая ошибка.)");
            return false;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }

        System.out.println("LOG: Зарегистрирован новый пользователь " + user.getFirstName() + "(" + user.getUserId() + ")");
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
                user.setUserId(resSet.getString(1));
                user.setStatus(resSet.getString(2));
                user.setFirstName(resSet.getString(3));
                user.setUsername(resSet.getString(4));
                user.setFullName(resSet.getString(5));
                user.setPhone(resSet.getString(6));
                user.setCardNumber(resSet.getString(7));
                user.setDormitory(resSet.getInt(8));
                user.setRoom(resSet.getInt(9));
                user.setWorkArea(resSet.getString(10));
                user.setDeliverySub(resSet.getBoolean(11));
                user.setDailyEarn(resSet.getFloat(12));
                user.setTotalEarn(resSet.getFloat(13));
                user.setTransferTotal(resSet.getFloat(14));
                user.setTimeLastMessage((Date) resSet.getObject(15));
                user.setWarns(resSet.getInt(16));
                user.setRate(resSet.getString(17));
                user.setStage(resSet.getString(18));
                user.setTemp(resSet.getString(19));
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
            st.setString(6, user.getCardNumber());
            st.setInt(7, user.getDormitory());
            st.setInt(8, user.getRoom());
            st.setString(9, user.getWorkArea());
            st.setBoolean(10, user.isDeliverySub());
            st.setDouble(11, user.getDailyEarn());
            st.setDouble(12, user.getTotalEarn());
            st.setDouble(13, user.getTransferTotal());
            st.setObject(14, user.getTimeLastMessage());
            st.setInt(15, user.getWarns());
            st.setString(16, user.getRate());
            st.setString(17, user.getStage());
            st.setString(18, user.getTemp());
            st.setString(19, user.getUserId());

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
