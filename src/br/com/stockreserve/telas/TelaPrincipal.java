/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package br.com.stockreserve.telas;

import java.awt.Desktop;
import java.net.URI;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A classe TelaPrincipal é responsável por exibir a interface principal da aplicação.
 * Ela estende a classe JFrame e contém componentes de interface gráfica do usuário,
 * como menus e painéis. Também gerencia a configuração do idioma e a exibição de dados
 * do usuário e da data atual.
 *
 * @author Feliipee013
 * @version 2.0
 */
public class TelaPrincipal extends javax.swing.JFrame {

    ImageIcon icon = new ImageIcon(getClass().getResource("/br.com.stockreserve.icones/logo_stockreserve.png"));
    private ResourceBundle bundle;

    /**
     * Construtor que inicializa a TelaPrincipal.
     * Dependendo da configuração do idioma selecionado, define o local e carrega
     * as strings de recursos correspondentes.
     */
    public TelaPrincipal() {
        Locale locale;
        if (LanguageSelection.selectedLanguage) {
            locale = Locale.of("en", "US");
        } else {
            locale = Locale.of("pt", "BR");
        }
        bundle = ResourceBundle.getBundle("br.com.stockreserve.erp", locale);
        initComponents();
        setIconImage(icon.getImage());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        desktop = new javax.swing.JDesktopPane();
        lblUsuario = new javax.swing.JLabel();
        lblData = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        menVen = new javax.swing.JMenu();
        menVenVender = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        menEsto = new javax.swing.JMenu();
        menEstPro = new javax.swing.JMenuItem();
        menEstStatus = new javax.swing.JMenuItem();
        menGer = new javax.swing.JMenu();
        menGerUsu = new javax.swing.JMenuItem();
        menGerRelPro = new javax.swing.JMenuItem();
        MenGerRelVen = new javax.swing.JMenuItem();
        telaRelatorioSalarios = new javax.swing.JMenuItem();
        menAju = new javax.swing.JMenu();
        menAjuSob = new javax.swing.JMenuItem();
        menAjuSup = new javax.swing.JMenuItem();
        menOpc = new javax.swing.JMenu();
        menOpcSai = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("StoSale");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        javax.swing.GroupLayout desktopLayout = new javax.swing.GroupLayout(desktop);
        desktop.setLayout(desktopLayout);
        desktopLayout.setHorizontalGroup(
            desktopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );
        desktopLayout.setVerticalGroup(
            desktopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 631, Short.MAX_VALUE)
        );

        lblUsuario.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblUsuario.setText("Usuário");

        lblData.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblData.setText("Data");

        menVen.setText("Vendas");
        menVen.setEnabled(false);

        menVenVender.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.ALT_DOWN_MASK));
        menVenVender.setText("Vender");
        menVenVender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menVenVenderActionPerformed(evt);
            }
        });
        menVen.add(menVenVender);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItem1.setText("Notas Fiscais");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        menVen.add(jMenuItem1);

        jMenuBar1.add(menVen);

        menEsto.setText("Estoque");
        menEsto.setEnabled(false);

        menEstPro.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.ALT_DOWN_MASK));
        menEstPro.setText("Estoque de Produtos");
        menEstPro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menEstProActionPerformed(evt);
            }
        });
        menEsto.add(menEstPro);

        menEstStatus.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_DOWN_MASK));
        menEstStatus.setText("Status do Estoque");
        menEstStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menEstStatusActionPerformed(evt);
            }
        });
        menEsto.add(menEstStatus);

        jMenuBar1.add(menEsto);

        menGer.setText("Gerência");
        menGer.setEnabled(false);

        menGerUsu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.ALT_DOWN_MASK));
        menGerUsu.setText("Usuários");
        menGerUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menGerUsuActionPerformed(evt);
            }
        });
        menGer.add(menGerUsu);

        menGerRelPro.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        menGerRelPro.setText("Relatório de Produtos");
        menGerRelPro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menGerRelProActionPerformed(evt);
            }
        });
        menGer.add(menGerRelPro);

        MenGerRelVen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        MenGerRelVen.setText("Relatorio de Vendas");
        MenGerRelVen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenGerRelVenActionPerformed(evt);
            }
        });
        menGer.add(MenGerRelVen);

        telaRelatorioSalarios.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        telaRelatorioSalarios.setText("Relatório de Usuários");
        telaRelatorioSalarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                telaRelatorioSalariosActionPerformed(evt);
            }
        });
        menGer.add(telaRelatorioSalarios);

        jMenuBar1.add(menGer);

        menAju.setText("Ajuda");

        menAjuSob.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_DOWN_MASK));
        menAjuSob.setText("Sobre");
        menAjuSob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menAjuSobActionPerformed(evt);
            }
        });
        menAju.add(menAjuSob);

        menAjuSup.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        menAjuSup.setText("Suporte");
        menAjuSup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menAjuSupActionPerformed(evt);
            }
        });
        menAju.add(menAjuSup);

        jMenuBar1.add(menAju);

        menOpc.setText("Opções");

        menOpcSai.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_DOWN_MASK));
        menOpcSai.setText("Sair");
        menOpcSai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menOpcSaiActionPerformed(evt);
            }
        });
        menOpc.add(menOpcSai);

        jMenuBar1.add(menOpc);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(desktop)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblData, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(desktop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(2, 2, 2)))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Trata a ação do menu que inicializa a tela "Sobre".
     *
     * @param evt o evento de ação gerado pelo menu
     */
    private void menAjuSobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menAjuSobActionPerformed
        //Inicializando a tela Sobre
        TelaSobre sobre = new TelaSobre();
        sobre.setVisible(true);
    }//GEN-LAST:event_menAjuSobActionPerformed

    /**
     * Trata a ação do menu que inicializa a tela de estoque de produtos.
     *
     * @param evt o evento de ação gerado pelo menu
     */
    private void menEstProActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menEstProActionPerformed
        //Inicializando a tela de estoque de produtos
        TelaProdutos produtos = new TelaProdutos();
        produtos.setVisible(true);
        desktop.add(produtos);
    }//GEN-LAST:event_menEstProActionPerformed

    /**
     * Trata o evento de ativação da janela, atualizando a data atual exibida.
     *
     * @param evt o evento de ativação da janela
     */
    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        //Fazendo aparecer a data atual
        Date data = new Date();
        DateFormat formatador = DateFormat.getDateInstance(DateFormat.SHORT);
        lblData.setText(formatador.format(data));
    }//GEN-LAST:event_formWindowActivated

    /**
     * Trata a ação do menu que inicializa a tela de gestão de usuários.
     *
     * @param evt o evento de ação gerado pelo menu
     */
    private void menGerUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menGerUsuActionPerformed
        //Inicializando a tela de gestão de usuários
        TelaUsuario usuario = new TelaUsuario();
        usuario.setVisible(true);
        desktop.add(usuario);
    }//GEN-LAST:event_menGerUsuActionPerformed

    /**
     * Trata a ação do menu que inicializa a tela de relatório de produtos.
     *
     * @param evt o evento de ação gerado pelo menu
     */
    private void menGerRelProActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menGerRelProActionPerformed
        //Inicializando a tela de relatorio de produtos
        TelaRelatorioProdutos relatorioProdu = new TelaRelatorioProdutos();
        relatorioProdu.setVisible(true);
        desktop.add(relatorioProdu);
    }//GEN-LAST:event_menGerRelProActionPerformed

    /**
     * Trata a ação do menu que inicializa a tela de relatório de vendas.
     *
     * @param evt o evento de ação gerado pelo menu
     */
    private void MenGerRelVenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenGerRelVenActionPerformed
        //Inicializando a tela de relatorio de vendas
        TelaRelatorioVendas relatorioVend = new TelaRelatorioVendas();
        relatorioVend.setVisible(true);
        desktop.add(relatorioVend);
    }//GEN-LAST:event_MenGerRelVenActionPerformed

    /**
     * Trata a ação do menu que exibe uma caixa de diálogo para confirmação de
     * saída.
     *
     * @param evt o evento de ação gerado pelo menu
     */
    private void menOpcSaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menOpcSaiActionPerformed
        //Exibe uma caixa de dialogo
        int sair = JOptionPane.showConfirmDialog(null, bundle.getString("close_msg"), bundle.getString("attention"), JOptionPane.YES_NO_OPTION);
        if (sair == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_menOpcSaiActionPerformed

    /**
     * Trata a ação do menu que inicializa a tela de venda.
     *
     * @param evt o evento de ação gerado pelo menu
     */
    private void menVenVenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menVenVenderActionPerformed
        //Inicializando a tela de venda
        TelaVender vender = new TelaVender();
        vender.setVisible(true);
        desktop.add(vender);
    }//GEN-LAST:event_menVenVenderActionPerformed

    /**
     * Trata a ação do menu que abre o suporte no Discord.
     *
     * @param evt o evento de ação gerado pelo menu
     */
    private void menAjuSupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menAjuSupActionPerformed
        //Adicionando o comando que irá abrir o suporte noo dicord
        try {
            Desktop.getDesktop().browse(new URI("https://discord.gg/f8kfVFfDSk"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_menAjuSupActionPerformed

    /**
     * Trata a ação do menu que inicializa a tela de status do estoque.
     *
     * @param evt o evento de ação gerado pelo menu
     */
    private void menEstStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menEstStatusActionPerformed
        //Inicializando a tela de status do estoque
        TelaEstoqueStatus status = new TelaEstoqueStatus();
        status.setVisible(true);
        desktop.add(status);
    }//GEN-LAST:event_menEstStatusActionPerformed

    /**
     * Trata a ação do menu que inicializa a tela de Notas Fiscais.
     *
     * @param evt o evento de ação gerado pelo menu
     */
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        //iniciando a tela de Notas Fiscais
        TelaNotasFiscais notas = new TelaNotasFiscais();
        notas.setVisible(true);
        desktop.add(notas);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void telaRelatorioSalariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_telaRelatorioSalariosActionPerformed
        // TODO add your handling code here:
        TelaRelatorioUsuarios Salarios = new TelaRelatorioUsuarios();
        Salarios.setVisible(true);
        desktop.add(Salarios);
    }//GEN-LAST:event_telaRelatorioSalariosActionPerformed

    /**
     * Método principal que inicializa e exibe a tela principal da aplicação.
     *
     * @param args Os argumentos da linha de comando.
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
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem MenGerRelVen;
    private javax.swing.JDesktopPane desktop;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPopupMenu jPopupMenu1;
    public static javax.swing.JLabel lblData;
    public static javax.swing.JLabel lblUsuario;
    private javax.swing.JMenu menAju;
    private javax.swing.JMenuItem menAjuSob;
    private javax.swing.JMenuItem menAjuSup;
    private javax.swing.JMenuItem menEstPro;
    private javax.swing.JMenuItem menEstStatus;
    public static javax.swing.JMenu menEsto;
    public static javax.swing.JMenu menGer;
    private javax.swing.JMenuItem menGerRelPro;
    private javax.swing.JMenuItem menGerUsu;
    private javax.swing.JMenu menOpc;
    private javax.swing.JMenuItem menOpcSai;
    public static javax.swing.JMenu menVen;
    private javax.swing.JMenuItem menVenVender;
    private javax.swing.JMenuItem telaRelatorioSalarios;
    // End of variables declaration//GEN-END:variables
}
