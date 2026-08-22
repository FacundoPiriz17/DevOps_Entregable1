CREATE TYPE estado_juego AS ENUM ('publicado', 'pausado');

CREATE TABLE Usuario (
    email TEXT PRIMARY KEY,
    nombre TEXT NOT NULL,
    pais TEXT NOT NULL
);

CREATE TABLE Login (
    email TEXT PRIMARY KEY,
    contrasenia TEXT NOT NULL,
    FOREIGN KEY (email) REFERENCES Usuario(email)
);

CREATE TABLE General (
    emailGeneral TEXT PRIMARY KEY
    FOREIGN KEY (emailGeneral) REFERENCES Usuario(email)
);

CREATE TABLE Administrador (
    emailAdmin TEXT PRIMARY KEY
    FOREIGN KEY (emailAdmin) REFERENCES Usuario(email)
);

CREATE TABLE Juego (
    identificador SERIAL PRIMARY KEY,
    nombre TEXT NOT NULL,
    descripcion TEXT NOT NULL,
    precio INT NOT NULL,
    estudio TEXT NOT NULL,
    estado estado_juego DEFAULT 'publicado',
    admin_registra TEXT NOT NULL,
    fecha_registro DATE DEFAULT CURRENT_DATE,
    FOREIGN KEY (admin_registra) REFERENCES Administrador(emailAdmin)
);

CREATE TABLE Imagen (
    url TEXT PRIMARY KEY,
    nombre TEXT NOT NULL,
    identificador_juego INT NOT NULL,
    FOREIGN KEY (identificador_juego) REFERENCES Juego(identificador)
);

CREATE TABLE Categoria (
    identificador SERIAL PRIMARY KEY,
    nombre TEXT NOT NULL
);

CREATE TABLE tiene (
    identificador_juego INT,
    identificador_categoria INT,
    FOREIGN KEY (identificador_juego) REFERENCES Juego(identificador),
    FOREIGN KEY (identificador_categoria) REFERENCES Categoria(identificador)
    PRIMARY KEY(identificador_juego, identificador_categoria)
);

CREATE TABLE biblioteca (
    identificador_juego INT,
    identificador_general INT,
    fecha_registro DATE DEFAULT CURRENT_DATE,
    es_favorito BOOLEAN DEFAULT false,
    ultima_partida DATE DEFAULT NULL,
    horas_totales INT DEFAULT 0,
    FOREIGN KEY (identificador_juego) REFERENCES Juego(identificador),
    FOREIGN KEY (identificador_general) REFERENCES General(emailGeneral),
    PRIMARY KEY(identificador_juego, identificador_general)
);

CREATE TABLE carrito (
    identificador_juego INT,
    identificador_general INT,
    FOREIGN KEY (identificador_juego) REFERENCES Juego(identificador),
    FOREIGN KEY (identificador_general) REFERENCES General(emailGeneral),
    PRIMARY KEY(identificador_juego, identificador_general)
);

CREATE TABLE deseados (
    identificador_juego INT,
    identificador_general INT,
    FOREIGN KEY (identificador_juego) REFERENCES Juego(identificador),
    FOREIGN KEY (identificador_general) REFERENCES General(emailGeneral),
    PRIMARY KEY(identificador_juego, identificador_general)
);