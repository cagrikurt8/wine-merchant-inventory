package system;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Map;

public class DBHelper {
    private String fileName;
    private String dbURL;
    private Connection connection;
    private Statement statement;
    private DefaultTableModel selectedModel = new DefaultTableModel(new String[] {"Supplier", "Wine", "Amount Purchased", "Purchased Price", "Paid"}, 0);

    private void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS supplier_orders(" +
                       "supplier_name TEXT NOT NULL," +
                       "wine_type TEXT NOT NULL," +
                       "amount_purchased INTEGER NOT NULL," +
                       "price_paid TEXT NOT NULL," +
                       "is_paid TEXT NOT NULL" +
                       ");";
        try {
            statement = connection.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            showErrorMessage(e);
        }
    }

    private void createInventoryTable() {
        String query = "CREATE TABLE IF NOT EXISTS inventory(" +
                       "wine_type TEXT NOT NULL," +
                       "amount INTEGER NOT NULL);";

        String ifExistQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='inventory';";

        Map<String, Integer> initialAmounts = Map.of("Merlot", 0, "Rose", 0, "Sauvignon",0);

        try {
            createConnection();
            statement = connection.createStatement();
            boolean ifExist = statement.execute(ifExistQuery);
            
            if (ifExist) {
                Statement createTable = connection.createStatement();
                createTable.execute(query);

                for (String wineType: initialAmounts.keySet()) {
                    Statement insertStmt = connection.createStatement();
                    String insertQuery = String.format("INSERT INTO inventory (wine_type, amount)" +
                                         "VALUES ('%s', %d)", wineType, initialAmounts.get(wineType));
                    insertStmt.execute(insertQuery);
                }

            }
            closeConnection();
        } catch (SQLException e) {
            showErrorMessage(e);
        }
    }

    private void createConnection() {
        try {
            connection = DriverManager.getConnection(dbURL);
        } catch (SQLException e) {
            showErrorMessage(e);
        }
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            showErrorMessage(e);
        }
    }

    public void updateAmount(String wineType, int newAmount) {
        String query = String.format("UPDATE inventory SET amount = amount + %d WHERE wine_type='%s';", newAmount, wineType);

        try {
            createConnection();
            statement = connection.createStatement();
            statement.execute(query);
            closeConnection();
        } catch (SQLException e) {
            showErrorMessage(e);
        }
    }

    public void insert(String supplierName, String wineType, int amount, String price, String isPaid) {
        String query = String.format("INSERT INTO supplier_orders (supplier_name, wine_type, amount_purchased, price_paid, is_paid) " +
                                     "VALUES ('%s', '%s', %d, '%s', '%s');", supplierName, wineType, amount, price, isPaid);
        try {
            createConnection();
            statement = connection.createStatement();
            statement.execute(query);
            closeConnection();
        } catch (SQLException e) {
            showErrorMessage(e);
        }
    }

    public void updateSingleOrder(String wineType) {
        String query = String.format("UPDATE inventory SET amount = amount - 12 WHERE wine_type='%s';", wineType);

        try {
            createConnection();
            statement = connection.createStatement();
            statement.execute(query);
            closeConnection();
        } catch (SQLException e) {
            showErrorMessage(e);
        }
    }

    public void updateMixedOrder(String wineType1, String wineType2) {
        String query = String.format("UPDATE inventory SET amount = amount - 6 WHERE wine_type='%s' OR wine_type='%s';", wineType1, wineType2);

        try {
            createConnection();
            statement = connection.createStatement();
            statement.execute(query);
            closeConnection();
        } catch (SQLException e) {
            showErrorMessage(e);
        }
    }

    public boolean checkSingleOrderAmount(String wineType) {
        String query = String.format("SELECT amount FROM inventory WHERE wine_type = '%s'", wineType);
        boolean condition = true;

        try {
            createConnection();
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            if (rs.getInt("amount") < 12) {
                condition = false;
            }
            closeConnection();
        } catch (SQLException e) {
            showErrorMessage(e);
        }
        return condition;
    }

    public boolean checkMixedOrderAmount(String wineType1, String wineType2) {
        String query = String.format("SELECT amount FROM inventory WHERE wine_type = '%s' OR wine_type = '%s';", wineType1, wineType2);
        boolean condition = true;

        try {
            createConnection();
            statement = connection.createStatement();
            ResultSet rs= statement.executeQuery(query);

            while (rs.next()) {
                int amount = rs.getInt("amount");

                if (amount < 6) {
                    condition = false;
                }
            }
            closeConnection();
        } catch (SQLException e) {
            showErrorMessage(e);
        }
        return condition;
    }

    public void setTableModel(String supplierName) {
        try {
            connection = DriverManager.getConnection(dbURL);
        } catch (SQLException e) {
            showErrorMessage(e);
        }

        if (!supplierName.equals("All Orders")) {
            for (int i = selectedModel.getRowCount() - 1; i >= 0; i--) {
                selectedModel.removeRow(i);
            }

            try {
                String query = String.format("SELECT * FROM supplier_orders WHERE supplier_name = '%s';", supplierName);
                statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(query);

                while (rs.next()) {
                    String s = rs.getString("supplier_name");
                    String w = rs.getString("wine_type");
                    String a = rs.getString("amount_purchased");
                    String p = rs.getString("price_paid");
                    String i = rs.getString("is_paid");

                    selectedModel.addRow(new Object[] {s, w, a, p, i});
                }
                closeConnection();
            } catch (SQLException e) {
                showErrorMessage(e);
            }

        } else {
            for (int i = selectedModel.getRowCount() - 1; i >= 0; i--) {
                selectedModel.removeRow(i);
            }

            try {
                String query = "SELECT * FROM supplier_orders;";
                statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(query);

                while (rs.next()) {
                    String s = rs.getString("supplier_name");
                    String w = rs.getString("wine_type");
                    String a = rs.getString("amount_purchased");
                    String p = rs.getString("price_paid");
                    String i = rs.getString("is_paid");

                    selectedModel.addRow(new Object[] {s, w, a, p, i});
                }
                closeConnection();
            } catch (SQLException e) {
                showErrorMessage(e);
            }
        }
    }

    public DefaultTableModel getTableModel() {
        return  selectedModel;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        dbURL = "jdbc:sqlite:C:\\Users\\cagri\\IdeaProjects\\Inventory and POS System\\Inventory and POS System\\task\\" + fileName;
        try {
            createConnection();
            statement = connection.createStatement();
            createTable();
            createInventoryTable();
            closeConnection();
        } catch (SQLException e) {
            showErrorMessage(e);
        }
    }

    public Map<String, Integer> getWineAmounts() {
        int merlotAmount = 0;
        int roseAmount = 0;
        int sauvignonAmount = 0;
        int totalAmount = 0;

        try {
            String queryMerlot = "SELECT amount FROM inventory WHERE wine_type = 'Merlot';";
            String queryRose = "SELECT amount FROM inventory WHERE wine_type = 'Rose';";
            String querySauvignon = "SELECT amount FROM inventory WHERE wine_type = 'Sauvignon';";

            createConnection();

            Statement merlotStmt = connection.createStatement();
            Statement roseStmt = connection.createStatement();
            Statement sauvignonStmt = connection.createStatement();

            ResultSet merlotRS = merlotStmt.executeQuery(queryMerlot);
            ResultSet roseRS = roseStmt.executeQuery(queryRose);
            ResultSet sauvignonRS = sauvignonStmt.executeQuery(querySauvignon);

            while (merlotRS.next()) {
                merlotAmount = merlotRS.getInt("amount");
            }

            while (roseRS.next()) {
                roseAmount = roseRS.getInt("amount");
            }

            while (sauvignonRS.next()) {
                sauvignonAmount = sauvignonRS.getInt("amount");
            }

            totalAmount = merlotAmount + roseAmount + sauvignonAmount;

            closeConnection();
        } catch (SQLException e) {
            showErrorMessage(e);
        }

        return Map.of("Merlot", merlotAmount, "Rose", roseAmount, "Sauvignon", sauvignonAmount, "Total", totalAmount);
    }

    public void showErrorMessage(SQLException exception) {
        System.out.println("Error : " + exception.getMessage());
        System.out.println("Error code : " + exception.getErrorCode());
    }
}
