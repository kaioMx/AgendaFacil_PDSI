CREATE TABLE usuario (
	idUsuario INTEGER PRIMARY KEY AUTOINCREMENT,
	email TEXT UNIQUE,
	senha TEXT,
	nome TEXT
);

CREATE TABLE tarefa (
    idTarefa INTEGER PRIMARY KEY AUTOINCREMENT,
	fk_idUsuario INTEGER,
    titulo TEXT,
    descricao TEXT,
    data TEXT,
    horaInicio TEXT,
    horaFim TEXT,
    status TEXT,
    somNotificacao BLOB,
	FOREIGN KEY (fk_idUsuario) REFERENCES usuario(idUsuario)
);

CREATE TABLE categoria (
    idCategoria INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT,
    cor TEXT
);

CREATE TABLE cria_categoria (
    fk_idTarefa INTEGER,
    fk_idCategoria INTEGER,
    FOREIGN KEY (fk_idTarefa) REFERENCES tarefa(idTarefa),
    FOREIGN KEY (fk_idCategoria) REFERENCES categoria(idCategoria)
);


INSERT INTO usuario (email, senha, nome) VALUES
('mcsapao@email.com', 'senha123', 'Mc Sapao'),
('djuninhoportugal@email.com', 'senha456', 'Dj Juninho Portugal'),
('carolbiazin@email.com', 'senha789', 'Carol Biazin');

INSERT INTO tarefa (fk_idUsuario, titulo, descricao, data, horaInicio, horaFim, status, somNotificacao) VALUES
(1, 'Gravar clipe novo', 'Sessão de gravação do clipe “Vou Desafiar Você”, com equipe de filmagem e figurino.', '2025-07-26', '2025-07-26 15:00:00', '2025-07-26 18:00:00', 'confirmado', NULL),
(2, 'Mixar set gospel funk', 'Montar e finalizar setlist gospel/funk para o culto jovem da comunidade.', '2025-07-27', '2025-07-27 10:00:00', '2025-07-27 12:00:00', 'em produção',  NULL),
(3, 'Academia', 'Treino de perna e ombro.', '2025-07-25', '2025-07-25 07:00:00', '2025-07-25 08:00:00', 'concluído', NULL);

INSERT INTO categoria (nome, cor) VALUES
('Musical', '#A020F0'), -- roxo
('Musical', '#A020F0'), -- roxo
--categoria padrão
('Academia', '#00FF00'), -- verde
('Estudo', '#FFFF00'), -- amarelo
('Trabalho', '#0000FF'); -- azul 

