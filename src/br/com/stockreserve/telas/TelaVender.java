/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package br.com.stockreserve.telas;

import br.com.stockreserve.dal.ModuloConexao;
import br.com.stockreserve.dal.Produto;
import br.com.stockreserve.dal.Titulo;
import br.com.stockreserve.dal.jsonUntil.JsonUtil;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author leog4
 */
public class TelaVender extends javax.swing.JFrame {
    
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form TelaVender
     */
    public TelaVender() {
        initComponents();
        conexao = ModuloConexao.conector();
    }
    
    //coloca o produto no titulo/carrinho
    public String colocarProdutoCarrinho(JTextField idProduto, JTextField quantidadeProduto) throws SQLException, ParseException, org.json.simple.parser.ParseException {
        String produtoId = idProduto.getText().trim();
        int produtoQuant = Integer.parseInt(quantidadeProduto.getText().trim());

        //chama o metodo para procurar o produto por id
        Produto produto = buscarProduto(produtoId);
        //if caso não ache o produto ou a quantidade em estoque é insuficiente
        if (produto == null) return "Produto não encontrado.";
        if (produto.getQuantidade() < produtoQuant) return "Quantidade insuficiente.";

        //verifica se já tem um titulo/carrinho em aberto
        Titulo titulo = buscarTituloAberto();
        //se não encontar cria um novo
        if (titulo == null) {
            titulo = criarNovoTitulo(produto, produtoQuant);
        } else {
            //se encontrar adiciona o produto ao carrinho
            adicionarProdutoAoTitulo(titulo, produto, produtoQuant);
        }

        //por fim chama o metódo para atualizar a quantidade em estoque
        atualizarEstoque(produto, produtoQuant);
        return "Produto adicionado ao carrinho.";
    }

    //metódo para buscar o produto no banco de dados
    private Produto buscarProduto(String id) throws SQLException {
        String sql = "SELECT * FROM produtos WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                //retorna o produto se achar
                return new Produto(
                    rs.getString("id"),
                    rs.getString("nome"),
                    rs.getDouble("preco"),
                    rs.getInt("quantidade"),
                    rs.getDate("vencimento").toLocalDate()
                );
            }
        }
        //retorna null se não achar
        return null;
    }

    //metódo para ver se tem um titulo em aberto
    private Titulo buscarTituloAberto() throws SQLException, ParseException, org.json.simple.parser.ParseException {
        String sql = "SELECT * FROM titulos WHERE pago = false LIMIT 1";
        try (Statement stmt = conexao.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String jsonProdutos = rs.getString("produtosCarrinho");
                List<Produto> produtos = JsonUtil.jsonParaProdutos(jsonProdutos);
                //se encontrar retorna o titulo
                return new Titulo(rs.getString("id"), rs.getDouble("preco"), false, produtos);
            }
        }
        //se não encontrar retorna nulo
        return null;
    }

    //metódo para criar um novo titulo e adiconar o produto a ele
    private Titulo criarNovoTitulo(Produto produto, int quantidade) throws SQLException {
        List<Produto> produtosCarrinho = new ArrayList<>();
        produtosCarrinho.add(new Produto(produto.getId(), produto.getNome(), produto.getPreco(), quantidade, produto.getVencimento()));
        String jsonProdutos = JsonUtil.produtosParaJson(produtosCarrinho);

        String sql = "INSERT INTO titulos (id, preco, pago, produtosCarrinho) VALUES (?, ?, ?, ?)";
        String idTitulo = UUID.randomUUID().toString();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, idTitulo);
            stmt.setDouble(2, produto.getPreco() * quantidade);
            stmt.setBoolean(3, false);
            stmt.setString(4, jsonProdutos);
            stmt.executeUpdate();
        }

        return new Titulo(idTitulo, produto.getPreco(), false, produtosCarrinho);
    }

    //metódo para adicionar um novo produto ao carrinho
    private void adicionarProdutoAoTitulo(Titulo titulo, Produto produto, int quantidade) throws SQLException {
        titulo.getProdutosCarrinho().add(new Produto(produto.getId(), produto.getNome(), produto.getPreco(), quantidade, produto.getVencimento()));
        String jsonProdutos = JsonUtil.produtosParaJson(titulo.getProdutosCarrinho());

        String sql = "UPDATE titulos SET produtosCarrinho = ?, preco = preco + ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, jsonProdutos);
            stmt.setDouble(2, produto.getPreco() * quantidade);
            stmt.setString(3, titulo.getId());
            stmt.executeUpdate();
        }
    }

    //atualiza a quantidade em estoque
    private void atualizarEstoque(Produto produto, int quantidade) throws SQLException {
        String sql = "UPDATE produtos SET quantidade = quantidade - ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, quantidade);
            stmt.setString(2, produto.getId());
            stmt.executeUpdate();
        }
    }
    
    //verifica se a quantidade de produtos no estoque é suficiente para adicionar ao carrinho
	public boolean aindaTemProduto(JTextField quantidadeProduto, JTextField idProduto) {
            String sql = "seletc quantidade from tbprodutos where idproduto = ?";
            try{
                pst = conexao.prepareStatement(sql);
            
	        String quantidadeText = quantidadeProduto.getText().trim();
	        String produtoId = idProduto.getText().trim();
                
                pst.setString(1, produtoId);
                rs = pst.executeQuery();
            
	        int aindaTem = Integer.parseInt(quantidadeText);

	        if ( rs.getInt(1) >= aindaTem) {
	            return true;
	        }
            } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
          }
            return false;
	}
    
        //metódo para adicionar produtos ao carrinho
    private void adicionarProdutos() throws SQLException{
    try {
        	        // Verifica se os campos não estão vazios
        	        if (idProdutos.getText().isEmpty() || quantidadeProdutos.getText().isEmpty()) {
        	            JOptionPane.showMessageDialog(null , "Por favor, preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
        	        }else{
                         // Verifica se há estoque suficiente
        	         boolean aindaTem = aindaTemProduto(quantidadeProdutos, idProdutos);
                         if (aindaTem) {
                            String mensagem = null;
                            //chama o metódo para adicionar o produto ao titulo criado
        	            mensagem = colocarProdutoCarrinho(idProdutos, quantidadeProdutos);
        	            JOptionPane.showMessageDialog(null , mensagem);

        	            // Limpa os campos de texto após a compra
        	            limpar();

        	            // Atualiza a tabela e o total dos títulos em aberto
        	            carregarTitulosEmAbertoNaTabela();
        	            atualizarTotalEmAberto();
        	            carregarProdutosNaTabela();
        	         }else {
        	            JOptionPane.showMessageDialog(null , "Quantidade em falta no estoque ou informações incorretas.", "Erro", JOptionPane.ERROR_MESSAGE);
        	            limpar();
        	         }
                        }
           }catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
          }                
    }
    
     private void limpar() {
        idProdutos.setText(null);
        quantidadeProdutos.setText(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jInternalFrame1 = new javax.swing.JInternalFrame();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelaProdutosCarrinho = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabelaProdutosEstoque = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabelaTotal = new javax.swing.JTable();
        btPagar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        buscaProdutos = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        quantidadeProdutos = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        idProdutos = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btRemover = new javax.swing.JButton();
        btAdicionar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jInternalFrame1.setBackground(new java.awt.Color(67, 106, 137));
        jInternalFrame1.setVisible(true);
        jInternalFrame1.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tabelaProdutosCarrinho.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "NOME", "PREÇO $", "QUANT"
            }
        ));
        tabelaProdutosCarrinho.setToolTipText("");
        jScrollPane1.setViewportView(tabelaProdutosCarrinho);
        tabelaProdutosCarrinho.getAccessibleContext().setAccessibleName("");

        jInternalFrame1.getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 60, 300, 500));

        tabelaProdutosEstoque.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "NOME", "PREÇO $", "QUANT"
            }
        ));
        jScrollPane2.setViewportView(tabelaProdutosEstoque);

        jInternalFrame1.getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 60, 300, 500));

        tabelaTotal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "TOTAL"
            }
        ));
        jScrollPane3.setViewportView(tabelaTotal);

        jInternalFrame1.getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 570, 100, 60));

        btPagar.setText("PAGAR");
        btPagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPagarActionPerformed(evt);
            }
        });
        jInternalFrame1.getContentPane().add(btPagar, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 570, 100, 60));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("CARRINHO");
        jInternalFrame1.getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 30, 290, -1));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("PRODUTOS EM ESTOQUE");
        jInternalFrame1.getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 30, 300, -1));
        jInternalFrame1.getContentPane().add(buscaProdutos, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 590, 300, -1));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("BUSCAR PRODUTOS");
        jInternalFrame1.getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 570, 300, -1));
        jInternalFrame1.getContentPane().add(quantidadeProdutos, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 210, 250, -1));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("COLOQUE A QUANTIDADE DO PRODUTO");
        jInternalFrame1.getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 180, 250, -1));
        jInternalFrame1.getContentPane().add(idProdutos, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 130, 250, -1));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("COLOQUE O ID DO PRODUTO");
        jInternalFrame1.getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, 250, -1));

        btRemover.setText("REMOVER");
        jInternalFrame1.getContentPane().add(btRemover, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 260, 110, -1));

        btAdicionar.setText("ADICIONAR");
        btAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAdicionarActionPerformed(evt);
            }
        });
        jInternalFrame1.getContentPane().add(btAdicionar, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 260, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jInternalFrame1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jInternalFrame1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPagarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btPagarActionPerformed

    private void btAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAdicionarActionPerformed
        try {
            adicionarProdutos();
        } catch (SQLException ex) {
            Logger.getLogger(TelaVender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btAdicionarActionPerformed

    /**
     * @param args the command line arguments
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
            java.util.logging.Logger.getLogger(TelaVender.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaVender.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaVender.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaVender.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaVender().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdicionar;
    private javax.swing.JButton btPagar;
    private javax.swing.JButton btRemover;
    private javax.swing.JTextField buscaProdutos;
    private javax.swing.JTextField idProdutos;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField quantidadeProdutos;
    private javax.swing.JTable tabelaProdutosCarrinho;
    private javax.swing.JTable tabelaProdutosEstoque;
    private javax.swing.JTable tabelaTotal;
    // End of variables declaration//GEN-END:variables
}
