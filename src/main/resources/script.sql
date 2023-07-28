--CREATE DATABASE test_db_florescer;
--USE test_db_florescer;

CREATE TABLE tbl_endereco(
id BIGINT AUTO_INCREMENT,
cep VARCHAR(12) NOT NULL,
logradouro VARCHAR(50) NOT NULL,
complemento VARCHAR(30) NULL,
bairro VARCHAR(30) NOT NULL,
localidade VARCHAR(30) NOT NULL,
uf varchar(2) NOT NULL,
PRIMARY KEY(id)
);

CREATE TABLE tbl_cliente(
id BIGINT AUTO_INCREMENT,
nome VARCHAR(50) NOT NULL,
cpf VARCHAR(11) NOT NULL,
rg VARCHAR(15) NOT NULL,
data_nascimento DATE NOT NULL,
id_endereco BIGINT NOT NULL,
PRIMARY KEY(id)
);

CREATE TABLE tbl_fornecedor(
id BIGINT AUTO_INCREMENT,
nome VARCHAR(100) NOT NULL,
cnpj VARCHAR(14) NOT NULL,
id_endereco BIGINT NOT NULL,
PRIMARY KEY(id)
);

CREATE TABLE tbl_produto(
id BIGINT AUTO_INCREMENT,
descricao VARCHAR(100) NOT NULL,
preco DECIMAL(8,2) NOT NULL,
quantidade_estoque MEDIUMINT NOT NULL,
sigla_filial VARCHAR(2) NOT NULL,
id_fornecedor BIGINT NOT NULL,
PRIMARY KEY(id)
);

CREATE TABLE tbl_pedido(
id BIGINT AUTO_INCREMENT,
data_hora_local DATETIME NOT NULL,
preco_total_itens DECIMAL(8,2) NOT NULL,
preco_total_pagamento DECIMAL(8,2) NOT NULL,
status VARCHAR(30) NOT NULL,
id_cliente BIGINT NOT NULL,
id_pagamento BIGINT NULL,
id_entrega BIGINT NULL,
observacao TEXT NULL,
PRIMARY KEY(id)
);

CREATE TABLE tbl_pedido_produto(
id BIGINT AUTO_INCREMENT,
id_pedido BIGINT NOT NULL,
id_produto BIGINT NOT NULL,
quantidade BIGINT NOT NULL,
PRIMARY KEY(id)
);

CREATE TABLE tbl_pagamento(
id BIGINT AUTO_INCREMENT,
data_hora_local DATETIME NOT NULL,
preco_total DECIMAL(8,2) NOT NULL,
forma_pagamento VARCHAR(30) NOT NULL,
status VARCHAR(30) NOT NULL,
observacao TEXT NULL,
PRIMARY KEY(id)
);

CREATE TABLE tbl_entrega(
id BIGINT AUTO_INCREMENT,
status VARCHAR(50) NOT NULL,
avaliacao_estrela VARCHAR(30) NULL,
preco DECIMAL(6,2) NOT NULL,
id_endereco BIGINT NOT NULL,
observacao TEXT NULL,
PRIMARY KEY(id)
);

CREATE TABLE tbl_reserva_estoque(
id BIGINT AUTO_INCREMENT,
id_produto BIGINT NOT NULL,
id_pedido BIGINT NOT NULL,
quantidade BIGINT NOT NULL, 
PRIMARY KEY(id)
);

ALTER TABLE tbl_cliente ADD CONSTRAINT fk_endereco_cliente	FOREIGN KEY (id_endereco) REFERENCES tbl_endereco (id);
ALTER TABLE tbl_fornecedor ADD CONSTRAINT fk_endereco_fornecedor FOREIGN KEY (id_endereco) REFERENCES tbl_endereco (id);

ALTER TABLE tbl_produto  ADD CONSTRAINT fk_fornecedor FOREIGN KEY (id_fornecedor) REFERENCES tbl_fornecedor (id);

ALTER TABLE tbl_pedido  ADD CONSTRAINT fk_cliente FOREIGN KEY (id_cliente) REFERENCES tbl_cliente (id);
ALTER TABLE tbl_pedido  ADD CONSTRAINT fk_pagamento FOREIGN KEY (id_pagamento) REFERENCES tbl_pagamento (id);
ALTER TABLE tbl_pedido  ADD CONSTRAINT fk_entrega FOREIGN KEY (id_entrega) REFERENCES tbl_entrega (id);


ALTER TABLE tbl_reservar_estoque  ADD CONSTRAINT fk_produto_reserva_estoque FOREIGN KEY (id_produto) REFERENCES tbl_produto (id);
ALTER TABLE tbl_reservar_estoque  ADD CONSTRAINT fk_pedido_reserva_estoque FOREIGN KEY (id_pedido) REFERENCES tbl_pedido (id);

ALTER TABLE tbl_pedido_produto  ADD CONSTRAINT fk_pedido FOREIGN KEY (id_pedido) REFERENCES tbl_pedido (id);
ALTER TABLE tbl_pedido_produto  ADD CONSTRAINT fk_produto FOREIGN KEY (id_produto) REFERENCES tbl_produto (id);

INSERT tbl_endereco (cep,logradouro,complemento,bairro,localidade,uf) VALUES ("01001000","Praça da Sé","lado ímpar","Sé","São Paulo", "sp");
INSERT tbl_endereco (cep,logradouro,complemento,bairro,localidade,uf) VALUES ("41654963","Rua das rosas","lado A","Almirante","Rio de Janeiro", "rj");
INSERT tbl_endereco (cep,logradouro,complemento,bairro,localidade,uf) VALUES ("41654123","Praça das borboletas","Quadra B","Mirante","Belo Horizonte", "bh");
INSERT tbl_endereco (cep,logradouro,complemento,bairro,localidade,uf) VALUES ("41456933","Praça piedade","barris","centro","Salvador", "ba");
INSERT tbl_endereco (cep,logradouro,complemento,bairro,localidade,uf) VALUES ("41654000","Rua das bandeiras","Quadra C","ilha do governador","Belo Horizonte", "bh");
INSERT tbl_endereco (cep,logradouro,complemento,bairro,localidade,uf) VALUES ("41456369","Alameda dos bandeirantes","Margaridas","centro","Pernambuco", "pe");

INSERT tbl_cliente (nome,cpf,rg,data_nascimento,id_endereco) VALUES ("Paula Nobrega Souto", "18641711031", "351509161", "1990-01-15", 1);
INSERT tbl_cliente (nome,cpf,rg,data_nascimento,id_endereco) VALUES ("Ricardo Manuel Peixoto", "86809129057", "447890190", "1985-02-10",1);
INSERT tbl_cliente (nome,cpf,rg,data_nascimento,id_endereco) VALUES ("Maria Claudia Medeiros", "20591023083", "237800263", "1960-10-18",2);
INSERT tbl_cliente (nome,cpf,rg,data_nascimento,id_endereco) VALUES ("Sizenando Mauro Farias", "68857406083", "416459407", "2005-11-03",3);
INSERT tbl_cliente (nome,cpf,rg,data_nascimento,id_endereco) VALUES ("Pamela Souto Gouveia", "60832579050", "483182448", "1999-12-31",4);

INSERT tbl_fornecedor (nome,cnpj,id_endereco) VALUES ("Flores plantações e sementes ltda","03538913000154",5);
INSERT tbl_fornecedor (nome,cnpj,id_endereco) VALUES ("Margaridas do sertão","67448732000110",6);

INSERT tbl_produto (descricao, preco, quantidade_estoque, sigla_filial,id_fornecedor) VALUES ("Rosa", 7.90, 100,"rj", 1);
INSERT tbl_produto (descricao, preco, quantidade_estoque, sigla_filial, id_fornecedor) VALUES ("Margarida", 10.00, 50, "ba", 1);
INSERT tbl_produto (descricao, preco, quantidade_estoque, sigla_filial, id_fornecedor) VALUES ("Orquidea", 13.80, 20, "sp", 2);

INSERT tbl_entrega (status,avaliacao_estrela,preco,id_endereco) VALUES ("TRANSITO", null, 30.00,1);
INSERT tbl_pagamento (data_hora_local, preco_total, forma_pagamento, status) VALUES ("2023-07-13 09:00:00", 37.90, "CREDITO", "PAGO");
INSERT tbl_pedido (data_hora_local, preco_total,status,id_cliente,id_pagamento,id_entrega) VALUES ("2023-07-13 08:58:00", 37.90, "PROCESSANDO_PAGAMENTO",1,1,1);
INSERT tbl_pedido_produto(id_pedido,id_produto,quantidade) VALUES (1,1,1);