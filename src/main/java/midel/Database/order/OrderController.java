package midel.Database.order;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import static midel.Database.DataBaseHandler.getDbConnection;

public class OrderController {
    public static void createOrder(Order order) {
        String insert = "INSERT INTO " + OrderTable.TABLE_NAME + "(" +
                OrderTable.STATUS + ",`" + OrderTable.FROM + "`," +
                OrderTable.EXECUTOR + "," + OrderTable.PLACE_PURCHASE + "," +
                OrderTable.ORDER_TEXT + "," + OrderTable.DELIVERY_PLACE + "," +
                OrderTable.COST + "," + OrderTable.PHOTO_CHECK + "," +
                OrderTable.DATE_CREATION + ")" + "VALUES(?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement st = getDbConnection().prepareStatement(insert);
            st.setString(1, order.getStatus());
            st.setString(2, order.getFrom());
            st.setString(3, order.getExecutor());
            st.setString(4, order.getPlacePurchase());
            st.setString(5, order.getOrderText());
            st.setString(6, order.getDeliveryPlace());
            st.setDouble(7, order.getCost());
            st.setBoolean(8, order.isPhotoCheck());
            st.setObject(9, order.getDateCreation());

            st.executeUpdate();
            st.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        System.out.println("LOG: New order creation - From: " + order.getFrom());
    }

    public static Order getOrderById(String orderId) {
        Order order = null;

        ResultSet resSet;
        String select = "SELECT * FROM " + OrderTable.TABLE_NAME + " WHERE " +
                OrderTable.ORDER_ID + "=?";

        try {
            PreparedStatement st = getDbConnection().prepareStatement(select);
            st.setString(1, orderId);
            resSet = st.executeQuery();

            while (resSet.next()) {
                order = new Order();
                order.setOrderId(resSet.getString(OrderTable.ORDER_ID));
                order.setStatus(resSet.getString(OrderTable.STATUS));
                order.setFrom(resSet.getString(OrderTable.FROM));
                order.setExecutor(resSet.getString(OrderTable.EXECUTOR));
                order.setPlacePurchase(resSet.getString(OrderTable.PLACE_PURCHASE));
                order.setOrderText(resSet.getString(OrderTable.ORDER_TEXT));
                order.setDeliveryPlace(resSet.getString(OrderTable.DELIVERY_PLACE));
                order.setCost(resSet.getDouble(OrderTable.COST));
                order.setPhotoCheck(resSet.getBoolean(OrderTable.PHOTO_CHECK));
                order.setDateCreation((Date) resSet.getObject(OrderTable.DATE_CREATION));
            }
            resSet.close();
            st.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return order;
    }

    public static void updateOrder(Order order) {
        String insert = "UPDATE " + OrderTable.TABLE_NAME + " SET " +
                OrderTable.STATUS + " = ?," +
                OrderTable.FROM + " = ?," +
                OrderTable.EXECUTOR + " = ?," +
                OrderTable.PLACE_PURCHASE + " = ?," +
                OrderTable.ORDER_TEXT + " = ?," +
                OrderTable.DELIVERY_PLACE + " = ?," +
                OrderTable.COST + " = ?," +
                OrderTable.PHOTO_CHECK + " = ?," +
                OrderTable.DATE_CREATION + " = ? " +
                "WHERE " + OrderTable.ORDER_ID + " = ?";// IF TABLE UPDATE

        try {
            PreparedStatement st = getDbConnection().prepareStatement(insert);

            st.setString(1, order.getStatus());
            st.setString(2, order.getFrom());
            st.setString(3, order.getExecutor());
            st.setString(4, order.getPlacePurchase());
            st.setString(5, order.getOrderText());
            st.setString(6, order.getDeliveryPlace());
            st.setDouble(7, order.getCost());
            st.setBoolean(8, order.isPhotoCheck());
            st.setObject(9, order.getDateCreation());
            st.setString(10, order.getOrderId());

            st.executeUpdate();

            st.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Order> getOrdersBy(String ordersTableField, Object value) {
        ArrayList<Order> orders = new ArrayList<>();

        ResultSet resSet;
        String select = "SELECT * FROM " + OrderTable.TABLE_NAME + " WHERE " +
                "`" + ordersTableField + "`" + " = ?";

        try {
            PreparedStatement st = getDbConnection().prepareStatement(select);
            switch (ordersTableField) {
                case OrderTable.COST:
                    st.setDouble(1, (double) value);
                    break;
                case OrderTable.DATE_CREATION:
                    st.setObject(1, value);
                    break;
                case OrderTable.PHOTO_CHECK:
                    st.setBoolean(1, (boolean) value);
                    break;
                default:
                    st.setString(1, (String) value);
            }
            resSet = st.executeQuery();

            while (resSet.next()) {
                Order order = new Order();
                order.setOrderId(resSet.getString(OrderTable.ORDER_ID));
                order.setStatus(resSet.getString(OrderTable.STATUS));
                order.setFrom(resSet.getString(OrderTable.FROM));
                order.setExecutor(resSet.getString(OrderTable.EXECUTOR));
                order.setPlacePurchase(resSet.getString(OrderTable.PLACE_PURCHASE));
                order.setOrderText(resSet.getString(OrderTable.ORDER_TEXT));
                order.setDeliveryPlace(resSet.getString(OrderTable.DELIVERY_PLACE));
                order.setCost(resSet.getDouble(OrderTable.COST));
                order.setPhotoCheck(resSet.getBoolean(OrderTable.PHOTO_CHECK));
                order.setDateCreation((Date) resSet.getObject(OrderTable.DATE_CREATION));

                orders.add(order);
            }
            resSet.close();
            st.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return orders;
    }

    public static void removeOrder(String orderId) {
        String delete = "DELETE FROM " + OrderTable.TABLE_NAME + " WHERE " +
                OrderTable.ORDER_ID + " = ?;";

        try {
            PreparedStatement st = getDbConnection().prepareStatement(delete);
            st.setString(1, orderId);

            st.executeUpdate();

            st.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
