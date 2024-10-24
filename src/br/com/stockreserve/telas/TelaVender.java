/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.stockreserve.telas;

import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;
import java.sql.*;
import br.com.stockreserve.dal.ModuloConexao;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Felipe
 */
public class TelaVender extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    private int quantidadeEsto;

    /**
     * Creates new form TelaVender
     */
    public TelaVender() {
        initComponents();
        conexao = ModuloConexao.conector();
    }
    
    
    //Método para adicionar produtos ao carrinho
    private void adicionarCarrinho() {
        
        //pegando a quantidade em estoque do banco de dados e armazenando para comparar com a quantidade a ser comprada
        String sql = "select quantidade from tbprodutos where idproduto=?";
        try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtProduId.getText());
                rs = pst.executeQuery();
                if(rs.next()){
                quantidadeEsto = rs.getInt(1);
                }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
        //Tratamento para garantir que a quantidade a ser comprada não seja zero ou negativo
        int quantidade;
        if (txtProduQuanti.getText().isEmpty()) {
            quantidade = 0;
        } else {
            quantidade = Integer.parseInt(txtProduQuanti.getText());
        }
        
        //Condições para que um produto possa ser adicionada ao carrinho
        if (quantidade <= 0 || quantidadeEsto < quantidade) {
            JOptionPane.showMessageDialog(null, "Digite uma quantidade válida");
        } else {//Pegando as informações da tabela de produtos e passando para a do carrinho
                //Junto com a quantidade e o total
            int setar = tblProdutos.getSelectedRow();
            String idProduto = tblProdutos.getModel().getValueAt(setar, 0).toString();
            String nomeProduto = tblProdutos.getModel().getValueAt(setar, 1).toString();
            String precoProduto = tblProdutos.getModel().getValueAt(setar, 2).toString();
            
            //coerção de string para double para fazer o valor total
            double preco = Double.parseDouble(precoProduto);
            double total = preco * quantidade;

            DefaultTableModel modelo = (DefaultTableModel) tblCarrinho.getModel();
            modelo.addRow(new Object[]{idProduto, nomeProduto, preco, quantidade, total});
            
            //limpando o campo de texto da quantidade
            txtProduQuanti.setText(null);
            
            //atualizando a quantidade no banco de dados
            sql = "update tbprodutos set quantidade = quantidade - ? where idproduto =?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, Integer.toString(quantidade));
                pst.setString(2, txtProduId.getText());
                pst.executeUpdate();
                //chamando m método para atualizar a tabela 
                preencherTabelaProduto();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    // Método para remover produto do carrinho
    private void removerDoCarrinho() {
        // Verifica se uma linha foi selecionada na tabela do carrinho
        int linhaSelecionada = tblCarrinho.getSelectedRow();

        if (linhaSelecionada != -1) { // Se uma linha está selecionada

            //atualizando a quantidade no banco de dados
            String sql = "update tbprodutos set quantidade = quantidade + ? where idproduto =?";
            String quantidadeCarrinho = tblCarrinho.getValueAt(linhaSelecionada, 3).toString();
            String idNoCarrinho = tblCarrinho.getValueAt(linhaSelecionada, 0).toString();
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, quantidadeCarrinho);
                pst.setString(2, idNoCarrinho);
                pst.executeUpdate();
                
                //chamando m método para atualizar a quantidade na tabela de produtos
                preencherTabelaProduto();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
            
            
            //Removendo a linha selecionada
            DefaultTableModel modelo = (DefaultTableModel) tblCarrinho.getModel();
            modelo.removeRow(linhaSelecionada);
            JOptionPane.showMessageDialog(null, "Produto removido do carrinho!");
        } else {
            JOptionPane.showMessageDialog(null, "Selecione um produto para remover!");
        }
        
        preencherTabelaProduto();
        
    }

    //Método para setar o id ao clicar tabela
    public void setarCampos() {
        int setar = tblProdutos.getSelectedRow();
        txtProduId.setText(tblProdutos.getModel().getValueAt(setar, 0).toString());
    }

    //Método para preencher a tabela ao abrir a aba de relatório de produtos
    private void preencherTabelaProduto() {
        String sql = "select idproduto as ID,nomeproduto as NOME, preco as PREÇO , quantidade as QUANT from tbprodutos";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tblProdutos.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    //Método para preencher a tabela ao abrir a aba de relatório de produtos
    private void pesquisarProduto() {
        String sql = "select idproduto as ID,nomeproduto as NOME, preco as PREÇO , quantidade as QUANT from tbprodutos where nomeproduto like ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtProduPesquisar.getText() + "%");
            rs = pst.executeQuery();
            //A linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela
            tblProdutos.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
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

        jScrollPane3 = new javax.swing.JScrollPane();
        tblTotal = new javax.swing.JTable();
        btPagar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtProduPesquisar = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProdutos = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCarrinho = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtProduId = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtProduQuanti = new javax.swing.JTextField();
        btAdicionar = new javax.swing.JButton();
        btRemover = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Vender");
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

        tblTotal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "TOTAL"
            }
        ));
        jScrollPane3.setViewportView(tblTotal);

        btPagar.setText("PAGAR");
        btPagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPagarActionPerformed(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("BUSCAR PRODUTOS");

        txtProduPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtProduPesquisarKeyReleased(evt);
            }
        });

        tblProdutos = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int ColIndex){
                return false;
            }
        };
        tblProdutos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "NOME", "PREÇO ", "QUANT"
            }
        ));
        tblProdutos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProdutosMouseClicked(evt);
            }
        });
        tblProdutos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblProdutosKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tblProdutos);

        tblCarrinho = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int ColIndex){
                return false;
            }
        };
        tblCarrinho.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "NOME", "PREÇO ", "QUANT", "TOTAL"
            }
        ));
        tblCarrinho.setToolTipText("");
        jScrollPane1.setViewportView(tblCarrinho);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("PRODUTOS EM ESTOQUE");

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("CARRINHO");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("COLOQUE O ID DO PRODUTO");

        txtProduId.setEnabled(false);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("COLOQUE A QUANTIDADE DO PRODUTO");

        btAdicionar.setText("ADICIONAR");
        btAdicionar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btAdicionarMouseClicked(evt);
            }
        });
        btAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAdicionarActionPerformed(evt);
            }
        });

        btRemover.setText("REMOVER");
        btRemover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRemoverActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProduId, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProduQuanti, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btAdicionar)
                        .addGap(47, 47, 47)
                        .addComponent(btRemover, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(91, 91, 91)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtProduPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(4, 4, 4))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(7, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel5)
                        .addGap(4, 4, 4)
                        .addComponent(txtProduId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jLabel4)
                        .addGap(14, 14, 14)
                        .addComponent(txtProduQuanti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btAdicionar)
                            .addComponent(btRemover))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btPagar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtProduPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4))))
        );

        setBounds(0, 0, 1000, 631);
    }// </editor-fold>//GEN-END:initComponents

    private void btPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPagarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btPagarActionPerformed

    private void btAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAdicionarActionPerformed

    }//GEN-LAST:event_btAdicionarActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        //chamando o método para preencher a tabela de produtos
        preencherTabelaProduto();
    }//GEN-LAST:event_formInternalFrameOpened

    private void tblProdutosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblProdutosKeyPressed

    }//GEN-LAST:event_tblProdutosKeyPressed

    private void tblProdutosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProdutosMouseClicked
        // Chamando o metodo setar campo
        setarCampos();
    }//GEN-LAST:event_tblProdutosMouseClicked

    private void btAdicionarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btAdicionarMouseClicked
        // TODO add your handling code here:
        adicionarCarrinho();
    }//GEN-LAST:event_btAdicionarMouseClicked

    private void txtProduPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProduPesquisarKeyReleased
        //Chamando o método para pesquisar produtos
        pesquisarProduto();
    }//GEN-LAST:event_txtProduPesquisarKeyReleased

    private void btRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRemoverActionPerformed
        //Chamando o método para remover do carrinho
        removerDoCarrinho();
    }//GEN-LAST:event_btRemoverActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdicionar;
    private javax.swing.JButton btPagar;
    private javax.swing.JButton btRemover;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tblCarrinho;
    private javax.swing.JTable tblProdutos;
    private javax.swing.JTable tblTotal;
    private javax.swing.JTextField txtProduId;
    private javax.swing.JTextField txtProduPesquisar;
    private javax.swing.JTextField txtProduQuanti;
    // End of variables declaration//GEN-END:variables
}
