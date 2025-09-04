 **EzLearnApp**  is an Android application designed to boost study efficiency using the Pomodoro technique. It allows users to manage tasks, receive notifications, and stay focused with timed study sessions.

 ## Features:

- User Authentication: Email/password registration and login (Firebase Authentication).
- Pomodoro Timer: Start/stop 25-minute study cycles, customizable durations, end-of-cycle notifications, and in-session notification blocking.
- Task Management: Create tasks (title, deadline, Pomodoro cycles), mark as complete, or delete.
- Lesson Management: Upload, view, and delete PDF lessons.
- Leaderboard & Stats: View personal scores (based on study time) and global leaderboard (real-time via Firestore).
- Profile Management: Limited username changes, view email and name.
- Offline Support: Local Room database with automatic cloud sync when online.


 ## Tech Stack

* Development Environment: Android Studio (2024.1.1 "Ladybug"), Java 17, Gradle 8.7.3.
* Architecture: MVVM (Model-View-ViewModel) with LiveData and ViewModel.
* Data Management: Firebase Firestore (cloud, real-time sync), Room Persistence Library (local SQLite).
* Authentication: Firebase Authentication.
* Testing: Unit tests (Mockito, Robolectric), UI tests (Espresso).
* Other: Navigation Component (navigation), NotificationManager (notifications).

Minimum API Level: 24 (Android 7.0), supports various screen sizes (e.g., Pixel 3a, Samsung Galaxy A52s).


 ## Installation

Clone the repository:
textgit clone https://github.com/JaniITmer/EzLearnApp.git

Open in Android Studio.
Set up the Firebase project (create a Firebase console, add the app, and place google-services.json in the app/ folder).
Build the project (Build > Make Project).
Run on an emulator or physical device (Run > Run 'app').

Note: Firebase API keys are confidential and not included in the repository. A personal Firebase project is required for full functionality.

## Usage

1. Registration/Login: Launch the app, register with email/password, or log in.
2. Start Pomodoro: On the Pomodoro tab, set the time and press "Start Study." Notifications are blocked during sessions.
3. Task Management: On the Tasks tab, create new tasks (title, deadline, Pomodoro cycles), mark as done, or delete.
4. Lesson Upload: On the Lessons tab, upload a PDF, view, or delete it.
5. Leaderboard: On the Leaderboard tab, view the global ranking and your score (points based on study time).
6. Profile: On the Profile tab, modify your username (limited).

Offline mode uses local data, syncing occurs with internet access.


## Testing

Unit Tests: Used Mockito and Robolectric to test ViewModels (e.g., Pomodoro start, task addition).
UI Tests: Simulated user interactions with Espresso (e.g., login, Pomodoro start, leaderboard load).
User Trials: Manual testing with three profiles (university student, high schooler, adult) to simulate real use.


All tests passed with high code coverage.

