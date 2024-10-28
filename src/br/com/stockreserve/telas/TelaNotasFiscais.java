/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.stockreserve.telas;

import br.com.stockreserve.dal.JsonUtil;
import br.com.stockreserve.dal.ModuloConexao;
import br.com.stockreserve.dal.Produto;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import org.json.simple.parser.ParseException;

/**
 *
 * @author leog4
 */
public class TelaNotasFiscais extends javax.swing.JInternalFrame {
    
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form TelaNotasFiscais
     */
    public TelaNotasFiscais() {
        initComponents();
        conexao = ModuloConexao.conector();
    }
    
    //Método para preencher a tabela ao abrir a aba de notas fiscais
    private void preencherTabelaNotasFiscais() {
        String sql = "SELECT idnotafiscal AS ID_NOTA, nomevendedor AS VENDEDOR, nomecliente AS CLIENTE, valor AS VALOR, datacompra AS `DATA/HORA` " +
                 "FROM tbnotasfiscais ";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tblNotasFiscais.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    //Método para preencher a tabela ao pesquisar nota
    private void pesquisarNota() {
        String sql = "SELECT idnotafiscal AS ID_NOTA, nomevendedor AS VENDEDOR, nomecliente AS CLIENTE, valor AS VALOR, datacompra AS `DATA/HORA` " +
                 "FROM tbnotasfiscais " +
                 "WHERE idnotafiscal LIKE ? OR nomevendedor LIKE ? OR nomecliente LIKE ?";
        try {
            pst = conexao.prepareStatement(sql);
            String searchTerm = txtBuscadorDeNotas.getText() + "%"; // Adiciona o curinga para LIKE
            pst.setString(1, searchTerm);
            pst.setString(2, searchTerm);
            pst.setString(3, searchTerm);
        
            rs = pst.executeQuery();

            // A linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela
            tblNotasFiscais.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    // Método para mostrar detalhes da nota fiscal
private void mostrarDetalhesNotaFiscal() throws SQLException, ParseException {
    String sql = "SELECT * FROM tbnotasfiscais WHERE idnotafiscal = ?";
    String sql2 = "SELECT produtos FROM tbnotasfiscais WHERE idnotafiscal = ?";
    
    int selectedRow = tblNotasFiscais.getSelectedRow();
    if (selectedRow >= 0) {
        // Obtendo o ID da nota fiscal selecionada
        String idNota = (String) tblNotasFiscais.getValueAt(selectedRow, 0);

        try (
            PreparedStatement pst1 = conexao.prepareStatement(sql);
            PreparedStatement pst2 = conexao.prepareStatement(sql2)
        ) {
            // Consulta principal
            pst1.setString(1, idNota);
            try (ResultSet rs1 = pst1.executeQuery()) {
                if (rs1.next()) {
                    // Criando um JFrame para mostrar os detalhes
                    JFrame detalhesFrame = new JFrame("Detalhes da Nota Fiscal");
                    detalhesFrame.setSize(600, 400);
                    detalhesFrame.setLayout(new BorderLayout());

                    // Painel para organizar o conteúdo
                    JPanel panelDetalhes = new JPanel(new GridBagLayout());
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(5, 5, 5, 5);
                    gbc.anchor = GridBagConstraints.WEST;

                    // Adicionando informações da nota fiscal
                    adicionarLabel(panelDetalhes, "ID Nota:", tblNotasFiscais.getValueAt(selectedRow, 0).toString(), 0, gbc);
                    adicionarLabel(panelDetalhes, "Vendedor:", tblNotasFiscais.getValueAt(selectedRow, 1).toString(), 1, gbc);
                    adicionarLabel(panelDetalhes, "Cliente:", tblNotasFiscais.getValueAt(selectedRow, 2).toString(), 2, gbc);
                    adicionarLabel(panelDetalhes, "Valor:", String.valueOf(tblNotasFiscais.getValueAt(selectedRow, 3)), 3, gbc);
                    adicionarLabel(panelDetalhes, "Data/Hora:", tblNotasFiscais.getValueAt(selectedRow, 4).toString(), 4, gbc);

                    // Adicionando a lista de produtos
                    gbc.gridy = 5;
                    gbc.gridwidth = 2;
                    gbc.anchor = GridBagConstraints.CENTER;
                    panelDetalhes.add(new JLabel("Produtos:"), gbc);

                    // Configuração da tabela de produtos
                    String[] colunas = {"ID", "Nome", "Preço", "Quantidade"};
                    DefaultTableModel produtoTableModel = new DefaultTableModel(colunas, 0);
                    JTable tableProdutos = new JTable(produtoTableModel);

                    // Consulta para obter os produtos
                    pst2.setString(1, idNota);
                    try (ResultSet rs2 = pst2.executeQuery()) {
                        if (rs2.next()) {
                            String jsonProdutos = rs2.getString("produtos");
                            List<Produto> produtos = JsonUtil.jsonParaProdutos(jsonProdutos);

                            // Adicionando produtos à tabela
                            for (Produto produto : produtos) {
                                produtoTableModel.addRow(new Object[]{
                                    produto.getId(),
                                    produto.getNome(),
                                    produto.getPreco(),
                                    produto.getQuantidade()
                                });
                            }
                        }
                    }

                    JScrollPane scrollPaneProdutos = new JScrollPane(tableProdutos);
                    gbc.gridy = 6;
                    gbc.fill = GridBagConstraints.BOTH;
                    gbc.weightx = 1.0;
                    gbc.weighty = 1.0;
                    panelDetalhes.add(scrollPaneProdutos, gbc);

                    detalhesFrame.add(panelDetalhes, BorderLayout.CENTER);
                    detalhesFrame.setLocationRelativeTo(null);
                    detalhesFrame.setVisible(true);

                } else {
                    JOptionPane.showMessageDialog(this, "Nota Fiscal não encontrada!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao buscar detalhes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(this, "Nenhuma nota fiscal selecionada!", "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}

// Método auxiliar para adicionar JLabel ao painel
private void adicionarLabel(JPanel panel, String labelText, String valueText, int row, GridBagConstraints gbc) {
    gbc.gridx = 0;
    gbc.gridy = row;
    panel.add(new JLabel(labelText), gbc);

    gbc.gridx = 1;
    panel.add(new JLabel(valueText), gbc);
}
    
    // Método para habilitar ou desabilitar o botão ver deltalhes
    private void verificarSelecaoTabela() {
        int linhaSelecionada = tblNotasFiscais.getSelectedRow();
        btnVerDetalhes.setEnabled(linhaSelecionada != -1); // Habilita o botão se houver uma linha selecionada
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblNotasFiscais = new javax.swing.JTable();
        txtBuscadorDeNotas = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnVerDetalhes = new javax.swing.JButton();

        setBackground(new java.awt.Color(204, 255, 204));
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setPreferredSize(new java.awt.Dimension(1000, 630));
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
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("MingLiU_HKSCS-ExtB", 0, 25)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("NOTAS FISCAIS");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 0, 220, 110));

        tblNotasFiscais.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID NOTA", "VENDEDOR", "CLIENTE", "VALOR", "DATA/HORA"
            }
        ));
        jScrollPane1.setViewportView(tblNotasFiscais);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 80, 900, -1));

        txtBuscadorDeNotas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscadorDeNotasKeyReleased(evt);
            }
        });
        getContentPane().add(txtBuscadorDeNotas, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 550, 370, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("BUSCAR NOTAS");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 520, 370, -1));

        btnVerDetalhes.setText("VER DETALHES");
        btnVerDetalhes.setEnabled(false);
        btnVerDetalhes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerDetalhesActionPerformed(evt);
            }
        });
        getContentPane().add(btnVerDetalhes, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 530, 180, 50));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscadorDeNotasKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscadorDeNotasKeyReleased
        // TODO add your handling code here:
        pesquisarNota();
    }//GEN-LAST:event_txtBuscadorDeNotasKeyReleased

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        // TODO add your handling code here:
        preencherTabelaNotasFiscais();
        //chamando o método para ativar o botão de ver detalhes
        tblNotasFiscais.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                verificarSelecaoTabela(); // Atualiza o estado do botão
            }
        });
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnVerDetalhesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerDetalhesActionPerformed
        try {
            // TODO add your handling code here:
            mostrarDetalhesNotaFiscal();
        } catch (SQLException ex) {
            Logger.getLogger(TelaNotasFiscais.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(TelaNotasFiscais.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnVerDetalhesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnVerDetalhes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblNotasFiscais;
    private javax.swing.JTextField txtBuscadorDeNotas;
    // End of variables declaration//GEN-END:variables
}
