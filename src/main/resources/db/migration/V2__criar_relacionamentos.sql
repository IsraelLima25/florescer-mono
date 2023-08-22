ALTER TABLE tbl_cliente ADD CONSTRAINT fk_endereco_cliente	FOREIGN KEY (id_endereco) REFERENCES tbl_endereco (id);
ALTER TABLE tbl_fornecedor ADD CONSTRAINT fk_endereco_fornecedor FOREIGN KEY (id_endereco) REFERENCES tbl_endereco (id);

ALTER TABLE tbl_produto  ADD CONSTRAINT fk_fornecedor FOREIGN KEY (id_fornecedor) REFERENCES tbl_fornecedor (id);

ALTER TABLE tbl_pedido  ADD CONSTRAINT fk_cliente FOREIGN KEY (id_cliente) REFERENCES tbl_cliente (id);
ALTER TABLE tbl_pedido  ADD CONSTRAINT fk_pagamento FOREIGN KEY (id_pagamento) REFERENCES tbl_pagamento (id);
ALTER TABLE tbl_pedido  ADD CONSTRAINT fk_entrega FOREIGN KEY (id_entrega) REFERENCES tbl_entrega (id);


ALTER TABLE tbl_reserva_estoque  ADD CONSTRAINT fk_produto_reserva_estoque FOREIGN KEY (id_produto) REFERENCES tbl_produto (id);
ALTER TABLE tbl_reserva_estoque  ADD CONSTRAINT fk_pedido_reserva_estoque FOREIGN KEY (id_pedido) REFERENCES tbl_pedido (id);

ALTER TABLE tbl_pedido_produto  ADD CONSTRAINT fk_pedido FOREIGN KEY (id_pedido) REFERENCES tbl_pedido (id);
ALTER TABLE tbl_pedido_produto  ADD CONSTRAINT fk_produto FOREIGN KEY (id_produto) REFERENCES tbl_produto (id);

ALTER TABLE tbl_usuario_perfil  ADD CONSTRAINT fk_usuario FOREIGN KEY (id_usuario) REFERENCES tbl_usuario (id);
ALTER TABLE tbl_usuario_perfil  ADD CONSTRAINT fk_perfil FOREIGN KEY (id_perfil) REFERENCES tbl_perfil (id);