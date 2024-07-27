package web.Qroz.controller;

import web.Qroz.domain.model.Cliente;
import web.Qroz.domain.model.Pedido;
import web.Qroz.domain.model.Produto;
import web.Qroz.domain.repository.ClienteRepository;
import web.Qroz.domain.repository.PedidoRepository;
import web.Qroz.domain.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping
    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Pedido> adicionar(@RequestParam Long clienteId, @RequestParam List<Long> produtoIds, @RequestBody Pedido pedido) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(clienteId);
        if (clienteOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Cliente cliente = clienteOptional.get();
        pedido.setCliente(cliente);

        List<Produto> produtos = produtoRepository.findAllById(produtoIds);
        if (produtos.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        pedido.setProdutos(produtos);

        BigDecimal total = produtos.stream()
                .map(Produto::getPreco) // Presumindo que Produto tem um campo preco
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        pedido.setTotal(total);

        Pedido novoPedido = pedidoRepository.save(pedido);
        return ResponseEntity.ok(novoPedido);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscar(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> atualizar(@PathVariable Long id, @RequestBody Pedido pedido) {
        if (!pedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pedido.setId(id);
        return ResponseEntity.ok(pedidoRepository.save(pedido));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!pedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
