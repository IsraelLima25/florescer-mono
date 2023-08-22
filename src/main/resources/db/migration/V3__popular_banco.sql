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

INSERT tbl_entrega (status,avaliacao_estrela,preco,id_endereco) VALUES ("EM_TRANSITO_PARA_DESTINO", null, 30.00,1);
INSERT tbl_pagamento (data_hora_local, preco_total, forma_pagamento, status) VALUES ("2023-07-13 09:00:00", 37.90, "CREDITO", "PROCESSADO");
INSERT tbl_pedido (data_hora_local, preco_total_itens, preco_total_pagamento,status,id_cliente,id_pagamento,id_entrega) VALUES ("2023-07-13 08:58:00", 37.90, 2.00, "AGUARDANDO_PROCESSAMENTO",1,1,1);
INSERT tbl_pedido_produto(id_pedido,id_produto,quantidade) VALUES (1,1,1);

INSERT tbl_perfil (descricao) VALUES ("ROLE_ADMIN");
INSERT tbl_usuario (login, senha) VALUES ("israelslf22@gmail.com","$2a$12$nA/7DjXyUWXVkQ8CgNfcQuTS4P0pf/KhRzjGlEnQs50hepGE8za8e");
INSERT tbl_usuario (login, senha) VALUES ("ilima@gmail.com","$2a$12$VlCQ/89XF/qO5QxiM8eGNerCWoRM6LJSjTirmiZWhOBQ769/vrRE6");
INSERT tbl_usuario_perfil (id_usuario, id_perfil) VALUES (1,1);