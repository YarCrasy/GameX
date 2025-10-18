CREATE TABLE Cliente (
    idCliente INT AUTO_INCREMENT,
    dni VARCHAR(20) NOT NULL,
    nombreCompleto VARCHAR(100) NOT NULL,
    email VARCHAR(150),
    direccion VARCHAR(255),
    esFrecuente BOOLEAN DEFAULT FALSE,
    CONSTRAINT PK_Cliente PRIMARY KEY (idCliente),
    CONSTRAINT UQ_Cliente_DNI UNIQUE (dni)
);

CREATE TABLE Juego (
    idJuego INT AUTO_INCREMENT,
    titulo VARCHAR(150) NOT NULL,
    plataforma VARCHAR(50) NOT NULL,
    precioAlquiler DECIMAL(7,2) NOT NULL,
    genero VARCHAR(50),
    stock INT DEFAULT 0,
    CONSTRAINT PK_Juego PRIMARY KEY (idJuego)
);

CREATE TABLE Alquiler (
    idAlquiler INT AUTO_INCREMENT,
    idCliente INT NOT NULL,
    fechaAlquiler DATE NOT NULL,
    hayRetraso BOOLEAN DEFAULT FALSE,
    multaRetraso DECIMAL(7,2) DEFAULT 0.00,
    CONSTRAINT PK_Alquiler PRIMARY KEY (idAlquiler),
    CONSTRAINT FK_Alquiler_Cliente FOREIGN KEY (idCliente)
    REFERENCES Cliente (idCliente) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE Alquilado (
    idAlquilado INT AUTO_INCREMENT,
    idAlquiler INT NOT NULL,
    idJuego INT NOT NULL,
    cantidad INT DEFAULT 1,
    precio DECIMAL(7,2),
    fechaDevolucion DATE,

    CONSTRAINT PK_Alquilado PRIMARY KEY (idAlquilado),
    CONSTRAINT FK_Alquilado_Alquiler FOREIGN KEY (idAlquiler)
        REFERENCES Alquiler (idAlquiler)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT FK_Alquilado_Juego FOREIGN KEY (idJuego)
        REFERENCES Juego (idJuego)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT UQ_Alquilado_Alquiler_Juego UNIQUE (idAlquiler, idJuego)
);

INSERT INTO Cliente (dni, nombreCompleto) VALUES
('12345678A', 'Cliente1 Apellido1'),
('87654321B', 'Cliente2 Apellido2'),
('11223344C', 'Cliente2 Apellido1'),
('44332211D', 'Cliente3 Apellido2');

INSERT INTO Juego (titulo, plataforma, precioAlquiler, genero, stock) VALUES
('Juego1', 'Nintendo Switch', 5.99, 'Action-Adventure', 10),
('Juego2', 'PlayStation 5', 6.99, 'RPG', 8),
('Juego3', 'Xbox Series X', 4.99, 'Shooter', 15),
('Juego4', 'PC', 3.99, 'Strategy', 20);

INSERT INTO Alquiler (idCliente, fechaAlquiler) VALUES
(1, '2024-06-01'),
(2, '2024-06-02'),
(1, '2024-06-03');

INSERT INTO Alquilado (idAlquiler, idJuego, cantidad, precio) VALUES
(1, 1, 1, 5.99),
(1, 3, 2, 9.98),
(2, 2, 1, 6.99),
(3, 4, 1, 3.99);

DELIMITER //
CREATE PROCEDURE GetGames()
BEGIN
    SELECT * FROM Juego;
END //

CREATE PROCEDURE GetGamesByTitle(IN gameTitle VARCHAR(150))
BEGIN
    SELECT * FROM Juego
    WHERE UPPER(titulo) LIKE CONCAT('%', UPPER(gameTitle), '%');
END //
