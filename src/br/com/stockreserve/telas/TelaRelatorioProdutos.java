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
import javax.swing.JOptionPane;
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
    
    //Método para a criação do grafico da validade dos produtos
    public void graficoBarra(){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.setValue(200, "", "Perto");
        dataset.setValue(150, "", "Longe");
        dataset.setValue(18, "", "Vencido");
        dataset.setValue(100, "", "NP");
        
        JFreeChart chart = ChartFactory.createBarChart("Data de validade dos produtos","Prazos","Quantidade", 
                dataset, PlotOrientation.VERTICAL, false,true,false);
        
        CategoryPlot categoryPlot = chart.getCategoryPlot();
        //categoryPlot.setRangeGridlinePaint(Color.BLUE);
        categoryPlot.setBackgroundPaint(Color.WHITE);
        BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
        Color clr3 = new Color(204,0,51);
        renderer.setSeriesPaint(0, clr3);
        
        ChartPanel barpChartPanel = new ChartPanel(chart);
        panelGraficoBarra1.removeAll();
        panelGraficoBarra1.add(barpChartPanel, BorderLayout.CENTER);
        panelGraficoBarra1.validate();
        
        
    }
   
    //Método para preencher a tabela ao abrir a aba de relatório de produtos
    private void preencherTabelaProduto() {
        String sql = "select idproduto as ID,nomeproduto as Nome, preco as Preço, quantidade as Quantidade,peso as Peso, vencimento as Vencimento from tbprodutos";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tblProdutos.setModel(DbUtils.resultSetToTableModel(rs));
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
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

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Relatório de Produtos");
        setDoubleBuffered(true);
        setVisible(true);
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 30, 560, 270));

        txtProduPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtProduPesquisarKeyReleased(evt);
            }
        });
        getContentPane().add(txtProduPesquisar, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 0, 230, -1));

        jLabel2.setText("Buscar");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 0, 40, -1));

        setBounds(0, 0, 1000, 631);
    }// </editor-fold>//GEN-END:initComponents

    private void tblProdutosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProdutosMouseClicked
       //
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelGraficoBarra1;
    private javax.swing.JTable tblProdutos;
    private javax.swing.JTextField txtProduPesquisar;
    // End of variables declaration//GEN-END:variables
}
