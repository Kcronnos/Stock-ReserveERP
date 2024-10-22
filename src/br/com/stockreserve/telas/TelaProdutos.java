/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.stockreserve.telas;
import br.com.stockreserve.dal.Criptografia;
import java.sql.*;
import br.com.stockreserve.dal.ModuloConexao;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Felipe
 */
public class TelaProdutos extends javax.swing.JInternalFrame {
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    /**
     * Creates new form TelaProdutos
     */
    public TelaProdutos() {
        initComponents();
        conexao = ModuloConexao.conector();
    }
    
    private void adicionarProdutos() {
        String sql = "insert into tbprodutos(idproduto, nomeproduto,preco,quantidade,peso,vencimento) values(?,?,?,?,?,?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtProduId.getText());
            pst.setString(2, txtProduNome.getText());
            pst.setString(3, txtProduPreco.getText());          
            pst.setString(4, txtProduQuanti.getText());
            pst.setString(5, txtProduPeso.getText());
            pst.setString(6,((JTextField)dcVencimento.getDateEditor().getUiComponent()).getText());

            //Validação dos campos obrigatórios
            if (txtProduId.getText().isEmpty() || txtProduNome.getText().isEmpty() || txtProduPreco.getText().isEmpty() || txtProduQuanti.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {
                //a linha abaixo atualiza a tabela usuarios com os dados do formularios
                //a estrutura abaixo é usada para confirma a inserção dos dados na tabela
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Produto cadastrado com sucesso!");
                    limpar();
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    //Método para alterar informações do produto
    private void alterarProduto() {
        String sql = "update tbprodutos set nomeproduto =?, preco=?, quantidade=?, peso=?, vencimento=? where idproduto=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtProduNome.getText());
            pst.setString(2, txtProduPreco.getText());
            pst.setString(3, txtProduQuanti.getText());
            pst.setString(4, txtProduPeso.getText());
            pst.setString(5,((JTextField)dcVencimento.getDateEditor().getUiComponent()).getText() );
            pst.setString(6, txtProduId.getText());

            if (txtProduId.getText().isEmpty() || txtProduNome.getText().isEmpty() || txtProduPreco.getText().isEmpty() || txtProduQuanti.getText().isEmpty() || txtProduPeso.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {
                //a linha abaixo atualiza a tabela usuarios com os dados do formularios
                //a estrutura abaixo é usada para confirma a inserção dos dados na tabela
                int alterado = pst.executeUpdate();
                if (alterado > 0) {
                    JOptionPane.showMessageDialog(null, "Dados do produto alterados com sucesso!");
                    limpar();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    //Método para remover produtos do banco de dados
    private void removerProduto() {
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover o produto?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from tbprodutos where idproduto=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtProduId.getText());
                int removido = pst.executeUpdate();
                if (removido > 0) {
                    JOptionPane.showMessageDialog(null, "Uusuário removido com sucesso!");
                    limpar();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
    
    //Método para pesquisar os produtos no banco de dados e adicionar a tabela enquanto você digita o nome
    private void pesquisarProdutos() {
        String sql = "select idproduto as ID, nomeproduto as Nome, preco as Preço,quantidade as Quantidade,peso as Peso, vencimento as Vencimento from tbprodutos where nomeproduto like ?";

        try {
            pst = conexao.prepareStatement(sql);
            //passando o conteúdo da caixa de pesquisa para o ?
            //atenção ao % que é a continuação da string sql
            pst.setString(1, txtProduPesquisar.getText() + "%");
            rs = pst.executeQuery();
            //A linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela
            tblProdutos.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    //Método para setar os campos do formulario com o conteudo da tabela
    public void setarCampos() {
        int setar = tblProdutos.getSelectedRow();
        txtProduId.setText(tblProdutos.getModel().getValueAt(setar, 0).toString());
        txtProduNome.setText(tblProdutos.getModel().getValueAt(setar, 1).toString());
        txtProduPreco.setText(tblProdutos.getModel().getValueAt(setar, 2).toString());
        txtProduQuanti.setText(tblProdutos.getModel().getValueAt(setar, 3).toString());
        txtProduPeso.setText(tblProdutos.getModel().getValueAt(setar, 4).toString());
        //a linha abaixo era pra preencher oss campo de vencimento
        //só preenche dps q vc seleciona alguma data por algum motivo que não sei ainda
        //dcVencimento.setDateFormatString(tblProdutos.getModel().getValueAt(setar, 5).toString());
    }
    
    private void limpar() {
        txtProduId.setText(null);
        txtProduNome.setText(null);
        txtProduPreco.setText(null);
        txtProduQuanti.setText(null);
        txtProduPeso.setText(null);
        dcVencimento.setDateFormatString(" ");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtUsuId = new javax.swing.JTextField();
        txtProduPesquisar = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProdutos = new javax.swing.JTable();
        txtProduId = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtProduPreco = new javax.swing.JTextField();
        txtProduNome = new javax.swing.JTextField();
        txtProduPeso = new javax.swing.JTextField();
        txtProduQuanti = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        dcVencimento = new com.toedter.calendar.JDateChooser();
        btnAdicionar = new javax.swing.JButton();
        btnAlterar = new javax.swing.JButton();
        btnRemover = new javax.swing.JButton();

        txtUsuId.setEnabled(false);
        txtUsuId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsuIdActionPerformed(evt);
            }
        });

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setPreferredSize(new java.awt.Dimension(1000, 631));

        txtProduPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtProduPesquisarKeyReleased(evt);
            }
        });

        tblProdutos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nome", "Preço", "Quantidade", "Peso", "Vencimento"
            }
        ));
        tblProdutos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProdutosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProdutos);

        txtProduId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProduIdActionPerformed(evt);
            }
        });

        jLabel1.setText("ID Produto");

        txtProduPreco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProduPrecoActionPerformed(evt);
            }
        });

        txtProduPeso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProduPesoActionPerformed(evt);
            }
        });

        jLabel2.setText("*Nome Produto");

        jLabel3.setText("*Quantidade");

        jLabel4.setText("*Preço Produto");

        jLabel5.setText("*Peso Produto");

        jLabel12.setText("*Campos Obrigatórios");

        jLabel6.setText("Vencimento");

        dcVencimento.setDateFormatString("yyyy-MM-dd");

        btnAdicionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br.com.stockreserve.icones/produto_adicionar.png"))); // NOI18N
        btnAdicionar.setToolTipText("Adicionar Produto");
        btnAdicionar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdicionar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        btnAlterar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br.com.stockreserve.icones/produto_editar.png"))); // NOI18N
        btnAlterar.setToolTipText("Alterar Dados");
        btnAlterar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAlterar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarActionPerformed(evt);
            }
        });

        btnRemover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br.com.stockreserve.icones/produto_remover.png"))); // NOI18N
        btnRemover.setToolTipText("Remover Produto");
        btnRemover.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemover.setPreferredSize(new java.awt.Dimension(80, 80));
        btnRemover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtProduId, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(txtProduNome, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtProduPreco, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel5))))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtProduPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(txtProduQuanti, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel6)
                                                .addGap(18, 18, 18)
                                                .addComponent(dcVencimento, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(165, 165, 165)
                                        .addComponent(btnRemover, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(txtProduPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 468, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 928, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtProduPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtProduId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtProduNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(txtProduQuanti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(jLabel6))
                    .addComponent(dcVencimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtProduPreco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtProduPeso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemover, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(102, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtProduPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProduPesquisarKeyReleased
        //Chamando o método para pesquisar produtos e preencher a tabela
        pesquisarProdutos();
    }//GEN-LAST:event_txtProduPesquisarKeyReleased

    private void tblProdutosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProdutosMouseClicked
        //Chamando o setar campos para preencher os campos do formulário
        setarCampos();
    }//GEN-LAST:event_tblProdutosMouseClicked

    private void txtUsuIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsuIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsuIdActionPerformed

    private void txtProduIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProduIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProduIdActionPerformed

    private void txtProduPesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProduPesoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProduPesoActionPerformed

    private void txtProduPrecoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProduPrecoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProduPrecoActionPerformed

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        //Chamando o método para adicionar produtos
        adicionarProdutos();
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void btnAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarActionPerformed
        //Chamando o método para alterar os dados do produto
        alterarProduto();
    }//GEN-LAST:event_btnAlterarActionPerformed

    private void btnRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverActionPerformed
        //Chamando o método para remover produtos
        removerProduto();
    }//GEN-LAST:event_btnRemoverActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JButton btnAlterar;
    private javax.swing.JButton btnRemover;
    private com.toedter.calendar.JDateChooser dcVencimento;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblProdutos;
    private javax.swing.JTextField txtProduId;
    private javax.swing.JTextField txtProduNome;
    private javax.swing.JTextField txtProduPeso;
    private javax.swing.JTextField txtProduPesquisar;
    private javax.swing.JTextField txtProduPreco;
    private javax.swing.JTextField txtProduQuanti;
    private javax.swing.JTextField txtUsuId;
    // End of variables declaration//GEN-END:variables
}
