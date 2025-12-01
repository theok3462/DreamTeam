🌟 Dream Team — Chess Game GUI (CS 3354 – Phase 3)



Semester: Fall 2025

Team Members: Theo Kliewer, Mario Mondragon, Ikram Yahya

Course: CS 3354 – Object-Oriented Design

Project: Chess Game — Phase 3



📝 Project Overview



This project implements a fully interactive Chess Game using Java and Swing.

Phase 3 delivers the complete gameplay experience, including:



Backend game logic (moves, captures, check \& checkmate)



Interactive GUI board



Move history tracking



Captured pieces display



Undo move



Player timers



Dark mode



Custom board colors and piece themes



Save \& Load system



Settings window



UML class diagram



Multiple extra credit features



🎮 Features Implemented

✔️ Game Logic



Legal movement rules for all pieces



Move validation



Captures



Check \& checkmate detection



Turn switching



Undo last move



✔️ GUI Features



Click to select piece, click destination



Highlight selected piece and legal squares



Move history panel



Captured pieces display



Player timers (Start / Stop / Reset)



Menu bar: New Game, Save, Load, Settings



Dark mode support



✔️ Customization Features



Change board light/dark square colors



Change piece theme (Default, Vibrant, Ocean)



Scale piece sizes



Switch between light mode and dark mode UI



🚀 How to Compile \& Run the Chess Game

🖥️ Option 1 — Run Using IntelliJ IDEA (RECOMMENDED)



Open IntelliJ IDEA.



Click File → Open….



Select the folder:



DreamTeam/



Wait for IntelliJ to finish indexing the project.



In the Project panel, open:



src/chessgui/ChessApp.java



At the top of that file, find:



public static void main(String\[] args)



Click the green ▶ Run button next to it.



The Chess Game GUI window will open.



🖥️ Option 2 — Run From Command Prompt (Windows)



Open Command Prompt.



Go to your project folder:



cd C:\\Users\\surfaceLaptop4\\OneDrive\\Desktop\\DreamTeam



Compile all Java source files into the out folder:



javac src\\\*\*\\\*.java -d out



Run the main class:



java -cp out chessgui.ChessApp



The Chess Game GUI window will open.



📸 Screenshots

1️⃣ New Game Screen



!\[New Game](screenshots/new\_game.png)



2️⃣ Mid Game



!\[Mid Game](screenshots/mid\_game.png)



3️⃣ Board \& Piece Settings



!\[Board Settings](screenshots/board\_settings.png)



4️⃣ Theme Preview (Vibrant / Ocean)



!\[Theme Board](screenshots/theme\_board.png)



5️⃣ Dark Square Color Picker



!\[Dark Picker](screenshots/dark\_square\_picker.png)



📊 UML Class Diagram



The UML diagram includes:



Piece inheritance



Board–Piece composition



GameState–Board relationship



GUI layout (ChessFrame with panels)



Theme / Settings relationship



Diagram image file in repo:



uml/phase3\_uml.png



✔️ Extra Credit Features Implemented

Extra Feature	Status

Dark Mode	✅

Custom Board Colors	✅

Multiple Piece Themes	✅

Timers	✅

Move History	✅

Captured Pieces Panel	✅

Save \& Load	✅

Settings Window	✅

Undo Move	✅

📂 Project Structure



src/



chess/ – backend logic (board, pieces, game rules)



chessgui/ – GUI logic (windows, panels, timers, history)



screenshots/ – images used in README



new\_game.png



mid\_game.png



board\_settings.png



theme\_board.png



dark\_square\_picker.png



uml/ – UML diagram



phase3\_uml.png



README.md – this document



👨‍🏫 Course Requirements Covered



Backend + GUI integrated



Valid move handling and rule enforcement



Check and checkmate detection



GUI supports gameplay, history, timers, and settings



UML class diagram included



Multiple commits with descriptive messages



README with screenshots, instructions, and features



Ready for in-class presentation



🎉 Final Notes



This project combines a complete chess rules engine with a Java Swing graphical user interface to create a polished, fully playable chess game with several customization and extra-credit features.



Enjoy the game! ♟️

