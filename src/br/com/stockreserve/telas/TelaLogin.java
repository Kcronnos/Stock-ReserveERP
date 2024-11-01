/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package br.com.stockreserve.telas;

import java.sql.*;
import br.com.stockreserve.dal.ModuloConexao;
import java.awt.Color;
import javax.swing.JOptionPane;
import br.com.stockreserve.dal.Criptografia;
import javax.swing.ImageIcon;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * TelaLogin é uma classe que representa a tela de login do sistema de gestão de
 * estoque. Ela permite que os usuários se autentiquem utilizando um nome de
 * usuário e uma senha. A classe é responsável pela interação com o banco de
 * dados para validar as credenciais do usuário e gerenciar a interface gráfica.
 *
 * A senha é criptografada antes de ser comparada com os dados armazenados no
 * banco de dados. A classe também suporta a internacionalização, permitindo que
 * a interface seja exibida em diferentes idiomas.
 *
 * @author Felipe
 * @version 2.0
 */
public class TelaLogin extends javax.swing.JFrame {

    ImageIcon icon = new ImageIcon(getClass().getResource("/br.com.stockreserve.icones/logo_stockreserve.png"));
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    //internationalization
    private ResourceBundle bundle;

    /**
     * Método responsável por realizar a autenticação do usuário no sistema.
     * Verifica o login e a senha informados pelo usuário na interface,
     * comparando-os com os dados armazenados na tabela `tbusuarios` do banco de
     * dados. Em caso de sucesso, habilita os menus correspondentes ao setor do
     * usuário e fecha a tela de login.
     *
     * A senha é criptografada antes de ser comparada com o valor armazenado no
     * banco. Caso as credenciais estejam incorretas, exibe uma mensagem de
     * erro.
     *
     * @throws Exception se ocorrer algum erro durante a execução da consulta ao
     * banco de dados.
     *
     * @author Feliipee013
     * @version 2.0
     */
    public void logar() {
        String sql = "Select * from tbusuarios where login = ? and senha = ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtLogin.getText());
            //As linha abaixo pega o texto do jpasswordfield e criptografa a senha
            String captura = new String(txtSenha.getPassword());
            captura = Criptografia.criptografar(captura);
            pst.setString(2, captura);

            rs = pst.executeQuery();
            if (rs.next()) {
                //a linha abaixo obtem o conteudo do campo setor da table tbusuarios
                String setor = rs.getString(5);
                //a estrutura abaixo faz o tratamento do perfil do usuario
                if (setor.equals("Gerência")) {
                    TelaPrincipal principal = new TelaPrincipal();
                    principal.setVisible(true);
                    TelaPrincipal.menVen.setEnabled(true);
                    TelaPrincipal.menEsto.setEnabled(true);
                    TelaPrincipal.menGer.setEnabled(true);
                    TelaPrincipal.lblUsuario.setText(rs.getString(2));
                    this.dispose();
                    conexao.close();
                } else {
                    if (setor.equals("Vendas")) {
                        TelaPrincipal principal = new TelaPrincipal();
                        principal.setVisible(true);
                        TelaPrincipal.menVen.setEnabled(true);
                        TelaPrincipal.lblUsuario.setText(rs.getString(2));
                        this.dispose();
                        conexao.close();
                    } else {
                        if (setor.equals("Estoque")) {
                            TelaPrincipal principal = new TelaPrincipal();
                            principal.setVisible(true);
                            TelaPrincipal.menEsto.setEnabled(true);
                            TelaPrincipal.lblUsuario.setText(rs.getString(2));
                            this.dispose();
                            conexao.close();
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, bundle.getString("login_error"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Construtor da classe TelaLogin. Inicializa a interface gráfica, define o
     * ícone da janela e estabelece a conexão com o banco de dados.
     *
     * A localização da interface é definida com base na seleção de idioma do
     * usuário.
     *
     * @author Feliipee013
     * @author ElinaldoLopes
     *
     * @version 2.0
     */
    public TelaLogin() {
        //internationalization
        Locale locale;
        if (LanguageSelection.selectedLanguage) {
            locale = Locale.of("en", "US");
        } else {
            locale = Locale.of("pt", "BR");
        }
        bundle = ResourceBundle.getBundle("br.com.stockreserve.erp", locale);

        initComponents();
        setIconImage(icon.getImage());
        conexao = ModuloConexao.conector();

        if (conexao != null) {
            lblStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br.com.stockreserve.icones/dbok.png")));
        } else {
            lblStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br.com.stockreserve.icones/dberror.png")));
        }
    }

    /**
     * Método que inicializa a interface gráfica da tela de login. Este método é
     * gerado automaticamente e não deve ser modificado.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnLogin = new javax.swing.JButton();
        txtSenha = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtLogin = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(bundle.getString("login"));
        setMinimumSize(new java.awt.Dimension(500, 400));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(67, 106, 137));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnLogin.setText(bundle.getString("login"));
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText(bundle.getString("password"));

        jLabel1.setText(bundle.getString("user"));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 25)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("StoSale");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br.com.stockreserve.icones/logo_stockreserve_64x64.png"))); // NOI18N

        jLabel5.setText(bundle.getString("welcome"));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(149, 149, 149)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(139, 139, 139)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel5))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabel1)
                        .addGap(21, 21, 21)
                        .addComponent(txtLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(156, 156, 156)
                        .addComponent(btnLogin)))
                .addContainerGap(94, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jLabel5)))
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel1))
                    .addComponent(txtLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel2))
                    .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addComponent(btnLogin)
                .addContainerGap(88, Short.MAX_VALUE))
        );

        lblStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br.com.stockreserve.icones/dberror.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(350, 350, 350)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Método que trata a ação do botão de login. Chama o método logar() quando
     * o botão é pressionado.
     *
     * @param evt O evento gerado pelo clique do mouse no botão.
     */
    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        logar();
    }//GEN-LAST:event_btnLoginActionPerformed

    /**
     * Método principal que inicia a aplicação.
     *
     * @param args Argumentos da linha de comando.
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaLogin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTextField txtLogin;
    private javax.swing.JPasswordField txtSenha;
    // End of variables declaration//GEN-END:variables
}
