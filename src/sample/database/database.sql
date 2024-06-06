-- Create the database
CREATE DATABASE EDULIB_Final;

USE EDULIB_Final;

-- Create userAccount table
CREATE TABLE userAccount (
  idUserAccount INT UNSIGNED NOT NULL AUTO_INCREMENT,
  firstName VARCHAR(200) NOT NULL,
  lastName VARCHAR(200) NOT NULL,
  userName VARCHAR(200) NOT NULL,
  password VARCHAR(200) NOT NULL,
  PRIMARY KEY(idUserAccount)
);

-- Insert data into userAccount table
INSERT INTO
  userAccount (firstName, lastName, userName, password)
VALUES
  ('Zaim', 'Abbasi', 'admin', 'zaim'),
  ('Ali', 'Khan', 'alikhan', 'password1'),
  ('Aisha', 'Farooq', 'aishafarooq', 'password2');

-- Create addBook table
CREATE TABLE addBook (
  id VARCHAR(200) NOT NULL,
  title VARCHAR(200) NOT NULL,
  author VARCHAR(200) NOT NULL,
  publisher VARCHAR(200) NOT NULL,
  quantity INT NOT NULL,
  isAvail BOOLEAN DEFAULT TRUE,
  PRIMARY KEY(id)
);

-- Insert data into addBook table
INSERT INTO
  addBook (id, title, author, publisher, quantity)
VALUES
  (
    'B001',
    'The Great Gatsby',
    'F. Scott Fitzgerald',
    'Scribner',
    5
  ),
  (
    'B002',
    'To Kill a Mockingbird',
    'Harper Lee',
    'J.B. Lippincott & Co.',
    3
  ),
  (
    'B003',
    '1984',
    'George Orwell',
    'Secker & Warburg',
    4
  ),
  (
    'B004',
    'Pride and Prejudice',
    'Jane Austen',
    'T. Egerton',
    6
  ),
  (
    'B005',
    'The Catcher in the Rye',
    'J.D. Salinger',
    'Little, Brown and Company',
    2
  ),
  (
    'B006',
    'The Hobbit',
    'J.R.R. Tolkien',
    'George Allen & Unwin',
    7
  ),
  (
    'B007',
    'Moby Dick',
    'Herman Melville',
    'Harper & Brothers',
    1
  ),
  (
    'B008',
    'War and Peace',
    'Leo Tolstoy',
    'The Russian Messenger',
    5
  ),
  (
    'B009',
    'The Odyssey',
    'Homer',
    'Ancient Greece',
    3
  ),
  (
    'B010',
    'Hamlet',
    'William Shakespeare',
    'N/A',
    4
  );

-- Create addMember table
CREATE TABLE addMember (
  memberID VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  email VARCHAR(200) NOT NULL,
  phone VARCHAR(200) NOT NULL,
  gender ENUM('female', 'male') NOT NULL,
  PRIMARY KEY(memberID)
);

-- Insert data into addMember table
INSERT INTO
  addMember (memberID, name, email, phone, gender)
VALUES
  (
    'M001',
    'Ahmed Ali',
    'ahmed.ali@gmail.com',
    '03001234567',
    'male'
  ),
  (
    'M002',
    'Fatima Khan',
    'fatima.khan@gmail.com',
    '03112345678',
    'female'
  ),
  (
    'M003',
    'Sara Ahmed',
    'sara.ahmed@gmail.com',
    '03212345678',
    'female'
  ),
  (
    'M004',
    'Usman Tariq',
    'usman.tariq@gmail.com',
    '03312345678',
    'male'
  ),
  (
    'M005',
    'Hina Qureshi',
    'hina.qureshi@gmail.com',
    '03412345678',
    'female'
  ),
  (
    'M006',
    'Bilal Sheikh',
    'bilal.sheikh@gmail.com',
    '03512345678',
    'male'
  ),
  (
    'M007',
    'Nadia Hassan',
    'nadia.hassan@gmail.com',
    '03612345678',
    'female'
  ),
  (
    'M008',
    'Zara Khan',
    'zara.khan@gmail.com',
    '03712345678',
    'female'
  ),
  (
    'M009',
    'Omar Riaz',
    'omar.riaz@gmail.com',
    '03812345678',
    'male'
  ),
  (
    'M010',
    'Adeel Malik',
    'adeel.malik@gmail.com',
    '03912345678',
    'male'
  );

-- Create issuedBooks table
CREATE TABLE issuedBooks (
  bookID VARCHAR(200) NOT NULL,
  memberID VARCHAR(200) NOT NULL,
  issueTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  renew_count INT DEFAULT 0,
  PRIMARY KEY(bookID, memberID),
  FOREIGN KEY(bookID) REFERENCES addBook(id),
  FOREIGN KEY(memberID) REFERENCES addMember(memberID)
);

-- Insert data into issuedBooks table
INSERT INTO
  issuedBooks (bookID, memberID, issueTime, renew_count)
VALUES
  ('B001', 'M001', CURRENT_TIMESTAMP, 0),
  ('B002', 'M002', CURRENT_TIMESTAMP, 1),
  ('B003', 'M003', CURRENT_TIMESTAMP, 0),
  ('B004', 'M004', CURRENT_TIMESTAMP, 2);
