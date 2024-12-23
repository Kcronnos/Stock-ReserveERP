/*
 * The MIT License
 *
 * Copyright 2024 Stock&Reserve.
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

import br.com.stockreserve.dal.JsonUtil;
import br.com.stockreserve.dal.ModuloConexao;
import br.com.stockreserve.dal.Produto;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Classe que representa a tela de notas fiscais, permitindo exibir e interagir
 * com os dados de notas fiscais. Inclui métodos para preencher tabelas,
 * pesquisar notas fiscais e mostrar detalhes de uma nota fiscal selecionada. A
 * interface é configurada para exibir informações em português ou inglês,
 * dependendo da seleção de idioma.
 *
 * @author leog4
 * @version 2.0
 */
public class TelaNotasFiscais extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    ResourceBundle bundle;

    /**
     * Construtor que cria a interface de notas fiscais, configurando o idioma e
     * a conexão com o banco de dados. Define o título da janela com base na
     * seleção de idioma.
     */
    public TelaNotasFiscais() {
        Locale locale;
        if (LanguageSelection.selectedLanguage) {
            locale = Locale.of("en", "US");
        } else {
            locale = Locale.of("pt", "BR");
        }
        bundle = ResourceBundle.getBundle("br.com.stockreserve.erp", locale);
        initComponents();
        setTitle(bundle.getString("invoices"));
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                tblNotasFiscais.clearSelection();
                btnVerDetalhes.setEnabled(false);
            }
        });
        conexao = ModuloConexao.conector();
    }

    /**
     * Preenche a tabela de notas fiscais com os dados do banco de dados.
     * Converte o valor de vendas se o idioma selecionado for o inglês.
     */
    private void preencherTabelaNotasFiscais() {
        String sql;
        if (LanguageSelection.selectedLanguage) {
            sql = "SELECT idnotafiscal AS ID_NOTA, nomevendedor AS VENDEDOR, nomecliente AS CLIENTE, valor / 5.78 AS VALOR, datacompra AS `DATA/HORA` "
                    + "FROM tbnotasfiscais ";
        } else {
            sql = "SELECT idnotafiscal AS ID_NOTA, nomevendedor AS VENDEDOR, nomecliente AS CLIENTE, valor AS VALOR, datacompra AS `DATA/HORA` "
                    + "FROM tbnotasfiscais ";
        }
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tblNotasFiscais.setModel(DbUtils.resultSetToTableModel(rs));
            atualizarNomesColunas();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Atualiza os nomes das colunas da tabela com base na seleção de idioma.
     */
    private void atualizarNomesColunas() {
        tblNotasFiscais.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ID_invoices"));
        tblNotasFiscais.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("seller"));
        tblNotasFiscais.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("client"));
        tblNotasFiscais.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("value"));
        tblNotasFiscais.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("dt"));
        tblNotasFiscais.getTableHeader().repaint();
    }

    /**
     * Realiza uma pesquisa de notas fiscais com base em um termo de pesquisa
     * inserido pelo usuário. Filtra os resultados pelo ID da nota fiscal,
     * vendedor ou cliente.
     */
    private void pesquisarNota() {
        String sql;
        if (LanguageSelection.selectedLanguage) {
            sql = "SELECT idnotafiscal AS "+bundle.getString("ID_invoices")+", nomevendedor AS "+bundle.getString("seller")+", nomecliente AS "+bundle.getString("client")+", valor / 5.78 AS "+bundle.getString("value")+", datacompra AS `"+bundle.getString("dt")+"` "
            + "FROM tbnotasfiscais "
            + "WHERE idnotafiscal LIKE ? OR nomevendedor LIKE ? OR nomecliente LIKE ?";
        } else {
            sql = "SELECT idnotafiscal AS "+bundle.getString("ID_invoices")+", nomevendedor AS "+bundle.getString("seller")+", nomecliente AS "+bundle.getString("client")+", valor AS "+bundle.getString("value")+", datacompra AS `"+bundle.getString("dt")+"` "
            + "FROM tbnotasfiscais "
            + "WHERE idnotafiscal LIKE ? OR nomevendedor LIKE ? OR nomecliente LIKE ?";
        }
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

    /**
     * Exibe uma janela com detalhes da nota fiscal selecionada, incluindo
     * informações do vendedor, cliente, valor e data da compra, além de uma
     * lista de produtos associados à nota fiscal.
     *
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     * @throws ParseException Se ocorrer um erro ao analisar os dados da nota
     * fiscal.
     */
    private void mostrarDetalhesNotaFiscal() throws SQLException, ParseException {
        String sql = "SELECT * FROM tbnotasfiscais WHERE idnotafiscal = ?";
        String sql2 = "SELECT produtos FROM tbnotasfiscais WHERE idnotafiscal = ?";

        int selectedRow = tblNotasFiscais.getSelectedRow();
        if (selectedRow >= 0) {
            // Obtendo o ID da nota fiscal selecionada
            String idNota = (String) tblNotasFiscais.getValueAt(selectedRow, 0);

            try (
                    PreparedStatement pst1 = conexao.prepareStatement(sql); PreparedStatement pst2 = conexao.prepareStatement(sql2)) {
                // Consulta principal
                pst1.setString(1, idNota);
                try (ResultSet rs1 = pst1.executeQuery()) {
                    if (rs1.next()) {
                        // Criando um JFrame para mostrar os detalhes
                        JFrame detalhesFrame = new JFrame("invoice_details");
                        detalhesFrame.setSize(600, 400);
                        detalhesFrame.setLayout(new BorderLayout());

                        // Painel para organizar o conteúdo
                        JPanel panelDetalhes = new JPanel(new GridBagLayout());
                        GridBagConstraints gbc = new GridBagConstraints();
                        gbc.insets = new Insets(5, 5, 5, 5);
                        gbc.anchor = GridBagConstraints.WEST;

                        // Adicionando informações da nota fiscal
                        adicionarLabel(panelDetalhes, bundle.getString("ID_invoices") + ":", tblNotasFiscais.getValueAt(selectedRow, 0).toString(), 0, gbc);
                        adicionarLabel(panelDetalhes, bundle.getString("seller") + ":", tblNotasFiscais.getValueAt(selectedRow, 1).toString(), 1, gbc);
                        adicionarLabel(panelDetalhes, bundle.getString("client") + ":", tblNotasFiscais.getValueAt(selectedRow, 2).toString(), 2, gbc);
                        adicionarLabel(panelDetalhes, bundle.getString("value") + ":", String.valueOf(tblNotasFiscais.getValueAt(selectedRow, 3)), 3, gbc);
                        adicionarLabel(panelDetalhes, bundle.getString("dt") + ":", tblNotasFiscais.getValueAt(selectedRow, 4).toString(), 4, gbc);

                        // Adicionando a lista de produtos
                        gbc.gridy = 5;
                        gbc.gridwidth = 2;
                        gbc.anchor = GridBagConstraints.CENTER;
                        panelDetalhes.add(new JLabel(bundle.getString("prods")), gbc);

                        // Configuração da tabela de produtos
                        String[] colunas = {"ID", bundle.getString("prod_name"), bundle.getString("prod_price"), bundle.getString("amount")};
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
                        JOptionPane.showMessageDialog(this, bundle.getString("invoice_not_found"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException | ParseException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, bundle.getString("error_fetching_details") + e.getMessage(), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("no_invoice_selected"), bundle.getString("warning"), JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Método auxiliar para adicionar um JLabel ao painel com informações de
     * rótulo e valor.
     *
     * @param panel O painel ao qual o JLabel será adicionado.
     * @param labelText O texto do rótulo.
     * @param valueText O texto do valor.
     * @param row A linha onde o JLabel será adicionado.
     * @param gbc O GridBagConstraints usado para posicionamento.
     */
    private void adicionarLabel(JPanel panel, String labelText, String valueText, int row, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(new JLabel(valueText), gbc);
    }

    /**
     * Habilita ou desabilita o botão "Ver Detalhes" com base na seleção de uma
     * linha na tabela de notas fiscais. Se uma linha estiver selecionada, o
     * botão é habilitado; caso contrário, o botão é desabilitado.
     */
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

        tblNotasFiscais = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int ColIndex){
                return false;
            }
        };
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
    /**
    * botão que faz a ação de exibir detalhes da notas fiscais
    */
    private javax.swing.JButton btnVerDetalhes;
    /**
    * exibe o texto notas fiscais
    */
    private javax.swing.JLabel jLabel1;
    /**
    * exibe texto buscar notas
    */
    private javax.swing.JLabel jLabel2;
    /**
    * scrollpane da tabela de notas fiscais
    */
    private javax.swing.JScrollPane jScrollPane1;
    /**
    * tabela que exibe notas fiscais
    */
    private javax.swing.JTable tblNotasFiscais;
    /**
    * recebe texto para buscar e filtrar notas fiscais
    */
    private javax.swing.JTextField txtBuscadorDeNotas;
    // End of variables declaration//GEN-END:variables
}
