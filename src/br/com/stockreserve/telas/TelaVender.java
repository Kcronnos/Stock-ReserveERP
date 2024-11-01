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
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;
import java.sql.*;
import br.com.stockreserve.dal.ModuloConexao;
import br.com.stockreserve.dal.Produto;
import br.com.stockreserve.dal.Titulo;
import br.com.stockreserve.telas.TelaPrincipal;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Classe que representa a tela de vendas do sistema, permitindo que o usuário
 * adicione produtos ao carrinho e realize a compra. Esta classe estende
 * JInternalFrame para ser utilizada em uma interface gráfica.
 *
 * @author leog4
 * @version 2.0
 */
public class TelaVender extends javax.swing.JInternalFrame {

    Connection conexao = null; // Conexão com o banco de dados
    PreparedStatement pst = null; // PreparedStatement para consultas
    ResultSet rs = null; // ResultSet para armazenar os resultados da consulta
    ResourceBundle bundle; // Recurso de bundle para suporte a múltiplos idiomas

    /**
     * Cria uma nova instância da tela de vendas. Inicializa os componentes e a
     * conexão com o banco de dados.
     */
    public TelaVender() {
        Locale locale;
        if (LanguageSelection.selectedLanguage) {
            locale = Locale.of("en", "US"); // Define o idioma para inglês
        } else {
            locale = Locale.of("pt", "BR"); // Define o idioma para português
        }
        bundle = ResourceBundle.getBundle("br.com.stockreserve.erp", locale);
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                tblProdutos.clearSelection(); // Limpa a seleção da tabela de produtos
                tblCarrinho.clearSelection(); // Limpa a seleção da tabela do carrinho
            }
        });
        initComponents(); // Inicializa os componentes da interface
        conexao = ModuloConexao.conector(); // Conecta ao banco de dados
    }

    /**
     * Método para adicionar produtos ao carrinho. Verifica se os campos não
     * estão vazios e se há produtos disponíveis no estoque. Atualiza as tabelas
     * de produtos e carrinho após a adição.
     *
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    private void adicionarProdutos() throws SQLException {
        try {
            // Verifica se os campos não estão vazios
            if (txtProduQuanti.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        bundle.getString("mandatory"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            //verifica se tem produto suficiente em estoque
            if (!aindaTemProduto(txtProduQuanti)) {
                JOptionPane.showMessageDialog(null,
                        bundle.getString("insufficient"),
                        bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
                txtProduQuanti.setText(null);
                return;
            }

            // Adiciona o produto ao título/carrinho
            String mensagem = colocarProdutoCarrinho(txtProduQuanti);
            JOptionPane.showMessageDialog(null, mensagem);

            // Limpa os campos de texto e atualiza tabelas
            txtProduQuanti.setText(null);
            preencherTabelaCarrinho();
            preencherTabelaProduto();
            preencherTabelaTotal();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, bundle.getString("error") + ": " + e.getMessage());
        }
    }

    /**
     * Coloca um produto no título/carrinho.
     *
     * @param quantidadeProduto Campo de texto que contém a quantidade do
     * produto.
     * @return Mensagem indicando o resultado da operação.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     * @throws ParseException Se ocorrer um erro ao analisar a data.
     * @throws org.json.simple.parser.ParseException Se ocorrer um erro ao
     * analisar JSON.
     */
    public String colocarProdutoCarrinho(JTextField quantidadeProduto) throws SQLException, ParseException, org.json.simple.parser.ParseException {
        int linhaSelecionada = tblProdutos.getSelectedRow();
        if (linhaSelecionada == -1) {
            return bundle.getString("electProduct"); // Mensagem se nenhum produto foi selecionado
        }

        String produtoId = tblProdutos.getValueAt(linhaSelecionada, 0).toString();
        int idproduto = Integer.parseInt(produtoId);
        int produtoQuant = Integer.parseInt(quantidadeProduto.getText().trim());

        Produto produto = buscarProduto(idproduto);
        if (produto == null) {
            return bundle.getString("productNotFound"); // Mensagem se o produto não for encontrado
        }
        if (produto.getQuantidade() < produtoQuant) {
            return bundle.getString("insufficient2"); // Mensagem se não houver quantidade suficiente
        }

        Titulo titulo = buscarTituloAberto();
        if (titulo == null) {
            titulo = criarNovoTitulo(produto, produtoQuant); // Cria um novo título se não houver
        } else {
            adicionarProdutoAoTitulo(titulo, produto, produtoQuant); // Adiciona ao título existente
        }

        atualizarEstoque(produto, produtoQuant); // Atualiza o estoque
        return bundle.getString("productAdded"); // Mensagem de sucesso
    }

    /**
     * Busca um produto no banco de dados pelo ID.
     *
     * @param id ID do produto a ser buscado.
     * @return O produto encontrado ou null se não for encontrado.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    private Produto buscarProduto(int id) throws SQLException {
        String sql = "SELECT * FROM tbprodutos WHERE idproduto = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Produto(
                            rs.getInt("idproduto"), rs.getString("nomeproduto"),
                            rs.getDouble("preco"), rs.getInt("quantidade"),
                            rs.getDate("vencimento") != null ? rs.getDate("vencimento").toLocalDate() : null
                    );
                }
            }
        }
        return null; // Retorna null se o produto não for encontrado
    }

    /**
     * Busca um título em aberto no banco de dados.
     *
     * @return O título encontrado ou null se não houver nenhum aberto.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     * @throws ParseException Se ocorrer um erro ao analisar a data.
     * @throws org.json.simple.parser.ParseException Se ocorrer um erro ao
     * analisar JSON.
     */
    private Titulo buscarTituloAberto() throws SQLException, ParseException, org.json.simple.parser.ParseException {
        String sql = "SELECT * FROM titulos WHERE pago = false LIMIT 1";
        try (Statement stmt = conexao.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String jsonProdutos = rs.getString("produtosCarrinho");
                List<Produto> produtos = JsonUtil.jsonParaProdutos(jsonProdutos);
                return new Titulo(rs.getString("idtitulo"), rs.getDouble("preco"), false, produtos);
            }
        }

        return null; // Retorna null se não houver título em aberto
    }

    /**
     * Cria um novo título e adiciona um produto a ele.
     *
     * @param produto Produto a ser adicionado.
     * @param quantidade Quantidade do produto a ser adicionada.
     * @return O título criado.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    private Titulo criarNovoTitulo(Produto produto, int quantidade) throws SQLException {
        List<Produto> produtosCarrinho = new ArrayList<>();
        produtosCarrinho.add(new Produto(produto.getId(), produto.getNome(),
                produto.getPreco(), quantidade, produto.getVencimento()));
        String jsonProdutos = JsonUtil.produtosParaJson(produtosCarrinho);

        String sql = "INSERT INTO titulos (idtitulo, preco, pago, produtosCarrinho) VALUES (?, ?, ?, ?)";
        String idTitulo = UUID.randomUUID().toString();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, idTitulo);
            stmt.setDouble(2, 0.0);
            stmt.setBoolean(3, false);
            stmt.setString(4, jsonProdutos);
            stmt.executeUpdate();
        }

        return new Titulo(idTitulo, produto.getPreco(), false, produtosCarrinho);
    }

    /**
     * Adiciona um novo produto ao título existente no carrinho.
     *
     * @param titulo Título ao qual o produto será adicionado.
     * @param produto Produto a ser adicionado.
     * @param quantidade Quantidade do produto a ser adicionada.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    private void adicionarProdutoAoTitulo(Titulo titulo, Produto produto, int quantidade) throws SQLException {
        List<Produto> produtosCarrinho = titulo.getProdutosCarrinho();
        boolean produtoExistente = false;

        // Verifica se o produto já existe no carrinho
        for (Produto prod : produtosCarrinho) {
            if (prod.getId() == produto.getId()) {
                // Atualiza a quantidade se o produto já existir
                prod.setQuantidade(prod.getQuantidade() + quantidade);
                produtoExistente = true;
                break;
            }
        }

        // Se o produto não existe, adiciona um novo
        if (!produtoExistente) {
            produtosCarrinho.add(new Produto(produto.getId(), produto.getNome(), produto.getPreco(), quantidade, produto.getVencimento()));
        }

        // Atualiza o JSON após a adição ou atualização
        String jsonProdutos = JsonUtil.produtosParaJson(produtosCarrinho);

        // Atualiza a tabela no banco de dados
        String sql = "UPDATE titulos SET produtosCarrinho = ? WHERE idtitulo = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, jsonProdutos);
            stmt.setString(2, titulo.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Atualiza o estoque de um produto após uma venda.
     *
     * @param produto Produto a ser atualizado.
     * @param quantidade Quantidade vendida do produto.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    private void atualizarEstoque(Produto produto, int quantidade) throws SQLException {
        String sql = "UPDATE tbprodutos SET quantidade = quantidade - ? WHERE idproduto = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, quantidade);
            stmt.setInt(2, produto.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Verifica se ainda há produto disponível no estoque.
     *
     * @param quantidadeProduto o campo de texto que contém a quantidade
     * desejada do produto
     * @return true se a quantidade disponível no estoque for suficiente para
     * atender a quantidade desejada, false caso contrário ou em caso de erro
     */
    public boolean aindaTemProduto(JTextField quantidadeProduto) {
        int linhaSelecionada = tblProdutos.getSelectedRow();
        if (linhaSelecionada == -1) {
            return false;
        }

        String produtoId = tblProdutos.getValueAt(linhaSelecionada, 0).toString();
        String sql = "SELECT quantidade FROM tbprodutos WHERE idproduto = ?";

        try (PreparedStatement pst = conexao.prepareStatement(sql)) {
            pst.setString(1, produtoId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int quantidadeEmEstoque = rs.getInt("quantidade");
                    int quantidadeDesejada = Integer.parseInt(quantidadeProduto.getText().trim());
                    return quantidadeEmEstoque >= quantidadeDesejada;
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, bundle.getString("invalid_quantity") + e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, bundle.getString("error") + ": " + e.getMessage());
        }
        return false;
    }

    /**
     * Devolve um produto ao estoque e atualiza as informações do carrinho. Se
     * uma linha está selecionada no carrinho, pergunta ao usuário se deseja
     * remover o produto. Se o usuário confirmar, atualiza a quantidade do
     * produto no estoque, remove o produto do carrinho, e atualiza as tabelas
     * de produtos e carrinho.
     */
    private void devolverAoEstoque() {
        // Verifica se uma linha foi selecionada na tabela do carrinho
        int linhaSelecionada = tblCarrinho.getSelectedRow();

        if (linhaSelecionada != -1) { // Se uma linha está selecionada
            // Pergunta ao usuário se deseja realmente remover o produto
            int resposta = JOptionPane.showConfirmDialog(null, bundle.getString("remove_from_cart"), bundle.getString("Confirmation"), JOptionPane.YES_NO_OPTION);

            if (resposta == JOptionPane.YES_OPTION) {
                // Atualizando a quantidade no banco de dados
                String sql = "UPDATE tbprodutos SET quantidade = quantidade + ? WHERE idproduto = ?";
                int quantidadeCarrinho = Integer.parseInt(tblCarrinho.getValueAt(linhaSelecionada, 3).toString()); // Supondo que a coluna 3 é a quantidade no carrinho
                int idNoCarrinho = Integer.parseInt(tblCarrinho.getValueAt(linhaSelecionada, 0).toString()); // Supondo que a coluna 0 é o ID do produto

                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setInt(1, quantidadeCarrinho); // Adiciona a quantidade de volta ao estoque
                    pst.setInt(2, idNoCarrinho); // Seleciona o produto pelo ID
                    pst.executeUpdate();

                    Titulo titulo = buscarTituloAberto();

                    // Chame o método para remover o produto do carrinho
                    removerProdutoDoCarrinho(titulo, idNoCarrinho, quantidadeCarrinho);

                    // Chamando métodos para atualizar as tabelas de produtos e carrinho
                    preencherTabelaCarrinho();
                    preencherTabelaProduto();
                    preencherTabelaTotal();

                    JOptionPane.showMessageDialog(null, bundle.getString("product_removed_success"));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, bundle.getString("error_removing") + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, bundle.getString("select_to_remove"));
        }

        preencherTabelaProduto();
    }

    /**
     * Remove um produto do carrinho associado a um título específico.
     *
     * @param titulo o título do carrinho onde o produto está
     * @param idProduto o ID do produto a ser removido
     * @param quantidade a quantidade do produto a ser removida
     * @throws SQLException se ocorrer um erro ao acessar o banco de dados
     */
    private void removerProdutoDoCarrinho(Titulo titulo, int idProduto, int quantidade) throws SQLException {
        List<Produto> produtosCarrinho = titulo.getProdutosCarrinho();
        Produto produtoEncontrado = null;

        // Iterando sobre a lista de produtos
        for (Produto produto : produtosCarrinho) {
            if (produto.getId() == idProduto) {
                produtoEncontrado = produto; // Armazenando o produto encontrado
                break; // Sai do loop assim que o produto for encontrado
            }
        }

        // Verifica se o produto foi encontrado
        if (produtoEncontrado != null) {
            // Remove o produto do carrinho com base no ID
            titulo.getProdutosCarrinho().removeIf(produto -> produto.getId() == idProduto);

            // Atualiza o JSON após a remoção
            String jsonProdutos = JsonUtil.produtosParaJson(titulo.getProdutosCarrinho());

            // Atualiza a tabela no banco de dados
            String sql = "UPDATE titulos SET produtosCarrinho = ?, preco = preco - ? WHERE idtitulo = ?";
            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                stmt.setString(1, jsonProdutos);
                stmt.setDouble(2, produtoEncontrado.getPreco() * quantidade); // Subtrai o preço do produto removido
                stmt.setString(3, titulo.getId());
                stmt.executeUpdate();
            }
        } else {
            // Mensagem de erro caso o produto não seja encontrado
            JOptionPane.showMessageDialog(null, bundle.getString("not_found_cart"));
        }
    }

    /**
     * Processa o pagamento de um título, gerando uma nota fiscal e removendo o
     * título do banco de dados.
     *
     * @param nomeCliente o nome do cliente para registrar no recibo
     * @throws SQLException se ocorrer um erro ao acessar o banco de dados
     * @throws ParseException se ocorrer um erro ao converter os dados de data
     * @throws org.json.simple.parser.ParseException se ocorrer um erro ao
     * processar JSON
     */
    private void fazerPagamento(String nomeCliente) throws SQLException, ParseException, org.json.simple.parser.ParseException {

        String sql = "insert into tbnotasfiscais(idnotafiscal,nomevendedor,nomecliente,valor,datacompra,produtos) values(?,?,?,?,?,?)";
        String sql2 = "DELETE FROM titulos WHERE idtitulo = ?";
        try {
            Titulo titulo = buscarTituloAberto();
            double total = 0.0;

            List<Produto> produtos = titulo.getProdutosCarrinho();
            for (Produto produto : produtos) {
                total = total + (produto.getPreco() * produto.getQuantidade());
            }
            titulo.setPreco(total);

            String jasonCarrinho = JsonUtil.produtosParaJson(produtos);
            Timestamp dataDaCompra = Timestamp.valueOf(LocalDateTime.now());
            String idNotaFiscal = "NF-" + titulo.getId();
            String nomeVendedor = TelaPrincipal.lblUsuario.getText();

            // Inserir a nota fiscal no banco de dados
            try (PreparedStatement pst1 = conexao.prepareStatement(sql); PreparedStatement pst2 = conexao.prepareStatement(sql2)) {

                pst1.setString(1, idNotaFiscal);
                pst1.setString(2, nomeVendedor);
                pst1.setString(3, nomeCliente);
                pst1.setDouble(4, total);
                pst1.setTimestamp(5, dataDaCompra);
                pst1.setString(6, jasonCarrinho);

                pst1.executeUpdate();

                // Deletar o título pago
                pst2.setString(1, titulo.getId());
                pst2.executeUpdate();

                preencherTabelaCarrinho();
                preencherTabelaProduto();
                preencherTabelaTotal();

                JOptionPane.showMessageDialog(null, bundle.getString("payment_successfully"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, bundle.getString("error_payment") + e.getMessage());
        }
    }

    /**
     * Habilita ou desabilita o botão btRemover com base na seleção de uma linha
     * no carrinho.
     */
    private void verificarSelecaoCarrinho() {
        int linhaSelecionada = tblCarrinho.getSelectedRow();
        btnRemover.setEnabled(linhaSelecionada != -1);// Habilita o botão se houver uma linha selecionada
        if (linhaSelecionada != -1) {
            tblProdutos.clearSelection();
        }
    }

    /**
     * Define o ID do produto ao clicar em uma linha da tabela de produtos e
     * limpa a seleção do carrinho.
     */
    public void setarCampos() {
        int setar = tblProdutos.getSelectedRow();
        txtProduId.setText(tblProdutos.getModel().getValueAt(setar, 0).toString());
        if (setar != -1) {
            tblCarrinho.clearSelection();
        }
    }

    /**
     * Preenche a tabela do carrinho com os produtos atualmente no carrinho.
     *
     * @throws ParseException se ocorrer um erro ao converter os dados de data
     * @throws org.json.simple.parser.ParseException se ocorrer um erro ao
     * processar JSON
     */
    private void preencherTabelaCarrinho() throws ParseException, org.json.simple.parser.ParseException {
        String sql = "SELECT produtosCarrinho FROM titulos WHERE pago = false LIMIT 1";
        try (PreparedStatement pst = conexao.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID", bundle.getString("name"), bundle.getString("price"), bundle.getString("amount"), "TOTAL"}, 0);

            if (rs.next()) {
                List<Produto> produtos = JsonUtil.jsonParaProdutos(rs.getString("produtosCarrinho"));
                for (Produto produto : produtos) {
                    model.addRow(new Object[]{
                        produto.getId(), produto.getNome(), produto.getPreco(),
                        produto.getQuantidade(), produto.getPreco() * produto.getQuantidade()
                    });
                }
            }
            tblCarrinho.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, bundle.getString("error_loading_cart") + e.getMessage());
        }
    }

    /**
     * Preenche a tabela do total com o valor total de todos os produtos no
     * carrinho.
     *
     * @throws ParseException se ocorrer um erro ao converter os dados de data
     * @throws org.json.simple.parser.ParseException se ocorrer um erro ao
     * processar JSON
     */
    private void preencherTabelaTotal() throws ParseException, org.json.simple.parser.ParseException {
        String sql = "SELECT produtosCarrinho FROM titulos WHERE pago = false LIMIT 1";
        try (PreparedStatement pst = conexao.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            double total = 0.0;
            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"TOTAL"}, 0);

            if (rs.next()) {
                List<Produto> produtos = JsonUtil.jsonParaProdutos(rs.getString("produtosCarrinho"));
                for (Produto produto : produtos) {
                    total = total + (produto.getPreco() * produto.getQuantidade());
                }
            }

            DecimalFormat df = new DecimalFormat("#.##");
            model.addRow(new Object[]{df.format(total)});
            tblTotal.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, bundle.getString("error_loading_cart") + e.getMessage());
        }
    }

    /**
     * Preenche a tabela de produtos ao abrir a aba de relatório de produtos.
     * Converte os preços dos produtos para uma moeda de referência, dividindo
     * por 5.78. Os dados são exibidos na tabela 'tblProdutos' usando a
     * biblioteca rs2xml.jar.
     */
    private void preencherTabelaProduto() {
        String sql = "select idproduto as ID,nomeproduto as NOME, preco / 5.78 as PREÇO , quantidade as QUANT from tbprodutos";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tblProdutos.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Pesquisa um produto na tabela de produtos usando um termo de busca
     * parcial. O termo é obtido a partir do campo de texto 'txtProduPesquisar'
     * e exibe resultados onde o nome do produto começa com o termo fornecido.
     * Os preços dos produtos também são convertidos para a moeda de referência
     * dividindo-os por 5.78. Os dados são exibidos na tabela 'tblProdutos'
     * usando a biblioteca rs2xml.jar.
     */
    private void pesquisarProduto() {
        String sql = "select idproduto as ID,nomeproduto as NOME, preco / 5.78 as PREÇO , quantidade as QUANT from tbprodutos where nomeproduto like ?";
        try {
            pst = conexao.prepareStatement(sql);
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

        btnPagar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtProduPesquisar = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProdutos = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCarrinho = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtProduId = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtProduQuanti = new javax.swing.JTextField();
        btnAdicionar = new javax.swing.JButton();
        btnRemover = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTotal = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle(bundle.getString("sell")); // NOI18N
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

        btnPagar.setText(bundle.getString("pay"));
        btnPagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPagarActionPerformed(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText(bundle.getString("search"));

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
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", bundle.getString("name"), bundle.getString("price"), bundle.getString("amount")
            }
        ));
        tblProdutos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProdutosMouseClicked(evt);
            }
        });
        tblProdutos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblProdutosKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tblProdutos);

        tblCarrinho = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int ColIndex){
                return false;
            }
        };
        tblCarrinho.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", bundle.getString("name"), bundle.getString("price"), bundle.getString("amount"), "TOTAL"
            }
        ));
        tblCarrinho.setToolTipText("");
        tblCarrinho.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCarrinhoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCarrinho);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText(bundle.getString("prod_stock"));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(bundle.getString("cart"));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText(bundle.getString("selc_prod_id"));

        txtProduId.setEnabled(false);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText(bundle.getString("put_amout_prod"));

        btnAdicionar.setText(bundle.getString("add"));
        btnAdicionar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAdicionarMouseClicked(evt);
            }
        });
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        btnRemover.setText(bundle.getString("remove"));
        btnRemover.setEnabled(false);
        btnRemover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverActionPerformed(evt);
            }
        });

        btnLimpar.setText(bundle.getString("clear"));
        btnLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparActionPerformed(evt);
            }
        });

        tblTotal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Total"
            }
        ));
        jScrollPane3.setViewportView(tblTotal);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(37, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProduId, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProduQuanti, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAdicionar)
                        .addGap(47, 47, 47)
                        .addComponent(btnRemover, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(91, 91, 91)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtProduPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(4, 4, 4))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel5)
                        .addGap(4, 4, 4)
                        .addComponent(txtProduId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jLabel4)
                        .addGap(14, 14, 14)
                        .addComponent(txtProduQuanti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAdicionar)
                            .addComponent(btnRemover))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addGap(13, 13, 13)
                            .addComponent(txtProduPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(4, 4, 4)))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );

        setBounds(0, 0, 1000, 630);
    }// </editor-fold>//GEN-END:initComponents

    private void btnPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPagarActionPerformed
        //confirma a compra e pega o nome do cliente
        String nomeCliente = null;
        int resposta = JOptionPane.showConfirmDialog(
                null,
                bundle.getString("confirm_payment"),
                bundle.getString("payment_confirmation"),
                JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            // Se a resposta for "Sim", pede o nome do cliente
            nomeCliente = JOptionPane.showInputDialog(
                    null,
                    bundle.getString("customer_name"),
                    bundle.getString("client_name"),
                    JOptionPane.QUESTION_MESSAGE);
        }
        if (resposta == JOptionPane.YES_OPTION) {
            try {
                //aciona o pagamento passando o nome do cliente
                fazerPagamento(nomeCliente);
            } catch (SQLException ex) {
                Logger.getLogger(TelaVender.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(TelaVender.class.getName()).log(Level.SEVERE, null, ex);
            } catch (org.json.simple.parser.ParseException ex) {
                Logger.getLogger(TelaVender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnPagarActionPerformed

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed

    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void tblProdutosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblProdutosKeyPressed

    }//GEN-LAST:event_tblProdutosKeyPressed

    private void tblProdutosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProdutosMouseClicked
        // Chamando o metodo setar campo
        setarCampos();
        //Desativando o botão de remover
        btnRemover.setEnabled(false);
    }//GEN-LAST:event_tblProdutosMouseClicked

    private void btnAdicionarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAdicionarMouseClicked
        try {
            // TODO add your handling code here:
            adicionarProdutos();
        } catch (SQLException ex) {

        }
    }//GEN-LAST:event_btnAdicionarMouseClicked

    private void txtProduPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProduPesquisarKeyReleased
        //Chamando o método para pesquisar produtos
        pesquisarProduto();
    }//GEN-LAST:event_txtProduPesquisarKeyReleased

    private void btnRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverActionPerformed
        //Chamando o método para remover do carrinho
        devolverAoEstoque();
    }//GEN-LAST:event_btnRemoverActionPerformed

    private void tblCarrinhoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCarrinhoMouseClicked
        //Ativando o botão de remover
        btnRemover.setEnabled(true);
    }//GEN-LAST:event_tblCarrinhoMouseClicked

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLimparActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        //chamando o método para ativar o botão de remover
        tblCarrinho.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                verificarSelecaoCarrinho(); // Atualiza o estado do botão
            }
        });
        //chamando o método para preencher a tabela de produtos, a tabela total e a tabela carrinho
        preencherTabelaProduto();
        try {
            preencherTabelaTotal();
        } catch (ParseException ex) {
            Logger.getLogger(TelaVender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(TelaVender.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            preencherTabelaCarrinho();
        } catch (org.json.simple.parser.ParseException ex) {
        } catch (ParseException ex) {
            Logger.getLogger(TelaVender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formInternalFrameOpened


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JButton btnLimpar;
    private javax.swing.JButton btnPagar;
    private javax.swing.JButton btnRemover;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tblCarrinho;
    private javax.swing.JTable tblProdutos;
    private javax.swing.JTable tblTotal;
    private javax.swing.JTextField txtProduId;
    private javax.swing.JTextField txtProduPesquisar;
    private javax.swing.JTextField txtProduQuanti;
    // End of variables declaration//GEN-END:variables
}
