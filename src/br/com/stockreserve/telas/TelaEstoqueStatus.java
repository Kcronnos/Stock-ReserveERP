/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.stockreserve.telas;

import br.com.stockreserve.dal.ModuloConexao;
import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.lang.model.util.ElementFilter;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Felipe
 */
public class TelaEstoqueStatus extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    ResourceBundle bundle;

    /**
     * Creates new form TelaEstoqueStatus
     */
    public TelaEstoqueStatus() {
        Locale locale = new Locale("en", "US");z
        bundle = ResourceBundle.getBundle("br.com.stockreserve.erp", locale);

        initComponents();
        conexao = ModuloConexao.conector();
    }

    // Método para verificar o estoque e emitir alertas quando necessário
    public void verificarEstoque() {
        String sql = """
        SELECT idproduto AS ID, 
               nomeproduto AS NOME, 
               preco AS PREÇO, 
               quantidade AS QUANTIDADE, 
               limite_minimo AS LIMITE_MÍNIMO, 
               vencimento AS VENCIMENTO,
               CASE 
                   WHEN quantidade = 0 THEN 'Vazio' 
                   WHEN quantidade < limite_minimo THEN 'Precisa Abastecer' 
                   ELSE 'OK' 
               END AS STATUS
        FROM tbprodutos;
        """;

        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tblEstoStatus.setModel(DbUtils.resultSetToTableModel(rs));

            // Aplica o renderizador colorido à coluna `status`
            tblEstoStatus.getColumnModel().getColumn(tblEstoStatus.getColumnCount() - 1)
                    .setCellRenderer(new StatusCellRenderer());

            // Aplica o renderizador colorido à coluna `VENCIMENTO`
            int vencimentoColIndex = tblEstoStatus.getColumnModel().getColumnIndex("VENCIMENTO");
            tblEstoStatus.getColumnModel().getColumn(vencimentoColIndex).setCellRenderer(new VencimentoCellRenderer());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // Renderizador personalizado para a coluna `status`
    private static class StatusCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Aplica cores com base no valor do `status`
            if (value != null) {
                switch (value.toString()) {
                    case "Vazio" ->
                        cell.setBackground(Color.RED);
                    case "Precisa Abastecer" ->
                        cell.setBackground(Color.YELLOW);
                    case "OK" ->
                        cell.setBackground(Color.GREEN);
                    default ->
                        cell.setBackground(Color.WHITE);
                }
            } else {
                cell.setBackground(Color.WHITE);
            }

            return cell;
        }
    }

    // Renderizador personalizado para a coluna `VENCIMENTO`
    private static class VencimentoCellRenderer extends DefaultTableCellRenderer {

        private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                try {
                    Date vencimentoDate = DATE_FORMAT.parse(value.toString());
                    Date currentDate = new Date();
                    long diffInMillis = vencimentoDate.getTime() - currentDate.getTime();
                    long daysUntilVencimento = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

                    if (daysUntilVencimento < 0) {
                        cell.setBackground(Color.RED); // Vencido
                    } else if (daysUntilVencimento <= 30) {
                        cell.setBackground(Color.YELLOW); // Faltam 30 dias ou menos
                    } else {
                        cell.setBackground(Color.GREEN); // Mais de 30 dias
                    }
                } catch (Exception e) {
                    cell.setBackground(Color.WHITE); // Caso a data não seja válida ou seja nula
                }
            } else {
                cell.setBackground(Color.WHITE); // Sem data de vencimento
            }

            return cell;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblEstoStatus = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setTitle(bundle.getString("stock_status"));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameDeiconified(evt);
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        tblEstoStatus = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int ColIndex){
                return false;
            }
        };
        tblEstoStatus.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", bundle.getString("name"), bundle.getString("price"), bundle.getString("amount"), bundle.getString("min_limit"), bundle.getString("maturity"), "STATUS"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblEstoStatus);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 4, Short.MAX_VALUE))
        );

        setBounds(279, 306, 721, 325);
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        //Chamando o método verificar estoque
        verificarEstoque();
    }//GEN-LAST:event_formInternalFrameOpened

    private void formInternalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameDeiconified
        //Chamando o método verificar estoque
        verificarEstoque();
    }//GEN-LAST:event_formInternalFrameDeiconified


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblEstoStatus;
    // End of variables declaration//GEN-END:variables
}
