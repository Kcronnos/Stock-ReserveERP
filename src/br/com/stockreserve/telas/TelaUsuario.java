/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.stockreserve.telas;

import br.com.stockreserve.dal.Criptografia;
import br.com.stockreserve.dal.ModuloConexao;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;
import java.util.ResourceBundle;
import java.util.Locale;

/**
 * Classe que representa a interface do usuário para gerenciamento de usuários.
 * Esta classe estende JInternalFrame e permite adicionar, alterar, remover e
 * pesquisar usuários no banco de dados.
 *
 * @author Felipe
 */
public class TelaUsuario extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    ResourceBundle bundle;

    /**
     * Cria um novo formulário TelaUsuario. Este construtor inicializa a
     * interface e a conexão com o banco de dados.
     */
    public TelaUsuario() {
        Locale locale;
        if (LanguageSelection.selectedLanguage) {
            locale = Locale.of("en", "US");
        } else {
            locale = Locale.of("pt", "BR");
        }
        bundle = ResourceBundle.getBundle("br.com.stockreserve.erp", locale);
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                tblUsuarios.clearSelection();
                btnAdicionar.setEnabled(true);
                btnAlterar.setEnabled(false);
                btnRemover.setEnabled(false);
                limpar();
            }
        });
        initComponents();
        conexao = ModuloConexao.conector();
    }

    /**
     * Método para adicionar usuários no banco de dados. Extrai as informações
     * dos campos de texto e as utiliza como parâmetros para o comando SQL de
     * inserção no banco. Inclui validação dos campos obrigatórios.
     *
     * @throws Exception se ocorrer algum erro durante a execução do comando
     * @author Feliipee013
     * @version 2.0
     */
    private void adicionarUsuarios() {
        String sql = "insert into tbusuarios(nome, login,senha,setor,fone) values(?,?,?,?,?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtUsuNome.getText());
            pst.setString(2, txtUsuLogin.getText());
            //As linhas abaixo pega o texto do jpasswordfield e criptografa a senha
            String captura = txtUsuSenha.getText();
            captura = Criptografia.criptografar(captura);
            pst.setString(3, captura);

            pst.setString(4, cboUsuSetor.getSelectedItem().toString());
            pst.setString(5, txtUsuFone.getText());

            //Validação dos campos obrigatórios
            if (txtUsuNome.getText().isEmpty() || txtUsuLogin.getText().isEmpty()
                    || txtUsuSenha.getText().isEmpty() || cboUsuSetor.getSelectedItem().toString().isEmpty() || txtUsuFone.getText().isEmpty() || cboUsuSetor.getSelectedItem().toString().equals(" ")) {
                JOptionPane.showMessageDialog(null, bundle.getString("mandatory"));
            } else {
                //a linha abaixo atualiza a tabela usuarios com os dados do formularios
                //a estrutura abaixo é usada para confirma a inserção dos dados na tabela
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, bundle.getString("user_added_success"));
                    limpar();
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

        //Atualizando a tabela de usuários após adicionar um novo usuário
        preencherTabelaUsuarios();
    }

    /**
     * Método para alterar informações do usuário no banco de dados. Atualiza os
     * dados do usuário selecionado com base nos valores dos campos de texto.
     * Inclui validação dos campos obrigatórios e notificação ao usuário em caso
     * de sucesso.
     *
     * @throws Exception se ocorrer algum erro durante a execução do comando
     * SQL.
     * @author Feliipee013
     * @version 2.0
     */
    private void alterarUsuarios() {
        String sql = "update tbusuarios set nome =?, login=?, senha=?, setor=?, fone=? where iduser=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtUsuNome.getText());
            pst.setString(2, txtUsuLogin.getText());
            //As linhas abaixo pega o texto do jpasswordfield e criptografa a senha
            String captura = txtUsuSenha.getText();
            captura = Criptografia.criptografar(captura);
            pst.setString(3, captura);

            pst.setString(4, cboUsuSetor.getSelectedItem().toString());
            pst.setString(5, txtUsuFone.getText());
            pst.setString(6, txtUsuId.getText());

            if (txtUsuId.getText().isEmpty() || txtUsuNome.getText().isEmpty() || txtUsuLogin.getText().isEmpty()
                    || txtUsuSenha.getText().isEmpty() || txtUsuFone.getText().isEmpty() || cboUsuSetor.getSelectedItem().toString().isEmpty()) {
                JOptionPane.showMessageDialog(null, bundle.getString("mandatory"));
            } else {
                //a linha abaixo atualiza a tabela usuarios com os dados do formularios
                //a estrutura abaixo é usada para confirma a inserção dos dados na tabela
                int alterado = pst.executeUpdate();
                if (alterado > 0) {
                    JOptionPane.showMessageDialog(null, bundle.getString("user_updated_success"));
                    limpar();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

        //Atualizando a tabela de usuários após a alteração de dados de um usuário
        preencherTabelaUsuarios();
    }

    /**
     * Método para remover usuários do banco de dados Solicita confirmação antes
     * de remover o usuário selecionado exibe uma mensagem de confirmação em
     * caso de successo.
     *
     * @throws Exception se ocorrer algum erro durante a execução do comando
     * @param confirma Exibe um diálogo de confirmação para o usuário.
     * @see JOptionPane#showConfirmDialog
     * @see JOptionPane#showMessageDialog
     * @see #limpar()
     * @see #preencherTabelaUsuarios()
     *
     * @author Feliipee013
     * @version 2.0
     */
    private void removerUsuarios() {
        int confirma = JOptionPane.showConfirmDialog(null, bundle.getString("confirm_remove_user"), bundle.getString("attention"), JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from tbusuarios where iduser=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtUsuId.getText());
                int removido = pst.executeUpdate();
                if (removido > 0) {
                    JOptionPane.showMessageDialog(null, bundle.getString("user_removed_success"));
                    limpar();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }

        //Atualizando a tabela de usuários após a remoção de um usuário
        preencherTabelaUsuarios();
    }

    /**
     * Método para pesquisar usuários no banco de dados. Filtra a tabela
     * enquanto o usuário digita o nome, utilizando a biblioteca rs2xml para
     * atualizar a tabela com os resultados da pesquisa.
     *
     * @throws Exception se ocorrer algum erro durante a execução do comando
     * SQL.
     * @see DbUtils#resultSetToTableModel
     *
     * @author Feliipee013
     * @version 2.0
     */
    private void pesquisarUsuarios() {
        String sql = "select iduser as ID, nome as " + bundle.getString("name") + ", login as LOGIN, senha as " + bundle.getString("password") + ", setor as " + bundle.getString("sector") + ", fone as " + bundle.getString("phone") + " from tbusuarios where nome like ?";

        try {
            pst = conexao.prepareStatement(sql);
            //passando o conteúdo da caixa de pesquisa para o ?
            //atenção ao % que é a continuação da string sql
            pst.setString(1, txtUsuPesquisar.getText() + "%");
            rs = pst.executeQuery();
            //A linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela
            tblUsuarios.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Método para preencher os campos de texto do formulário com o conteúdo da
     * linha selecionada na tabela. Ao clicar em uma linha, as informações do
     * usuário são exibidas nos campos correspondentes.
     *
     * @see javax.swing.JTable#getSelectedRow()
     *
     * @author Feliipee013
     * @version 2.0
     */
    public void setarCampos() {
        int setar = tblUsuarios.getSelectedRow();
        txtUsuId.setText(tblUsuarios.getModel().getValueAt(setar, 0).toString());
        txtUsuNome.setText(tblUsuarios.getModel().getValueAt(setar, 1).toString());
        txtUsuLogin.setText(tblUsuarios.getModel().getValueAt(setar, 2).toString());
        cboUsuSetor.setSelectedItem(tblUsuarios.getModel().getValueAt(setar, 4).toString());
        txtUsuFone.setText(tblUsuarios.getModel().getValueAt(setar, 5).toString());
        btnAdicionar.setEnabled(false);
        btnAlterar.setEnabled(true);
        btnRemover.setEnabled(true);
    }

    /**
     * Método responsável por preencher a tabela de usuários. Atualiza a tabela
     * com todos os usuários cadastrados no banco de dados.
     *
     * @throws Exception se ocorrer algum erro durante a execução do comando
     * SQL.
     * @see DbUtils#resultSetToTableModel
     *
     * @author Feliipee013
     * @version 2.0
     */
    private void preencherTabelaUsuarios() {
        String sql = "select iduser as ID, nome as " + bundle.getString("name") + ", login as LOGIN,senha as " + bundle.getString("password") + ",setor as " + bundle.getString("sector") + ", fone as " + bundle.getString("phone") + " from tbusuarios";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tblUsuarios.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Método responsável por limpar todos os campos do formulário. Esse método
     * é acionado após a execução de ações como adicionar, alterar ou remover.
     *
     * @author Feliipee013
     * @version 2.0
     */
    private void limpar() {
        txtUsuId.setText(null);
        txtUsuNome.setText(null);
        txtUsuFone.setText(null);
        txtUsuLogin.setText(null);
        txtUsuSenha.setText(null);
        cboUsuSetor.setSelectedItem(" ");
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
        tblUsuarios = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtUsuId = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtUsuNome = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtUsuLogin = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtUsuFone = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtUsuSenha = new javax.swing.JTextField();
        txtUsuPesquisar = new javax.swing.JTextField();
        cboUsuSetor = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        btnAdicionar = new javax.swing.JButton();
        btnAlterar = new javax.swing.JButton();
        btnRemover = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(67, 106, 137));
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle(bundle.getString("user_title"));
        setName(""); // NOI18N
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

        tblUsuarios = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int ColIndex){
                return false;
            }
        };
        tblUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID",
                bundle.getString("name"),
                "LOGIN",
                bundle.getString("password"),
                bundle.getString("sector"),
                bundle.getString("phone")
            }
        ));
        tblUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUsuariosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblUsuarios);

        jLabel12.setText(bundle.getString("mandatoryf"));

        jLabel1.setText("user_id");

        txtUsuId.setEnabled(false);
        txtUsuId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsuIdActionPerformed(evt);
            }
        });

        jLabel2.setText("*"+bundle.getString("name"));

        jLabel3.setText("*LOGIN");

        jLabel4.setText("*"+bundle.getString("phone"));

        jLabel5.setText("*"+bundle.getString("password"));

        txtUsuPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtUsuPesquisarKeyReleased(evt);
            }
        });

        cboUsuSetor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", bundle.getString("stock"), bundle.getString("management"), bundle.getString("sales")}));

        jLabel6.setText("*"+bundle.getString("sector"));

        btnAdicionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br.com.stockreserve.icones/user_add.png"))); // NOI18N
        btnAdicionar.setToolTipText(bundle.getString("add_user"));
        btnAdicionar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdicionar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        btnAlterar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br.com.stockreserve.icones/user_editar.png"))); // NOI18N
        btnAlterar.setToolTipText(bundle.getString("change_data"));
        btnAlterar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAlterar.setEnabled(false);
        btnAlterar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarActionPerformed(evt);
            }
        });

        btnRemover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br.com.stockreserve.icones/user_remover.png"))); // NOI18N
        btnRemover.setToolTipText(bundle.getString("remove_user"));
        btnRemover.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemover.setEnabled(false);
        btnRemover.setPreferredSize(new java.awt.Dimension(80, 80));
        btnRemover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverActionPerformed(evt);
            }
        });

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText(bundle.getString("search"));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(txtUsuPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 468, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(120, 120, 120)
                                .addComponent(jLabel12))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(4, 4, 4)
                                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(24, 24, 24)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtUsuNome)
                                            .addComponent(txtUsuLogin, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                                            .addComponent(txtUsuId, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(118, 118, 118)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addComponent(jLabel6))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtUsuSenha)
                                            .addComponent(txtUsuFone)
                                            .addComponent(cboUsuSetor, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(29, 29, 29)
                                        .addComponent(btnAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(150, 150, 150)
                                        .addComponent(btnRemover, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtUsuPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(8, 8, 8)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsuId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboUsuSetor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtUsuNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtUsuFone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtUsuLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(txtUsuSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnRemover, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        setBounds(0, 0, 1000, 631);
    }// </editor-fold>//GEN-END:initComponents

    private void txtUsuPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUsuPesquisarKeyReleased
        //Chamando o método para pesquisar usuários e preencher a tabela
        pesquisarUsuarios();
    }//GEN-LAST:event_txtUsuPesquisarKeyReleased

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        //Chamando o método para adicionar usuários
        adicionarUsuarios();
        btnAlterar.setEnabled(false);
        btnRemover.setEnabled(false);
        btnAdicionar.setEnabled(true);
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void txtUsuIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsuIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsuIdActionPerformed

    private void tblUsuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUsuariosMouseClicked
        //Chamando o setar campos para preencher os campos do formulário
        setarCampos();
    }//GEN-LAST:event_tblUsuariosMouseClicked

    private void btnAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarActionPerformed
        //Chamando o método para alterar os dados do usuário
        alterarUsuarios();
        btnAlterar.setEnabled(false);
        btnRemover.setEnabled(false);
        btnAdicionar.setEnabled(true);
    }//GEN-LAST:event_btnAlterarActionPerformed

    private void btnRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverActionPerformed
        //Chamando o método para remover usuários
        removerUsuarios();
        btnAlterar.setEnabled(false);
        btnRemover.setEnabled(false);
        btnAdicionar.setEnabled(true);
    }//GEN-LAST:event_btnRemoverActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        //Chamando o método de preencher a tabela dos usuários 
        preencherTabelaUsuarios();
    }//GEN-LAST:event_formInternalFrameOpened


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JButton btnAlterar;
    private javax.swing.JButton btnRemover;
    private javax.swing.JComboBox<String> cboUsuSetor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblUsuarios;
    private javax.swing.JTextField txtUsuFone;
    private javax.swing.JTextField txtUsuId;
    private javax.swing.JTextField txtUsuLogin;
    private javax.swing.JTextField txtUsuNome;
    private javax.swing.JTextField txtUsuPesquisar;
    private javax.swing.JTextField txtUsuSenha;
    // End of variables declaration//GEN-END:variables
}
