package system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RootWindow extends JFrame {
    //Stage 1 elements
    private JLabel instructionLabel = new JLabel();
    private JPanel stage1Panel = new JPanel();
    private JPanel topPanel = new JPanel();
    private JPanel middlePanel = new JPanel();
    private JPanel bottomPanel = new JPanel();
    private JRadioButton merlotButton = new JRadioButton("Merlot");
    private JRadioButton roseButton = new JRadioButton("Rose");
    private JRadioButton sauvignonButton = new JRadioButton("Sauvignon");
    private JLabel caseLabel  = new JLabel();
    private JComboBox amountComboBox = new JComboBox();
    private JLabel caseLabelEnd  = new JLabel();
    private JLabel supplierLabel  = new JLabel();
    private JComboBox supplierComboBox = new JComboBox();
    private JLabel priceLabel  = new JLabel();
    private JTextField purchasedPriceTextField = new JTextField();
    private JButton submitButton = new JButton("Submit");
    private JLabel successLabel = new JLabel();
    private JLabel messageLabel = new JLabel();
    private ButtonGroup radioButtonGroup = new ButtonGroup();
    private String wineModel;
    private Integer[] amounts = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private String[] suppliers = new String[]{"Saba", "Tom", "Jesus", "Testy", "Mctesterson"};

    //Stage2 elements
    JTabbedPane orderWindows = new JTabbedPane();
    JPanel stage2Panel = new JPanel();
    JComboBox supplierOrderComboBox = new JComboBox();
    JButton filterButton = new JButton("Filter");
    DefaultTableModel ordersTableModel = new DefaultTableModel();
    JTable ordersTable = new JTable(ordersTableModel);

    //Stage3 elements
    private DBHelper helper = new DBHelper();

    //Stage4 elements
    JLabel inventoryLabel = new JLabel("Inventory", SwingConstants.CENTER);
    JPanel stage4Panel = new JPanel();
    JLabel merlotLabel = new JLabel("Red Merlot", SwingConstants.CENTER);
    JLabel merlotAmountLabel = new JLabel("", SwingConstants.CENTER);
    JLabel roseLabel = new JLabel("Rose Zinfandel", SwingConstants.CENTER);
    JLabel roseAmountLabel = new JLabel("", SwingConstants.CENTER);
    JLabel sauvignonLabel = new JLabel("White Sauvignon", SwingConstants.CENTER);
    JLabel sauvignonAmountLabel = new JLabel("", SwingConstants.CENTER);
    JLabel totalLabel = new JLabel("Total", SwingConstants.CENTER);
    JLabel totalAmountLabel = new JLabel("", SwingConstants.CENTER);
    JLabel[] inventoryNameLabels = new JLabel[] {merlotLabel, roseLabel, sauvignonLabel, totalLabel};
    JLabel[] inventoryAmountLabels = new JLabel[] {merlotAmountLabel, roseAmountLabel, sauvignonAmountLabel, totalAmountLabel};

    //Stage5 elements
    JPanel stage5Panel = new JPanel();
    JPanel posTopPanel = new JPanel();
    JLabel posInstructionLabel = new JLabel("Select if the sale is...");
    JRadioButton singleTypeOrderButton = new JRadioButton("Single Type");
    JRadioButton mixedTypeOrderButton = new JRadioButton("Mixed Type");
    JPanel posMidPanel = new JPanel();
    JPanel singleTypePanel = new JPanel();
    JLabel singleWineSelectionLabel = new JLabel("Select the kind of wine", SwingConstants.CENTER);
    JLabel mixedWineSelectionLabel1 = new JLabel("Select wine 1:", SwingConstants.CENTER);
    JLabel mixedWineSelectionLabel2 = new JLabel("Select wine 2:", SwingConstants.CENTER);
    JComboBox singleWineKindBox = new JComboBox();
    JButton singleConfirmButton = new JButton("Confirm");
    JPanel mixedTypePanel = new JPanel();
    JComboBox mixedWineComboBox1 = new JComboBox();
    JComboBox mixedWineComboBox2 = new JComboBox();
    JButton mixedConfirmButton = new JButton("Confirm");
    JPanel posBottomPanel = new JPanel();
    JLabel customerLabel = new JLabel("Customer name:", SwingConstants.CENTER);
    JLabel saleAmountLabel = new JLabel("Sale amount:", SwingConstants.CENTER);
    JTextField customerNameField = new JTextField();
    JTextField saleAmountField = new JTextField();
    JRadioButton loyaltyDiscountButton = new JRadioButton("Loyalty Discount (15%)");
    JButton submitOrderButton = new JButton("Submit");
    JLabel saleMessageLabel = new JLabel();
    JLabel saleSuccessMessageLabel = new JLabel();
    ButtonGroup typeButtonGroup = new ButtonGroup();
    String[] wineTypes = new String[] {"Merlot", "Rose", "Sauvignon"};

    public RootWindow(String fileName) {
        super("Wine Merchant");

        helper.setFileName(fileName);
        getContentPane().setBackground(new Color(192, 192, 192));
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        setAllNames();
        groupRadioButtons();
        setAllComponents();

        setLocationRelativeTo(null);
        setVisible(true);

        merlotButton.addActionListener(e -> {
            String temp = "Selected type: " + merlotButton.getText();
            caseLabel.setText(temp);
            wineModel = merlotButton.getText();
            commonRadioButtonActions();
        });

        roseButton.addActionListener(e -> {
            String temp = "Selected type: " + roseButton.getText();
            caseLabel.setText(temp);
            wineModel = roseButton.getText();
            commonRadioButtonActions();
        });

        sauvignonButton.addActionListener(e -> {
            String temp = "Selected type: " + sauvignonButton.getText();
            caseLabel.setText(temp);
            wineModel = sauvignonButton.getText();
            commonRadioButtonActions();
        });

        submitButton.addActionListener(e -> {
            String message = "";

            boolean condition = amountComboBox.getSelectedItem() != null && (amountComboBox.getSelectedItem().toString().matches("[^A-z]")) && ((Integer.parseInt(amountComboBox.getSelectedItem().toString()) >= 1 && Integer.parseInt(amountComboBox.getSelectedItem().toString()) <= 10)) && (Arrays.asList(suppliers).contains(supplierComboBox.getSelectedItem().toString()));
            if (condition) {
                int amount = Integer.parseInt(amountComboBox.getSelectedItem().toString());
                int bottles = 12 * amount;
                String supplier = supplierComboBox.getSelectedItem().toString();
                message = String.format("Added %d cases of %s from %s (%d bottles).", amount, wineModel, supplier, bottles);
                successLabel.setVisible(true);

                helper.insert(supplier, wineModel, amount, purchasedPriceTextField.getText(), "false");
                helper.setTableModel("All Orders");
                helper.updateAmount(wineModel, bottles);
                setWineAmountLabels();
            } else {
                successLabel.setVisible(false);
                message = "Error! The entered values are not valid.";
            }

            messageLabel.setText(message);
            messageLabel.setVisible(true);
            supplierComboBox.setSelectedItem(null);
            amountComboBox.setSelectedItem(null);
            purchasedPriceTextField.setText(null);
        });

        filterButton.addActionListener(e -> {
            String supplier = supplierOrderComboBox.getSelectedItem().toString();
            helper.setTableModel(supplier);
        });

        singleTypeOrderButton.addActionListener(e -> {
            singleTypePanel.setVisible(true);
            mixedTypePanel.setVisible(false);
        });

        mixedTypeOrderButton.addActionListener(e -> {
            mixedTypePanel.setVisible(true);
            singleTypePanel.setVisible(false);
        });

        singleConfirmButton.addActionListener(e -> {
            String wineType = singleWineKindBox.getSelectedItem().toString();
            saleMessageLabel.setText("Selected wine: " + wineType);

            customerNameField.setEnabled(true);
            saleAmountField.setEnabled(true);
            loyaltyDiscountButton.setEnabled(true);
            submitOrderButton.setEnabled(true);
        });

        mixedConfirmButton.addActionListener(e -> {
            String wineType1 = mixedWineComboBox1.getSelectedItem().toString();
            String wineType2 = mixedWineComboBox2.getSelectedItem().toString();

            if (wineType1.equals(wineType2)) {
                saleMessageLabel.setText("Error! Wine types should be different.");
            } else {
                saleMessageLabel.setText("Selected wines: 1. " + wineType1 + " 2. " + wineType2);
                customerNameField.setEnabled(true);
                saleAmountField.setEnabled(true);
                loyaltyDiscountButton.setEnabled(true);
                submitOrderButton.setEnabled(true);
            }
        });

        loyaltyDiscountButton.addActionListener(e -> {
            double price = Double.valueOf(saleAmountField.getText());

            saleAmountField.setText(String.valueOf(price * 0.85));
        });

        submitOrderButton.addActionListener(e -> {
            Pattern customerPattern = Pattern.compile(".*[\\s'\\d].*");
            Matcher customerMatcher = customerPattern.matcher(customerNameField.getText());

            Pattern salePattern = Pattern.compile(".*[A-z].*");
            Matcher saleMatcher = salePattern.matcher(saleAmountField.getText());

            if (singleTypePanel.isVisible() && !helper.checkSingleOrderAmount(singleWineKindBox.getSelectedItem().toString())) {
                saleSuccessMessageLabel.setText("Error! Not enough amount.");

            } else if (mixedTypePanel.isVisible() && !helper.checkMixedOrderAmount(mixedWineComboBox1.getSelectedItem().toString(), mixedWineComboBox2.getSelectedItem().toString())) {
                saleSuccessMessageLabel.setText("Error! Not enough amount.");

            } else if (!customerMatcher.find() && !saleMatcher.find()) {
                String customer = customerNameField.getText();

                if (singleTypePanel.isVisible()) {
                    String wineType = singleWineKindBox.getSelectedItem().toString();
                    helper.updateSingleOrder(wineType);
                    setWineAmountLabels();

                } else if (mixedTypePanel.isVisible()) {
                    String wineType1 = mixedWineComboBox1.getSelectedItem().toString();
                    String wineType2 = mixedWineComboBox2.getSelectedItem().toString();
                    helper.updateMixedOrder(wineType1, wineType2);
                    setWineAmountLabels();
                }

                saleSuccessMessageLabel.setText("Order sold to " + customer + " of £" + saleAmountField.getText());
                saleMessageLabel.setText("");
                customerNameField.setEnabled(false);
                saleAmountField.setEnabled(false);
                loyaltyDiscountButton.setEnabled(false);
                submitOrderButton.setEnabled(false);

                singleTypePanel.setVisible(false);
                mixedTypePanel.setVisible(false);

            }  else {
                saleSuccessMessageLabel.setText("Error! Valid values should be entered!");
            }
            customerNameField.setText("");
            saleAmountField.setText("");
        });
    }

    private void groupRadioButtons() {
        JRadioButton[] radioButtons = new JRadioButton[] {merlotButton, roseButton, sauvignonButton};
        JRadioButton[] typeButtons = new JRadioButton[] {singleTypeOrderButton, mixedTypeOrderButton};

        for (JRadioButton radioButton: radioButtons) {
            radioButton.setBackground(new Color(179, 45, 0));
            radioButton.setHorizontalAlignment(SwingConstants.CENTER);
            radioButton.setFont(new Font("Arial", Font.PLAIN, 18));
            radioButton.setForeground(Color.BLACK);
        }

        for (JRadioButton typeButton: typeButtons) {
            typeButton.setBackground(new Color(179, 45, 0));
            typeButton.setHorizontalAlignment(SwingConstants.CENTER);
            typeButton.setFont(new Font("Arial", Font.PLAIN, 18));
            typeButton.setForeground(new Color(255, 240, 179));
        }

        loyaltyDiscountButton.setBackground(new Color(200, 200, 200));

        radioButtonGroup.add(merlotButton);
        radioButtonGroup.add(roseButton);
        radioButtonGroup.add(sauvignonButton);

        typeButtonGroup.add(singleTypeOrderButton);
        typeButtonGroup.add(mixedTypeOrderButton);

        submitButton.setEnabled(false);
        submitOrderButton.setEnabled(false);
        loyaltyDiscountButton.setEnabled(false);
    }

    private void commonRadioButtonActions() {
        middlePanel.setVisible(true);
        submitButton.setEnabled(true);
    }

    private void setComboBoxes() {
        supplierOrderComboBox.addItem("All Orders");

        for (Integer amount: amounts) {
            amountComboBox.addItem(amount);
        }

        for (String supplier: suppliers) {
            supplierComboBox.addItem(supplier);
            supplierOrderComboBox.addItem(supplier);
        }

        for (String wineType: wineTypes) {
            singleWineKindBox.addItem(wineType);
            mixedWineComboBox1.addItem(wineType);
            mixedWineComboBox2.addItem(wineType);
        }

        amountComboBox.setSelectedItem(null);
        supplierComboBox.setSelectedItem(null);
        amountComboBox.setEditable(true);
        supplierComboBox.setEditable(true);
        supplierOrderComboBox.setEditable(true);

        //Stage5
        singleWineKindBox.setAlignmentX(SwingConstants.CENTER);
        mixedWineComboBox1.setAlignmentX(SwingConstants.CENTER);
        mixedWineComboBox2.setAlignmentX(SwingConstants.CENTER);
    }

    private void setStage1Panel() {
        //Stage1
        topPanel.setBackground(new Color(179, 45, 0));
        topPanel.setLayout(new GridLayout(2, 1));
        JPanel tempTopPanel1 = new JPanel();
        tempTopPanel1.setBackground(new Color(179, 45, 0));
        tempTopPanel1.add(instructionLabel);
        JPanel tempTopPanel2 = new JPanel();
        tempTopPanel2.setBackground(new Color(179, 45, 0));
        tempTopPanel2.setLayout(new GridLayout(1, 3));
        tempTopPanel2.add(merlotButton);
        tempTopPanel2.add(roseButton);
        tempTopPanel2.add(sauvignonButton);
        topPanel.add(tempTopPanel1);
        topPanel.add(tempTopPanel2);

        middlePanel.setBackground(new Color(192, 192, 192));
        middlePanel.setLayout(new GridLayout(3, 1));
        JPanel tempMiddlePanel1 = new JPanel();
        tempMiddlePanel1.setBackground(new Color(192, 192, 192));
        tempMiddlePanel1.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        tempMiddlePanel1.add(caseLabel);
        tempMiddlePanel1.add(amountComboBox);
        tempMiddlePanel1.add(caseLabelEnd);
        JPanel tempMiddlePanel2 = new JPanel();
        tempMiddlePanel2.setBackground(new Color(192, 192, 192));
        tempMiddlePanel2.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        tempMiddlePanel2.add(supplierLabel);
        tempMiddlePanel2.add(supplierComboBox);
        JPanel tempMiddlePanel3 = new JPanel();
        tempMiddlePanel3.setBackground(new Color(192, 192, 192));
        tempMiddlePanel3.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        tempMiddlePanel3.add(priceLabel);
        purchasedPriceTextField.setPreferredSize(new Dimension(150, 25));
        tempMiddlePanel3.add(purchasedPriceTextField);
        middlePanel.add(tempMiddlePanel1);
        middlePanel.add(tempMiddlePanel2);
        middlePanel.add(tempMiddlePanel3);

        bottomPanel.setBackground(new Color(192, 192, 192));
        bottomPanel.setLayout(new GridLayout(3,1));
        JPanel tempBottomPanel1 = new JPanel();
        tempBottomPanel1.setBackground(new Color(192, 192, 192));
        successLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tempBottomPanel1.add(successLabel);
        JPanel tempBottomPanel2 = new JPanel();
        tempBottomPanel2.setBackground(new Color(192, 192, 192));
        submitButton.setHorizontalAlignment(SwingConstants.CENTER);
        tempBottomPanel2.add(submitButton);
        JPanel tempBottomPanel3 = new JPanel();
        tempBottomPanel3.setBackground(new Color(192, 192, 192));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tempBottomPanel3.add(messageLabel);
        bottomPanel.add(tempBottomPanel1);
        bottomPanel.add(tempBottomPanel2);
        bottomPanel.add(tempBottomPanel3);

        middlePanel.setVisible(false);

        stage1Panel.setLayout(new GridLayout(3, 1));
        stage1Panel.setBackground(new Color(192, 192, 192));
        stage1Panel.add(topPanel);
        stage1Panel.add(middlePanel);
        stage1Panel.add(bottomPanel);
        stage1Panel.setVisible(true);
    }

    private void setStage2Panel() {
        //Stage2
        JPanel tempStage2Panel = new JPanel(new GridLayout(2, 1));
        tempStage2Panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        tempStage2Panel.setPreferredSize(new Dimension(500, 50));
        supplierOrderComboBox.setPreferredSize(new Dimension(250, 25));
        filterButton.setPreferredSize(new Dimension(150, 25));
        tempStage2Panel.add(supplierOrderComboBox);
        tempStage2Panel.add(filterButton);
        JScrollPane tablePanel = new JScrollPane(ordersTable);
        tablePanel.setPreferredSize(new Dimension(450, 450));
        stage2Panel.add(tempStage2Panel);
        stage2Panel.add(tablePanel);
    }

    private void setStage4Panel() {
        //Stage4
        stage4Panel.setBackground(new Color(150, 150, 150));
        stage4Panel.setLayout(new BorderLayout());
        JPanel inventoryHeaderPanel = new JPanel();
        inventoryHeaderPanel.setBackground(new Color(179, 45, 0));
        inventoryHeaderPanel.add(inventoryLabel);
        JPanel inventoryPanel = new JPanel();
        inventoryPanel.setLayout(new GridLayout(4, 2, 0, 0));
        inventoryPanel.setBackground(new Color(200, 200, 200));
        inventoryPanel.add(merlotLabel);
        inventoryPanel.add(roseLabel);
        inventoryPanel.add(merlotAmountLabel);
        inventoryPanel.add(roseAmountLabel);
        inventoryPanel.add(sauvignonLabel);
        inventoryPanel.add(totalLabel);
        inventoryPanel.add(sauvignonAmountLabel);
        inventoryPanel.add(totalAmountLabel);

        stage4Panel.add(inventoryHeaderPanel, BorderLayout.NORTH);
        stage4Panel.add(inventoryPanel, BorderLayout.CENTER);
    }

    private void setStage5Panel() {
        //Stage5
        stage5Panel.setBackground(new Color(200, 200, 200));
        stage5Panel.setLayout(new GridLayout(3,1));

        posTopPanel.setBackground(new Color(179, 45, 0));
        posTopPanel.setLayout(new GridLayout(2,1));

        JPanel tempInstructionPanel = new JPanel();
        tempInstructionPanel.setLayout(new FlowLayout(FlowLayout.CENTER,0, 10));
        tempInstructionPanel.setBackground(new Color(179, 45, 0));
        tempInstructionPanel.add(posInstructionLabel);
        posTopPanel.add(tempInstructionPanel);

        JPanel tempTypePanel = new JPanel();
        tempTypePanel.setBackground(new Color(179, 45, 0));
        tempTypePanel.setLayout(new FlowLayout(FlowLayout.CENTER,100, 10));
        tempTypePanel.add(singleTypeOrderButton);
        tempTypePanel.add(mixedTypeOrderButton);
        posTopPanel.add(tempTypePanel);

        posMidPanel.setBackground(new Color(200, 200, 200));
        posMidPanel.setLayout(new BorderLayout());

        singleTypePanel.setBackground(new Color(200, 200, 200));
        singleTypePanel.setLayout(new BoxLayout(singleTypePanel, BoxLayout.PAGE_AXIS));
        JPanel tempSingle1 = new JPanel();
        JPanel tempSingle2 = new JPanel();
        JPanel tempSingle3 = new JPanel();
        JPanel[] tempSingles = new JPanel[] {tempSingle1, tempSingle2, tempSingle3};

        for (JPanel tempSingle: tempSingles) {
            tempSingle.setBackground(new Color(200, 200, 200));
        }

        tempSingle1.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        tempSingle2.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        tempSingle3.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));

        tempSingle1.add(singleWineSelectionLabel);
        tempSingle2.add(singleWineKindBox);
        tempSingle3.add(singleConfirmButton);
        singleTypePanel.add(tempSingle1);
        singleTypePanel.add(tempSingle2);
        singleTypePanel.add(tempSingle3);
        singleTypePanel.setVisible(false);

        mixedTypePanel.setBackground(new Color(200, 200, 200));
        mixedTypePanel.setLayout(new BoxLayout(mixedTypePanel, BoxLayout.PAGE_AXIS));
        JPanel tempMixed1 = new JPanel();
        JPanel tempMixed2 = new JPanel();
        JPanel tempMixed3 = new JPanel();
        JPanel tempMixed4 = new JPanel();
        JPanel tempMixed5 = new JPanel();

        JPanel[] tempMixeds = new JPanel[] {tempMixed1, tempMixed2, tempMixed3, tempMixed4, tempMixed5};

        int counter = 0;

        for (JPanel tempMixed: tempMixeds) {
            tempMixed.setBackground(new Color(200, 200, 200));
            if (counter < 4) {
                tempMixed.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
            } else {
                tempMixed.setLayout(new FlowLayout(FlowLayout.CENTER,0,20));
            }
            mixedTypePanel.add(tempMixed);
            counter++;
        }

        tempMixed1.add(mixedWineSelectionLabel1);
        tempMixed2.add(mixedWineComboBox1);
        tempMixed3.add(mixedWineSelectionLabel2);
        tempMixed4.add(mixedWineComboBox2);
        tempMixed5.add(mixedConfirmButton);
        mixedTypePanel.add(tempMixed1);
        mixedTypePanel.add(tempMixed2);
        mixedTypePanel.add(tempMixed3);
        mixedTypePanel.add(tempMixed4);
        mixedTypePanel.add(tempMixed5);
        mixedTypePanel.setVisible(false);

        JPanel midPanel = new JPanel();
        midPanel.setBackground(new Color(200, 200, 200));

        midPanel.add(singleTypePanel);
        midPanel.add(mixedTypePanel);

        JPanel saleMessagePanel = new JPanel();
        saleMessagePanel.setBackground(new Color(200, 200, 200));
        saleMessagePanel.add(saleMessageLabel);

        posMidPanel.add(midPanel, BorderLayout.CENTER);
        posMidPanel.add(saleMessagePanel, BorderLayout.SOUTH);

        posBottomPanel.setLayout(new BorderLayout());
        posBottomPanel.setBackground(new Color(200, 200, 200));

        JPanel bottomPanel1 = new JPanel();
        JPanel bottomPanel2 = new JPanel();
        JPanel bottomPanel3 = new JPanel();

        for (JPanel bottomPanel: new JPanel[] {bottomPanel1, bottomPanel2, bottomPanel3}) {
            bottomPanel.setBackground(new Color(200, 200, 200));
        }

        JPanel upperBottom = new JPanel();
        upperBottom.setBackground(new Color(200, 200, 200));
        upperBottom.setLayout(new FlowLayout(FlowLayout.CENTER, 80, 25));
        upperBottom.add(customerLabel);
        upperBottom.add(saleAmountLabel);

        JPanel midBottom = new JPanel();
        midBottom.setBackground(new Color(200, 200, 200));
        midBottom.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 25));
        customerNameField.setPreferredSize(new Dimension(150, 25));
        saleAmountField.setPreferredSize(new Dimension(150, 25));
        customerNameField.setEnabled(false);
        saleAmountField.setEnabled(false);
        midBottom.add(customerNameField);
        midBottom.add(saleAmountField);

        JPanel lowerBottom = new JPanel();
        lowerBottom.setBackground(new Color(200, 200, 200));
        lowerBottom.setLayout(new FlowLayout(FlowLayout.CENTER, 70, 25));
        submitOrderButton.setPreferredSize(new Dimension(100, 25));
        lowerBottom.add(loyaltyDiscountButton);
        lowerBottom.add(submitOrderButton);

        bottomPanel1.add(upperBottom);
        bottomPanel2.add(midBottom);
        bottomPanel3.add(lowerBottom);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(200, 200, 200));
        bottomPanel.setLayout(new GridLayout(3, 1));

        bottomPanel.add(bottomPanel1);
        bottomPanel.add(bottomPanel2);
        bottomPanel.add(bottomPanel3);

        JPanel successMessagePanel = new JPanel();
        successMessagePanel.setBackground(new Color(200, 200, 200));
        successMessagePanel.add(saleSuccessMessageLabel);

        posBottomPanel.add(bottomPanel, BorderLayout.CENTER);
        posBottomPanel.add(successMessagePanel, BorderLayout.SOUTH);

        stage5Panel.add(posTopPanel);
        stage5Panel.add(posMidPanel);
        stage5Panel.add(posBottomPanel);
    }

    private void setPanels() {
        setStage1Panel();
        setStage2Panel();
        setStage4Panel();
        setStage5Panel();

        orderWindows.add(stage1Panel, "Input Supplier Order");
        orderWindows.add(stage2Panel, "Supplier Orders List");
        orderWindows.add(stage4Panel, "Inventory");
        orderWindows.add(stage5Panel, "Customer Sale");

        add(orderWindows);
    }

    private void setJLabels() {
        JLabel[] jLabels = new JLabel[] {instructionLabel, caseLabel, caseLabelEnd, supplierLabel, priceLabel, successLabel, messageLabel, posInstructionLabel, saleMessageLabel, saleSuccessMessageLabel, singleWineSelectionLabel, mixedWineSelectionLabel1, mixedWineSelectionLabel2};

        for (JLabel label: jLabels) {
            label.setFont(new Font("Arial", Font.PLAIN, 18));

            if (label.getName() == "InstructionLabel" || label.getName() == "MessageLabel" || label.getName() == "SuccessLabel" || label.getName() == "PosInstructionLabel" || label.getName() == "SaleMessageLabel" || label.getName() == "SaleSuccessMessageLabel" || label.getName() == "SingleWine" || label.getName() == "MixedWine1" || label.getName() == "MixedWine2") {
                label.setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                label.setHorizontalAlignment(SwingConstants.LEFT);
            }
        }

        instructionLabel.setForeground(new Color(255, 240, 179));
        instructionLabel.setText("Select the type of wine:");

        caseLabel.setForeground(new Color(179, 45, 0));
        caseLabel.setText("Selected type: ");

        caseLabelEnd.setForeground(new Color(179, 45, 0));
        caseLabelEnd.setText("cases.");

        supplierLabel.setForeground(new Color(179, 45, 0));
        supplierLabel.setText("Supplier:");

        priceLabel.setForeground(new Color(179, 45, 0));
        priceLabel.setText("Purchased Price: £");

        successLabel.setForeground(new Color(0, 100, 0));
        successLabel.setText("Success!");
        successLabel.setVisible(false);

        messageLabel.setForeground(new Color(179, 45, 0));
        messageLabel.setVisible(false);

        //Stage4
        inventoryLabel.setForeground(new Color(255, 240, 179));
        inventoryLabel.setBackground(new Color(255, 240, 179));
        inventoryLabel.setFont(new Font("Arial", Font.PLAIN, 24));

        merlotLabel.setForeground(new Color(179, 45, 0));
        merlotAmountLabel.setForeground(new Color(179, 45, 0));

        roseLabel.setForeground(new Color(179, 0, 179));
        roseAmountLabel.setForeground(new Color(179, 0, 179));

        sauvignonLabel.setForeground(new Color(0, 102, 0));
        sauvignonAmountLabel.setForeground(new Color(0, 102, 0));

        totalLabel.setForeground(Color.BLACK);
        totalAmountLabel.setForeground(Color.black);

        for (JLabel nameLabel: inventoryNameLabels) {
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        }

        for (JLabel amountLabel: inventoryAmountLabels) {
            amountLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        }

        //Stage5
        posInstructionLabel.setForeground(new Color(255, 240, 179));
        singleWineSelectionLabel.setForeground(new Color(179, 45, 0));
        mixedWineSelectionLabel1.setForeground(new Color(179, 45, 0));
        mixedWineSelectionLabel2.setForeground(new Color(179, 45, 0));
        customerLabel.setForeground(new Color(179, 45, 0));
        customerLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        saleAmountLabel.setForeground(new Color(179, 45, 0));
        saleAmountLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        saleMessageLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        saleMessageLabel.setForeground(new Color(0, 100, 0));
        saleSuccessMessageLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        saleSuccessMessageLabel.setForeground(new Color(179, 45, 0));
    }

    private void setAllNames() {
        //Stage1
        merlotButton.setName("MerlotButton");
        roseButton.setName("RoseButton");
        sauvignonButton.setName("SauvignonButton");
        topPanel.setName("TopPanel");
        middlePanel.setName("MiddlePanel");
        bottomPanel.setName("BottomPanel");
        instructionLabel.setName("InstructionLabel");
        messageLabel.setName("MessageLabel");
        successLabel.setName("SuccessLabel");
        amountComboBox.setName("AmountComboBox");
        supplierComboBox.setName("SupplierComboBox");
        purchasedPriceTextField.setName("PurchasedPriceTextField");
        submitButton.setName("SubmitButton");

        //Stage2
        orderWindows.setName("TabbedPane");
        supplierOrderComboBox.setName("SupplierOrderComboBox");
        filterButton.setName("FilterButton");
        ordersTable.setName("OrdersTable");

        //Stage4
        merlotAmountLabel.setName("MerlotAmountLabel");
        roseAmountLabel.setName("RoseAmountLabel");
        sauvignonAmountLabel.setName("SauvignonAmountLabel");
        totalAmountLabel.setName("TotalAmountLabel");

        //Stage5
        posTopPanel.setName("PosTopPanel");
        posInstructionLabel.setName("PosInstructionLabel");
        singleTypeOrderButton.setName("SingleTypeOrderButton");
        mixedTypeOrderButton.setName("MixedTypeOrderButton");
        posMidPanel.setName("PosMidPanel");
        singleTypePanel.setName("SingleTypePanel");
        singleWineSelectionLabel.setName("SingleWine");
        singleWineKindBox.setName("SingleWineKindBox");
        singleConfirmButton.setName("SingleConfirmButton");
        mixedWineSelectionLabel1.setName("MixedWine1");
        mixedWineSelectionLabel2.setName("MixedWine2");
        mixedTypePanel.setName("MixedTypePanel");
        mixedWineComboBox1.setName("MixedWineComboBox1");
        mixedWineComboBox2.setName("MixedWineComboBox2");
        mixedConfirmButton.setName("MixedConfirmButton");
        posBottomPanel.setName("PosBottomPanel");
        customerLabel.setName("CustomerLabel");
        saleAmountLabel.setName("SaleAmountLabel");
        customerNameField.setName("CustomerNameField");
        saleAmountField.setName("SaleAmountField");
        loyaltyDiscountButton.setName("LoyaltyDiscountButton");
        submitOrderButton.setName("SubmitOrderButton");
        saleMessageLabel.setName("SaleMessageLabel");
        saleSuccessMessageLabel.setName("SaleSuccessMessageLabel");
    }

    private void setTables() {
        helper.setTableModel("All Orders");
        ordersTable = new JTable(helper.getTableModel());
        ordersTable.setName("OrdersTable");
    }

    private void setWineAmountLabels() {
        Map wineAmounts = helper.getWineAmounts();

        int merlotAmount = (int) wineAmounts.get("Merlot");
        int roseAmount = (int) wineAmounts.get("Rose");
        int sauvignonAmount = (int) wineAmounts.get("Sauvignon");
        int totalAmount = (int) wineAmounts.get("Total");

        merlotAmountLabel.setText(String.format("%d", merlotAmount));
        roseAmountLabel.setText(String.format("%d", roseAmount));
        sauvignonAmountLabel.setText(String.format("%d", sauvignonAmount));
        totalAmountLabel.setText(String.format("%d", totalAmount));
    }

    private void setAllComponents() {
        setTables();
        setComboBoxes();
        setJLabels();
        setPanels();
        setWineAmountLabels();
    }
}
