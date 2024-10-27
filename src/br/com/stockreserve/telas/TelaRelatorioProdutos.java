/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.stockreserve.telas;

import java.awt.BorderLayout;
import java.awt.Color;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import java.sql.*;
import br.com.stockreserve.dal.ModuloConexao;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Felipe
 */
public class TelaRelatorioProdutos extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form TelaRelatorios
     */
    public TelaRelatorioProdutos() {
        initComponents();
        conexao = ModuloConexao.conector();
        graficoBarra();
    }

    private void alterarPreco() {
        String sql = "update tbprodutos set preco =? where idproduto =?";

        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtNovoPreco.getText().replace(",", "."));
            pst.setString(2, txtIdProdu.getText());

            if (txtNovoPreco.getText().isEmpty() || txtIdProdu.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Tenha certeza que os campos estão preenchidos");
            } else {
                int alterado = pst.executeUpdate();
                if (alterado > 0) {
                    JOptionPane.showMessageDialog(null, "Dados do produto alterados com sucesso!");
                    preencherTabelaProduto();
                    limpar();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    //Método para criar o graádico de barras
    public void graficoBarra() {
        int produtosVencidos = 0;
        int produtosPertoDeVencer = 0;
        int produtosLongeDeVencer = 0;
        int produtosSemData = 0;

        String sql = """
    SELECT 
        COUNT(CASE WHEN vencimento < CURDATE() THEN 1 END) AS vencidos,
        COUNT(CASE WHEN vencimento >= CURDATE() AND vencimento <= DATE_ADD(CURDATE(), INTERVAL 31 DAY) THEN 1 END) AS perto,
        COUNT(CASE WHEN vencimento > DATE_ADD(CURDATE(), INTERVAL 31 DAY) THEN 1 END) AS longe,
        COUNT(CASE WHEN vencimento IS NULL THEN 1 END) AS sem_data
    FROM tbprodutos;
    """;

        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();

            if (rs.next()) {
                produtosVencidos = rs.getInt("vencidos");
                produtosPertoDeVencer = rs.getInt("perto");
                produtosLongeDeVencer = rs.getInt("longe");
                produtosSemData = rs.getInt("sem_data");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar dados do gráfico: " + e);
        }

        // Configuração do gráfico de barras
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(produtosVencidos, "Vencido", "Status");
        dataset.addValue(produtosPertoDeVencer, "Perto de Vencer", "Status");
        dataset.addValue(produtosLongeDeVencer, "Longe de Vencer", "Status");
        dataset.addValue(produtosSemData, "Sem Data", "Status");

        JFreeChart chart = ChartFactory.createBarChart(
                "Data de Validade dos Produtos",
                "Status",
                "Quantidade",
                dataset,
                PlotOrientation.VERTICAL,
                true, // Exibe legenda
                true,
                false
        );

        CategoryPlot categoryPlot = chart.getCategoryPlot();
        categoryPlot.setBackgroundPaint(Color.WHITE);

        // Configuração das cores para as barras
        BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
        renderer.setSeriesPaint(0, Color.RED);       // Vencido
        renderer.setSeriesPaint(1, Color.YELLOW);    // Perto de Vencer
        renderer.setSeriesPaint(2, Color.GREEN);     // Longe de Vencer
        renderer.setSeriesPaint(3, Color.GRAY);      // Sem Data

        ChartPanel barChartPanel = new ChartPanel(chart);
        panelGraficoBarra1.removeAll();
        panelGraficoBarra1.add(barChartPanel, BorderLayout.CENTER);
        panelGraficoBarra1.validate();
    }

    //Método para preencher a tabela ao abrir a aba de relatório de produtos
    private void preencherTabelaProduto() {
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
            tblProdutos.setModel(DbUtils.resultSetToTableModel(rs));

            // Aplica o renderizador colorido à coluna `STATUS`
            tblProdutos.getColumnModel().getColumn(tblProdutos.getColumnCount() - 1)
                    .setCellRenderer(new TelaRelatorioProdutos.StatusCellRenderer());

            // Aplica o renderizador colorido à coluna `VENCIMENTO`
            int vencimentoColIndex = tblProdutos.getColumnModel().getColumnIndex("VENCIMENTO");
            tblProdutos.getColumnModel().getColumn(vencimentoColIndex)
                    .setCellRenderer(new TelaRelatorioProdutos.VencimentoCellRenderer());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    //Método para pesquisar os produtos no banco de dados e adicionar a tabela enquanto você digita o nome
    private void pesquisarProdutos() {
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
    FROM tbprodutos
    WHERE nomeproduto LIKE ?
    """;

        try {
            pst = conexao.prepareStatement(sql);
            //passando o conteúdo da caixa de pesquisa para o ?
            //atenção ao % que é a continuação da string sql
            pst.setString(1, txtProduPesquisar.getText() + "%");
            rs = pst.executeQuery();
            //A linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela
            tblProdutos.setModel(DbUtils.resultSetToTableModel(rs));

            // Configura o renderizador de cores para a coluna `STATUS`
            int statusColIndex = tblProdutos.getColumnModel().getColumnIndex("STATUS");
            tblProdutos.getColumnModel().getColumn(statusColIndex)
                    .setCellRenderer(new TelaRelatorioProdutos.StatusCellRenderer());

            // Configura o renderizador de cores para a coluna `VENCIMENTO`
            int vencimentoColIndex = tblProdutos.getColumnModel().getColumnIndex("VENCIMENTO");
            tblProdutos.getColumnModel().getColumn(vencimentoColIndex)
                    .setCellRenderer(new TelaRelatorioProdutos.VencimentoCellRenderer());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    //Método para setar o id ao clicar na tabela
    private void setarCampos() {
        int setar = tblProdutos.getSelectedRow();
        txtIdProdu.setText(tblProdutos.getModel().getValueAt(setar, 0).toString());
    }
    
    //Método para limpar os campos após alterar o preço
    private void limpar() {
        txtIdProdu.setText(null);
        txtNovoPreco.setText(null);
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
                    java.util.Date vencimentoDate = DATE_FORMAT.parse(value.toString());
                    java.util.Date currentDate = new java.util.Date();
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

        panelGraficoBarra1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProdutos = new javax.swing.JTable();
        txtProduPesquisar = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtIdProdu = new javax.swing.JTextField();
        txtNovoPreco = new javax.swing.JTextField();
        btnAlterarPreco = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Relatório de Produtos");
        setDoubleBuffered(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
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

        panelGraficoBarra1.setLayout(new java.awt.BorderLayout());
        getContentPane().add(panelGraficoBarra1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 420, 300));

        tblProdutos = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int ColIndex){
                return false;
            }
        };
        tblProdutos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "NOME", "PREÇO", "QUANTIDADE", "LIMITE_MÍNIMO", "VENCIMENTO", "STATUS"
            }
        ));
        tblProdutos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProdutosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProdutos);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 30, 560, 270));

        txtProduPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtProduPesquisarKeyReleased(evt);
            }
        });
        getContentPane().add(txtProduPesquisar, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 0, 230, -1));

        jLabel2.setText("Buscar");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 0, 40, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Alterar o Preço:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 310, -1, -1));

        jLabel3.setText("ID");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 350, 50, -1));

        jLabel4.setText("Novo Preço");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 380, -1, -1));

        txtIdProdu.setEnabled(false);
        getContentPane().add(txtIdProdu, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 350, 110, -1));
        getContentPane().add(txtNovoPreco, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 380, 110, -1));

        btnAlterarPreco.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br.com.stockreserve.icones/produto_editar.png"))); // NOI18N
        btnAlterarPreco.setToolTipText("Alterar Preço");
        btnAlterarPreco.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAlterarPreco.setPreferredSize(new java.awt.Dimension(50, 50));
        btnAlterarPreco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarPrecoActionPerformed(evt);
            }
        });
        getContentPane().add(btnAlterarPreco, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 350, -1, 50));

        setBounds(0, 0, 1000, 631);
    }// </editor-fold>//GEN-END:initComponents

    private void tblProdutosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProdutosMouseClicked
        //Chamando o método para setar campos
        setarCampos();
    }//GEN-LAST:event_tblProdutosMouseClicked

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
        //Era pra a tabela está sendo ativada com a chamada do método abaixo ahhhhhh
        //Fuciona normal nas outras telas, mas aqui parou de funcionar não sei o porquê
        //Agora só ativa quando clica em qualquer lugar dentro da aba
        preencherTabelaProduto();
    }//GEN-LAST:event_formInternalFrameActivated

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        //Chamando o método pra preencher a tabela ao abrir a aba
        preencherTabelaProduto();
    }//GEN-LAST:event_formInternalFrameOpened

    private void txtProduPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProduPesquisarKeyReleased
        //Chamando o método de pesquisar produtos
        pesquisarProdutos();
    }//GEN-LAST:event_txtProduPesquisarKeyReleased

    private void btnAlterarPrecoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarPrecoActionPerformed
        //Chamando o método para alterar preço
        alterarPreco();
    }//GEN-LAST:event_btnAlterarPrecoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlterarPreco;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelGraficoBarra1;
    private javax.swing.JTable tblProdutos;
    private javax.swing.JTextField txtIdProdu;
    private javax.swing.JTextField txtNovoPreco;
    private javax.swing.JTextField txtProduPesquisar;
    // End of variables declaration//GEN-END:variables
}
