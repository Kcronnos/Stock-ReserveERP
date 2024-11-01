/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.stockreserve.telas;
import br.com.stockreserve.dal.Criptografia;
import java.sql.*;
import br.com.stockreserve.dal.ModuloConexao;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import net.proteanit.sql.DbUtils;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author Felipe
 */
public class TelaProdutos extends javax.swing.JInternalFrame {
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    ResourceBundle bundle;
    /**
     * Creates new form TelaProdutos
     */
    public TelaProdutos() {
        Locale locale;
        if (LanguageSelection.selectedLanguage) {
            locale = Locale.of("en", "US");
        } else {
            locale = Locale.of("pt", "BR");
        }   
        bundle = ResourceBundle.getBundle("br.com.stockreserve.erp", locale);
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
               tblProdutos.clearSelection();
               limpar();
               btnAdicionar.setEnabled(true);
               btnAlterar.setEnabled(false);
               btnRemover.setEnabled(false);
            }
        });
        initComponents();
        setTitle(bundle.getString("prod_title"));
        conexao = ModuloConexao.conector();
    }
    
    private void adicionarProdutos() {
        String sql = "insert into tbprodutos(idproduto, nomeproduto,preco,quantidade,limite_minimo,vencimento) values(?,?,?,?,?,?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtProduId.getText());
            pst.setString(2, txtProduNome.getText());
            pst.setString(3, txtProduPreco.getText());          
            pst.setString(4, txtProduQuanti.getText());
            pst.setString(5, txtProduLimi.getText());
            
            //Checa pra ver se a data é nula ou não
            if(dcVencimento.getDate() == null) {
                pst.setString(6,null);
            } else {
                pst.setString(6,((JTextField)dcVencimento.getDateEditor().getUiComponent()).getText());
            }

            //Validação dos campos obrigatórios
            if (txtProduId.getText().isEmpty() || txtProduNome.getText().isEmpty() || txtProduPreco.getText().isEmpty() || txtProduQuanti.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, bundle.getString("mandatory"));
            } else {
                //a linha abaixo atualiza a tabela usuarios com os dados do formularios
                //a estrutura abaixo é usada para confirma a inserção dos dados na tabela
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, bundle.getString("product_added_success"));
                    limpar();
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
        //Atualizando a tabela de produtos após a adição de um novo produto
        preencherTabelaProduto();
    }
    
    //Método para alterar informações do produto
    private void alterarProduto() {
        String sql = "update tbprodutos set nomeproduto =?, preco=?, quantidade=?, limite_minimo=?, vencimento=? where idproduto=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtProduNome.getText());
            pst.setString(2, txtProduPreco.getText().replace(",","."));
            pst.setString(3, txtProduQuanti.getText());
            pst.setString(4, txtProduLimi.getText());
            //Checa pra ver se a data é nula ou não
            if(dcVencimento.getDate() == null) {
                pst.setString(5,null);
            } else {
                pst.setString(5,((JTextField)dcVencimento.getDateEditor().getUiComponent()).getText());
            }
            pst.setString(6, txtProduId.getText());

            if (txtProduId.getText().isEmpty() || txtProduNome.getText().isEmpty() || txtProduPreco.getText().isEmpty() || txtProduQuanti.getText().isEmpty() || txtProduLimi.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, bundle.getString("mandatory"));
            } else {
                //a linha abaixo atualiza a tabela produtos com os dados do formularios
                //a estrutura abaixo é usada para confirma a inserção dos dados na tabela
                int alterado = pst.executeUpdate();
                if (alterado > 0) {
                    JOptionPane.showMessageDialog(null, bundle.getString("product_updated_success"));
                    limpar();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
        //Atualizando a tabela de produtos após a alteração de dados de um produto
        preencherTabelaProduto();
    }
    
    //Método para remover produtos do banco de dados
    private void removerProduto() {
        int confirma = JOptionPane.showConfirmDialog(null, bundle.getString("confirm_remove_product"), bundle.getString("attention"), JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from tbprodutos where idproduto=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtProduId.getText());
                int removido = pst.executeUpdate();
                if (removido > 0) {
                    JOptionPane.showMessageDialog(null, bundle.getString("product_removed_success"));
                    limpar();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
        
        //Atualizando a tabela de produtos após a remoção de um produto
        preencherTabelaProduto();
    }
    
    //Método para pesquisar os produtos no banco de dados e adicionar a tabela enquanto você digita o nome
    private void pesquisarProdutos() {
        String sql;
        if (LanguageSelection.selectedLanguage) {
            sql = "select idproduto as ID, nomeproduto as "+ bundle.getString("name")+", preco / 5.78 as "+bundle.getString("price")+",quantidade as "+bundle.getString("amount")+", limite_minimo as "+bundle.getString("min_limit")+", vencimento as "+bundle.getString("expiry")+" from tbprodutos where nomeproduto like ?";
        } else {
            sql = "select idproduto as ID, nomeproduto as "+ bundle.getString("name")+", preco as "+bundle.getString("price")+",quantidade as "+bundle.getString("amount")+", limite_minimo as "+bundle.getString("min_limit")+", vencimento as "+bundle.getString("expiry")+" from tbprodutos where nomeproduto like ?";
        }

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
        txtProduLimi.setText(tblProdutos.getModel().getValueAt(setar, 4).toString());
        //a linha abaixo era pra preencher oss campo de vencimento
        //só preenche dps q vc seleciona alguma data por algum motivo que não sei ainda
        //dcVencimento.setDateFormatString(tblProdutos.getModel().getValueAt(setar, 5).toString());
        
        btnAdicionar.setEnabled(false);
        btnAlterar.setEnabled(true);
        btnRemover.setEnabled(true);
    }
    
    //Método para preencher a tabela dos produtos ao abrir a tela de produtos
    private void preencherTabelaProduto() {
        String sql;
        if (LanguageSelection.selectedLanguage) {
            sql = "select idproduto as ID, nomeproduto as "+ bundle.getString("name")+", preco / 5.78 as "+bundle.getString("price")+",quantidade as "+bundle.getString("amount")+", limite_minimo as "+bundle.getString("min_limit")+", vencimento as "+bundle.getString("expiry")+" from tbprodutos";
        } else {
            sql = "select idproduto as ID, nomeproduto as "+ bundle.getString("name")+", preco as "+bundle.getString("price")+",quantidade as "+bundle.getString("amount")+", limite_minimo as "+bundle.getString("min_limit")+", vencimento as "+bundle.getString("expiry")+" from tbprodutos";
        }
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tblProdutos.setModel(DbUtils.resultSetToTableModel(rs));
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void limpar() {
        txtProduId.setText(null);
        txtProduNome.setText(null);
        txtProduPreco.setText(null);
        txtProduQuanti.setText(null);
        txtProduLimi.setText(null);
        dcVencimento.setDate(null);
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
        txtProduLimi = new javax.swing.JTextField();
        txtProduQuanti = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnAdicionar = new javax.swing.JButton();
        btnAlterar = new javax.swing.JButton();
        btnRemover = new javax.swing.JButton();
        dcVencimento = new com.toedter.calendar.JDateChooser();

        txtUsuId.setEnabled(false);
        txtUsuId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsuIdActionPerformed(evt);
            }
        });

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle(bundle.getString("prod_title"));
        setPreferredSize(new java.awt.Dimension(1000, 631));
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
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                bundle.getString("prod_id"),
                bundle.getString("prod_name"),
                bundle.getString("prod_price"),
                bundle.getString("amount"),
                bundle.getString("min_limit"),
                bundle.getString("expiry")
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

        jLabel1.setText(bundle.getString("prod_id"));

        txtProduPreco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProduPrecoActionPerformed(evt);
            }
        });

        txtProduLimi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProduLimiActionPerformed(evt);
            }
        });

        jLabel2.setText("*"+bundle.getString("prod_name"));

        jLabel3.setText("*"+bundle.getString("amount"));

        jLabel4.setText("*"+bundle.getString("prod_price"));

        jLabel5.setText("*"+bundle.getString("min_limit"));

        jLabel12.setText(bundle.getString("mandatoryf"));

        jLabel6.setText(bundle.getString("expiry"));

        btnAdicionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br.com.stockreserve.icones/produto_adicionar.png"))); // NOI18N
        btnAdicionar.setToolTipText(bundle.getString("prod_add"));
        btnAdicionar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdicionar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        btnAlterar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br.com.stockreserve.icones/produto_editar.png"))); // NOI18N
        btnAlterar.setToolTipText(bundle.getString("change_data"));
        btnAlterar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAlterar.setEnabled(false);
        btnAlterar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarActionPerformed(evt);
            }
        });

        btnRemover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br.com.stockreserve.icones/produto_remover.png"))); // NOI18N
        btnRemover.setToolTipText(bundle.getString("prod_remove"));
        btnRemover.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemover.setEnabled(false);
        btnRemover.setPreferredSize(new java.awt.Dimension(80, 80));
        btnRemover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverActionPerformed(evt);
            }
        });

        dcVencimento.setDateFormatString("yyyy-MM-dd");

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
                                            .addComponent(txtProduLimi, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addComponent(txtProduQuanti, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jLabel6)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(dcVencimento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(165, 165, 165)
                                        .addComponent(btnRemover, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(txtProduPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 468, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 928, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(33, 33, 33))
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
                    .addComponent(txtProduLimi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemover, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        setBounds(0, 0, 1000, 631);
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

    private void txtProduLimiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProduLimiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProduLimiActionPerformed

    private void txtProduPrecoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProduPrecoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProduPrecoActionPerformed

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        //Chamando o método para adicionar produtos
        adicionarProdutos();
        btnAlterar.setEnabled(false);
        btnRemover.setEnabled(false);
        btnAdicionar.setEnabled(true);
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void btnAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarActionPerformed
        //Chamando o método para alterar os dados do produto
        alterarProduto();
        btnAlterar.setEnabled(false);
        btnRemover.setEnabled(false);
        btnAdicionar.setEnabled(true);
    }//GEN-LAST:event_btnAlterarActionPerformed

    private void btnRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverActionPerformed
        //Chamando o método para remover produtos
        removerProduto();
        btnAlterar.setEnabled(false);
        btnRemover.setEnabled(false);
        btnAdicionar.setEnabled(true);
    }//GEN-LAST:event_btnRemoverActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        //Chamando o método de preencher a tabel
        preencherTabelaProduto();
    }//GEN-LAST:event_formInternalFrameOpened


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
    private javax.swing.JTextField txtProduLimi;
    private javax.swing.JTextField txtProduNome;
    private javax.swing.JTextField txtProduPesquisar;
    private javax.swing.JTextField txtProduPreco;
    private javax.swing.JTextField txtProduQuanti;
    private javax.swing.JTextField txtUsuId;
    // End of variables declaration//GEN-END:variables
}
