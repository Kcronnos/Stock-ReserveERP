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
public class TelaRelatorios extends javax.swing.JInternalFrame {
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    /**
     * Creates new form TelaRelatorios
     */
    public TelaRelatorios() {
        initComponents();
        conexao = ModuloConexao.conector();
        graficoBarra1();
        graficoBarra2();
    }
    
    //Método para a criação do grafico da validade dos produtos
    public void graficoBarra1(){
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
    
    //Método para a criação do grafico da quantidade de vendas
    public void graficoBarra2(){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.setValue(200, "Quantidade", "1");
        dataset.setValue(150, "Quantidade", "2");
        dataset.setValue(18, "Quantidade", "3");
        dataset.setValue(100, "Quantidade", "4");
        dataset.setValue(80, "Quantidade", "5");
        dataset.setValue(250, "Quantidade", "6");
        dataset.setValue(250, "Quantidade", "7");
        dataset.setValue(260, "Quantidade", "8");
        dataset.setValue(150, "Quantidade", "9");
        dataset.setValue(150, "Quantidade", "10");
        dataset.setValue(150, "Quantidade", "11");
        dataset.setValue(150, "Quantidade", "12");
        
        JFreeChart chart = ChartFactory.createBarChart("Vendas Realizadas","Mês","Quantidade", 
                dataset, PlotOrientation.VERTICAL, false,true,false);
        
        CategoryPlot categoryPlot = chart.getCategoryPlot();
        //categoryPlot.setRangeGridlinePaint(Color.BLUE);
        categoryPlot.setBackgroundPaint(Color.WHITE);
        BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
        Color clr3 = new Color(204,0,51);
        renderer.setSeriesPaint(0, clr3);
        
        ChartPanel barpChartPanel = new ChartPanel(chart);
        panelGraficoBarra2.removeAll();
        panelGraficoBarra2.add(barpChartPanel, BorderLayout.CENTER);
        panelGraficoBarra2.validate();
        
        
    }
    
    private void preencherTabelaProduto() {
        String sql = "select idproduto as ID, nomeproduto as Nome, preco as Preço,quantidade as Quantidade,peso as Peso, vencimento as Vencimento from tbprodutos";
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

        panelGraficoBarra2 = new javax.swing.JPanel();
        panelGraficoBarra1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProdutos = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVendedores = new javax.swing.JTable();
        txtVendPesquisar = new javax.swing.JTextField();
        dcPesquisarData = new com.toedter.calendar.JDateChooser();
        txtProduPesquisar = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
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

        panelGraficoBarra2.setLayout(new java.awt.BorderLayout());
        getContentPane().add(panelGraficoBarra2, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 410, 300));

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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, 420, 250));

        tblVendedores = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int ColIndex){
                return false;
            }
        };
        tblVendedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Nome Vendedor", "Data", "Valor Venda", "ID Nota Fiscal"
            }
        ));
        jScrollPane2.setViewportView(tblVendedores);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 350, 410, 260));
        getContentPane().add(txtVendPesquisar, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 320, 220, -1));

        dcPesquisarData.setDateFormatString("yyyy-MM-dd");
        getContentPane().add(dcPesquisarData, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 320, -1, -1));

        txtProduPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtProduPesquisarKeyReleased(evt);
            }
        });
        getContentPane().add(txtProduPesquisar, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 320, 230, -1));

        jLabel1.setText("Buscar");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 320, 40, -1));

        jLabel2.setText("Buscar");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, 40, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblProdutosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProdutosMouseClicked
       //
    }//GEN-LAST:event_tblProdutosMouseClicked

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
      
    }//GEN-LAST:event_formInternalFrameActivated

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        // TODO add your handling code here:
        preencherTabelaProduto();
    }//GEN-LAST:event_formInternalFrameOpened

    private void txtProduPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProduPesquisarKeyReleased
        //Chamando o método de pesquisar produtos
        pesquisarProdutos();
    }//GEN-LAST:event_txtProduPesquisarKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser dcPesquisarData;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel panelGraficoBarra1;
    private javax.swing.JPanel panelGraficoBarra2;
    private javax.swing.JTable tblProdutos;
    private javax.swing.JTable tblVendedores;
    private javax.swing.JTextField txtProduPesquisar;
    private javax.swing.JTextField txtVendPesquisar;
    // End of variables declaration//GEN-END:variables
}
