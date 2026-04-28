# NotesApp
A Java console application where users can register, log in, and manage personal notes. The application includes role-based access with USER and ADMIN roles. Developed as part of a course in IT security.

## Setup
- Configure the database connection in db.properties.
- Ensure the MySQL database and required tables exist.
- Run the Main class to start the application.
## Usage
Users can:
- register a new account
- log in with a username and password
- create, view, edit, and delete their own notes
- change their password

Admin users can:
- view all users in the system
- view all notes in the system
- delete notes created by other users
## Technologies
- MySQL
- BCrypt (password hashing)
