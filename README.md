# Memory Game - OOP Implementation Details

## 🏗️ How the Game Was Made

The Memory Game was built using **Java Swing** for the graphical interface, following **Object-Oriented Programming (OOP)** principles to keep the code organized, reusable, and maintainable.

---

## 🧩 OOP Components Used

### 1. **Classes & Objects**

The entire game is structured around classes that represent real-world game components. Each class acts like a blueprint for creating specific objects:

| Class | Real-World Analogy |
|-------|-------------------|
| `Game` | The game itself - the main container |
| `Model` | The game's internal "brain" - stores all data and rules |
| `View` | What the player sees on screen |
| `Controller` | The "referee" managing the game flow |
| `MemoryButton` | A physical card on the table |
| `HomePage` | The game's title screen |
| `Dialogs` | Popup messages for win/loss |

### 2. **Encapsulation** (Data Hiding)

Each class hides its internal data and only exposes what's necessary to the outside world.

**How it's used:**
- The `Model` class keeps track of remaining attempts privately
- You cannot directly change the number of attempts from outside
- Instead, you must use a specific method like `decrementTries()`
- The list of cards is hidden - only the controller can access it

**Why this matters:**
- Prevents accidental cheating (players can't modify their attempts)
- Makes debugging easier since data changes happen through known paths
- Protects game rules from being bypassed

### 3. **MVC Architecture** (Model-View-Controller)

This is the most important design pattern used in the project. It separates the game into three connected parts:

| Component | Responsibility |
|-----------|---------------|
| **Model** | Stores all game data (buttons, images, attempts left, game state) |
| **View** | Manages everything the user sees (panels, buttons, labels, background) |
| **Controller** | Handles user actions, updates the model, and refreshes the view |

**How they communicate:**
- User clicks a card → View captures the click
- View tells the Controller what happened
- Controller updates the Model (decrease attempts, check match)
- Controller asks the View to refresh the display
- This creates a clean separation where each part has one job

### 4. **Inheritance**

Custom components extend Java's built-in classes to add specialized behavior.

**Examples:**
- `MemoryButton` inherits from `JButton` (a standard Java button) but adds card-specific features like image handling
- `HomePage` inherits from `JPanel` (a standard container) but customizes with a background image and specific layout

**Why this matters:** Reuses all the existing functionality from Java's Swing library while only adding what's unique to the game.

### 5. **Polymorphism** (Many Forms)

The same method name can behave differently depending on the object using it.

**Examples in the game:**
- The `paintComponent` method draws differently for the `HomePage` vs the `Game View`
- The `actionPerformed` method responds differently depending on which button is clicked (start button, restart button, or card button)
- The `showDialog` method displays different messages based on win or loss

### 6. **Interfaces & Event Listeners**

Java's event system uses interfaces to define how the game responds to user actions.

**How it works:**
- The `ActionListener` interface defines what happens when any button is clicked
- `ButtonActionListener` implements this interface specifically for card clicks
- This allows the game to wait passively for user input and react instantly

### 7. **Composition** (Has-A Relationships)

Classes contain other classes as their components, creating "has-a" relationships.

**Examples:**
- The `Game` class **has-a** `Model`, a `View`, and a `Controller`
- The `View` class **has-a** collection of `MemoryButton` objects
- The `View` also **has-a** label showing remaining attempts
- The `Model` **has-a** list of all cards and their states

---

## 🔄 How All Components Work Together

### Complete Game Flow:

**Step 1 - Launch:**
- The `Main` class starts the application
- It creates and displays the `HomePage`

**Step 2 - Starting the Game:**
- Player clicks the START button on the `HomePage`
- The `Controller` creates a new `Model` and `View`
- The `Model` loads all card images from resources
- The `Model` shuffles the cards randomly

**Step 3 - During Gameplay:**
- The `View` displays the game board with all cards face down
- Player clicks a card (`MemoryButton`)
- The `ButtonActionListener` detects the click
- The `Controller` processes the game logic:
  - Flipping the card
  - Checking if two cards are selected
  - Determining if they match or not

**Step 4 - Card Matching:**
- If cards match → They remain face up permanently
- If cards don't match → The `Utilities` class creates a short delay, then flips them back
- Each pair of flips decreases the attempts counter

**Step 5 - End Game:**
- If all pairs are matched → The `Dialogs` class shows a win message
- If attempts reach zero → The `Dialogs` class shows a lose message
- Player can restart or close the game

---

## 🛠️ Supporting Components

| Component | Purpose |
|-----------|---------|
| `Utilities` | A helper class with reusable methods for loading images and creating timers |
| `Dialogs` | Manages all popup messages (win/loss notifications) |
| `ButtonActionListener` | Specifically handles every card click event |
| `ReferencedIcon` | Efficiently manages button icons to save memory |

---

## ✅ Summary of OOP Benefits in This Project

| OOP Principle | How It Helped |
|---------------|---------------|
| **Modularity** | Each class has exactly one job, making the code easy to understand |
| **Reusability** | The `Utilities` class can be used anywhere in the project |
| **Extensibility** | Easy to add new features like sound effects or difficulty levels without breaking existing code |
| **Maintainability** | If you want to change card design, you only edit `MemoryButton` class |
| **Testability** | You can test the `Model` logic completely separately from the visual `View` |

---

## 🎯 Key Takeaways

This project demonstrates:

✅ **Separation of Concerns** - The user interface, game logic, and data storage are completely separate

✅ **Event-Driven Programming** - The game responds to player actions rather than running on a fixed timeline

✅ **Real-World Modeling** - Game components (cards, board, attempts) are represented as actual objects in code

✅ **Clean Architecture** - New developers can understand the code quickly because each file has a clear purpose

---

The Memory Game is a practical, real-world example of how Object-Oriented Programming makes complex applications manageable, extensible, and professional-grade.
