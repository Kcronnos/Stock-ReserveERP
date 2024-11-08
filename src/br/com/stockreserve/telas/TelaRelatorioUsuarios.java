/*
 * The MIT License
 *
 * Copyright 2024 leog4.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package br.com.stockreserve.telas;

import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;
import br.com.stockreserve.dal.ModuloConexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author leog4
 */
public class TelaRelatorioUsuarios extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    ResourceBundle bundle;

    /**
     * Creates new form TelaRelatorioSalarios
     */
    public TelaRelatorioUsuarios() {
        Locale locale;
        if (LanguageSelection.selectedLanguage) {
            locale = Locale.of("en", "US");
        } else {
            locale = Locale.of("pt", "BR");
        }
        bundle = ResourceBundle.getBundle("br.com.stockreserve.erp", locale);
        initComponents();
        conexao = ModuloConexao.conector();
        preencherTabelaRelatorioSalarios();
    }

    private void preencherTabelaRelatorioSalarios() {
        // SQL que inclui uma subconsulta para calcular a média de avaliações para cada vendedor
        String sql;
        if (LanguageSelection.selectedLanguage) {
            sql = "SELECT s.iduser AS ID, u.nome AS "+bundle.getString("seller")+", s.salario / 5.78 AS "+bundle.getString("base_salary")+", "
                + "s.comissao / 5.78 AS "+bundle.getString("commission")+", s.total /5.78 AS TOTAL, s.datacomissao AS "+bundle.getString("date")+", "
                + "(SELECT AVG(avaliacao) FROM tbusuarios_avaliacoes WHERE iduser = s.iduser) AS AVERAGE_RATING " //por algum motivo, se eu coloco o bundle.getString("average_rating") ele dá erro, então deixei em inglês mesmo
                + "FROM tblsalarios s "
                + "JOIN tbusuarios u ON s.iduser = u.iduser";
        } else {
            sql = "SELECT s.iduser AS ID, u.nome AS "+bundle.getString("seller")+", s.salario AS "+bundle.getString("base_salary")+", "
                + "s.comissao AS "+bundle.getString("commission")+", s.total AS TOTAL, s.datacomissao AS "+bundle.getString("date")+", "
                + "(SELECT AVG(avaliacao) FROM tbusuarios_avaliacoes WHERE iduser = s.iduser) AS "+bundle.getString("average_rating")+" "
                + "FROM tblsalarios s "
                + "JOIN tbusuarios u ON s.iduser = u.iduser";
        }
        

        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();

            // Configura o modelo da tabela incluindo a coluna de média de avaliações
            DefaultTableModel model = new DefaultTableModel(new Object[][]{}, new String[]{
                "ID", bundle.getString("seller"), bundle.getString("base_salary"), bundle.getString("commission"), "TOTAL", bundle.getString("date"), bundle.getString("average_rating")
            });

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("ID"),
                    rs.getString(bundle.getString("seller")),
                    rs.getDouble(bundle.getString("base_salary")),
                    rs.getDouble(bundle.getString("commission")),
                    rs.getDouble("TOTAL"),
                    rs.getDate(bundle.getString("date")),
                    rs.getDouble(bundle.getString("average_rating")) // Adiciona a média de avaliações
                });
            }

            tblSalarios.setModel(model); // Define o modelo na JTable

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, bundle.getString("error")+": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void pesquisarSalarios() {
        String sql;
        if (LanguageSelection.selectedLanguage) {
            sql = "SELECT s.iduser AS ID, u.nome AS "+bundle.getString("seller")+", s.salario / 5.78 AS "+bundle.getString("base_salary")+", "
                + "s.comissao / 5.78 AS "+bundle.getString("commission")+", s.total /5.78 AS TOTAL, s.datacomissao AS "+bundle.getString("date")+", "
                + "(SELECT AVG(avaliacao) FROM tbusuarios_avaliacoes WHERE iduser = s.iduser) AS AVeRAGE_RATING " ///por algum motivo, se eu coloco o bundle.getString("average_rating") ele dá erro, então deixei em inglês mesmo
                + "FROM tblsalarios s "
                + "JOIN tbusuarios u ON s.iduser = u.iduser "
                + "WHERE (s.iduser = ? OR u.nome LIKE ?)";
        } else {
        sql = "SELECT s.iduser AS ID, u.nome AS "+bundle.getString("seller")+", s.salario AS "+bundle.getString("base_salary")+", "
               + "s.comissao AS "+bundle.getString("commission")+", s.total AS TOTAL, s.datacomissao AS "+bundle.getString("date")+", "
               + "(SELECT AVG(avaliacao) FROM tbusuarios_avaliacoes WHERE iduser = s.iduser) AS "+bundle.getString("average_rating")+" "
               + "FROM tblsalarios s "
               + "JOIN tbusuarios u ON s.iduser = u.iduser "
               + "WHERE (s.iduser = ? OR u.nome LIKE ?)";
        }

        try {
            pst = conexao.prepareStatement(sql);

            // Obtendo o termo de busca dos campos de texto
            String searchTerm = txtBuscarSalarios.getText() + "%";
            pst.setString(1, searchTerm);
            pst.setString(2, searchTerm);

            // Executando a consulta
            rs = pst.executeQuery();

            // A linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela
            tblSalarios.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
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
        tblSalarios = new javax.swing.JTable();
        txtBuscarSalarios = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Relatório de Usuários");
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
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        tblSalarios = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int ColIndex){
                return false;
            }
        };
        tblSalarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "VENDEDOR", "SALARIO BASE", "COMISSÃO", "TOTAL", "DATA", "AVALIAÇÃO"
            }
        ));
        jScrollPane1.setViewportView(tblSalarios);

        txtBuscarSalarios.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarSalariosKeyReleased(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("BUSCAR");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 25)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("RELATÓRIO USUÁRIOS");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGap(232, 232, 232)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtBuscarSalarios, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 897, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtBuscarSalarios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 408, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        // TODO add your handling code here:
        preencherTabelaRelatorioSalarios();
    }//GEN-LAST:event_formInternalFrameOpened

    private void txtBuscarSalariosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarSalariosKeyReleased
        // TODO add your handling code here:
        pesquisarSalarios();
    }//GEN-LAST:event_txtBuscarSalariosKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblSalarios;
    private javax.swing.JTextField txtBuscarSalarios;
    // End of variables declaration//GEN-END:variables
}
