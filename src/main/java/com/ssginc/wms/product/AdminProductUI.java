package com.ssginc.wms.product;

import com.ssginc.wms.frame.AdminFrame;
import com.ssginc.wms.user.LoginFrameUI;
import com.ssginc.wms.product.ProductDAO;
import com.ssginc.wms.product.ProductVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class AdminProductUI extends AdminFrame {
    private JTable productTable;
    private JTextField searchField;
    private JLabel welcomeLabel;
    private JComboBox<String> categoryComboBox;
    private DefaultTableModel tableModel;
    private ProductDAO productDAO;
    Color color = new Color(0x000000);
    Color color2 = new Color(0xFCEA8B9B, true);
    Color color3 = new Color(0xD0C86B);

    public AdminProductUI(String id) {
        super(id);
        this.productDAO = new ProductDAO();
        Font fontC = new Font("맑은 고딕", Font.BOLD, 12);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        categoryComboBox = new JComboBox<>(new String[]

                {
                        "전체", "상품 코드", "상품 이름", "주문 가격", "공급 가격", "재고 수량", "카테고리 코드"
                });
        searchField = new

                JTextField(15);

        JButton searchButton = new JButton("검색");
        filterPanel.add(categoryComboBox);
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        centerPanel.add(filterPanel, BorderLayout.NORTH);

        // 테이블 설정
        tableModel = new

                DefaultTableModel(new String[]{
                "상품 코드", "상품 이름", "주문 가격", "공급 가격", "재고 수량", "카테고리 코드"
        }, 0);
        productTable = new

                JTable(tableModel);

        JScrollPane tableScrollPane = new JScrollPane(productTable);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("상품목록 추가");
        JButton updateButton = new JButton("상품목록 수정");
        JButton deleteButton = new JButton("상품목록 삭제");
        bottomPanel.add(addButton);
        addButton.setFont(fontC);
        bottomPanel.add(updateButton);
        updateButton.setFont(fontC);
        bottomPanel.add(deleteButton);
        deleteButton.setFont(fontC);

        add(bottomPanel, BorderLayout.SOUTH);

        // Event Listeners
        searchButton.addActionListener(e ->

                loadProductData((String) categoryComboBox.

                        getSelectedItem(), searchField.

                        getText()));

        addButton.addActionListener(e -> InsertProductFrame(this));

        updateButton.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "수정할 행을 선택해주세요.");
                return;
            }

            // 선택된 row에서 productId 가져오기
            int productId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            ProductVO selectedProduct = productDAO.getProductById(productId);

            if (selectedProduct == null) {
                JOptionPane.showMessageDialog(this, "선택한 상품 정보를 불러올 수 없습니다.");
                return;
            }

            // 수정 창 띄우기
            UpdateProductFrame(this, selectedProduct);
        });

        deleteButton.addActionListener(this::deleteSelectedRows);

        loadProductData("전체", "");

        setLocationRelativeTo(null);

        setVisible(true);
    }

    public void loadProductData(String selectedColumn, String searchKeyword) {
        tableModel.setRowCount(0);

        String columnName = switch (selectedColumn) {
            case "상품 코드" -> "product_id";
            case "상품 이름" -> "product_name";
            case "주문 가격" -> "ord_price";
            case "공급 가격" -> "supply_price";
            case "재고 수량" -> "product_amount";
            case "카테고리 코드" -> "category_id";
            default -> null;
        };

        List<ProductVO> products = productDAO.getProducts(columnName, searchKeyword);
        for (ProductVO product : products) {
            tableModel.addRow(new Object[]{
                    ProductService.encodeProductId(product.getProductId()),
                    product.getProductName(),
                    product.getOrderPrice(),
                    product.getSupplyPrice(),
                    product.getProductAmount(),
                    ProductService.encodeCategoryId(product.getCategoryId())
            });
        }
    }

    private void deleteSelectedRows(ActionEvent e) {
        int[] selectedRows = productTable.getSelectedRows();

        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "삭제할 행을 선택해주세요.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "선택한 항목을 삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        for (int i = selectedRows.length - 1; i >= 0; i--) {
            int productId = Integer.parseInt(tableModel.getValueAt(selectedRows[i], 0).toString());
            if (productDAO.deleteProduct(productId)) {
                tableModel.removeRow(selectedRows[i]);
            } else {
                JOptionPane.showMessageDialog(this, "상품 삭제 중 오류가 발생했습니다.");
            }
        }
    }

    // InsertProductFrame class (상품 등록)
    public void InsertProductFrame(AdminProductUI parentFrame) {
        JFrame insertFrame = new JFrame("상품 등록");
        insertFrame.setSize(450, 450);
        insertFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        insertFrame.setLayout(null);

        JLabel nameLabel = new JLabel("상  품    이  름:");
        JTextField nameField = new JTextField();
        nameLabel.setBounds(70, 50, 100, 30);
        nameField.setBounds(190, 50, 180, 30);

        JLabel ordPriceLabel = new JLabel("주  문    가  격:");
        JTextField ordPriceField = new JTextField();
        ordPriceLabel.setBounds(70, 100, 100, 30);
        ordPriceField.setBounds(190, 100, 180, 30);

        JLabel supplyPriceLabel = new JLabel("공  급    가  격:");
        JTextField supplyPriceField = new JTextField();
        supplyPriceLabel.setBounds(70, 150, 100, 30);
        supplyPriceField.setBounds(190, 150, 180, 30);

        JLabel quantityLabel = new JLabel("재  고    수  량:");
        JTextField quantityField = new JTextField();
        quantityLabel.setBounds(70, 200, 100, 30);
        quantityField.setBounds(190, 200, 180, 30);

        JLabel categoryLabel = new JLabel("카테고리 코드:");
        JTextField categoryField = new JTextField();
        categoryLabel.setBounds(70, 250, 100, 30);
        categoryField.setBounds(190, 250, 180, 30);

        JButton saveButton = new JButton("등록");
        saveButton.setBounds(150, 300, 100, 40);

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                int ordPrice = Integer.parseInt(ordPriceField.getText());
                int supplyPrice = Integer.parseInt(supplyPriceField.getText());
                int quantity = Integer.parseInt(quantityField.getText());
                int category = Integer.parseInt(categoryField.getText());

                ProductVO product = new ProductVO();
                product.setProductName(name);
                product.setOrderPrice(ordPrice);
                product.setSupplyPrice(supplyPrice);
                product.setProductAmount(quantity);
                product.setCategoryId(category);

                if (productDAO.insertProduct(product)) {
                    JOptionPane.showMessageDialog(insertFrame, "상품이 성공적으로 추가되었습니다.");
                    parentFrame.loadProductData("전체", "");
                    insertFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(insertFrame, "상품 추가에 실패했습니다.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(insertFrame, "올바른 숫자 값을 입력해주세요.");
            }
        });

        insertFrame.add(nameLabel);
        insertFrame.add(nameField);
        insertFrame.add(ordPriceLabel);
        insertFrame.add(ordPriceField);
        insertFrame.add(supplyPriceLabel);
        insertFrame.add(supplyPriceField);
        insertFrame.add(quantityLabel);
        insertFrame.add(quantityField);
        insertFrame.add(categoryLabel);
        insertFrame.add(categoryField);
        insertFrame.add(saveButton);

        insertFrame.setLocationRelativeTo(parentFrame);
        insertFrame.setVisible(true);
    }

    // UpdateProductFrame 수정
    public void UpdateProductFrame(AdminProductUI parentFrame, ProductVO product) {
        JFrame updateFrame = new JFrame("상품 수정");
        updateFrame.setSize(450, 450);
        updateFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        updateFrame.setLayout(null);

        JLabel nameLabel = new JLabel("상  품    이  름:");
        JTextField nameField = new JTextField(product.getProductName());
        nameLabel.setBounds(70, 50, 100, 30);
        nameField.setBounds(190, 50, 180, 30);

        JLabel ordPriceLabel = new JLabel("주  문    가  격:");
        JTextField ordPriceField = new JTextField(String.valueOf(product.getOrderPrice()));
        ordPriceLabel.setBounds(70, 100, 100, 30);
        ordPriceField.setBounds(190, 100, 180, 30);

        JLabel supplyPriceLabel = new JLabel("공  급    가  격:");
        JTextField supplyPriceField = new JTextField(String.valueOf(product.getSupplyPrice()));
        supplyPriceLabel.setBounds(70, 150, 100, 30);
        supplyPriceField.setBounds(190, 150, 180, 30);

        JLabel quantityLabel = new JLabel("재  고    수  량:");
        JTextField quantityField = new JTextField(String.valueOf(product.getProductAmount()));
        quantityLabel.setBounds(70, 200, 100, 30);
        quantityField.setBounds(190, 200, 180, 30);

        JLabel categoryLabel = new JLabel("카테고리 코드:");
        JTextField categoryField = new JTextField(String.valueOf(product.getCategoryId()));
        categoryLabel.setBounds(70, 250, 100, 30);
        categoryField.setBounds(190, 250, 180, 30);

        JButton saveButton = new JButton("수 정");
        saveButton.setBounds(150, 300, 100, 40);

        saveButton.addActionListener(e -> {
            try {
                product.setProductName(nameField.getText());
                product.setOrderPrice(Integer.parseInt(ordPriceField.getText()));
                product.setSupplyPrice(Integer.parseInt(supplyPriceField.getText()));
                product.setProductAmount(Integer.parseInt(quantityField.getText()));
                product.setCategoryId(Integer.parseInt(categoryField.getText()));

                if (productDAO.updateProduct(product)) {
                    JOptionPane.showMessageDialog(updateFrame, "상품이 성공적으로 수정되었습니다.");
                    parentFrame.loadProductData("전체", "");
                    updateFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(updateFrame, "상품 수정에 실패했습니다.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(updateFrame, "올바른 숫자 값을 입력해주세요.");
            }
        });

        updateFrame.add(nameLabel);
        updateFrame.add(nameField);
        updateFrame.add(ordPriceLabel);
        updateFrame.add(ordPriceField);
        updateFrame.add(supplyPriceLabel);
        updateFrame.add(supplyPriceField);
        updateFrame.add(quantityLabel);
        updateFrame.add(quantityField);
        updateFrame.add(categoryLabel);
        updateFrame.add(categoryField);
        updateFrame.add(saveButton);

        updateFrame.setLocationRelativeTo(parentFrame);
        updateFrame.setVisible(true);
    }
}